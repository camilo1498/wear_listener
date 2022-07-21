package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.WindowManager
import com.google.android.gms.wearable.*
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : Activity(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener{

    private lateinit var binding: ActivityMainBinding
    private val qrCodeData: BarcodeEncoder = BarcodeEncoder()
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
                    messageClient.sendMessage(it.id, "/token", Random(34535345).toString().toByteArray()).addOnSuccessListener {
                        Log.d("Wear", "Sent message to phone")
                    }
                }
            }

        }


    }


    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                Log.d("Wear", event.toString())
            }
        }
    }

    override fun onMessageReceived(message: MessageEvent) {
        Log.d("Weaar", String(message.data))
        //binding.textTitle.text = String(message.data)

        //below line is for getting the windowmanager service.
        //below line is for getting the windowmanager service.
        val manager = getSystemService(WINDOW_SERVICE) as WindowManager
        //initializing a variable for default display.
        //initializing a variable for default display.
        val display: Display = manager.defaultDisplay
        //creating a variable for point which is to be displayed in QR Code.
        //creating a variable for point which is to be displayed in QR Code.
        val point = Point()
        display.getSize(point)
        //getting width and height of a point
        //getting width and height of a point
        val width = point.x
        val height = point.y
        //generating dimension from width and height.
        //generating dimension from width and height.
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4
        //setting this dimensions inside our qr code encoder to generate our qr code.
        //setting this dimensions inside our qr code encoder to generate our qr code.

        val bitmap: Bitmap = qrCodeData.encodeBitmap(
            message.data.decodeToString(),
            BarcodeFormat.QR_CODE,
            200,
            200,
        )


        try {
            //getting our qrcode in the form of bitmap.
            // the bitmap is set inside our image view using .setimagebitmap method.
            binding.qrImage.setImageBitmap(bitmap)

        } catch (e: Exception) {
            //this method is called for exception handling.
            Log.e("Tag", e.toString())
        }

    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Termianted", "app was closed")
    }



}