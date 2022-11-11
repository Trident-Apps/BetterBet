package com.cyberlink.photodirecto.ui.states

sealed class AppsState {
    object Idle : AppsState()
    object Loading : AppsState()
    data class Data(val data: MutableMap<String, Any>?) : AppsState()
    data class Error(val error: String?) : AppsState()
}
