package io.deus.wallet.modules.settings.appearance

import androidx.lifecycle.viewModelScope
import io.deus.wallet.core.ILocalStorage
import io.deus.wallet.core.ViewModelUiState
import io.deus.wallet.core.managers.BaseTokenManager
import io.deus.wallet.entities.LaunchPage
import io.deus.wallet.modules.balance.BalanceViewType
import io.deus.wallet.modules.balance.BalanceViewTypeManager
import io.deus.wallet.modules.theme.ThemeService
import io.deus.wallet.modules.theme.ThemeType
import io.deus.wallet.ui.compose.Select
import io.deus.wallet.ui.compose.SelectOptional
import io.horizontalsystems.marketkit.models.Token
import kotlinx.coroutines.launch


class AppearanceViewModel(
    private val launchScreenService: LaunchScreenService,
    private val appIconService: AppIconService,
    private val themeService: ThemeService,
    private val baseTokenManager: BaseTokenManager,
    private val balanceViewTypeManager: BalanceViewTypeManager,
    private val localStorage: ILocalStorage
) : ViewModelUiState<AppearanceUIState>() {
    private var launchScreenOptions = launchScreenService.optionsFlow.value
    private var appIconOptions = appIconService.optionsFlow.value
    private var themeOptions = themeService.optionsFlow.value
    private var baseTokenOptions = buildBaseTokenSelect(baseTokenManager.baseTokenFlow.value)
    private var marketsTabEnabled = localStorage.marketsTabEnabled
    private var balanceViewTypeOptions =
        buildBalanceViewTypeSelect(balanceViewTypeManager.balanceViewTypeFlow.value)

    init {
        viewModelScope.launch {
            launchScreenService.optionsFlow
                .collect {
                    handleUpdatedLaunchScreenOptions(it)
                }
        }
        viewModelScope.launch {
            appIconService.optionsFlow
                .collect {
                    handleUpdatedAppIconOptions(it)
                }
        }
        viewModelScope.launch {
            themeService.optionsFlow
                .collect {
                    handleUpdatedThemeOptions(it)
                }
        }
        viewModelScope.launch {
            baseTokenManager.baseTokenFlow
                .collect { baseToken ->
                    handleUpdatedBaseToken(buildBaseTokenSelect(baseToken))
                }
        }
        viewModelScope.launch {
            balanceViewTypeManager.balanceViewTypeFlow
                .collect {
                    handleUpdatedBalanceViewType(buildBalanceViewTypeSelect(it))
                }
        }
    }

    override fun createState() = AppearanceUIState(
        launchScreenOptions = launchScreenOptions,
        appIconOptions = appIconOptions,
        themeOptions = themeOptions,
        baseTokenOptions = baseTokenOptions,
        balanceViewTypeOptions = balanceViewTypeOptions,
        marketsTabEnabled = marketsTabEnabled
    )

    private fun buildBaseTokenSelect(token: Token?): SelectOptional<Token> {
        return SelectOptional(token, baseTokenManager.tokens)
    }

    private fun buildBalanceViewTypeSelect(value: BalanceViewType): Select<BalanceViewType> {
        return Select(value, balanceViewTypeManager.viewTypes)
    }

    private fun handleUpdatedLaunchScreenOptions(launchScreenOptions: Select<LaunchPage>) {
        this.launchScreenOptions = launchScreenOptions
        emitState()
    }

    private fun handleUpdatedAppIconOptions(appIconOptions: Select<AppIcon>) {
        this.appIconOptions = appIconOptions
        emitState()
    }

    private fun handleUpdatedThemeOptions(themeOptions: Select<ThemeType>) {
        this.themeOptions = themeOptions
        emitState()
    }

    private fun handleUpdatedBalanceViewType(balanceViewTypeOptions: Select<BalanceViewType>) {
        this.balanceViewTypeOptions = balanceViewTypeOptions
        emitState()
    }

    private fun handleUpdatedBaseToken(baseTokenOptions: SelectOptional<Token>) {
        this.baseTokenOptions = baseTokenOptions
        emitState()
    }

    fun onEnterLaunchPage(launchPage: LaunchPage) {
        launchScreenService.setLaunchScreen(launchPage)
    }

    fun onEnterAppIcon(enabledAppIcon: AppIcon) {
        appIconService.setAppIcon(enabledAppIcon)
    }

    fun onEnterTheme(themeType: ThemeType) {
        themeService.setThemeType(themeType)
    }

    fun onEnterBaseToken(token: Token) {
        baseTokenManager.setBaseToken(token)
    }

    fun onEnterBalanceViewType(viewType: BalanceViewType) {
        balanceViewTypeManager.setViewType(viewType)
    }

    fun onSetMarketTabsEnabled(enabled: Boolean) {
        if (enabled.not() && (launchScreenOptions.selected == LaunchPage.Market || launchScreenOptions.selected == LaunchPage.Watchlist)) {
            launchScreenService.setLaunchScreen(LaunchPage.Auto)
        }
        localStorage.marketsTabEnabled = enabled

        marketsTabEnabled = enabled
        emitState()
    }

}

data class AppearanceUIState(
    val launchScreenOptions: Select<LaunchPage>,
    val appIconOptions: Select<AppIcon>,
    val themeOptions: Select<ThemeType>,
    val baseTokenOptions: SelectOptional<Token>,
    val balanceViewTypeOptions: Select<BalanceViewType>,
    val marketsTabEnabled: Boolean,
)
