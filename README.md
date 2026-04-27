# Research Space

A tactile, anti-material design notebook app for researchers. Built with Kotlin & Jetpack Compose.

## Features

- **Spatial 2D Canvas** — Pan & zoom infinite canvas. Notes aren't in lists; they're placed freely in 2D space.
- **Orbital Hub** — Circular navigation at center-bottom that expands into a radial menu with haptic feedback.
- **Floating Toolbar** — Overlaps the note editor edge by 20%, creating a non-traditional "stuck" look.
- **OpenGraph Link Capture** — Paste any URL to auto-extract og:title, og:image, og:description.
- **Visual Link Cards** — Links render as "Visual Artifacts" with blurred image backgrounds and white typography.
- **Paper-Stack Layering** — Neumorphic shadows with grain textures for tactile minimalism.

## Design System

| Element | Value |
|---------|-------|
| Background | `#F5F5F7` (Off-white) |
| Text | `#1D1D1F` (Ink-black) |
| Borders | `0.5dp` subtle grey |
| Shadows | Neumorphism-inspired soft shadows |
| Navigation | Orbital Hub (no standard BottomBar) |

## Tech Stack

- Kotlin + Jetpack Compose
- Coil for image loading
- Jsoup for OpenGraph parsing
- OkHttp for networking
- Gson for JSON serialization

## Build

```bash
./gradlew assembleRelease
```

The signed APK will be at `app/build/outputs/apk/release/`.
