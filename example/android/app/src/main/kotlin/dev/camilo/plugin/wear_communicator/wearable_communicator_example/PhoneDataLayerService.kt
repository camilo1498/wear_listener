package dev.camilo.plugin.wear_communicator.wearable_communicator_example

import android.graphics.Bitmap
import android.util.Base64
import com.google.android.gms.wearable.*
import java.io.ByteArrayOutputStream

class PhoneDataLayerService: WearableListenerService() {

    private val messageClient by lazy { Wearable.getMessageClient(this) }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val mPrefs = getSharedPreferences("FlutterSharedPreferences", 0)
        when (messageEvent.path) {
            SEND_TOKEN -> {
                val qrCode = GenerateQrCode().getQrCodeBitmap(mPrefs.getString("flutter." + "wear", "").toString(), this)
                val bitmap = bitMapToString(qrCode!!)

                messageClient.sendMessage(messageEvent.sourceNodeId, SEND_TOKEN,
                    bitmap.toByteArray())

            }
        }
    }

    private fun bitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    companion object {
        private const val SEND_TOKEN = "/token"
    }

}