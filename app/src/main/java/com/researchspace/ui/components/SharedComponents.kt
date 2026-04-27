package com.researchspace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.ui.theme.RSColors

/**
 * Grainy texture overlay composable — adds subtle paper-like grain to any content.
 */
@Composable
fun GrainyOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(RSColors.GrainOverlay)
    )
}

/**
 * Neumorphic container — provides soft inset/outset shadow effect.
 * More realistic than standard neumorphism.
 */
@Composable
fun NeuContainer(
    modifier: Modifier = Modifier,
    elevated: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .shadow(
                elevation = if (elevated) 12.dp else 4.dp,
                shape = shape,
                ambientColor = RSColors.ShadowDark.copy(alpha = 0.15f),
                spotColor = RSColors.ShadowDark.copy(alpha = 0.05f)
            )
            .clip(shape)
            .background(RSColors.NeuSurface)
            .border(
                width = 0.5.dp,
                color = RSColors.CardBorder,
                shape = shape
            )
            .padding(16.dp)
    ) {
        content()
    }
}

/**
 * URL input field with paste detection.
 * Styled to match the anti-material design system.
 */
@Composable
fun LinkInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Paste a URL to capture metadata…"
) {
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = RSColors.ShadowDark.copy(alpha = 0.1f)
            )
            .clip(shape)
            .background(RSColors.CardSurface)
            .border(
                width = 0.5.dp,
                color = RSColors.SubtleGrey,
                shape = shape
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = RSColors.MutedText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = RSColors.InkBlack
                ),
                cursorBrush = SolidColor(RSColors.InkBlack),
                modifier = Modifier.weight(1f),
                singleLine = true
            ) { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 13.sp,
                            color = RSColors.MutedText
                        )
                    }
                    innerTextField()
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                onClick = onSubmit,
                shape = RoundedCornerShape(6.dp),
                color = RSColors.InkBlack.copy(alpha = 0.06f)
            ) {
                Text(
                    text = "Capture",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = RSColors.InkBlack,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Empty state indicator for the canvas.
 */
@Composable
fun EmptyCanvasState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your research canvas is empty.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = RSColors.MutedText,
            letterSpacing = (-0.3).sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the orb below to begin.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = RSColors.SubtleGrey
        )
    }
}
