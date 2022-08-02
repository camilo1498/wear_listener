package dev.camilo.plugin.wear_communicator.wearable_communicator

import android.app.Activity
import androidx.annotation.NonNull
import com.google.android.gms.wearable.*
import io.flutter.Log

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.json.JSONObject

class WearableCommunicatorPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener, CapabilityClient.OnCapabilityChangedListener, WearableListenerService() {

  private lateinit var channel : MethodChannel

  private var activity: Activity? = null
  private val messageListenerIds = mutableListOf<Int>()
  private val dataListenerIds = mutableListOf<Int>()

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.flutterEngine.dartExecutor, "wearableCommunicator")
    channel.setMethodCallHandler(this);
  }

  companion object {
    const val TAG = "WearableCommunicator"
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      "getWearableNode" -> {
        getWearableNode(result)
      }
      "sendMessage" -> {
        sendMessage(call, result)
      }
      "setData" -> {
        setData(call, result)
      }
      "listenMessages" -> {
        registerMessageListener(call)
        result.success(null)
      }
      "listenData" -> {
        registerDataLayerListener(call)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  /// message listener
  private fun registerMessageListener(call: MethodCall) {
    try {
      val id = call.arguments<Int>()
      messageListenerIds.add(id!!)
    } catch (ex: Exception) {
      Log.e(TAG, ex.localizedMessage!!, ex)
    }
  }

  // dataLaye listener
  private fun registerDataLayerListener(call: MethodCall) {
    try {
      val id = call.arguments<Int>()
      dataListenerIds.add(id!!)
    } catch (ex: Exception) {
      Log.e(TAG, ex.localizedMessage!!, ex)
    }
  }

  /// get wear node
  private fun getWearableNode(result: Result) {
    try {
      Wearable.getNodeClient(activity!!).connectedNodes.addOnSuccessListener { nodes ->
        if(nodes.isNotEmpty()){
          nodes.forEach { node ->
            Log.d(TAG,"node: name => $node")
            result.success(hashMapOf(
              "id" to node.id,
              "name" to node.displayName,
              "connected" to true
            ))


          }
        } else{
          Log.d(TAG, "disconnected")
          result.success(hashMapOf(
            "id" to null,
            "name" to null,
            "connected" to false
          ))
        }


      }.addOnFailureListener { ex ->
        result.error(ex.message!!, ex.localizedMessage, ex)
      }
    } catch (ex: Exception){
      Log.d(TAG, "Failed to get node", ex)
    }
  }

  ///send data to wear
  private fun sendMessage(call: MethodCall, result: Result) {
    if (activity == null) {
      result.success(null)
    } else {
      try {
        val argument = call.arguments<HashMap<String, Any>>()
        val client = Wearable.getMessageClient(activity!!)
        Wearable.getNodeClient(activity!!).connectedNodes.addOnSuccessListener { nodes ->
          nodes.forEach { node ->
            val json = (argument as Map<*, *>?)?.let { JSONObject(it).toString() }
            client.sendMessage(node.id, "/MessageChannel", json!!.toByteArray()).addOnSuccessListener {
              Log.d(TAG,"sent message: $json to ${node.displayName}")
            }
          }
          result.success(null)
        }.addOnFailureListener { ex ->
          result.error(ex.message!!, ex.localizedMessage, ex)
        }

      } catch (ex: Exception) {
        Log.d(TAG, "Failed to send message", ex)
      }
    }
  }


  /// set data to wear from dataLayer
  private fun setData(call: MethodCall, result: Result) {
    try {
      val data = call.argument<HashMap<String, Any>>("data") ?: return
      Log.d(TAG, data.toString())
      val name = call.argument<String>("path") ?: return
      val request = PutDataMapRequest.create(name).run {
        loop@ for ((key, value) in data) {
          when(value) {
            is String -> {
              dataMap.putString(key, value)
            }
            is Int -> dataMap.putInt(key, value)
            is Float -> dataMap.putFloat(key, value)
            is Double -> dataMap.putDouble(key, value)
            is Long -> dataMap.putLong(key, value)
            is Boolean -> dataMap.putBoolean(key, value)
            is List<*> -> {
              if (value.isEmpty()) continue@loop
              value.asArrayListOfType<Int>()?.let {
                dataMap.putIntegerArrayList(key, it)
              }
              value.asArrayListOfType<String>()?.let {
                dataMap.putStringArrayList(key, it)
              }
              value.asArrayOfType<Float>()?.let {
                dataMap.putFloatArray(key, it.toFloatArray())
              }
              value.asArrayOfType<Long>()?.let {
                dataMap.putLongArray(key, it.toLongArray())
              }
            }
            else -> {
              Log.d(TAG, "could not translate value of type ${value.javaClass.name}")
            }
          }
        }
        asPutDataRequest()
      }
      Wearable.getDataClient(activity!!).putDataItem(request).addOnSuccessListener {
        Log.d(TAG, "Set data on wear")
      }
      result.success(null)
    } catch (ex: Exception) {
      Log.e(TAG, "Failed to send message", ex)
    }
  }



  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    startWearableClients(activity!!)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    val a = activity ?: return
    detachWearableClients(a)
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    startWearableClients(activity!!)
  }

  override fun onDetachedFromActivity() {
    val a = activity ?: return
    detachWearableClients(a)
    activity = null
  }

  private fun startWearableClients(a: Activity) {
    Wearable.getMessageClient(a).addListener(this)
    Wearable.getDataClient(a).addListener(this)
  }

  private fun detachWearableClients(a: Activity) {
    Wearable.getMessageClient(a).removeListener(this)
    Wearable.getDataClient(a).removeListener(this)
  }

  override fun onMessageReceived(message: MessageEvent) {
    val data = String(message.data)
    messageListenerIds.forEach { id ->
      channel.invokeMethod("messageReceived", hashMapOf(
        "id" to id,
        "args" to data
      ))
    }

  }

  override fun onDataChanged(events: DataEventBuffer) {
    events.forEach { event ->
      if (event.type == DataEvent.TYPE_CHANGED) {
        val datamap = DataMapItem.fromDataItem(event.dataItem).dataMap
        val map = hashMapOf<String, Any>()
        for (key in datamap.keySet()) {
          map[key] = datamap.get(key)!!
        }
        dataListenerIds.forEach { id ->
          channel.invokeMethod("dataReceived", hashMapOf(
            "id" to id,
            "args" to map
          ))
        }

      }
    }
  }

  override fun onCapabilityChanged(devices: CapabilityInfo) {
    super.onCapabilityChanged(devices)
    Log.d("wearable_communicator devices", devices.nodes.toString())
  }
}

inline fun <reified T> List<*>.asArrayListOfType(): ArrayList<T>? =
  if (all { it is T })
    @Suppress("UNCHECKED_CAST")
    this as ArrayList<T> else
    null

inline fun <reified T> List<*>.asArrayOfType(): Array<T>? =
  if (all { it is T })
    @Suppress("UNCHECKED_CAST")
    this as Array<T> else
    null

