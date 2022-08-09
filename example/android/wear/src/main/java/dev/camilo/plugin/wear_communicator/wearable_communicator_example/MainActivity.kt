@file:OptIn(DelicateCoroutinesApi::class)

package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.content.Intent
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.google.android.gms.wearable.*
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.functions.GenerateQrCode
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
class MainActivity : ComponentActivity(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener {

    /** mutable variables **/
    var value = mutableStateOf(12f)
    private var qrCode = mutableStateOf<Bitmap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** generate qr code **/
        GenerateQrCode().sendRequest(this@MainActivity)
        /** set default app theme **/
        setTheme(android.R.style.Theme_DeviceDefault)
        /** initialize countdown**/
        Timer().schedule(object : TimerTask() {
            override fun run() {
                counter.start()
            }
        }, 700)

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
                            .fillMaxSize().wrapContentSize(Alignment.Center).padding(end = 2.dp),
                        contentAlignment = Alignment.Center,
                    ){
                        /** get wear screen size **/
                        val configuration = LocalConfiguration.current
                        val screenHeight = configuration.screenHeightDp.dp
                        val screenWidth = configuration.screenWidthDp.dp

                        /** validate if qrCode is not null **/
                        if(qrCode.value != null)
                            Image(
                                bitmap = qrCode.value!!.asImageBitmap(),
                                contentDescription = "qr code",
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .height(screenHeight - 12.dp)
                                    .width(screenWidth - 12.dp),
                            )
                    }
                }
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("Wear Data Changed", dataEvents.toString())
        /** listen data layer changes **/
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                Log.d("Wear Data Changed", event.toString())
            }
        }
    }

    override fun onMessageReceived(message: MessageEvent) {
        try {
         if(message.path == "/token") {
             /** decode qr token to base 64 **/
             val imageByte = Base64.decode(message.data, 0)
             Log.e("message", "message")

             /** set QR to bitmap **/
             GlobalScope.launch(Dispatchers.Main) {
                 qrCode.value = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
             }
         } else if(message.path == "/start-sessions_activity"){
             Log.e("message", String(message.data))
            if(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                this.onDestroy()
                startActivity(
                    Intent(this, SessionActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
         }
        } catch (e: Exception) {
            Log.e("Wear", e.message.toString())
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
        counter.start()
    }

    /** count down function **/
    private val counter = object : CountDownTimer(12000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            /** update count value **/
            value.value = (millisUntilFinished / 1000).toFloat()
        }

        @Suppress("DEPRECATION")
        override fun onFinish() {
            GenerateQrCode().sendRequest(this@MainActivity)
            value.value = 12f
            Handler().postDelayed({
                this.start()
            }, 600)
        }
    }
}