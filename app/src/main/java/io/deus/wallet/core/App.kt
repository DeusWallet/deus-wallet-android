package io.deus.wallet.core

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import io.deus.wallet.BuildConfig
import io.deus.wallet.core.factories.AccountFactory
import io.deus.wallet.core.factories.AdapterFactory
import io.deus.wallet.core.factories.EvmAccountManagerFactory
import io.deus.wallet.core.managers.AccountCleaner
import io.deus.wallet.core.managers.AccountManager
import io.deus.wallet.core.managers.AdapterManager
import io.deus.wallet.core.managers.AppVersionManager
import io.deus.wallet.core.managers.BackgroundStateChangeListener
import io.deus.wallet.core.managers.BackupManager
import io.deus.wallet.core.managers.BalanceHiddenManager
import io.deus.wallet.core.managers.BaseTokenManager
import io.deus.wallet.core.managers.BinanceKitManager
import io.deus.wallet.core.managers.BtcBlockchainManager
import io.deus.wallet.core.managers.CexAssetManager
import io.deus.wallet.core.managers.CoinManager
import io.deus.wallet.core.managers.ConnectivityManager
import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.managers.EvmBlockchainManager
import io.deus.wallet.core.managers.EvmLabelManager
import io.deus.wallet.core.managers.EvmNodeSyncManager
import io.deus.wallet.core.managers.EvmSyncSourceManager
import io.deus.wallet.core.managers.KeyStoreCleaner
import io.deus.wallet.core.managers.LanguageManager
import io.deus.wallet.core.managers.LocalStorageManager
import io.deus.wallet.core.managers.MarketFavoritesManager
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.core.managers.NetworkManager
import io.deus.wallet.core.managers.NftAdapterManager
import io.deus.wallet.core.managers.NftMetadataManager
import io.deus.wallet.core.managers.NftMetadataSyncer
import io.deus.wallet.core.managers.NumberFormatter
import io.deus.wallet.core.managers.RateAppManager
import io.deus.wallet.core.managers.ReleaseNotesManager
import io.deus.wallet.core.managers.RestoreSettingsManager
import io.deus.wallet.core.managers.SolanaKitManager
import io.deus.wallet.core.managers.SolanaRpcSourceManager
import io.deus.wallet.core.managers.SolanaWalletManager
import io.deus.wallet.core.managers.SpamManager
import io.deus.wallet.core.managers.SubscriptionManager
import io.deus.wallet.core.managers.SystemInfoManager
import io.deus.wallet.core.managers.TermsManager
import io.deus.wallet.core.managers.TokenAutoEnableManager
import io.deus.wallet.core.managers.TorManager
import io.deus.wallet.core.managers.TransactionAdapterManager
import io.deus.wallet.core.managers.TronAccountManager
import io.deus.wallet.core.managers.TronKitManager
import io.deus.wallet.core.managers.UserManager
import io.deus.wallet.core.managers.WalletActivator
import io.deus.wallet.core.managers.WalletManager
import io.deus.wallet.core.managers.WalletStorage
import io.deus.wallet.core.managers.WordsManager
import io.deus.wallet.core.managers.ZcashBirthdayProvider
import io.deus.wallet.core.providers.AppConfigProvider
import io.deus.wallet.core.providers.CexProviderManager
import io.deus.wallet.core.providers.EvmLabelProvider
import io.deus.wallet.core.providers.FeeRateProvider
import io.deus.wallet.core.providers.FeeTokenProvider
import io.deus.wallet.core.stats.StatsManager
import io.deus.wallet.core.storage.AccountsStorage
import io.deus.wallet.core.storage.AppDatabase
import io.deus.wallet.core.storage.BlockchainSettingsStorage
import io.deus.wallet.core.storage.EnabledWalletsStorage
import io.deus.wallet.core.storage.EvmNodeSyncStorage
import io.deus.wallet.core.storage.EvmSyncSourceStorage
import io.deus.wallet.core.storage.NftStorage
import io.deus.wallet.core.storage.RestoreSettingsStorage
import io.deus.wallet.core.utils.EvmNodeSync
import io.deus.wallet.modules.backuplocal.fullbackup.BackupProvider
import io.deus.wallet.modules.balance.BalanceViewTypeManager
import io.deus.wallet.modules.chart.ChartIndicatorManager
import io.deus.wallet.modules.contacts.ContactsRepository
import io.deus.wallet.modules.keystore.KeyStoreActivity
import io.deus.wallet.modules.launcher.LauncherActivity
import io.deus.wallet.modules.lockscreen.LockScreenActivity
import io.deus.wallet.modules.market.favorites.MarketFavoritesMenuService
import io.deus.wallet.modules.market.topnftcollections.TopNftCollectionsRepository
import io.deus.wallet.modules.market.topnftcollections.TopNftCollectionsViewItemFactory
import io.deus.wallet.modules.market.topplatforms.TopPlatformsRepository
import io.deus.wallet.modules.pin.PinComponent
import io.deus.wallet.modules.pin.core.PinDbStorage
import io.deus.wallet.modules.profeatures.ProFeaturesAuthorizationManager
import io.deus.wallet.modules.profeatures.storage.ProFeaturesStorage
import io.deus.wallet.modules.settings.appearance.AppIconService
import io.deus.wallet.modules.settings.appearance.LaunchScreenService
import io.deus.wallet.modules.theme.ThemeService
import io.deus.wallet.modules.theme.ThemeType
import io.deus.wallet.modules.walletconnect.WCManager
import io.deus.wallet.modules.walletconnect.WCSessionManager
import io.deus.wallet.modules.walletconnect.storage.WCSessionStorage
import io.deus.wallet.widgets.MarketWidgetManager
import io.deus.wallet.widgets.MarketWidgetRepository
import io.deus.wallet.widgets.MarketWidgetWorker
import io.horizontalsystems.core.BackgroundManager
import io.horizontalsystems.core.CoreApp
import io.horizontalsystems.core.ICoreApp
import io.horizontalsystems.core.security.EncryptionManager
import io.horizontalsystems.core.security.KeyStoreManager
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.hdwalletkit.Mnemonic
import io.reactivex.plugins.RxJavaPlugins
import java.util.logging.Level
import java.util.logging.Logger
import androidx.work.Configuration as WorkConfiguration

class App : CoreApp(), WorkConfiguration.Provider, ImageLoaderFactory {

    companion object : ICoreApp by CoreApp {

        lateinit var preferences: SharedPreferences
        lateinit var feeRateProvider: FeeRateProvider
        lateinit var localStorage: ILocalStorage
        lateinit var marketStorage: IMarketStorage
        lateinit var torKitManager: ITorManager
        lateinit var restoreSettingsStorage: IRestoreSettingsStorage
        lateinit var currencyManager: CurrencyManager
        lateinit var languageManager: LanguageManager

        lateinit var blockchainSettingsStorage: BlockchainSettingsStorage
        lateinit var evmSyncSourceStorage: EvmSyncSourceStorage
        lateinit var btcBlockchainManager: BtcBlockchainManager
        lateinit var wordsManager: WordsManager
        lateinit var networkManager: INetworkManager
        lateinit var backgroundStateChangeListener: BackgroundStateChangeListener
        lateinit var appConfigProvider: AppConfigProvider
        lateinit var adapterManager: IAdapterManager
        lateinit var transactionAdapterManager: TransactionAdapterManager
        lateinit var walletManager: IWalletManager
        lateinit var walletActivator: WalletActivator
        lateinit var tokenAutoEnableManager: TokenAutoEnableManager
        lateinit var walletStorage: IWalletStorage
        lateinit var accountManager: IAccountManager
        lateinit var userManager: UserManager
        lateinit var accountFactory: IAccountFactory
        lateinit var backupManager: IBackupManager
        lateinit var proFeatureAuthorizationManager: ProFeaturesAuthorizationManager
        lateinit var zcashBirthdayProvider: ZcashBirthdayProvider

        lateinit var connectivityManager: ConnectivityManager
        lateinit var appDatabase: AppDatabase
        lateinit var accountsStorage: IAccountsStorage
        lateinit var evmNodeSyncStorage: IEvmNodeSyncStorage
        lateinit var enabledWalletsStorage: IEnabledWalletStorage
        lateinit var binanceKitManager: BinanceKitManager
        lateinit var solanaKitManager: SolanaKitManager
        lateinit var tronKitManager: TronKitManager
        lateinit var numberFormatter: IAppNumberFormatter
        lateinit var feeCoinProvider: FeeTokenProvider
        lateinit var accountCleaner: IAccountCleaner
        lateinit var rateAppManager: IRateAppManager
        lateinit var evmNodeSyncManager: IEvmNodeSyncManager
        lateinit var coinManager: ICoinManager
        lateinit var wcSessionManager: WCSessionManager
        lateinit var wcManager: WCManager
        lateinit var termsManager: ITermsManager
        lateinit var marketFavoritesManager: MarketFavoritesManager
        lateinit var marketKit: MarketKitWrapper
        lateinit var releaseNotesManager: ReleaseNotesManager
        lateinit var restoreSettingsManager: RestoreSettingsManager
        lateinit var evmSyncSourceManager: EvmSyncSourceManager
        lateinit var evmBlockchainManager: EvmBlockchainManager
        lateinit var solanaRpcSourceManager: SolanaRpcSourceManager
        lateinit var nftMetadataManager: NftMetadataManager
        lateinit var nftAdapterManager: NftAdapterManager
        lateinit var nftMetadataSyncer: NftMetadataSyncer
        lateinit var evmLabelManager: EvmLabelManager
        lateinit var baseTokenManager: BaseTokenManager
        lateinit var balanceViewTypeManager: BalanceViewTypeManager
        lateinit var balanceHiddenManager: BalanceHiddenManager
        lateinit var marketWidgetManager: MarketWidgetManager
        lateinit var marketWidgetRepository: MarketWidgetRepository
        lateinit var contactsRepository: ContactsRepository
        lateinit var subscriptionManager: SubscriptionManager
        lateinit var cexProviderManager: CexProviderManager
        lateinit var cexAssetManager: CexAssetManager
        lateinit var chartIndicatorManager: ChartIndicatorManager
        lateinit var backupProvider: BackupProvider
        lateinit var spamManager: SpamManager
        lateinit var statsManager: StatsManager
    }

    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            //Disable logging for lower levels in Release build
            Logger.getLogger("").level = Level.SEVERE
        }

        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            Log.w("RxJava ErrorHandler", e)
        }

        instance = this
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        LocalStorageManager(preferences).apply {
            localStorage = this
            pinSettingsStorage = this
            lockoutStorage = this
            thirdKeyboardStorage = this
            marketStorage = this
        }

        val appConfig = AppConfigProvider(localStorage)
        appConfigProvider = appConfig

        torKitManager = TorManager(instance, localStorage)
        subscriptionManager = SubscriptionManager()

        marketKit = MarketKitWrapper(
            context = this,
            hsApiBaseUrl = appConfig.marketApiBaseUrl,
            hsApiKey = appConfig.marketApiKey,
            subscriptionManager = subscriptionManager
        )

        feeRateProvider = FeeRateProvider(appConfigProvider)
        backgroundManager = BackgroundManager(this)

        appDatabase = AppDatabase.getInstance(this)

        evmNodeSyncStorage = EvmNodeSyncStorage(appDatabase)
        evmNodeSyncManager = EvmNodeSyncManager(evmNodeSyncStorage)

        blockchainSettingsStorage = BlockchainSettingsStorage(appDatabase)
        evmSyncSourceStorage = EvmSyncSourceStorage(appDatabase)
        evmSyncSourceManager = EvmSyncSourceManager(appConfigProvider, blockchainSettingsStorage, evmSyncSourceStorage, evmNodeSyncManager )

        btcBlockchainManager = BtcBlockchainManager(blockchainSettingsStorage, marketKit)

        binanceKitManager = BinanceKitManager()

        accountsStorage = AccountsStorage(appDatabase)
        restoreSettingsStorage = RestoreSettingsStorage(appDatabase)

        EvmNodeSync.setEvmNodeSyncStorage(evmNodeSyncStorage)
        AppLog.logsDao = appDatabase.logsDao()

        accountCleaner = AccountCleaner()
        accountManager = AccountManager(accountsStorage, accountCleaner)
        userManager = UserManager(accountManager)

        val proFeaturesStorage = ProFeaturesStorage(appDatabase)
        proFeatureAuthorizationManager = ProFeaturesAuthorizationManager(proFeaturesStorage, accountManager, appConfigProvider)

        enabledWalletsStorage = EnabledWalletsStorage(appDatabase)
        walletStorage = WalletStorage(marketKit, enabledWalletsStorage)

        walletManager = WalletManager(accountManager, walletStorage)
        coinManager = CoinManager(marketKit, walletManager)

        solanaRpcSourceManager = SolanaRpcSourceManager(blockchainSettingsStorage, marketKit)
        val solanaWalletManager = SolanaWalletManager(walletManager, accountManager, marketKit)
        solanaKitManager = SolanaKitManager(appConfigProvider, solanaRpcSourceManager, solanaWalletManager, backgroundManager)

        tronKitManager = TronKitManager(appConfigProvider, backgroundManager)

        wordsManager = WordsManager(Mnemonic())
        networkManager = NetworkManager()
        accountFactory = AccountFactory(accountManager, userManager)
        backupManager = BackupManager(accountManager)


        KeyStoreManager(
            keyAlias = "MASTER_KEY",
            keyStoreCleaner = KeyStoreCleaner(localStorage, accountManager, walletManager),
            logger = AppLogger("key-store")
        ).apply {
            keyStoreManager = this
            keyProvider = this
        }

        encryptionManager = EncryptionManager(keyProvider)

        walletActivator = WalletActivator(walletManager, marketKit)
        tokenAutoEnableManager = TokenAutoEnableManager(appDatabase.tokenAutoEnabledBlockchainDao())

        val evmAccountManagerFactory = EvmAccountManagerFactory(
            accountManager,
            walletManager,
            marketKit,
            tokenAutoEnableManager
        )
        evmBlockchainManager = EvmBlockchainManager(
            backgroundManager,
            evmSyncSourceManager,
            marketKit,
            evmAccountManagerFactory,
        )

        val tronAccountManager = TronAccountManager(
            accountManager,
            walletManager,
            marketKit,
            tronKitManager,
            tokenAutoEnableManager
        )
        tronAccountManager.start()

        systemInfoManager = SystemInfoManager(appConfigProvider)

        languageManager = LanguageManager()
        currencyManager = CurrencyManager(localStorage, appConfigProvider)
        numberFormatter = NumberFormatter(languageManager)

        connectivityManager = ConnectivityManager(backgroundManager)

        zcashBirthdayProvider = ZcashBirthdayProvider(this)
        restoreSettingsManager = RestoreSettingsManager(restoreSettingsStorage, zcashBirthdayProvider)

        evmLabelManager = EvmLabelManager(
            EvmLabelProvider(),
            appDatabase.evmAddressLabelDao(),
            appDatabase.evmMethodLabelDao(),
            appDatabase.syncerStateDao()
        )

        val adapterFactory = AdapterFactory(
            context = instance,
            btcBlockchainManager = btcBlockchainManager,
            evmBlockchainManager = evmBlockchainManager,
            evmSyncSourceManager = evmSyncSourceManager,
            binanceKitManager = binanceKitManager,
            solanaKitManager = solanaKitManager,
            tronKitManager = tronKitManager,
            backgroundManager = backgroundManager,
            restoreSettingsManager = restoreSettingsManager,
            coinManager = coinManager,
            evmLabelManager = evmLabelManager,
            localStorage = localStorage
        )
        adapterManager = AdapterManager(walletManager, adapterFactory, btcBlockchainManager, evmBlockchainManager, binanceKitManager, solanaKitManager, tronKitManager)
        transactionAdapterManager = TransactionAdapterManager(adapterManager, adapterFactory)

        feeCoinProvider = FeeTokenProvider(marketKit)

        pinComponent = PinComponent(
            pinSettingsStorage = pinSettingsStorage,
            excludedActivityNames = listOf(
                KeyStoreActivity::class.java.name,
                LockScreenActivity::class.java.name,
                LauncherActivity::class.java.name,
            ),
            userManager = userManager,
            pinDbStorage = PinDbStorage(appDatabase.pinDao())
        )

        statsManager = StatsManager(appDatabase.statsDao(), localStorage, marketKit, appConfigProvider)
        backgroundStateChangeListener = BackgroundStateChangeListener(systemInfoManager, keyStoreManager, pinComponent, statsManager).apply {
            backgroundManager.registerListener(this)
        }

        rateAppManager = RateAppManager(walletManager, adapterManager, localStorage)

        wcManager = WCManager(accountManager)

        termsManager = TermsManager(localStorage)

        marketWidgetManager = MarketWidgetManager()
        marketFavoritesManager = MarketFavoritesManager(appDatabase, marketWidgetManager)

        marketWidgetRepository = MarketWidgetRepository(
            marketKit,
            marketFavoritesManager,
            MarketFavoritesMenuService(localStorage, marketWidgetManager),
            TopNftCollectionsRepository(marketKit),
            TopNftCollectionsViewItemFactory(numberFormatter),
            TopPlatformsRepository(marketKit, currencyManager),
            currencyManager
        )

        releaseNotesManager = ReleaseNotesManager(systemInfoManager, localStorage, appConfigProvider)

        setAppTheme()

        val nftStorage = NftStorage(appDatabase.nftDao(), marketKit)
        nftMetadataManager = NftMetadataManager(marketKit, appConfigProvider, nftStorage)
        nftAdapterManager = NftAdapterManager(walletManager, evmBlockchainManager)
        nftMetadataSyncer = NftMetadataSyncer(nftAdapterManager, nftMetadataManager, nftStorage)

        initializeWalletConnectV2(appConfig)

        wcSessionManager = WCSessionManager(accountManager, WCSessionStorage(appDatabase))

        baseTokenManager = BaseTokenManager(coinManager, localStorage)
        balanceViewTypeManager = BalanceViewTypeManager(localStorage)
        balanceHiddenManager = BalanceHiddenManager(localStorage, backgroundManager)

        contactsRepository = ContactsRepository(marketKit)
        cexProviderManager = CexProviderManager(accountManager)
        cexAssetManager = CexAssetManager(marketKit, appDatabase.cexAssetsDao())
        chartIndicatorManager = ChartIndicatorManager(appDatabase.chartIndicatorSettingsDao(), localStorage)

        backupProvider = BackupProvider(
            localStorage = localStorage,
            languageManager = languageManager,
            walletStorage = enabledWalletsStorage,
            settingsManager = restoreSettingsManager,
            accountManager = accountManager,
            accountFactory = accountFactory,
            walletManager = walletManager,
            restoreSettingsManager = restoreSettingsManager,
            blockchainSettingsStorage = blockchainSettingsStorage,
            evmBlockchainManager = evmBlockchainManager,
            marketFavoritesManager = marketFavoritesManager,
            balanceViewTypeManager = balanceViewTypeManager,
            appIconService = AppIconService(localStorage),
            themeService = ThemeService(localStorage),
            chartIndicatorManager = chartIndicatorManager,
            chartIndicatorSettingsDao = appDatabase.chartIndicatorSettingsDao(),
            balanceHiddenManager = balanceHiddenManager,
            baseTokenManager = baseTokenManager,
            launchScreenService = LaunchScreenService(localStorage),
            currencyManager = currencyManager,
            btcBlockchainManager = btcBlockchainManager,
            evmSyncSourceManager = evmSyncSourceManager,
            evmSyncSourceStorage = evmSyncSourceStorage,
            solanaRpcSourceManager = solanaRpcSourceManager,
            contactsRepository = contactsRepository
        )

        spamManager = SpamManager(localStorage)

        startTasks()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .components {
                add(SvgDecoder.Factory())
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    private fun initializeWalletConnectV2(appConfig: AppConfigProvider) {
        val projectId = appConfig.walletConnectProjectId
        val serverUrl = "wss://${appConfig.walletConnectUrl}?projectId=$projectId"
        val connectionType = ConnectionType.AUTOMATIC
        val appMetaData = Core.Model.AppMetaData(
            name = appConfig.walletConnectAppMetaDataName,
            description = "",
            url = appConfig.walletConnectAppMetaDataUrl,
            icons = listOf(appConfig.walletConnectAppMetaDataIcon),
            redirect = null,
        )

        CoreClient.initialize(
            metaData = appMetaData,
            relayServerUrl = serverUrl,
            connectionType = connectionType,
            application = this,
            onError = { error ->
                Log.w("AAA", "error", error.throwable)
            },
        )
        Web3Wallet.initialize(Wallet.Params.Init(core = CoreClient)) { error ->
            Log.e("AAA", "error", error.throwable)
        }
    }

    private fun setAppTheme() {
        val nightMode = when (localStorage.currentTheme) {
            ThemeType.Light -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeType.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeType.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }

    override val workManagerConfiguration: androidx.work.Configuration
        get() = if (BuildConfig.DEBUG) {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
        } else {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .build()
        }

    override fun localizedContext(): Context {
        return localeAwareContext(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAwareContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAwareContext(this)
    }



    private fun startTasks() {
        Thread {
            EthereumKit.init()
            adapterManager.startAdapterManager()
            marketKit.sync()
            rateAppManager.onAppLaunch()
            evmNodeSyncManager.onAppLaunch()
            nftMetadataSyncer.start()
            pinComponent.initDefaultPinLevel()
            accountManager.clearAccounts()
            wcSessionManager.start()

            AppVersionManager(systemInfoManager, localStorage).apply { storeAppVersion() }

            if (MarketWidgetWorker.hasEnabledWidgets(instance)) {
                MarketWidgetWorker.enqueueWork(instance)
            } else {
                MarketWidgetWorker.cancel(instance)
            }

            evmLabelManager.sync()
            contactsRepository.initialize()

        }.start()
    }
}
