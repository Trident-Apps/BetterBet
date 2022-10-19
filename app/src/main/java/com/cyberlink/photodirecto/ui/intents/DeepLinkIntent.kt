package com.cyberlink.photodirecto.ui.intents

sealed class DeepLinkIntent {
    object FetchDeepLink : DeepLinkIntent()
}
