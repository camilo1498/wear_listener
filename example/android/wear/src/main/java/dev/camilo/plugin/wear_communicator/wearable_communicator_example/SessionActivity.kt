package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

class SessionActivity: ComponentActivity(), DataClient.OnDataChangedListener, MessageClient.OnMessageReceivedListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** set default app theme **/
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            Box(modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)){
                Text(text = "sessions")
            }
        }
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(message: MessageEvent) {

    }

}