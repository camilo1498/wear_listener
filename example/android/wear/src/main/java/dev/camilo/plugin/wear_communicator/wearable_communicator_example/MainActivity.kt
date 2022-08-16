package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.view_models.MainPageViewModel
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.views.MainPage
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    /** instances **/
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    /** view model instances **/
    private val clientDataViewModel by viewModels<MainPageViewModel>()

    /** variables **/
    private val timer = mutableStateOf(12f)
    var canCount = mutableStateOf<Boolean>(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            clientDataViewModel.sendRequestToPhone(this@MainActivity)
            counter.start()
            Handler().postDelayed({
                canCount.value = true
                counter.start()
            }, 600)
        }

        setTheme(android.R.style.Theme_DeviceDefault)
        super.onCreate(savedInstanceState)

        setContent { 
            MainPage(
                timer = timer.value,
                image = clientDataViewModel.image
            )
        }
    }



    /** count down function **/
    private val counter = object : CountDownTimer(13000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            /** update count value **/
            if(canCount.value) {
                timer.value = (millisUntilFinished / 1000).toFloat()
            }
        }

        @Suppress("DEPRECATION")
        override fun onFinish() {
            clientDataViewModel.sendRequestToPhone(this@MainActivity)
            timer.value = 12f
            canCount.value = false
            Handler().postDelayed({
                canCount.value = true
                this.start()
            }, 600)
        }
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(clientDataViewModel)
        messageClient.addListener(clientDataViewModel)
        capabilityClient.addListener(clientDataViewModel, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
        counter.start()
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(clientDataViewModel)
        messageClient.removeListener(clientDataViewModel)
        capabilityClient.removeListener(clientDataViewModel)
        counter.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        dataClient.removeListener(clientDataViewModel)
        messageClient.removeListener(clientDataViewModel)
        capabilityClient.removeListener(clientDataViewModel)
        counter.cancel()
    }

    override fun onMessageReceived(messageChangedEvent: MessageEvent) {
        if(messageChangedEvent.path == "/start-sessions_activity"){
            Log.e("message", messageChangedEvent.path)
            if(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                this.onDestroy()
                startActivity(
                    Intent(this, SessionActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }
}