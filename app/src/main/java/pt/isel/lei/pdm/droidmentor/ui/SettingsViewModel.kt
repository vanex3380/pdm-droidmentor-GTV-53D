package pt.isel.lei.pdm.droidmentor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.isel.lei.pdm.droidmentor.services.ApiKeyRepository

class SettingsViewModel(private val repository: ApiKeyRepository) : ViewModel() {

    // Texto na caixa da chave
    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input.asStateFlow()

    // true se já existe uma chave guardada
    val hasKey: StateFlow<Boolean> =
        repository.apiKey.map { it != null }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun onInputChange(text: String) {
        _input.value = text
    }

    fun save() {
        val key = _input.value.trim()
        if (key.isEmpty()) return
        viewModelScope.launch {
            repository.save(key)
            _input.value = "" // limpa a caixa depois de guardar
        }
    }
}
