package io.deus.wallet.modules.evmfee

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.deus.wallet.core.ethereum.EvmCoinService
import io.deus.wallet.entities.DataState
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.fee.FeeItem
import kotlinx.coroutines.launch

class EvmFeeCellViewModel(
    val feeService: IEvmFeeService,
    val gasPriceService: IEvmGasPriceService,
    val coinService: EvmCoinService
) : ViewModel() {

    val feeLiveData = MutableLiveData<FeeItem?>()
    val viewStateLiveData = MutableLiveData<ViewState>()

    init {
        viewModelScope.launch {
            feeService.transactionStatusFlow.collect {
                syncTransactionStatus(it)
            }
        }
    }

    private fun syncTransactionStatus(transactionStatus: DataState<Transaction>) {
        when (transactionStatus) {
            DataState.Loading -> {}
            is DataState.Error -> {
                viewStateLiveData.postValue(ViewState.Error(transactionStatus.error))
                feeLiveData.postValue(null)
            }
            is DataState.Success -> {
                val transaction = transactionStatus.data

                if (transaction.errors.isNotEmpty()) {
                    viewStateLiveData.postValue(ViewState.Error(transaction.errors.first()))
                } else {
                    viewStateLiveData.postValue(ViewState.Success)
                }

                val feeAmountData = coinService.amountData(transactionStatus.data.gasData.estimatedFee, transactionStatus.data.gasData.isSurcharged)
                val feeViewItem = FeeItem(
                    primary = feeAmountData.primary.getFormattedPlain(),
                    secondary = feeAmountData.secondary?.getFormattedPlain()
                )
                feeLiveData.postValue(feeViewItem)
            }
        }
    }
}
