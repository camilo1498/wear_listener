package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.Image
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.widgets.ProgressIndicatorWidget
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.wearable.*
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.functions.GenerateQrCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : ComponentActivity(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener {

    var value = mutableStateOf(12f)
    private var qrCode = mutableStateOf<Bitmap?>(null)

    private val counter = object : CountDownTimer(12000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            value.value = (millisUntilFinished / 1000).toFloat()
        }

        override fun onFinish() {
            value.value = 13f

            this.start()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("","Start")
    }
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            qrCode.value = GenerateQrCode().getQrCodeBitmap("0", this@MainActivity)
        }

        Timer("delay", false).schedule(2000) {
            counter.start()
        }

        setContent {
            Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center){
                ProgressIndicatorWidget(
                    textValue = value.value
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ){

                        AndroidView(
                                factory = {
                                    ImageView.inflate(it, R.layout.activity_main, null)
                                },
                                modifier = Modifier.fillMaxSize(),
                                update = {
                                    val imageView = it.findViewById<ImageView>(R.id.qrImage)
                                    imageView.setImageBitmap(qrCode.value)
                                }

                        )

                    }
                }
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                Log.d("Wear data changed: ", event.toString())
            }
        }
    }

    override fun onMessageReceived(message: MessageEvent) {
        try {
            if(message.path == "/token"){
                CoroutineScope(Dispatchers.IO).launch {
                   qrCode.value = GenerateQrCode().getQrCodeBitmap(message.data.decodeToString(), this@MainActivity)
                }
            }

        } catch (e: Exception) {
            //this method is called for exception handling.
            Log.e("Tag: ", e.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getDataClient(this).addListener(this)
    }
}