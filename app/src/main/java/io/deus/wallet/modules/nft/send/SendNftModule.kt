package io.deus.wallet.modules.nft.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.adapters.nft.INftAdapter
import io.deus.wallet.core.managers.EvmKitWrapper
import io.deus.wallet.core.managers.NftMetadataManager
import io.deus.wallet.core.utils.AddressUriParser
import io.deus.wallet.entities.DataState
import io.deus.wallet.entities.nft.EvmNftRecord
import io.deus.wallet.entities.nft.NftUid
import io.deus.wallet.modules.address.AddressParserViewModel
import io.deus.wallet.modules.send.evm.SendEvmAddressService
import io.deus.wallet.modules.send.evm.confirmation.EvmKitWrapperHoldingViewModel

object SendNftModule {

    @Suppress("UNCHECKED_CAST")
    class Factory(
        val evmNftRecord: EvmNftRecord,
        val nftUid: NftUid,
        val nftBalance: Int,
        private val adapter: INftAdapter,
        private val sendEvmAddressService: SendEvmAddressService,
        private val nftMetadataManager: NftMetadataManager,
        private val evmKitWrapper: EvmKitWrapper
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEip721ViewModel::class.java -> {
                    SendEip721ViewModel(
                        nftUid,
                        adapter,
                        sendEvmAddressService,
                        nftMetadataManager
                    ) as T
                }
                SendEip1155ViewModel::class.java -> {
                    SendEip1155ViewModel(
                        nftUid,
                        nftBalance,
                        adapter,
                        sendEvmAddressService,
                        nftMetadataManager
                    ) as T
                }
                EvmKitWrapperHoldingViewModel::class.java -> {
                    EvmKitWrapperHoldingViewModel(evmKitWrapper) as T
                }
                AddressParserViewModel::class.java -> {
                    AddressParserViewModel(AddressUriParser(nftUid.blockchainType, null), null) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    data class SendEip721UiState(
        val name: String,
        val imageUrl: String?,
        val addressError: Throwable?,
        val canBeSend: Boolean
    )

    data class SendEip1155UiState(
        val name: String,
        val imageUrl: String?,
        val addressError: Throwable?,
        val amountState: DataState<Int>?,
        val canBeSend: Boolean
    )

}