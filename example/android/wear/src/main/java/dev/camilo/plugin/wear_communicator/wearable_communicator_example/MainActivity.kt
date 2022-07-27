package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.widgets.ProgressIndicatorWidget
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Base64
import android.util.Log
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.github.alexzhirkevich.customqrgenerator.QrCodeGenerator
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrGenerator
import com.github.alexzhirkevich.customqrgenerator.QrOptions
import com.github.alexzhirkevich.customqrgenerator.style.*
import com.google.android.gms.wearable.*
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.functions.GenerateQrCode
import kotlinx.coroutines.*
import java.lang.Math.sqrt
import java.util.*
import java.util.concurrent.ForkJoinPool
import kotlin.concurrent.schedule
import kotlin.random.Random

class MainActivity : ComponentActivity(),
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener {

    var value = mutableStateOf(0f)
    private var qrCode = mutableStateOf<Bitmap?>(null)

    private val counter = object : CountDownTimer(12000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            value.value = (millisUntilFinished / 1000).toFloat()
        }

        override fun onFinish() {
            GenerateQrCode().sendRequest(this@MainActivity)
            Handler().postDelayed({
                this.start()
            }, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenerateQrCode().sendRequest(this@MainActivity)
        setTheme(android.R.style.Theme_DeviceDefault)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                counter.start()
            }
        }, 1000)
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
                                    .fillMaxSize().wrapContentSize(Alignment.Center),
                            contentAlignment = Alignment.Center,
                    ){
                        if(qrCode.value != null)
                            Image(
                                    bitmap = qrCode.value!!.asImageBitmap(),
                                    contentDescription = "qr code",
                                    alignment = Alignment.Center,
                                    modifier = Modifier
                                            .height(300.dp)
                                            .width(300.dp),
                            )
                        /*AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = {
                                    ImageView.inflate(it, R.layout.activity_main)
                                },
                                update = {
                                    val imageView = it.findViewById<ImageView>(R.id.qrImage)
                                    imageView.setImageBitmap(qrCode.value!!)
                                }
                        )*/
                    }
                }
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
            }
        }
    }

    override fun onMessageReceived(message: MessageEvent) {
        try {
            val imageByte = Base64.decode(message.data, 0)
            qrCode.value = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        counter.cancel()
    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getDataClient(this).addListener(this)
    }
}