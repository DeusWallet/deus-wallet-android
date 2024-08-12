package io.deus.wallet.modules.nft.send

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.requireInput
import io.deus.wallet.entities.nft.EvmNftRecord
import io.deus.wallet.entities.nft.NftKey
import io.deus.wallet.entities.nft.NftUid
import io.deus.wallet.modules.address.AddressInputModule
import io.deus.wallet.modules.address.AddressParserViewModel
import io.deus.wallet.modules.address.AddressViewModel
import io.deus.wallet.modules.send.evm.SendEvmAddressService
import io.deus.wallet.modules.send.evm.confirmation.EvmKitWrapperHoldingViewModel
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.MenuItem
import io.deus.wallet.ui.compose.components.ScreenMessageWithAction
import io.horizontalsystems.nftkit.models.NftType
import kotlinx.parcelize.Parcelize

class SendNftFragment : BaseComposeFragment() {

    @Parcelize
    data class Input(val nftUid: String) : Parcelable

    @Composable
    override fun GetContent(navController: NavController) {
        val factory = getFactory(navController.requireInput<Input>().nftUid)

        when (factory?.evmNftRecord?.nftType) {
            NftType.Eip721 -> {
                val evmKitWrapperViewModel by navGraphViewModels<EvmKitWrapperHoldingViewModel>(
                    R.id.nftSendFragment
                ) { factory }
                val initiateLazyViewModel =
                    evmKitWrapperViewModel //needed in SendEvmConfirmationFragment

                val eip721ViewModel by viewModels<SendEip721ViewModel> { factory }
                val addressViewModel by viewModels<AddressViewModel> {
                    AddressInputModule.FactoryNft(factory.nftUid.blockchainType)
                }
                val addressParserViewModel by viewModels<AddressParserViewModel> { factory }
                SendEip721Screen(
                    navController,
                    eip721ViewModel,
                    addressViewModel,
                    addressParserViewModel,
                    R.id.nftSendFragment,
                )
            }

            NftType.Eip1155 -> {
                val evmKitWrapperViewModel by navGraphViewModels<EvmKitWrapperHoldingViewModel>(
                    R.id.nftSendFragment
                ) { factory }
                val initiateLazyViewModel =
                    evmKitWrapperViewModel //needed in SendEvmConfirmationFragment

                val eip1155ViewModel by viewModels<SendEip1155ViewModel> { factory }
                val addressViewModel by viewModels<AddressViewModel> {
                    AddressInputModule.FactoryNft(factory.nftUid.blockchainType)
                }
                val addressParserViewModel by viewModels<AddressParserViewModel> { factory }
                SendEip1155Screen(
                    navController,
                    eip1155ViewModel,
                    addressViewModel,
                    addressParserViewModel,
                    R.id.nftSendFragment,
                )
            }

            else -> {
                ShowErrorMessage(navController)
            }
        }
    }

}

private fun getFactory(nftUidString: String): SendNftModule.Factory? {
    val nftUid = NftUid.fromUid(nftUidString)

    val account = App.accountManager.activeAccount ?: return null

    if (account.isWatchAccount) return null

    val nftKey = NftKey(account, nftUid.blockchainType)

    val adapter = App.nftAdapterManager.adapter(nftKey) ?: return null

    val nftRecord = adapter.nftRecord(nftUid) ?: return null

    val evmNftRecord = (nftRecord as? EvmNftRecord) ?: return null

    val evmKitWrapper = App.evmBlockchainManager
        .getEvmKitManager(nftUid.blockchainType)
        .getEvmKitWrapper(account, nftUid.blockchainType)

    return SendNftModule.Factory(
        evmNftRecord,
        nftUid,
        nftRecord.balance,
        adapter,
        SendEvmAddressService(),
        App.nftMetadataManager,
        evmKitWrapper
    )
}

@Composable
private fun ShowErrorMessage(navController: NavController) {
    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.SendNft_Title),
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = { navController.popBackStack() }
                    )
                )
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            ScreenMessageWithAction(
                text = stringResource(R.string.Error),
                icon = R.drawable.ic_error_48
            )
        }
    }
}
