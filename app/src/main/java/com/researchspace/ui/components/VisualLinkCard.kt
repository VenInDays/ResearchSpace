package com.researchspace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.researchspace.data.LinkMetadata
import com.researchspace.ui.theme.RSColors

/**
 * Visual Link Card — renders a link as a "Visual Artifact".
 * Uses og:image as background with Gaussian blur overlay (15dp) and white typography.
 * Anti-Material: Not a standard Card. Feels like a torn clipping pasted onto the canvas.
 */
@Composable
fun VisualLinkCard(
    metadata: LinkMetadata,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val cardShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .width(280.dp)
            .height(160.dp)
            .shadow(
                elevation = 6.dp,
                shape = cardShape,
                ambientColor = RSColors.ShadowDark.copy(alpha = 0.25f),
                spotColor = RSColors.ShadowDark.copy(alpha = 0.1f)
            )
            .clip(cardShape)
            .border(
                width = 0.5.dp,
                color = RSColors.CardBorder,
                shape = cardShape
            )
    ) {
        // Background image with blur
        if (metadata.hasImage) {
            // Blurred background layer
            AsyncImage(
                model = metadata.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Render blur effect for Gaussian blur (radius ~15dp)
                        renderEffect = android.graphics.BlurEffect.create(
                            15f, 15f,
                            android.graphics.Shader.TileMode.CLAMP
                        )
                    }
            )

            // Slightly sharper overlay image
            AsyncImage(
                model = metadata.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 0.4f
                    }
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = RSColors.LinkOverlay
                    )
            )
        } else {
            // Fallback: grainy off-white with subtle texture
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = RSColors.SubtleGrey
                    )
                    .background(
                        color = RSColors.GrainOverlay
                    )
            )
        }

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Domain label
            Text(
                text = metadata.domain,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = metadata.title.ifBlank { metadata.url },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            // Description
            if (metadata.description.isNotBlank()) {
                Text(
                    text = metadata.description,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.75f),
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Bottom bar: site name
            if (metadata.siteName.isNotBlank()) {
                Text(
                    text = metadata.siteName,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}
