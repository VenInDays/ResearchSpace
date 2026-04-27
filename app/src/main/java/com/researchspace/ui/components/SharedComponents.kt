package com.researchspace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.ui.theme.RSColors

@Composable
fun EmptyCanvasState(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Your research canvas is empty.",
            fontSize = 15.sp, fontWeight = FontWeight.Light,
            color = RSColors.MutedText, letterSpacing = (-0.3).sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Tap the orb below to begin.",
            fontSize = 12.sp, fontWeight = FontWeight.Normal,
            color = RSColors.FaintText
        )
    }
}
