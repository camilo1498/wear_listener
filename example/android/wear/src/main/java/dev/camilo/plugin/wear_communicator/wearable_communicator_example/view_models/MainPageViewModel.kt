package dev.camilo.plugin.wear_communicator.wearable_communicator_example.view_models

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.*
import kotlinx.coroutines.*
import kotlin.random.Random

class MainPageViewModel(
    application: Application
) : AndroidViewModel(application),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val _events = mutableStateListOf<Event>()

    var image by mutableStateOf<Bitmap?>(null)
        private set

    private var loadPhotoJob: Job = Job().apply { complete() }

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

    fun sendRequestToPhone(activity: Activity) {
        /** instance of Wearable message client **/
        val messageClient = Wearable.getMessageClient(activity)
        /** get connected nodes (devices) **/
        Wearable.getNodeClient(activity).connectedNodes.addOnSuccessListener { nodes ->
            nodes.forEach {
                /** send request by path **/
                messageClient.sendMessage(
                    it.id,
                    "/token", Random.nextInt(126565653).toString().toByteArray()
                ).addOnSuccessListener {
                    Log.d("Wear", "Sent message to phone")
                }
            }
        }
    }

    override fun onMessageReceived(messageChangedEvent: MessageEvent) {
        Log.e("data", messageChangedEvent.path.toString())
        try {
            if(messageChangedEvent.path == "/token") {
                /** set QR to bitmap **/
                loadPhotoJob.cancel()
                loadPhotoJob = viewModelScope.launch {
                    /** decode qr token to base 64 **/
                    val imageByte = Base64.decode(messageChangedEvent.data, 0)
                    Log.e("message", "message")
                    image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
                }
            }
        } catch (e: Exception) {
            Log.e("Wear", e.message.toString())
        }
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