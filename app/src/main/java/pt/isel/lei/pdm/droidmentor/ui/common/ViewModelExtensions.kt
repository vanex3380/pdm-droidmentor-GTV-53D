package pt.isel.lei.pdm.droidmentor.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Cria uma factory a partir de uma lambda que constrói o ViewModel
@Suppress("UNCHECKED_CAST")
fun <T> viewModelInit(block: () -> T) =
    object : ViewModelProvider.Factory {
        // Só é chamada se o ViewModel ainda não existir
        override fun <T : ViewModel> create(modelClass: Class<T>): T = block() as T
    }
