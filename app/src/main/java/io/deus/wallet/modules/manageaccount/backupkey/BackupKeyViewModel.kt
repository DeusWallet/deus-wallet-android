package io.deus.wallet.modules.manageaccount.backupkey

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.deus.wallet.entities.Account
import io.deus.wallet.entities.AccountType
import io.deus.wallet.modules.manageaccount.recoveryphrase.RecoveryPhraseModule

class BackupKeyViewModel(val account: Account) : ViewModel() {

    var passphrase by mutableStateOf("")
        private set

    var wordsNumbered by mutableStateOf<List<RecoveryPhraseModule.WordNumbered>>(listOf())
        private set

    init {
        if (account.type is AccountType.Mnemonic) {
            wordsNumbered = account.type.words.mapIndexed { index, word ->
                RecoveryPhraseModule.WordNumbered(word, index + 1)
            }
            passphrase = account.type.passphrase
        }
    }
}
