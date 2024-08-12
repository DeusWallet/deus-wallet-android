package io.deus.wallet.modules.settings.appearance

import io.deus.wallet.core.ILocalStorage
import io.deus.wallet.entities.LaunchPage
import io.deus.wallet.ui.compose.Select
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LaunchScreenService(private val localStorage: ILocalStorage) {
    private val screens by lazy { LaunchPage.values().asList() }

    private val _optionsFlow = MutableStateFlow(
        Select(localStorage.launchPage ?: LaunchPage.Auto, screens)
    )
    val optionsFlow = _optionsFlow.asStateFlow()

    fun setLaunchScreen(launchPage: LaunchPage) {
        localStorage.launchPage = launchPage

        _optionsFlow.update {
            Select(launchPage, screens)
        }
    }
}
