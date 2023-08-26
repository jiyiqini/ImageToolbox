package ru.tech.imageresizershrinker.presentation.root.widget.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.domain.model.SettingsState
import ru.tech.imageresizershrinker.domain.use_case.get_settings_state.GetSettingsStateUseCase
import ru.tech.imageresizershrinker.presentation.crash_screen.CrashActivity
import ru.tech.imageresizershrinker.presentation.root.utils.exception.GlobalExceptionHandler
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.adjustFontSize

@AndroidEntryPoint
open class M3Activity : AppCompatActivity() {

    private lateinit var settingsState: SettingsState

    override fun attachBaseContext(newBase: Context) {
        settingsState = runBlocking {
            EntryPointAccessors
                .fromApplication(newBase, SettingsStateEntryPoint::class.java)
                .getSettingsStateUseCase()
        }
        val newOverride = Configuration(newBase.resources?.configuration)
        settingsState.fontScale?.let { newOverride.fontScale = it }
        applyOverrideConfiguration(newOverride)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        adjustFontSize(settingsState.fontScale)
        enableEdgeToEdge()
        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
    }

    override fun recreate() {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            kotlinx.coroutines.delay(10L)
            super.recreate()
        }
    }

}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface SettingsStateEntryPoint {
    val getSettingsStateUseCase: GetSettingsStateUseCase
}