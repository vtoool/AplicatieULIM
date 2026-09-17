package com.victor.ulim

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.victor.ulim.ui.AppViewModel
import com.victor.ulim.ui.UlimApp
import com.victor.ulim.ui.theme.ULIMTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: AppViewModel = viewModel()
            ULIMTheme {
                // In-app localization: ro (default) / ru / en, independent of the device locale.
                val baseContext = LocalContext.current
                val localizedContext = remember(vm.language) {
                    baseContext.withLocale(vm.language)
                }
                CompositionLocalProvider(
                    // The wrapped context can't be unwrapped back to the activity, so
                    // provide the activity-backed owners explicitly.
                    LocalActivityResultRegistryOwner provides this,
                    LocalOnBackPressedDispatcherOwner provides this,
                    LocalContext provides localizedContext,
                    LocalConfiguration provides localizedContext.resources.configuration
                ) {
                    UlimApp(vm)
                }
            }
        }
    }
}

/** Returns a context whose configuration uses the given language tag. */
private fun Context.withLocale(languageTag: String): Context {
    val locale = Locale.forLanguageTag(languageTag)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}
