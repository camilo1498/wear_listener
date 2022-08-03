package dev.camilo.plugin.wear_communicator.wearable_communicator

import android.app.Activity
import android.net.Uri
import androidx.annotation.NonNull
import com.google.android.gms.wearable.*
import io.flutter.Log

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.json.JSONObject

class WearableCommunicatorPlugin: FlutterPlugin, MethodCallHandler,
  ActivityAware, MessageClient.OnMessageReceivedListener,
  DataClient.OnDataChangedListener, CapabilityClient.OnCapabilityChangedListener,
  WearableListenerService() {

  private lateinit var channel : MethodChannel

  private var activity: Activity? = null
  private val messageListenerIds = mutableListOf<Int>()
  private val pairedDevicesListenerIds = mutableListOf<Int>()
  private var binary: BinaryMessenger? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "wearableCommunicator")
    binary = flutterPluginBinding.binaryMessenger
    channel.setMethodCallHandler(this)
  }

  /** Method channels **/
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
        result.success("no data")
      }
      "listenMessages" -> {
        registerMessageListener(call)
        result.success(null)
      }
      "listenDevices" -> {
        registerPairedDevicesListener(call)
        result.success(null)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  /** Message listener **/
  private fun registerMessageListener(call: MethodCall) {
    try {
      val id = call.arguments<Int>()
      messageListenerIds.add(id!!)
    } catch (ex: Exception) {
      Log.e(TAG, ex.localizedMessage!!, ex)
    }
  }

  /** Wear node listener **/
  private fun registerPairedDevicesListener(call: MethodCall) {
    try {
      val id = call.arguments<Int>()
      pairedDevicesListenerIds.add(id!!)
    } catch (ex: Exception) {
      Log.e(TAG, ex.localizedMessage!!, ex)
    }
  }

  /** get Wear nodes **/
  private fun getWearableNode(result: Result) {
    try {
      /** search reachable nodes **/
      Wearable.getNodeClient(activity!!).connectedNodes.addOnSuccessListener { nodes ->
        if(nodes.isNotEmpty()){
          /** save available nodes **/
          val device = mutableListOf<Map<String, String>>()
          nodes.forEach{ node ->
            device.add(mapOf(
              "id" to node.id.toString(),
              "name" to node.displayName.toString(),
              "connected" to node.isNearby.toString()
            ))
          }
          /** send to method channel **/
          result.success(device)
        } else{
          /** send to method channel **/
          result.success(
            mutableListOf(hashMapOf(
            "id" to "no data",
            "name" to "no data",
            "connected" to false
          ))
          )
        }

        /** Error handler **/
      }.addOnFailureListener { ex ->
        result.error(ex.message!!, ex.localizedMessage, ex)
      }
    } catch (ex: Exception){
      Log.d(TAG, "Failed to get node", ex)
    }
  }

  /** Send messaged to wear **/
  private fun sendMessage(call: MethodCall, result: Result) {
    if (activity == null) {
      result.success(null)
    } else {
      try {
        val argument = call.arguments<HashMap<String, Any>>()
        val client = Wearable.getMessageClient(activity!!)
        /** get available nodes **/
        Wearable.getNodeClient(activity!!).connectedNodes.addOnSuccessListener { nodes ->
          /** send to method channel **/
          nodes.forEach { node ->
            val json = (argument as Map<*, *>?)?.let { JSONObject(it).toString() }
            client.sendMessage(node.id, "/MessageChannel", json!!.toByteArray()).addOnSuccessListener {
              Log.d(TAG,"sent message: $json to ${node.displayName}")
            }
          }
          result.success(null)

          /** Error handler **/
        }.addOnFailureListener { ex ->
          result.error(ex.message!!, ex.localizedMessage, ex)
        }

      } catch (ex: Exception) {
        Log.d(TAG, "Failed to send message", ex)
      }
    }
  }

  /** Close channel **/
  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  /** Initialize/reactivate  listeners **/
  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    startWearableClients(activity!!)
  }

  /** Close listeners **/
  override fun onDetachedFromActivityForConfigChanges() {
    val a = activity ?: return
    detachWearableClients(a)
    activity = null
  }

  /** Initialize/reactivate listeners **/
  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    startWearableClients(activity!!)
  }

  /** Close listeners **/
  override fun onDetachedFromActivity() {
    val a = activity ?: return
    detachWearableClients(a)
    activity = null
  }

  /** Initialize listeners **/
  private fun startWearableClients(a: Activity) {
    Wearable.getMessageClient(a).addListener(this)
    Wearable.getDataClient(a).addListener(this)
    /** required to listen connected devices **/
    Wearable.getCapabilityClient(a).addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
  }

  /** Close listeners **/
  private fun detachWearableClients(a: Activity) {
    Wearable.getMessageClient(a).removeListener(this)
    Wearable.getDataClient(a).removeListener(this)
    Wearable.getCapabilityClient(a).removeListener(this)
  }

  /** Listen received messages **/
  override fun onMessageReceived(message: MessageEvent) {
    /** send to method channel **/
    val data = String(message.data)
    messageListenerIds.forEach { id ->
      channel.invokeMethod("messageReceived", hashMapOf(
        "id" to id,
        "args" to data
      ))
    }
  }

  /** Listen wear connected nodes **/
  override fun onCapabilityChanged(devices: CapabilityInfo) {
    super.onCapabilityChanged(devices)
    /** save available nodes **/
    val device = mutableListOf<Map<String, String>>()
    devices.nodes.forEach{ nodes ->
      device.add(mapOf(
        "id" to nodes.id.toString(),
        "name" to nodes.displayName.toString(),
        "connected" to nodes.isNearby.toString()
      ))
    }
    /** send to method channel **/
    pairedDevicesListenerIds.forEach { id ->
      channel.invokeMethod("availableNode", hashMapOf(
        "id" to id,
        "args" to device.toString()
      ))

    }

  }

  companion object {
    const val TAG = "WearableCommunicator"
  }
}