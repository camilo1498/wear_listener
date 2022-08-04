package dev.camilo.plugin.wear_communicator.wearable_communicator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.NonNull
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.*
import org.json.JSONObject

class WearableCommunicatorPlugin: FlutterPlugin, MethodCallHandler,
  ActivityAware, MessageClient.OnMessageReceivedListener,
  DataClient.OnDataChangedListener, CapabilityClient.OnCapabilityChangedListener,
  WearableListenerService() {

  /** instances **/
  private lateinit var channel : MethodChannel
  private var activity: Activity? = null
  private lateinit var context: Context
  private lateinit var capabilityClient: CapabilityClient
  private lateinit var remoteActivityHelper: RemoteActivityHelper
  private lateinit var nodeClient: NodeClient

  /** variables **/
  private val messageListenerIds = mutableListOf<Int>()
  private val pairedDevicesListenerIds = mutableListOf<Int>()
  private var allNodesWithInstallApp: Set<Node>? = null
  private var allConnectedNodes: List<Node>? = null


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "wearableCommunicator")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
    CoroutineScope(Dispatchers.Main).launch {
      withContext(Dispatchers.Default) {
        /** Initialize instances **/
        remoteActivityHelper = RemoteActivityHelper(context)
        capabilityClient = Wearable.getCapabilityClient(context)
        nodeClient = Wearable.getNodeClient(context)
      }
    }
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
      "getWearableNodeWithInstallApp" -> {
        getNodesWithInstalledApp(result)
      }
      "sendMessage" -> {
        sendMessage(call, result)
      }
      "listenMessages" -> {
        registerMessageListener(call)
        result.success(null)
      }
      "listenDevices" -> {
        registerPairedDevicesListener(call)
        result.success(null)
      }
      "openPlayStoreInWearable" -> {
        openPlayStoreOnWearDevicesWithoutApp(call, result)
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

  /** get nodes with installed application **/
  private fun getNodesWithInstalledApp(result: Result) {
    try {
      Thread(Runnable {
        /** search all paired nodes with installed app ("wear path is the same that android wear capabilities file "res/values/wear.xml") **/
        allNodesWithInstallApp = Tasks.await(capabilityClient.getCapability("wear", CapabilityClient.FILTER_ALL)).nodes
        val device = mutableListOf<Map<String, String>>()
        /** Validate nodes **/
        if(allNodesWithInstallApp != null && (allNodesWithInstallApp as MutableSet<Node>).isNotEmpty()) {
          (allNodesWithInstallApp as MutableSet<Node>).forEach { node ->
            /** add node into a new json structure **/
            device.add(mapOf(
              "id" to node.id.toString(),
              "name" to node.displayName.toString(),
              "connected" to node.isNearby.toString()
            ))

            /** send to method channel **/
          }
          result.success(device)
        } else {
          /** send to method channel **/
          result.success(mapOf(
            "id" to "null",
            "name" to "null",
            "connected" to "false"
          ))
        }
      }).start()
    } catch (ex: Exception) {
      Log.d(TAG, "Failed to get node", ex)
    }
  }

  /** get all Wear nodes **/
  private fun getWearableNode(result: Result) {
    try {
      Thread(Runnable {
        //getNodesWithInstalledApp(result)
        /** search reachable nodes **/
        allConnectedNodes = Tasks.await(nodeClient.connectedNodes)

        /** validate nodes **/
        if(allConnectedNodes != null && allConnectedNodes!!.isNotEmpty()){
          val device = mutableListOf<Map<String, String>>()
          /** add node into a new json structure **/
          allConnectedNodes!!.forEach { node ->

            device.add(mapOf(
              "id" to node.id.toString(),
              "name" to node.displayName.toString(),
              "connected" to node.isNearby.toString()
            ))
          }
          /** send to method channel **/
          result.success(device)
        } else {
          /** send to method channel **/
          result.success(
            mutableListOf(hashMapOf(
              "id" to "no data",
              "name" to "no data",
              "connected" to false
            ))
          )
        }
      }).start()
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
    /** required to listen connected devices ("wear path is the same that android wear capabilities file "res/values/wear.xml") **/
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
    Log.e("device", devices.nodes.toString())
    /** send to method channel **/
    pairedDevicesListenerIds.forEach { id ->
      channel.invokeMethod("availableNode", hashMapOf(
        "id" to id,
        "args" to device.toString()
      ))

    }

  }

  /** Open wearable play store **/
  private fun openPlayStoreOnWearDevicesWithoutApp(call: MethodCall, result: Result) {
    try {
      /** Local variables **/
      val argument = call.arguments<HashMap<String, Any>>()
      val nodeId: String = argument!!.map { msg -> msg.value}[0].toString()
      var isInstall = false

      /** search node into list **/
      for(node in allNodesWithInstallApp!!) {
        if(node.id.toString() == nodeId) {
          isInstall = true
        }
      }

      /** validate if app is install in wearable **/
      if(isInstall) {
        Log.e(TAG, "This device already has an installed app")
        result.success(mapOf(
          "success" to "error",
          "message" to "This device already has an installed app"
        ))
      } else {
        /** Initialize intent **/
        val intent = Intent(Intent.ACTION_VIEW)
          .addCategory(Intent.CATEGORY_BROWSABLE)
          .setData(Uri.parse("market://details?id=wearablesoftware.wearspotifyplayer&hl=es_CO&gl=US"))

        /** Open play store in wearOS **/
        remoteActivityHelper.startRemoteActivity(
          targetIntent = intent,
          targetNodeId = nodeId
        )
        result.success(mapOf(
          "success" to "error",
          "message" to "opening in wearable"
        ))
        Log.d(TAG, "opening in wearable")
      }
    } catch (cancellationException: CancellationException) {
      Log.e(TAG, "Opening action was canceled")

    } catch (throwable: Throwable) {
      Log.e(TAG, throwable.message.toString()/*"Request was cancelled normally"*/)
    }

  }

  companion object {
    const val TAG = "WearableCommunicator"
  }

}