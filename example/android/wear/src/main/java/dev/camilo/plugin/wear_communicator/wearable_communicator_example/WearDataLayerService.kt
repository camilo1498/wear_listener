package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.*

class WearDataLayerService: WearableListenerService(),  CapabilityClient.OnCapabilityChangedListener {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        Log.d("wear data change", dataEvents.toString())
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        Log.d("wear data layer", messageEvent.data.toString())
        when (messageEvent.path) {
            "/start-sessions_activity" -> {
                startActivity(
                    Intent(this, SessionActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }

    override fun onCapabilityChanged(device: CapabilityInfo) {
        super.onCapabilityChanged(device)
        Log.d("wear listener devices", device.nodes.toString())
    }

}