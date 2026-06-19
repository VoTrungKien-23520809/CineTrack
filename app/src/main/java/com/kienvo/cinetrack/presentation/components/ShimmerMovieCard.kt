

package com.kienvo.cinetrack.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun ShimmerMovieGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(6) { ShimmerMovieCard() }
    }
}

@Composable
fun ShimmerDetailView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .shimmer()
    ) {
        // Backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Column(modifier = Modifier.padding(20.dp)) {
            // Title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.height(12.dp))
            // Meta chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            // Overview lines
            repeat(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 3) 0.6f else 1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(20.dp))
            // Cast avatars
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(4) {
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerMovieCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shimmer()
    ) {
        // Poster placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.height(8.dp))
        // Title placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.height(4.dp))
        // Rating placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.height(8.dp))
    }
}