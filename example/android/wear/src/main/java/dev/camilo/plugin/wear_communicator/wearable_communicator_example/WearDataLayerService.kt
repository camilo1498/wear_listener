package dev.camilo.plugin.wear_communicator.wearable_communicator_example

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
    }

    override fun onCapabilityChanged(device: CapabilityInfo) {
        super.onCapabilityChanged(device)
        Log.d("wear listener devices", device.nodes.toString())
    }

}