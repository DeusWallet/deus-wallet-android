package io.deus.wallet.modules.settings.main

import io.deus.wallet.R
import io.deus.wallet.core.IAccountManager
import io.deus.wallet.core.IBackupManager
import io.deus.wallet.core.ITermsManager
import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.managers.LanguageManager
import io.deus.wallet.core.providers.AppConfigProvider
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.entities.Currency
import io.deus.wallet.modules.walletconnect.WCManager
import io.deus.wallet.modules.walletconnect.WCSessionManager
import io.horizontalsystems.core.IPinComponent
import io.horizontalsystems.core.ISystemInfoManager
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSettingsService(
    private val backupManager: IBackupManager,
    private val languageManager: LanguageManager,
    private val systemInfoManager: ISystemInfoManager,
    private val currencyManager: CurrencyManager,
    private val termsManager: ITermsManager,
    private val pinComponent: IPinComponent,
    private val wcSessionManager: WCSessionManager,
    private val wcManager: WCManager,
    private val accountManager: IAccountManager,
    private val appConfigProvider: AppConfigProvider
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    val appWebPageLink = appConfigProvider.appWebPageLink
    private val backedUpSubject = BehaviorSubject.create<Boolean>()
    val backedUpObservable: Observable<Boolean> get() = backedUpSubject

    private val pinSetSubject = BehaviorSubject.create<Boolean>()
    val pinSetObservable: Observable<Boolean> get() = pinSetSubject

    val termsAccepted by termsManager::allTermsAccepted
    val termsAcceptedFlow by termsManager::termsAcceptedSignalFlow

    private val baseCurrencySubject = BehaviorSubject.create<Currency>()
    val baseCurrencyObservable: Observable<Currency> get() = baseCurrencySubject

    private val walletConnectSessionCountSubject = BehaviorSubject.create<Int>()
    val walletConnectSessionCountObservable: Observable<Int> get() = walletConnectSessionCountSubject

    val hasNonStandardAccount: Boolean
        get() = accountManager.hasNonStandardAccount

    private var disposables: CompositeDisposable = CompositeDisposable()

    val appVersion: String
        get() {
            var appVersion = systemInfoManager.appVersion
            if (Translator.getString(R.string.is_release) == "false") {
                appVersion += " (${appConfigProvider.appBuild})"
            }

            return appVersion
        }

    val allBackedUp: Boolean
        get() = backupManager.allBackedUp

    val pendingRequestCountFlow by wcSessionManager::pendingRequestCountFlow

    val walletConnectSessionCount: Int
        get() = wcSessionManager.sessions.count()

    val currentLanguageDisplayName: String
        get() = languageManager.currentLanguageName

    val baseCurrency: Currency
        get() = currencyManager.baseCurrency

    val isPinSet: Boolean
        get() = pinComponent.isPinSet

    fun start() {
        disposables.add(backupManager.allBackedUpFlowable.subscribe {
            backedUpSubject.onNext(it)
        })

        coroutineScope.launch {
            wcSessionManager.sessionsFlow.collect{
                walletConnectSessionCountSubject.onNext(walletConnectSessionCount)
            }
        }

        disposables.add(currencyManager.baseCurrencyUpdatedSignal.subscribe {
            baseCurrencySubject.onNext(currencyManager.baseCurrency)
        })

        disposables.add(pinComponent.pinSetFlowable.subscribe {
            pinSetSubject.onNext(pinComponent.isPinSet)
        })
    }

    fun stop() {
        disposables.clear()
    }

    fun getWalletConnectSupportState(): WCManager.SupportState {
        return wcManager.getWalletConnectSupportState()
    }
}
