package com.researchspace

import android.app.Application

/**
 * Application class for Research Space.
 * Minimal — no heavy initialization needed.
 */
class ResearchApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global libraries here (e.g., Coil, analytics)
    }
}
