package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearDataLayerService: WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
    }


    companion object {
        private const val TAG = "Wear DataLayer service"

        private const val GET_TOKEN = "/token"
        private const val DATA_ITEM_RECEIVED_PATH = "/data-item-received"
        const val COUNT_PATH = "/count"
    }

}