package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.graphics.Bitmap
import android.util.Base64
import com.google.android.gms.wearable.*
import java.io.ByteArrayOutputStream

class PhoneDataLayerService: WearableListenerService() {

    /** instance of wearable message client **/
    private val messageClient by lazy { Wearable.getMessageClient(this) }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        /** instance of shared preferences **/
        val mPrefs = getSharedPreferences("FlutterSharedPreferences", 0)

        /** received message path **/
        when (messageEvent.path) {
            "/token" -> {
                /** Get user token from local storage **/
                val token = mPrefs.getString("flutter." + "wear", "")

                /** Generate QR Code token **/
                val qrCode = GenerateQrCode().getQrCodeBitmap(token.toString(), this)

                /** convert qrcode bitmap to string **/
                val bitmap = bitMapToString(qrCode!!)

                /** send string qrcode to wear device **/
                messageClient.sendMessage(messageEvent.sourceNodeId, "/token",
                    bitmap.toByteArray())

            }
        }
    }

    /** convert bitmap to string base64 **/
    private fun bitMapToString(bitmap: Bitmap): String {
        val arrayOutput = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, arrayOutput)
        val image = arrayOutput.toByteArray()
        return Base64.encodeToString(image, Base64.DEFAULT)
    }
}