package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.alexzhirkevich.customqrgenerator.QrCodeGenerator
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrGenerator
import com.github.alexzhirkevich.customqrgenerator.QrOptions
import com.github.alexzhirkevich.customqrgenerator.style.*
import com.google.android.gms.wearable.Wearable
import kotlin.random.Random

class GenerateQrCode {

    @SuppressLint("Range")
    fun getQrCodeBitmap(token: String, context: Context): Bitmap? {
        val data = QrData.Url(token)
        val generator: QrCodeGenerator = QrGenerator()
        val options = QrOptions.Builder(750)
                .setPadding(.0f)
                .setColors(
                        QrColors(
                                dark = QrColor.Solid(Color.BLACK),
                                bitmapBackground = QrColor.Solid(Color.TRANSPARENT),
                                codeBackground = QrColor
                                        .Solid(Color.TRANSPARENT),
                        )
                )
                .setLogo(
                        QrLogo(
                                drawable = ContextCompat
                                        .getDrawable(context, R.drawable.logo_bt_b)!!,
                                size = .3f,
                                padding = .1f,
                                shape = QrLogoShape
                                        .Circle,

                                )
                )
                .setElementsShapes(
                        QrElementsShapes(
                                darkPixel = QrPixelShape.Circle(),
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
        return generator.generateQrCode(data, options)

    }



    fun sendRequest(activity: MainActivity) {
        val messageClient = Wearable.getMessageClient(activity)
        Wearable.getNodeClient(activity).connectedNodes.addOnSuccessListener { nodes ->
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