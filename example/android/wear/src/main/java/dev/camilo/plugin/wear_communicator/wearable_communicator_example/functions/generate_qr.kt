package dev.camilo.plugin.wear_communicator.wearable_communicator_example.functions

import android.util.Log
import com.google.android.gms.wearable.Wearable
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.MainActivity
import kotlin.random.Random

class GenerateQrCode {

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