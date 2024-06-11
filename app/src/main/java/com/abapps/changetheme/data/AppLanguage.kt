package com.abapps.changetheme.data

import com.abapps.changetheme.util.LanguageCode
import kotlinx.coroutines.flow.MutableStateFlow

object AppLanguage {
    val code: MutableStateFlow<String> = MutableStateFlow(LanguageCode.EN.value)
}