package dev.camilo.plugin.wear_communicator.wearable_communicator_example.widgets

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text

@Composable
fun ProgressIndicatorWidget(
    modifier: Modifier = Modifier,
    textValue: Float,
    content: @Composable () -> Unit
)  {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged {
                size = it
            }
    ) {
        /** show progress indicator **/
        CircularProgressbar(
            number = textValue * 10
        )

        /** show timer countdown **/
        Box(modifier = Modifier
            .size(size.width.dp / 5, 90.dp)
            .align(Alignment.TopCenter)
            .fillMaxSize()
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = textValue.toInt().toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    /** show Qr code **/
    Box(  modifier = Modifier
        .size(170.dp)
        .clip(shape = CircleShape),
        contentAlignment = Alignment.Center,
    ){
        content()
    }
}

@Composable
fun CircularProgressbar(
    number: Float = 70f,
    size: Dp = 360.dp,
    indicatorThickness: Dp = 14.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
) {


    // Number Animation
    val animateNumber = animateFloatAsState(
        targetValue = number,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )

    // This is to start the animation when the activity is opened
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size = size)
    ) {
        Canvas(
            modifier = Modifier
                .size(size = size)
        ) {

            // Background circle
            drawArc(
                color = Color.Black,
                startAngle = 300f,
                sweepAngle = 298f,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )

            val sweepAngle = if((animateNumber.value / 100) * 247 in 0.0..298.0) {
                (animateNumber.value / 100) * 247
            }else {
                298f
            }
            // Foreground circle
            drawArc(
                color = Color(0xFFE7662B) ,
                startAngle = 300f,
                sweepAngle = sweepAngle ,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )
        }

    }

}