package it.unibo.composeui.theme // o il tuo package dei colori

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF2196F3)          // Blu medio brillante
val OnPrimary = Color(0xFFFFFFFF)        // Bianco
val PrimaryContainer = Color(0xFFE0F7FA) // Azzurrino molto chiaro
val OnPrimaryContainer = Color(0xFF000000) // Nero per leggibilità

val Secondary = Color(0xFF1565C0)         // Blu scuro
val OnSecondary = Color(0xFFFFFFFF)       // Bianco
val SecondaryContainer = Color(0xFFE3F2FD) // Azzurrino chiaro
val OnSecondaryContainer = Color(0xFF000000)

val Tertiary = Color(0xFF0D47A1)          // Blu molto scuro
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFE1F5FE)
val OnTertiaryContainer = Color(0xFF000000)

val Background = Color(0xFFF5F5F5)        // Grigio molto chiaro
val OnBackground = Color(0xFF000000)      // Nero
val Surface = Color(0xFFFFFFFF)           // Bianco
val OnSurface = Color(0xFF000000)

val Error = Color(0xFFD32F2F)
val OnError = Color(0xFFFFFFFF)


// Questa variabile non è un colore standard di Material Design 3 e non sarà usata direttamente
// nello ColorScheme, ma puoi usarla in specifici Composables se lo desideri.
val LightPrimaryBackground = Color(0xFFF5F5F5) // Potrebbe essere simile al nuovo SecondaryContainer

// Dark theme overrides
val DarkBackground = Color(0xFF121212)      // Grigio molto scuro
val DarkOnBackground = Color.White          // Bianco
val DarkSurface = Color(0xFF1E1E1E)         // Grigio scuro (per cards)
val DarkOnSurface = Color.White             // Bianco
val DarkSecondaryContainer = Color(0xFF1976D2) // Blu scuro per secondary container