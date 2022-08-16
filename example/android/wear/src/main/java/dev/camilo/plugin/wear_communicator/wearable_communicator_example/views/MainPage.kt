package dev.camilo.plugin.wear_communicator.wearable_communicator_example.views

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.*
import dev.camilo.plugin.wear_communicator.wearable_communicator_example.widgets.ProgressIndicatorWidget
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MainPage(
    image: Bitmap?,
    timer: Float?
) {
    val scalingLazyListState = rememberScalingLazyListState()

    Scaffold(
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) },
        modifier = Modifier.background(color = Color.White)
    ) {
        ProgressIndicatorWidget(textValue = timer!!) {
            Box(
                modifier = Modifier
                    .size(188.dp)
                    .wrapContentSize(Alignment.Center)
                    .padding(start = 1.dp, top = 1.dp)
                    .clip(shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if(image == null) {
                    Image(
                        painterResource(id = dev.camilo.plugin.wear_communicator.wearable_communicator_example.R.drawable.common_full_open_on_phone),
                        contentDescription = stringResource(
                            id = dev.camilo.plugin.wear_communicator.wearable_communicator_example.R.string.photo_placeholder,
                        ),
                        modifier = Modifier.fillMaxSize().background(color = Color.Transparent),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.tint(color = Color.Black)
                    )
                } else {
                    Image(
                        image.asImageBitmap(),
                        contentDescription = stringResource(
                            id = dev.camilo.plugin.wear_communicator.wearable_communicator_example.R.string.captured_photo
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}