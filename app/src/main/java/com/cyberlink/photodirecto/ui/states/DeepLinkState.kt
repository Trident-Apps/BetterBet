package com.cyberlink.photodirecto.ui.states

sealed class DeepLinkState {
    object Idle : DeepLinkState()
    object Loading : DeepLinkState()
    data class DeepLink(val deepLink: String?) : DeepLinkState()
    data class Error(val error: String?) : DeepLinkState()
}