import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_59_60 : Migration(59, 60) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `EvmNodeSyncRecord` (`chainId` INTEGER NOT NULL, `url` TEXT NOT NULL, `height` INTEGER NOT NULL, `latency` INTEGER NOT NULL,  PRIMARY KEY(`chainId`, `url`))")
    }
}
