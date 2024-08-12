package io.deus.wallet.modules.walletconnect.list.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.managers.FaqManager
import io.deus.wallet.core.utils.ModuleField
import io.deus.wallet.modules.contacts.screen.ConfirmationBottomSheet
import io.deus.wallet.modules.evmfee.ButtonsGroupWithShade
import io.deus.wallet.modules.qrscanner.QRScannerActivity
import io.deus.wallet.modules.swap.settings.Caution
import io.deus.wallet.modules.walletconnect.list.WalletConnectListModule
import io.deus.wallet.modules.walletconnect.list.WalletConnectListUiState
import io.deus.wallet.modules.walletconnect.list.WalletConnectListViewModel
import io.deus.wallet.modules.walletconnect.list.WalletConnectListViewModel.ConnectionResult
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.DisposableLifecycleCallbacks
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.ButtonPrimaryPurple
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.ListEmptyView
import io.deus.wallet.ui.compose.components.MenuItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WCSessionsScreen(
    navController: NavController,
    deepLinkUri: String?
) {
    val context = LocalContext.current
    val invalidUrlBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    val viewModel = viewModel<WalletConnectListViewModel>(
        factory = WalletConnectListModule.Factory()
    )
    val qrScannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.setConnectionUri(result.data?.getStringExtra(ModuleField.SCAN_ADDRESS) ?: "")
        }
    }

    val uiState by viewModel.uiState.collectAsState(initial = WalletConnectListUiState())

    when (viewModel.connectionResult) {
        ConnectionResult.Error -> {
            LaunchedEffect(viewModel.connectionResult) {
                coroutineScope.launch {
                    delay(300)
                    invalidUrlBottomSheetState.show()
                }
            }
            viewModel.onRouteHandled()
        }

        else -> Unit
    }

    LaunchedEffect(Unit) {
        if (deepLinkUri != null) {
            viewModel.setConnectionUri(deepLinkUri)
        }
    }

    DisposableLifecycleCallbacks(
        onResume = {
            viewModel.refreshList()
        }
    )

    ModalBottomSheetLayout(
        sheetState = invalidUrlBottomSheetState,
        sheetBackgroundColor = ComposeAppTheme.colors.transparent,
        sheetContent = {
            ConfirmationBottomSheet(
                title = stringResource(R.string.WalletConnect_Title),
                text = stringResource(R.string.WalletConnect_Error_InvalidUrl),
                iconPainter = painterResource(R.drawable.ic_wallet_connect_24),
                iconTint = ColorFilter.tint(ComposeAppTheme.colors.jacob),
                confirmText = stringResource(R.string.Button_TryAgain),
                cautionType = Caution.Type.Warning,
                cancelText = stringResource(R.string.Button_Cancel),
                onConfirm = {
                    coroutineScope.launch {
                        invalidUrlBottomSheetState.hide()
                        qrScannerLauncher.launch(QRScannerActivity.getScanQrIntent(context, true))
                    }
                },
                onClose = {
                    coroutineScope.launch { invalidUrlBottomSheetState.hide() }
                }
            )
        }
    ) {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                AppBar(
                    title = stringResource(R.string.WalletConnect_Title),
                    navigationIcon = {
                        HsBackButton(onClick = { navController.popBackStack() })
                    },
                    menuItems = listOf(
                        MenuItem(
                            title = TranslatableString.ResString(R.string.Info_Title),
                            icon = R.drawable.ic_info_24,
                            onClick = {
                                FaqManager.showFaqPage(navController, FaqManager.faqPathDefiRisks)
                            }
                        )
                    )
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.weight(1f)) {
                    if (uiState.sessionViewItems.isEmpty() && uiState.pairingsNumber == 0) {
                        ListEmptyView(
                            text = stringResource(R.string.WalletConnect_NoConnection),
                            icon = R.drawable.ic_wallet_connet_48
                        )
                    } else {
                        WCSessionList(
                            viewModel,
                            navController
                        )
                    }
                }
                ButtonsGroupWithShade {
                    ButtonPrimaryPurple(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .fillMaxWidth(),
                        title = stringResource(R.string.WalletConnect_NewConnect),
                        onClick = { qrScannerLauncher.launch(QRScannerActivity.getScanQrIntent(context, true)) }
                    )
                }
            }
        }
    }
}
