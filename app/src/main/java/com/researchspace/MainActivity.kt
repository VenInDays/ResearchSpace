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

class MainActivity : ComponentActivity() {
    private val viewModel: ResearchViewModel by lazy { ResearchViewModel(application) }

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
