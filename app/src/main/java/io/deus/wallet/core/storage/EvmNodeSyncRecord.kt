package io.deus.wallet.core.storage

import androidx.room.Entity

@Entity(primaryKeys = ["chainId", "url"])
data class EvmNodeSyncRecord(
    val chainId: Int,
    val url: String,
    val height: Int,
    val latency: Int,
) {

    override fun equals(other: Any?): Boolean {
        if (other is EvmNodeSyncRecord) {
            return chainId == other.chainId && url == other.url
        }

        return false
    }

    override fun hashCode(): Int {
        return chainId.hashCode() + url.hashCode()
    }

}
