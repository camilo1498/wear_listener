package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.widgets.ProgressIndicatorWidget
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Base64
import android.util.Log
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
import com.google.android.gms.wearable.*
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.functions.GenerateQrCode
import java.util.*
class MainActivity : ComponentActivity(),
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener {

    /** mutable variables **/
    var value = mutableStateOf(0f)
    private var qrCode = mutableStateOf<Bitmap?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenerateQrCode().sendRequest(this@MainActivity)
        setTheme(android.R.style.Theme_DeviceDefault)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                counter.start()
            }
        }, 1000)

        /** render main layout **/
        setContent {
            /** main container **/
            Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                    contentAlignment = Alignment.Center){
                /** show progress indicator **/
                ProgressIndicatorWidget(
                        /** set value **/
                        textValue = value.value
                ) {
                    /** show qr code **/
                    Box(
                            modifier = Modifier
                                    .fillMaxSize().wrapContentSize(Alignment.Center),
                            contentAlignment = Alignment.Center,
                    ){
                        /** validate if qrCode is not null **/
                        if(qrCode.value != null)
                            Image(
                                    bitmap = qrCode.value!!.asImageBitmap(),
                                    contentDescription = "qr code",
                                    alignment = Alignment.Center,
                                    modifier = Modifier
                                            .height(300.dp)
                                            .width(300.dp),
                            )
                    }
                }
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        /** listen data layer changes **/
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                Log.d("Wear Data Changed", event.toString())
            }
        }
    }

    override fun onMessageReceived(message: MessageEvent) {
        try {
            /** decode qr token to base 64 **/
            val imageByte = Base64.decode(message.data, 0)

            /** set QR to bitmap **/
            qrCode.value = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /** stop countdown **/
        counter.cancel()
    }

    override fun onResume() {
        super.onResume()
        /** listen changes **/
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getDataClient(this).addListener(this)
    }

    /** count down function **/
    private val counter = object : CountDownTimer(12000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            /** update count value **/
            value.value = (millisUntilFinished / 1500).toFloat()
        }

        @Suppress("DEPRECATION")
        override fun onFinish() {
            GenerateQrCode().sendRequest(this@MainActivity)
            Handler().postDelayed({
                this.start()
            }, 1500)
        }
    }
}