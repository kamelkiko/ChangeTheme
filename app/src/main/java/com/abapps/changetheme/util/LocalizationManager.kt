package com.abapps.changetheme.util

import androidx.compose.ui.unit.LayoutDirection
import com.abapps.changetheme.resource.strings.IStringResources
import com.abapps.changetheme.resource.strings.arabic.Arabic
import com.abapps.changetheme.resource.strings.english.English

object LocalizationManager {

    fun getStringResources(languageCode: LanguageCode): IStringResources {
        return when (languageCode) {
            LanguageCode.EN -> English()
            LanguageCode.AR -> Arabic()
        }
    }

    fun getLayoutDirection(languageCode: LanguageCode): LayoutDirection {
        return when (languageCode) {
            LanguageCode.EN -> LayoutDirection.Ltr
            else -> LayoutDirection.Rtl
        }
    }
}