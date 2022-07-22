package dev.camilo.plugin.wear_communicator.wearable_communicator_example.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.theme.Typography
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.theme.wearColorPalette

@Composable
fun WearAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = wearColorPalette,
        typography = Typography,
        // For shapes, we generally recommend using the default Material Wear shapes which are
        // optimized for round and non-round devices.
        content = content
    )
}
