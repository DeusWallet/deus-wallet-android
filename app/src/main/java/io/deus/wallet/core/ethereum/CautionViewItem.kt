package io.deus.wallet.core.ethereum

data class CautionViewItem(val title: String, val text: String, val type: Type) {
    enum class Type {
        Error, Warning
    }
}
