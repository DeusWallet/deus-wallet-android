package io.deus.wallet.core.storage

import androidx.room.*

@Dao
interface EvmNodeSyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(evmNodeRow: EvmNodeSyncRecord)

    @Update
    fun update(evmNodeRow: EvmNodeSyncRecord)

    @Query("SELECT * FROM EvmNodeSyncRecord WHERE chainId == :chainId")
    fun getAll(chainId: Int): List<EvmNodeSyncRecord>

    @Query("SELECT COUNT(*) FROM EvmNodeSyncRecord")
    fun getTotalCount(): Int

    @Query("DELETE FROM EvmNodeSyncRecord WHERE chainId == :chainId AND url == :url")
    fun delete(chainId: Int, url: String)

    @Query("DELETE FROM EvmNodeSyncRecord")
    fun deleteAll()

}
