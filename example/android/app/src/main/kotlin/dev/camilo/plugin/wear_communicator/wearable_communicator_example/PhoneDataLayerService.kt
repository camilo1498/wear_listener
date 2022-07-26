package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

class PhoneDataLayerService: WearableListenerService() {

    private val messageClient by lazy { Wearable.getMessageClient(this) }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val mPrefs = getSharedPreferences("FlutterSharedPreferences", 0)
        when (messageEvent.path) {
            SEND_TOKEN -> {
                messageClient.sendMessage(messageEvent.sourceNodeId, SEND_TOKEN,
                    mPrefs.getString("flutter." + "wear", "").toString().toByteArray())

                Log.d("Background", "Message from ${messageEvent.sourceNodeId}")
                Log.d("sharedPreferences","${mPrefs.getString("flutter." + "wear", "")}")
            }
        }
    }

    companion object {
        private const val SEND_TOKEN = "/token"
    }

}