package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.github.alexzhirkevich.customqrgenerator.QrCodeGenerator
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.QrGenerator
import com.github.alexzhirkevich.customqrgenerator.QrOptions
import com.github.alexzhirkevich.customqrgenerator.style.*

class GenerateQrCode {
    fun getQrCodeBitmap(token: String, context: Context): Bitmap? {
        val data = QrData.Url(token)
        val generator: QrCodeGenerator = QrGenerator()
        val options = QrOptions.Builder(700)
                .setPadding(.0f)
                .setColors(
                        QrColors(
                                dark = QrColor.Solid(Color.BLACK),
                                bitmapBackground = QrColor.Solid(Color.TRANSPARENT),
                                codeBackground = QrColor
                                        .Solid(Color.TRANSPARENT),
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
}