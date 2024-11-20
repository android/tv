package com.google.tv.material.catalog.screens

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.google.tv.material.catalog.ExampleAction
import com.google.tv.material.catalog.ExamplesScreenWithDottedBackground
import kotlin.math.pow

@Composable
fun MotionScreen() {
    val actions = listOf(
        ExampleAction(
            title = "Standard",
            content = {
                StandardMotion()
            }
        ),
        ExampleAction(
            title = "Browse",
            content = {
                BrowseMotion()
            }
        ),
        ExampleAction(
            title = "Enter",
            content = {
                EnterMotion()
            }
        ),
        ExampleAction(
            title = "Exit",
            content = {
                ExitMotion()
            }
        ),
    )

    ExamplesScreenWithDottedBackground(actions)
}

@Composable
private fun StandardMotion() {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        val path = remember { Path() }
        val color = Color(0xFFF4CA55)
        val transition by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    2000,
                    delayMillis = 300,
                    easing = CubicBezierEasing(0.2f, 0.1f, 0.0f, 1.0f)
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "",
        )
        val circleColor = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier
                .size(160.dp)
        ) {
            val width = size.width
            val height = size.height
            val x = getOffset(transition, 0f, width.times(0.2f), 0f, width)
            val y = getOffset(transition, height, width.times(0.9f), 0f, 0f)
            drawRect(
                color = Color.White.copy(0.12f),
                topLeft = Offset(0f, 0f),
                style = Stroke(width = 1.dp.toPx())
            )
            path.moveTo(0f, height)
            path.cubicTo(
                x1 = width.times(0.2f),
                y1 = height.times(0.9f),
                x2 = 0f,
                y2 = 0f,
                x3 = width,
                y3 = 0f
            )
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = color,
                radius = width.times(0.03f),
                center = Offset(0f, height)
            )
            drawCircle(
                color = color,
                radius = width.times(0.03f),
                center = Offset(width, 0f)
            )
            drawCircle(
                color = circleColor,
                radius = 10.dp.toPx(),
                center = Offset(x, y)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Standard",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = "0.20, 0.10, 0.00, 1.00",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight(400)
        )
    }
}

@Composable
private fun BrowseMotion() {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        val path = remember { Path() }
        val color = Color(0xFF719DEF)
        val transition by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    2000,
                    delayMillis = 300,
                    easing = CubicBezierEasing(0.2f, 0.1f, 0.0f, 1.0f)
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "",
        )
        val circleColor = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier
                .size(160.dp)
        ) {
            val width = size.width
            val height = size.height
            val x = getOffset(transition, 0f, width.times(0.18f), width.times(0.22f), width)
            val y = getOffset(transition, height, 0f, 0f, 0f)
            drawRect(
                color = Color.White.copy(0.12f),
                topLeft = Offset(0f, 0f),
                style = Stroke(width = 1.dp.toPx())
            )
            path.moveTo(0f, height)
            path.cubicTo(
                x1 = width.times(0.18f),
                y1 = 0f,
                x2 = width.times(0.22f),
                y2 = 0f,
                x3 = width,
                y3 = 0f
            )
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = color,
                radius = width.times(0.03f),
                center = Offset(0f, height)
            )
            drawCircle(
                color = color,
                radius = width.times(0.03f),
                center = Offset(width, 0f)
            )
            drawCircle(
                color = circleColor,
                radius = 10.dp.toPx(),
                center = Offset(x, y)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Browse",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = "0.18, 1.00, 0.22, 1.00",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight(400)
        )
    }
}

@Composable
private fun EnterMotion() {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        val path = remember { Path() }
        val color = Color(0xFF73B67A)
        val transition by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    2000,
                    delayMillis = 300,
                    easing = CubicBezierEasing(0.2f, 0.1f, 0.0f, 1.0f)
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "",
        )
        val circleColor = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier
                .size(160.dp)
        ) {
            val width = size.width
            val height = size.height
            val x = getOffset(transition, 0f, width.times(0.12f), width.times(0.4f), width)
            val y = getOffset(transition, height, 0f, 0f, 0f)
            drawRect(
                color = Color.White.copy(0.12f),
                topLeft = Offset(0f, 0f),
                style = Stroke(width = 1.dp.toPx())
            )
            path.moveTo(0f, height)
            path.cubicTo(
                x1 = width.times(0.12f),
                y1 = 0f,
                x2 = width.times(0.4f),
                y2 = 0f,
                x3 = width,
                y3 = 0f
            )
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = color,
                radius = width.times(0.03f),
                center = Offset(0f, height)
            )
            drawCircle(
                color = color,
                radius = width.times(0.03f),
                center = Offset(width, 0f)
            )
            drawCircle(
                color = circleColor,
                radius = 10.dp.toPx(),
                center = Offset(x, y)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Enter",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = "0.12, 1.00, 0.40, 1.00",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight(400)
        )
    }
}

@Composable
private fun ExitMotion() {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        val path = remember { Path() }
        val color = Color(0xFFDE6F62)
        val transition by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    2000,
                    delayMillis = 300,
                    easing = CubicBezierEasing(0.2f, 0.1f, 0.0f, 1.0f)
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "",
        )
        val circleColor = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier
                .size(160.dp)
        ) {
            val width = size.width
            val height = size.height
            val x = getOffset(transition, 0f, width.times(0.4f), width.times(0.12f), width)
            val y = getOffset(transition, height, 0f, 0f, 0f)
            drawRect(
                color = Color.White.copy(0.12f),
                topLeft = Offset(0f, 0f),
                style = Stroke(width = 1.dp.toPx())
            )
            path.moveTo(0f, height)
            path.cubicTo(
                x1 = width.times(0.4f),
                y1 = 0f,
                x2 = width.times(0.12f),
                y2 = 0f,
                x3 = width,
                y3 = 0f
            )
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = color,
                radius = width.times(0.03f),
                center = Offset(0f, height)
            )
            drawCircle(
                color = color,
                radius = width.times(0.03f),
                center = Offset(width, 0f)
            )
            drawCircle(
                color = circleColor,
                radius = 10.dp.toPx(),
                center = Offset(x, y)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Exit",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = "0.40, 1.00, 0.12, 1.00",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight(400)
        )
    }
}

fun getOffset(t: Float, a1: Float, a2: Float, a3: Float, a4: Float): Float {
    return ((1 - t).pow(3).times(a1)
            + 3f.times((1 - t).pow(2)).times(t).times(a2)
            + 3f.times(1 - t).times(t.pow(2)).times(a3)
            + t.pow(3).times(a4))
}
