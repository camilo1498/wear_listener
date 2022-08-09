package dev.camilo.plugin.wear_communicator.wearable_communicator_example.functions

import android.util.Log
import com.google.android.gms.wearable.Wearable
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.MainActivity
import kotlin.random.Random

class GenerateQrCode {

    /** send request to phone **/
    fun sendRequest(activity: MainActivity) {
        /** instance of Wearable message client **/
        val messageClient = Wearable.getMessageClient(activity)
        /** get connected nodes (devices) **/
        Wearable.getNodeClient(activity).connectedNodes.addOnSuccessListener { nodes ->
            nodes.forEach {
                /** send request by path **/
                messageClient.sendMessage(
                        it.id,
                        "/token", Random.nextInt(126565653).toString().toByteArray()
                ).addOnSuccessListener {
                    Log.d("Wear", "Sent message to phone")
                }
            }
        }
    }
}