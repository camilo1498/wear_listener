package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.google.android.gms.wearable.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : Activity(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener {
    /** variables **/
    private val counter = object : CountDownTimer(13000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            binding.timer.text = (millisUntilFinished / 1000).toString()
            
        }

        override fun onFinish() {
            this.start()
        }
    }.start()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** instance view **/
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /** send message to phone **/
        binding.messageBtn.setOnClickListener {
            val messageClient = Wearable.getMessageClient(this)
            Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
                nodes.forEach {
                    messageClient.sendMessage(
                        it.id,
                        "/token",
                        Random(34535345).toString().toByteArray()
                    ).addOnSuccessListener {
                        Log.d("Wear", "Sent message to phone")
                    }
                }
            }

        }
        counter.start()

    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                Log.d("Wear", event.toString())
            }
        }
    }

    private fun getQrCodeBitmap(token: String): Bitmap {
        val size = 512 //pixels
        val hints = hashMapOf<EncodeHintType, Int>().also {
            it[EncodeHintType.MARGIN] = 1
        } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(token, BarcodeFormat.QR_CODE, size, size, hints)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.WHITE else Color.BLACK)
                }
            }
        }
    }

    override fun onMessageReceived(message: MessageEvent) {
        Log.d("Weaar", String(message.data))

        try {
            binding.qrImage.setImageBitmap(getQrCodeBitmap(message.data.decodeToString()))

        } catch (e: Exception) {
            //this method is called for exception handling.
            Log.e("Tag", e.toString())
        }

    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getDataClient(this).addListener(this)
        counter.start()
    }

    override fun onDestroy() {
        Log.e("Terminated", "app was closed")
        counter.cancel()
        super.onDestroy()
    }


}