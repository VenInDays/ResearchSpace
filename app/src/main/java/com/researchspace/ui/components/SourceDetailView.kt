package com.researchspace.ui.components

import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.researchspace.data.LinkMetadata
import com.researchspace.ui.theme.RSColors

/**
 * Source Detail View — shown when a source marker icon is clicked.
 * Full-size link card with blurred image background, metadata, and mono-spaced URL.
 */
@Composable
fun SourceDetailView(
    metadata: LinkMetadata,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(20.dp, shape, ambientColor = RSColors.ShadowAmbient, spotColor = RSColors.ShadowSpot)
                .clip(shape)
                .border(0.5.dp, RSColors.GlassBorder, shape)
        ) {
            if (metadata.hasImage) {
                AsyncImage(
                    model = metadata.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Modifier.graphicsLayer {
                                renderEffect = android.graphics.RenderEffect
                                    .createBlurEffect(20f, 20f, android.graphics.Shader.TileMode.CLAMP)
                                    .asComposeRenderEffect()
                            }
                        } else {
                            Modifier.graphicsLayer { scaleX = 1.25f; scaleY = 1.25f; alpha = 0.45f }
                        }
                    )
                )
                Box(modifier = Modifier.fillMaxSize().background(RSColors.LinkOverlay))
            } else {
                Box(modifier = Modifier.fillMaxSize().background(RSColors.Paper).background(RSColors.GrainMedium))
            }

            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(28.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(0.8f), modifier = Modifier.size(16.dp))
            }

            // Content
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Domain
                Text(
                    text = metadata.domain,
                    fontSize = 10.sp, fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(0.6f), letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Title
                Text(
                    text = metadata.title.ifBlank { metadata.url },
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                    color = Color.White, lineHeight = 22.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
                    if (metadata.description.isNotBlank()) {
                        Text(
                            text = metadata.description,
                            fontSize = 12.sp, fontWeight = FontWeight.Normal,
                            color = Color.White.copy(0.75f), lineHeight = 16.sp,
                            maxLines = 2, overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    // Mono-spaced URL
                    Text(
                        text = metadata.url,
                        fontSize = 10.sp, fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White.copy(0.5f), letterSpacing = (-0.3).sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
