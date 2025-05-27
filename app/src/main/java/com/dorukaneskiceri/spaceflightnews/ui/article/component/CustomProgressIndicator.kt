package com.dorukaneskiceri.spaceflightnews.ui.article.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomProgressIndicator(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
) {
    if (isVisible) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 80.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                trackColor = MaterialTheme.colorScheme.background,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomProgressIndicatorPreview() {
    CustomProgressIndicator(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
        isVisible = true,
    )
}
