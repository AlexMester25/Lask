package dev.alexmester.impl.presentation.mvi

sealed class ProfileIntent {
    data class UpdateName(val name: String) : ProfileIntent()
    data class UpdateAvatar(val uri: String?) : ProfileIntent()
    data object NavigateToReadArticles : ProfileIntent()
    data object NavigateToClappedArticles : ProfileIntent()
}