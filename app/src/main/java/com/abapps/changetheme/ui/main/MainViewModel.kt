package com.abapps.changetheme.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abapps.changetheme.data.AppLanguage
import com.abapps.changetheme.util.LanguageCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(LanguageCode.EN)
    val state = _state.asStateFlow()

    init {
        getLanguageCode()
    }

    private fun getLanguageCode() {
        viewModelScope.launch(Dispatchers.IO) {
            AppLanguage.code.collectLatest { lang ->
                _state.update {
                    LanguageCode.entries.find { languageCode ->
                        languageCode.value == lang
                    } ?: LanguageCode.EN
                }
            }
        }
    }

    fun changeLanguage(language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            AppLanguage.code.emit(language)
        }
    }
}