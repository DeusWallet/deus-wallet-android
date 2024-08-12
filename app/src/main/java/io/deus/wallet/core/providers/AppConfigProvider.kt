package io.deus.wallet.core.providers

import io.deus.wallet.BuildConfig
import io.deus.wallet.R
import io.deus.wallet.core.ILocalStorage
import io.deus.wallet.core.order
import io.deus.wallet.entities.Currency
import io.horizontalsystems.marketkit.models.BlockchainType

class AppConfigProvider(localStorage: ILocalStorage) {

    val appId by lazy { localStorage.appId }
    val appVersion by lazy { BuildConfig.VERSION_NAME }
    val appBuild by lazy { BuildConfig.VERSION_CODE }
    val companyWebPageLink by lazy { Translator.getString(R.string.companyWebPageLink) }
    val appWebPageLink by lazy { Translator.getString(R.string.appWebPageLink) }
    val analyticsLink by lazy { Translator.getString(R.string.analyticsLink) }
    val appGithubLink by lazy { Translator.getString(R.string.appGithubLink) }
    val appTwitterLink by lazy { Translator.getString(R.string.appTwitterLink) }
    val appTelegramLink by lazy { Translator.getString(R.string.appTelegramLink) }
    val appRedditLink by lazy { Translator.getString(R.string.appRedditLink) }
    val reportEmail by lazy { Translator.getString(R.string.reportEmail) }
    val releaseNotesUrl by lazy { Translator.getString(R.string.releaseNotesUrl) }
    val mempoolSpaceUrl: String = "https://mempool.space"
    val walletConnectUrl = "relay.walletconnect.com"
    val walletConnectProjectId by lazy { Translator.getString(R.string.walletConnectV2Key) }
    val walletConnectAppMetaDataName by lazy { Translator.getString(R.string.walletConnectAppMetaDataName) }
    val walletConnectAppMetaDataUrl by lazy { Translator.getString(R.string.walletConnectAppMetaDataUrl) }
    val walletConnectAppMetaDataIcon by lazy { Translator.getString(R.string.walletConnectAppMetaDataIcon) }
    val accountsBackupFileSalt by lazy { Translator.getString(R.string.accountsBackupFileSalt) }

    val blocksDecodedEthereumRpc by lazy {
        Translator.getString(R.string.blocksDecodedEthereumRpc)
    }
    val twitterBearerToken by lazy {
        Translator.getString(R.string.twitterBearerToken)
    }
    val etherscanApiKey by lazy {
        Translator.getString(R.string.etherscanKey)
    }
    val bscscanApiKey by lazy {
        Translator.getString(R.string.bscscanKey)
    }
    val polygonscanApiKey by lazy {
        Translator.getString(R.string.polygonscanKey)
    }
    val snowtraceApiKey by lazy {
        Translator.getString(R.string.snowtraceApiKey)
    }
    val optimisticEtherscanApiKey by lazy {
        Translator.getString(R.string.optimisticEtherscanApiKey)
    }
    val arbiscanApiKey by lazy {
        Translator.getString(R.string.arbiscanApiKey)
    }
    val gnosisscanApiKey by lazy {
        Translator.getString(R.string.gnosisscanApiKey)
    }
    val ftmscanApiKey by lazy {
        Translator.getString(R.string.ftmscanApiKey)
    }
    val guidesUrl by lazy {
        Translator.getString(R.string.guidesUrl)
    }
    val faqUrl by lazy {
        Translator.getString(R.string.faqUrl)
    }
    val coinsJsonUrl by lazy {
        Translator.getString(R.string.coinsJsonUrl)
    }
    val providerCoinsJsonUrl by lazy {
        Translator.getString(R.string.providerCoinsJsonUrl)
    }

    val marketApiBaseUrl by lazy {
        Translator.getString(R.string.marketApiBaseUrl)
    }

    val marketApiKey by lazy {
        Translator.getString(R.string.marketApiKey)
    }

    val openSeaApiKey by lazy {
        Translator.getString(R.string.openSeaApiKey)
    }

    val solscanApiKey by lazy {
        Translator.getString(R.string.solscanApiKey)
    }

    val trongridApiKeys: List<String> by lazy {
        Translator.getString(R.string.trongridApiKeys).split(",")
    }

    val udnApiKey by lazy {
        Translator.getString(R.string.udnApiKey)
    }

    val oneInchApiKey by lazy {
        Translator.getString(R.string.oneInchApiKey)
    }

    val fiatDecimal: Int = 2
    val feeRateAdjustForCurrencies: List<String> = listOf("USD", "EUR")

    val currencies: List<Currency> = listOf(
        Currency("AUD", "A$", 2, R.drawable.icon_32_flag_australia),
        Currency("ARS", "$", 2, R.drawable.icon_32_flag_argentine),
        Currency("BRL", "R$", 2, R.drawable.icon_32_flag_brazil),
        Currency("CAD", "C$", 2, R.drawable.icon_32_flag_canada),
        Currency("CHF", "₣", 2, R.drawable.icon_32_flag_switzerland),
        Currency("CNY", "¥", 2, R.drawable.icon_32_flag_china),
        Currency("EUR", "€", 2, R.drawable.icon_32_flag_europe),
        Currency("GBP", "£", 2, R.drawable.icon_32_flag_england),
        Currency("HKD", "HK$", 2, R.drawable.icon_32_flag_hongkong),
        Currency("HUF", "Ft", 2, R.drawable.icon_32_flag_hungary),
        Currency("ILS", "₪", 2, R.drawable.icon_32_flag_israel),
        Currency("INR", "₹", 2, R.drawable.icon_32_flag_india),
        Currency("JPY", "¥", 2, R.drawable.icon_32_flag_japan),
        Currency("NOK", "kr", 2, R.drawable.icon_32_flag_norway),
        Currency("PHP", "₱", 2, R.drawable.icon_32_flag_philippine),
        Currency("RUB", "₽", 2, R.drawable.icon_32_flag_russia),
        Currency("SGD", "S$", 2, R.drawable.icon_32_flag_singapore),
        Currency("USD", "$", 2, R.drawable.icon_32_flag_usa),
        Currency("ZAR", "R", 2, R.drawable.icon_32_flag_south_africa),
    )

    val donateAddresses: Map<BlockchainType, String> by lazy {
        mapOf(
            BlockchainType.Bitcoin to "bc1qv0h0m2u005wsmwcxheflyzljjhle8t0mh340eu",
            BlockchainType.BitcoinCash to "bitcoincash:qpyjst6k6hh4gzd76qg25w4zmqdpjkrtqc6mpd5fkg",
            BlockchainType.ECash to "ecash:qqmeq8crlgdvznz88jlc5se4340e39azwsnph9cj65",
            BlockchainType.Litecoin to "ltc1q4xdng3wkznnueyjet33yce2cq3ku8qf770q7c2",
            BlockchainType.Dash to "XmJrUX9xDjTHxwh6iC6ZGvKxvUPXZ6ZPqQ",
            BlockchainType.Zcash to "zs10as78vw9a5gsncegy0l2vvfd98vu7cq7xcexmuusp9cwxuwmwnqcghz7qzrud3g9wrz3k0zgja9",
            BlockchainType.Ethereum to "0x9aEdF35602DfE33Aefe7Ec3839DeA5d55DAe75C4",
            BlockchainType.BinanceSmartChain to "0x9aEdF35602DfE33Aefe7Ec3839DeA5d55DAe75C4",
            BlockchainType.BinanceChain to "bnb1qxz3xutms4psfmmva98wl5qpjj8m4r0nrqg3sc",
            BlockchainType.Polygon to "0x9aEdF35602DfE33Aefe7Ec3839DeA5d55DAe75C4",
            BlockchainType.Avalanche to "0x9aEdF35602DfE33Aefe7Ec3839DeA5d55DAe75C4",
            BlockchainType.Optimism to "0x9aEdF35602DfE33Aefe7Ec3839DeA5d55DAe75C4",
            BlockchainType.ArbitrumOne to "0x9aEdF35602DfE33Aefe7Ec3839DeA5d55DAe75C4",
            BlockchainType.Solana to "35UEXQCHj3rPkAXSqakR8gkebdMsxnmzX4WvxWWmXUD7",
            BlockchainType.Gnosis to "0x9aEdF35602DfE33Aefe7Ec3839DeA5d55DAe75C4",
            BlockchainType.Fantom to "0x9aEdF35602DfE33Aefe7Ec3839DeA5d55DAe75C4",
            BlockchainType.Ton to "UQCC14CKROJ4rUy59TWrBz3aNM5h0mU6BfgfgH8AN1ab6tXy",
            BlockchainType.Tron to "TTxDhRFL7FGVGCuRSthbsBWR5kUAXpu8Dp",
        ).toList().sortedBy { (key, _) -> key.order }.toMap()
    }
}
