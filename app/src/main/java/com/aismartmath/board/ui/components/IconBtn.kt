package com.aismartmath.board.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Web: .ib button (42 x 38, rounded 10). Squashes when pressed and gives a light tick.
 * [onLongClick] is the phone's version of a hover tooltip. [enabled] = false greys it out; [dim] only fades it.
 */
@Composable
fun IconBtn(
    label: String,
    modifier: Modifier = Modifier,
    floating: Boolean = false,
    enabled: Boolean = true,
    dim: Boolean = false,
    compact: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val clickNow by rememberUpdatedState(onClick)
    val longNow by rememberUpdatedState(onLongClick)
    val enabledNow by rememberUpdatedState(enabled)
    val sc by animateFloatAsState(
        if (pressed) 0.88f else 1f,
        spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "ibPress",
    )
    val fade by animateFloatAsState(if (!enabled) 0.3f else if (dim) 0.5f else 1f, label = "ibFade")
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier
            .size(width = if (compact) 36.dp else 42.dp, height = 38.dp)
            .graphicsLayer { scaleX = sc; scaleY = sc; alpha = fade }
            .clip(shape)
            .background(if (floating) Color(0xCC0B1020) else Color.White.copy(alpha = 0.09f))
            .then(if (floating) Modifier.border(BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)), shape) else Modifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = {
                        if (enabledNow) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            clickNow()
                        }
                    },
                    onLongPress = {
                        val l = longNow
                        if (enabledNow && l != null) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            l()
                        }
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 18.sp)
    }
}
