package io.deus.wallet.modules.settings.security.passcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App

object SecurityPasscodeSettingsModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SecuritySettingsViewModel(
                App.systemInfoManager,
                App.pinComponent,
                App.balanceHiddenManager,
                App.localStorage,
            ) as T
        }
    }

}
