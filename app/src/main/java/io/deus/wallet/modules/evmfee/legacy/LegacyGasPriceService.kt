package io.deus.wallet.modules.evmfee.legacy

import io.deus.wallet.core.Warning
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.DataState
import io.deus.wallet.modules.evmfee.Bound
import io.deus.wallet.modules.evmfee.FeeSettingsWarning
import io.deus.wallet.modules.evmfee.GasPriceInfo
import io.deus.wallet.modules.evmfee.IEvmGasPriceService
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.models.GasPrice
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.lang.Long.max
import java.math.BigDecimal

class LegacyGasPriceService(
    private val gasPriceProvider: LegacyGasPriceProvider,
    private val minRecommendedGasPrice: Long? = null,
    initialGasPrice: Long? = null,
) : IEvmGasPriceService() {

    var recommendedGasPrice: Long? = null
    private var disposable: Disposable? = null

    private val overpricingBound = Bound.Multiplied(BigDecimal(1.5))
    private val riskOfStuckBound = Bound.Multiplied(BigDecimal(0.9))

    private var state: DataState<GasPriceInfo> = DataState.Loading

    override fun createState() = state

    private val recommendedGasPriceSingle
        get() = recommendedGasPrice?.let { Single.just(it) }
            ?: gasPriceProvider.gasPriceSingle()
                .map { it }
                .doOnSuccess { gasPrice ->
                    val adjustedGasPrice = max(gasPrice.toLong(), minRecommendedGasPrice ?: 0)
                    recommendedGasPrice = adjustedGasPrice
                }

    init {
        if (initialGasPrice != null) {
            setGasPrice(initialGasPrice)
        } else {
            setRecommended()
        }
    }

    override fun setRecommended() {
        state = DataState.Loading
        emitState()

        disposable?.dispose()

        recommendedGasPriceSingle
            .subscribeIO({ recommended ->
                state = DataState.Success(
                    GasPriceInfo(
                        gasPrice = GasPrice.Legacy(recommended),
                        gasPriceDefault = GasPrice.Legacy(recommended),
                        default = true,
                        warnings = listOf(),
                        errors = listOf()
                    )
                )

                emitState()
            }, {
                state = DataState.Error(it)

                emitState()
            }).let {
                disposable = it
            }
    }

    fun setGasPrice(value: Long) {
        state = DataState.Loading
        emitState()
        disposable?.dispose()

        recommendedGasPriceSingle
            .subscribeIO({ recommended ->
                val warnings = mutableListOf<Warning>()
                val errors = mutableListOf<Throwable>()

                if (value < riskOfStuckBound.calculate(recommended)) {
                    warnings.add(FeeSettingsWarning.RiskOfGettingStuckLegacy)
                }

                if (value >= overpricingBound.calculate(recommended)) {
                    warnings.add(FeeSettingsWarning.Overpricing)
                }

                state = DataState.Success(
                    GasPriceInfo(
                        gasPrice = GasPrice.Legacy(value),
                        gasPriceDefault = GasPrice.Legacy(recommended),
                        default = false,
                        warnings = warnings,
                        errors = errors
                    )
                )
                emitState()
            }, {
                state = DataState.Error(it)
                emitState()
            }).let {
                disposable = it
            }
    }
}
