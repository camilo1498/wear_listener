package dev.camilo.plugin.wear_communicator.wearable_communicator_example.view_models

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.wearable.*

class MainPageViewModel(
    application: Application
) : AndroidViewModel(application),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val _events = mutableStateListOf<Event>()

    override fun onDataChanged(dataChangedEvent: DataEventBuffer) {
        Log.e("data changed", "data")
        _events.addAll(
            dataChangedEvent.map { dataEvent ->
                Log.e("data changed", dataEvent.toString())
                val title = when (dataEvent.type) {
                    DataEvent.TYPE_CHANGED -> dev.camilo.plugin.wear_communicator.wearable_communicator_example.R.string.data_item_changed
                    DataEvent.TYPE_DELETED -> dev.camilo.plugin.wear_communicator.wearable_communicator_example.R.string.data_item_deleted
                    else -> dev.camilo.plugin.wear_communicator.wearable_communicator_example.R.string.data_item_unknown
                }

                Event(
                    title = title,
                    text = dataEvent.dataItem.toString()
                )
            }
        )
    }

    override fun onMessageReceived(messageChangedEvent: MessageEvent) {
        Log.e("message changed", messageChangedEvent.toString())
        _events.add(
            Event(
                title = dev.camilo.plugin.wear_communicator.wearable_communicator_example.R.string.message,
                text = messageChangedEvent.toString()
            )
        )
    }

    override fun onCapabilityChanged(capabilityChangedEvent: CapabilityInfo) {
        Log.e("capability changed", capabilityChangedEvent.toString())
        _events.add(
            Event(
                title = dev.camilo.plugin.wear_communicator.wearable_communicator_example.R.string.capability_changed,
                text = capabilityChangedEvent.toString()
            )
        )
    }
}

data class Event(
    @StringRes val title: Int,
    val text: String
)
