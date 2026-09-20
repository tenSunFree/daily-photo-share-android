package com.sun.daily_photo_share_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sun.daily_photo_share_android.core.designsystem.DailyTheme
import com.sun.daily_photo_share_android.core.designsystem.components.DailyButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FoundationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/** Architecture smoke test: Verifies that Theme, Token, Component, and Hilt are all integrated. Not the final Today UI. */
@Composable
private fun FoundationScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DailyTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(DailyTheme.spacing.md),
    ) {
        Text(
            text = "Daily Photo Share",
            style = MaterialTheme.typography.headlineMedium,
        )
        DailyButton(text = "拍照", onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun FoundationScreenPreview() {
    DailyTheme { FoundationScreen() }
}