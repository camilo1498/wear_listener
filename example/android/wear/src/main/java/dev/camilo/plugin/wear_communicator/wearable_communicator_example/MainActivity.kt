package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.annotation.SuppressLint
import android.graphics.Bitmap
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.widgets.ProgressIndicatorWidget
import android.os.Bundle
import android.os.CountDownTimer
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
import androidx.core.content.ContextCompat
import com.github.alexzhirkevich.customqrgenerator.QrCodeGenerator
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrGenerator
import com.github.alexzhirkevich.customqrgenerator.QrOptions
import com.github.alexzhirkevich.customqrgenerator.style.*
import com.google.android.gms.wearable.*
import java.lang.Math.sqrt
import kotlin.random.Random

class MainActivity : ComponentActivity(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener {

    var value = mutableStateOf(0f)
    private lateinit var qrCode: Bitmap

    private val counter = object : CountDownTimer(13000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            value.value = (millisUntilFinished / 1000).toFloat()
        }

        override fun onFinish() {
            this.start()
            sendRequest()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        counter.start()
        sendRequest()
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
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center,
                    ){
                        Image(
                            bitmap = getQrCodeBitmap("sadsdsd").asImageBitmap(),
                            contentDescription = "qr code",
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .height(200.dp)
                                .width(200.dp),
                        )
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
        try {
            qrCode = getQrCodeBitmap(message.data.decodeToString())
            Log.d("decode token", (qrCode.toString()))
        } catch (e: Exception) {
            //this method is called for exception handling.
            Log.e("Tag", e.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Termianted", "app was closed")
        counter.cancel()
    }

    @SuppressLint("Range")
    private fun getQrCodeBitmap(token: String): Bitmap {


//        val size = 512 //pixels
//        val hints = hashMapOf<EncodeHintType, Int>().also {
//            it[EncodeHintType.MARGIN] = 0
//        } // Make the QR code buffer border narrower
//        val bits = QRCodeWriter().encode(token, BarcodeFormat.QR_CODE, size, size, hints)
//        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also {
//            for (x in 0 until size) {
//                for (y in 0 until size) {
//                    it.setPixel(x, y, if (bits[x, y]) android.graphics.Color.WHITE else android.graphics.Color.BLACK)
//                }
//            }
//        }
        val data = QrData.Url("Manco")

        val options = QrOptions.Builder(1024)
                .setPadding(.0f)
                .setColors(
                        QrColors(
                                dark = QrColor
                                        .Solid(android.graphics.Color.parseColor("#000000")),
                                bitmapBackground = QrColor.Solid(android.graphics.Color.WHITE),
                                codeBackground = QrColor
                                        .Solid(android.graphics.Color.WHITE),
                        )
                )
                .setLogo(
                        QrLogo(
                                drawable = ContextCompat
                                        .getDrawable(this, R.drawable.logo_bt_b)!!,
                                size = .5f,
                                padding = .1f,
                                shape = QrLogoShape
                                        .Circle,

                        )
                )
                .setElementsShapes(
                        QrElementsShapes(
                                darkPixel = Circle,
                                ball = QrBallShape
                                        .RoundCorners(.5f),
                                frame = QrFrameShape
                                        .RoundCorners(.5f),
                                background = QrBackgroundShape
                                        .RoundCorners(.5f),
                                lightPixel = QrPixelShape.RoundCorners(),
                        )
                )
                .setCodeShape(QrShape.Circle())
                .build()
        val generator: QrCodeGenerator = QrGenerator()

        return generator.generateQrCode(data, options)
    }
    object Circle : QrPixelShape {
        override fun invoke(
                i: Int, j: Int, elementSize: Int,
                qrPixelSize: Int, neighbors: Neighbors
        ): Boolean {
            val center = elementSize/2.0
            return (kotlin.math.sqrt((center - i) * (center - i) + (center - j) * (center - j)) < center)
        }
    }
    private fun sendRequest() {

        val messageClient = Wearable.getMessageClient(this@MainActivity)
        Wearable.getNodeClient(this@MainActivity).connectedNodes.addOnSuccessListener { nodes ->
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
}