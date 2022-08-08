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
import java.nio.channels.Selector
import java.util.stream.Collectors
import kotlin.streams.asStream

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
      "getAllConnectedAndInstalledNodes" -> {
        getAllPairedAndInstalledNodes(result)
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
              "connected" to node.isNearby.toString(),
              "isInstall" to true.toString()
            ))

            /** send to method channel **/
          }
          result.success(device)
        } else {
          /** send to method channel **/
          result.success(mapOf(
            "id" to "null",
            "name" to "null",
            "connected" to "false",
            "isInstall" to false.toString()
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

  /** Get all connected nodes and installed all nodes **/
  private fun getAllPairedAndInstalledNodes(result: Result) {
   try {
     Thread(Runnable {
       /** Get and node data **/
       allConnectedNodes = Tasks.await(nodeClient.connectedNodes)
       allNodesWithInstallApp = Tasks.await(capabilityClient.getCapability("wear", CapabilityClient.FILTER_ALL)).nodes

       /** local variables **/
       val newAllConnectedNodesList = mutableListOf<Map<String, String>>()
       val newAllInstalledNodesList = mutableListOf<Map<String, String>>()

       /** set new json data structure **/
       allConnectedNodes!!.forEach { node ->
         newAllConnectedNodesList.add(mapOf(
           "id" to node.id.toString(),
           "name" to node.displayName,
           "connected" to node.isNearby.toString(),
           "isInstall" to false.toString()
         ))
       }
       allNodesWithInstallApp!!.forEach { node ->
         newAllInstalledNodesList.add(mapOf(
           "id" to node.id.toString(),
           "name" to node.displayName,
           "connected" to node.isNearby.toString(),
           "isInstall" to true.toString()
         ))
       }

       /** merge both list into one **/
       val localNodeList: MutableList<Map<String, String>> = (newAllConnectedNodesList.associateBy { it["id"] } + newAllInstalledNodesList.associateBy{ it["id"]})
         .values.toMutableList()

       /** send to method channel **/
       result.success(localNodeList)
     }).start()
   } catch (ex:Exception) {
     Log.d(TAG, "Failed to get node", ex)
   }
  }

  /** Send messaged to wear **/
  private fun sendMessage(call: MethodCall, result: Result) {
    if (activity == null) {
      result.success(null)
    } else {
      try {
        Thread(Runnable {
          val argument = call.arguments<HashMap<String, Any>>()
          val client = Wearable.getMessageClient(activity!!)
          val nodeId: String = argument!!["node_id"].toString()
          val path: String = argument["path"].toString()
          val data = argument["data"]
          Log.e("path", path)

          /** send to method channel **/
          val json = (data as Map<*, *>?)?.let { JSONObject(it).toString() }
          client.sendMessage(nodeId, path, json!!.toByteArray()).addOnSuccessListener {
            Log.d(TAG,"sent message: $json to $nodeId")
          }
          result.success(null)

        }).start()
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
    super.onMessageReceived(message)
    try {
      /** send to method channel **/
      messageListenerIds.forEach { id ->
        channel.invokeMethod("messageReceived", hashMapOf(
          "id" to id,
          "args" to JSONObject(mapOf(
            "path" to message.path,
            "size" to message.data.size.toString(),
            "data" to String(message.data)
          )).toString()
        ))
      }
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
    }
  }

  /** Listen wear connected nodes **/
  override fun onCapabilityChanged(devices: CapabilityInfo) {
    super.onCapabilityChanged(devices)
    try {
      Thread(Runnable {
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

      }).start()
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
    }
  }

  /** Open wearable play store **/
  private fun openPlayStoreOnWearDevicesWithoutApp(call: MethodCall, result: Result) {
    try {
     Thread(Runnable {
       /** Local variables **/
       val argument = call.arguments<HashMap<String, Any>>()
       val nodeId: String = argument!!["node_id"].toString()
       val marketUri: String = argument["market_id"].toString()
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
         /** Open play store in wearOS **/
         remoteActivityHelper.startRemoteActivity(
           targetIntent = Intent(Intent.ACTION_VIEW)
             .addCategory(Intent.CATEGORY_BROWSABLE)
             .setData(Uri.parse("market://details?id=${marketUri}")),
           targetNodeId = nodeId
         )
         result.success(mapOf(
           "success" to "error",
           "message" to "opening in wearable"
         ))
         Log.d(TAG, "opening in wearable")
       }
     }).start()
    } catch (cancellationException: CancellationException) {
      Log.e(TAG, "Opening action was canceled")

    } catch (throwable: Throwable) {
      Log.e(TAG, throwable.message.toString())
    }

  }

  companion object {
    const val TAG = "WearableCommunicator"
  }
}