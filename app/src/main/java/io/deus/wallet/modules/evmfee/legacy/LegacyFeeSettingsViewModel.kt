package io.deus.wallet.modules.evmfee.legacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.core.ethereum.EvmCoinService
import io.deus.wallet.core.feePriceScale
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.entities.DataState
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.evmfee.FeeSummaryViewItem
import io.deus.wallet.modules.evmfee.FeeViewItem
import io.deus.wallet.modules.evmfee.GasPriceInfo
import io.deus.wallet.modules.evmfee.IEvmFeeService
import io.deus.wallet.modules.evmfee.Transaction
import io.deus.wallet.modules.fee.FeeItem
import kotlinx.coroutines.launch

class LegacyFeeSettingsViewModel(
    private val gasPriceService: LegacyGasPriceService,
    private val feeService: IEvmFeeService,
    private val coinService: EvmCoinService
) : ViewModel() {

    private val scale = coinService.token.blockchainType.feePriceScale

    var feeSummaryViewItem by mutableStateOf<FeeSummaryViewItem?>(null)
        private set

    var feeViewItem by mutableStateOf<FeeViewItem?>(null)
        private set

    init {
        viewModelScope.launch {
            gasPriceService.stateFlow.collect {
                sync(it)
            }
        }

        viewModelScope.launch {
            feeService.transactionStatusFlow.collect {
                syncTransactionStatus(it)
            }
        }
    }

    fun onSelectGasPrice(gasPrice: Long) {
        gasPriceService.setGasPrice(gasPrice)
    }

    fun onIncrementGasPrice(currentWeiValue: Long) {
        gasPriceService.setGasPrice(currentWeiValue + scale.scaleValue)
    }

    fun onDecrementGasPrice(currentWeiValue: Long) {
        gasPriceService.setGasPrice((currentWeiValue - scale.scaleValue).coerceAtLeast(0))
    }

    private fun syncTransactionStatus(transactionStatus: DataState<Transaction>) {
        syncFeeViewItems(transactionStatus)
    }

    private fun syncFeeViewItems(transactionStatus: DataState<Transaction>) {
        val notAvailable = Translator.getString(R.string.NotAvailable)
        when (transactionStatus) {
            DataState.Loading -> {
                feeSummaryViewItem = FeeSummaryViewItem(null, notAvailable, ViewState.Loading)
            }
            is DataState.Error -> {
                feeSummaryViewItem = FeeSummaryViewItem(null, notAvailable, ViewState.Error(transactionStatus.error))
            }
            is DataState.Success -> {
                val transaction = transactionStatus.data
                val viewState = transaction.errors.firstOrNull()?.let { ViewState.Error(it) } ?: ViewState.Success
                val feeAmountData = coinService.amountData(transactionStatus.data.gasData.estimatedFee, transactionStatus.data.gasData.isSurcharged)
                val feeItem = FeeItem(feeAmountData.primary.getFormattedPlain(), feeAmountData.secondary?.getFormattedPlain())
                val gasLimit = App.numberFormatter.format(transactionStatus.data.gasData.gasLimit.toBigDecimal(), 0, 0)

                feeSummaryViewItem = FeeSummaryViewItem(feeItem, gasLimit, viewState)
            }
        }
    }

    private fun sync(state: DataState<GasPriceInfo>) {
        if (state is DataState.Success) {
            feeViewItem = FeeViewItem(weiValue = state.data.gasPrice.max, scale = scale, warnings = state.data.warnings, errors = state.data.errors)
        }
    }

}
