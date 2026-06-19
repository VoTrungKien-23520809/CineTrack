    package com.kienvo.cinetrack.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kienvo.cinetrack.ui.theme.CinemaGold

@Composable
fun RatingAndNoteSection(
    draftRating: Int?,
    draftNote: String,
    onRatingClick: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Text(
        text = "Đánh giá của bạn",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (1..5).forEach { star ->
            IconButton(
                onClick = { onRatingClick(star) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "$star sao",
                    tint = if (draftRating != null && star <= draftRating)
                        CinemaGold
                    else
                        MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = draftNote,
        onValueChange = onNoteChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Ghi chú của bạn") },
        placeholder = { Text("Cảm nhận về bộ phim...") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(Modifier.height(12.dp))
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text("Lưu đánh giá", fontWeight = FontWeight.SemiBold)
    }
}
