package com.researchspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.researchspace.ui.screens.ResearchSpaceScreen
import com.researchspace.ui.theme.RSColors
import com.researchspace.ui.theme.ResearchSpaceTheme
import com.researchspace.viewmodel.ResearchViewModel

/**
 * Core Activity for the Research Space application.
 *
 * Anti-Material Design approach:
 * - No standard TopAppBar
 * - No FloatingActionButton
 * - No standard Cards
 * - Edge-to-edge display
 * - Status bar and nav bar blend into the off-white background
 */
class MainActivity : ComponentActivity() {

    private val viewModel: ResearchViewModel by lazy {
        ResearchViewModel(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ResearchSpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RSColors.OffWhite
                ) {
                    ResearchSpaceScreen(viewModel = viewModel)
                }
            }
        }
    }
}
