package io.deus.wallet.modules.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.modules.theme.ThemeService
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.WithTranslatableTitle

object AppearanceModule {

    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val launchScreenService = LaunchScreenService(App.localStorage)
            val appIconService = AppIconService(App.localStorage)
            val themeService = ThemeService(App.localStorage)
            return AppearanceViewModel(
                launchScreenService,
                appIconService,
                themeService,
                App.baseTokenManager,
                App.balanceViewTypeManager,
                App.localStorage
            ) as T
        }
    }

}

enum class AppIcon(val icon: Int, val titleText: String) : WithTranslatableTitle {
    Main(R.drawable.launcher_main_preview, "Main"),
    Dark(R.drawable.launcher_dark_preview, "Dark"),
    Mono(R.drawable.launcher_mono_preview, "Mono"),
    Leo(R.drawable.launcher_leo_preview, "Chess"),
    Mustang(R.drawable.launcher_mustang_preview, "Chess"),
    Yak(R.drawable.launcher_yak_preview, "Chess"),
    Punk(R.drawable.launcher_punk_preview, "Calculator"),
    Ape(R.drawable.launcher_ape_preview, "Calculator"),
    Ball8(R.drawable.launcher_8ball_preview, "Calculator");

    override val title: TranslatableString
        get() = TranslatableString.PlainString(titleText)

    val launcherName: String
        get() = "${App.instance.packageName}.${this.name}LauncherAlias"


    companion object {
        private val map = values().associateBy(AppIcon::name)
        private val titleMap = values().associateBy(AppIcon::titleText)

        fun fromString(type: String?): AppIcon? = map[type]
        fun fromTitle(title: String?): AppIcon? = titleMap[title]
    }
}