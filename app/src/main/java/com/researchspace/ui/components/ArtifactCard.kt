package com.researchspace.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 * Artifact Card v2 — in-context link preview rendered within the note flow.
 * - og:image as heavily blurred textured background
 * - Light color overlay for readability
 * - og:title + shortened og:url in white typography
 * - Paper-tear clipping aesthetic
 */
@Composable
fun ArtifactCard(
    metadata: LinkMetadata,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (metadata.hasImage) 100.dp else 56.dp)
            .shadow(4.dp, shape, ambientColor = RSColors.ShadowAmbient, spotColor = RSColors.ShadowSpot)
            .clip(shape)
            .border(0.5.dp, RSColors.GlassBorder, shape)
    ) {
        if (metadata.hasImage) {
            // Heavily blurred background image
            AsyncImage(
                model = metadata.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier.graphicsLayer {
                            renderEffect = android.graphics.RenderEffect
                                .createBlurEffect(18f, 18f, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    } else {
                        Modifier.graphicsLayer { scaleX = 1.2f; scaleY = 1.2f; alpha = 0.5f }
                    }
                )
            )
            // Light color overlay (warm tint)
            Box(modifier = Modifier.fillMaxSize().background(Color(0xCCF5F5F0)))
        } else {
            // Fallback grain texture
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(RSColors.Paper)
                    .background(RSColors.GrainMedium)
            )
        }

        // Content
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            Text(
                text = metadata.title.ifBlank { metadata.domain },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RSColors.InkBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            // URL — mono-spaced, muted
            Text(
                text = metadata.shortUrl,
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Monospace,
                color = RSColors.MonoText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = (-0.2).sp
            )
        }
    }
}
