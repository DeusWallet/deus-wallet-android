package io.deus.wallet.modules.settings.about

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.deus.wallet.R
import io.deus.wallet.core.ITermsManager
import io.deus.wallet.core.providers.AppConfigProvider
import io.deus.wallet.core.providers.Translator
import io.horizontalsystems.core.ISystemInfoManager
import kotlinx.coroutines.launch

class AboutViewModel(
    private val appConfigProvider: AppConfigProvider,
    private val termsManager: ITermsManager,
    private val systemInfoManager: ISystemInfoManager,
) : ViewModel() {

    val githubLink = appConfigProvider.appGithubLink
    val appWebPageLink = appConfigProvider.appWebPageLink
    val appVersion: String
        get() {
            var appVersion = systemInfoManager.appVersion
            if (Translator.getString(R.string.is_release) == "false") {
                appVersion += " (${appConfigProvider.appBuild})"
            }

            return appVersion
        }

    var termsShowAlert by mutableStateOf(!termsManager.allTermsAccepted)
        private set

    init {
        viewModelScope.launch {
            termsManager.termsAcceptedSignalFlow.collect {
                termsShowAlert = !it
            }
        }
    }

}
