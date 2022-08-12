package dev.camilo.plugin.wear_communicator.wearable_communicator_example.views

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.*

@Composable
fun MainPage() {
    val scalingLazyListState = rememberScalingLazyListState()

    Scaffold(
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) },
        timeText = { TimeText() }
    ) {

    }
}