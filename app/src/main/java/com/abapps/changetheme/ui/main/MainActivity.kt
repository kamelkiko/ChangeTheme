package com.abapps.changetheme.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abapps.changetheme.resource.AppTheme
import com.abapps.changetheme.resource.LocalThemeIsDark
import com.abapps.changetheme.resource.Resources
import com.abapps.changetheme.util.LanguageCode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel by viewModels<MainViewModel>()
            val languageCode by mainViewModel.state.collectAsState()
            AppTheme(languageCode = languageCode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var isDark by LocalThemeIsDark.current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(onClick = { isDark = !isDark }) {
                            Text(text = if (isDark) "Light" else "Dark")
                        }
                        Button(onClick = {
                            mainViewModel.changeLanguage(
                                if (languageCode == LanguageCode.EN) LanguageCode.AR.value
                                else LanguageCode.EN.value
                            )
                        }
                        ) {
                            Text(text = Resources.strings.hello)
                        }
                    }
                }
            }
        }
    }
}