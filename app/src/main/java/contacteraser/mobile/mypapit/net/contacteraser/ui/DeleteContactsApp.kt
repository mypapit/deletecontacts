package contacteraser.mobile.mypapit.net.contacteraser.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import contacteraser.mobile.mypapit.net.contacteraser.R
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactFilter
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ThemeMode
import contacteraser.mobile.mypapit.net.contacteraser.ui.components.DeleteConfirmationDialog
import contacteraser.mobile.mypapit.net.contacteraser.ui.components.DeletionProgressDialog
import contacteraser.mobile.mypapit.net.contacteraser.ui.navigation.AppDestination
import contacteraser.mobile.mypapit.net.contacteraser.ui.screens.ContactsScreen
import contacteraser.mobile.mypapit.net.contacteraser.ui.screens.OverviewScreen
import contacteraser.mobile.mypapit.net.contacteraser.ui.screens.SettingsScreen
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ContactsViewModel
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ThemeViewModel
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.UiMessage

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun DeleteContactsApp(
    activity: Activity,
    contactsViewModel: ContactsViewModel,
    themeViewModel: ThemeViewModel,
    themeMode: ThemeMode
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by contactsViewModel.uiState.collectAsStateWithLifecycle()
    val windowSizeClass = calculateWindowSizeClass(activity)
    val useNavigationRail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AppDestination.OVERVIEW.route
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingWriteAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    fun readPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED

    fun writePermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val hasRead = results[Manifest.permission.READ_CONTACTS] ?: readPermissionGranted()
        val hasWrite = results[Manifest.permission.WRITE_CONTACTS] ?: writePermissionGranted()
        val permanentlyDenied = !hasRead && !ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_CONTACTS
        )
        contactsViewModel.onPermissionsUpdated(
            hasRead = hasRead,
            hasWrite = hasWrite,
            permanentlyDenied = permanentlyDenied
        )
        val action = pendingWriteAction
        pendingWriteAction = null
        if (hasRead && hasWrite && action != null) {
            action()
        } else if (!hasRead || (action != null && !hasWrite)) {
            contactsViewModel.showPermissionRequiredMessage()
        }
    }

    fun requestReadPermission() {
        pendingWriteAction = null
        permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
    }

    fun requestAllContactPermissions() {
        pendingWriteAction = null
        permissionLauncher.launch(
            arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
        )
    }

    fun runWithWritePermissions(action: () -> Unit) {
        if (readPermissionGranted() && writePermissionGranted()) {
            action()
        } else {
            pendingWriteAction = action
            permissionLauncher.launch(
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
            )
        }
    }

    fun navigateToDestination(destination: AppDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun inspectContacts(filter: ContactFilter) {
        contactsViewModel.setFilter(filter)
        navigateToDestination(AppDestination.CONTACTS)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val hasRead = readPermissionGranted()
                val hasWrite = writePermissionGranted()
                contactsViewModel.onPermissionsUpdated(
                    hasRead = hasRead,
                    hasWrite = hasWrite,
                    permanentlyDenied = false
                )
                if (hasRead) {
                    contactsViewModel.refreshAll()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage ?: return@LaunchedEffect
        val text = resolveUiMessage(context, message)
        snackbarHostState.showSnackbar(text)
        contactsViewModel.consumeSnackbar()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (!useNavigationRail) {
                NavigationBar {
                    AppDestination.entries.forEach { destination ->
                        val selected = currentRoute == destination.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateToDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(destination.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            if (useNavigationRail) {
                NavigationRail {
                    AppDestination.entries.forEach { destination ->
                        val selected = currentRoute == destination.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateToDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(destination.labelRes)) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = AppDestination.OVERVIEW.route,
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) {
                    composable(AppDestination.OVERVIEW.route) {
                        OverviewScreen(
                            uiState = uiState,
                            onRefresh = contactsViewModel::refreshAll,
                            onRequestReadPermission = ::requestReadPermission,
                            onOpenSettings = { openAppSettings(context) },
                            onInspectContacts = ::inspectContacts,
                            onDeleteWithoutPhone = {
                                runWithWritePermissions(contactsViewModel::requestDeleteWithoutPhone)
                            },
                            onDeleteAll = {
                                runWithWritePermissions(contactsViewModel::requestDeleteAll)
                            }
                        )
                    }
                    composable(AppDestination.CONTACTS.route) {
                        ContactsScreen(
                            uiState = uiState,
                            onRefresh = contactsViewModel::refreshAll,
                            onRequestReadPermission = ::requestReadPermission,
                            onOpenSettings = { openAppSettings(context) },
                            onFilterChange = contactsViewModel::setFilter,
                            onSearchQueryChange = contactsViewModel::setSearchQuery,
                            onToggleSelection = contactsViewModel::toggleSelection,
                            onSelectAllVisible = contactsViewModel::selectAllVisible,
                            onClearSelection = contactsViewModel::clearSelection,
                            onToggleExpanded = contactsViewModel::toggleExpanded,
                            onDeleteSelected = {
                                runWithWritePermissions(contactsViewModel::requestDeleteSelected)
                            },
                            onDeleteWithoutPhone = {
                                runWithWritePermissions(contactsViewModel::requestDeleteWithoutPhone)
                            }
                        )
                    }
                    composable(AppDestination.SETTINGS.route) {
                        SettingsScreen(
                            themeMode = themeMode,
                            uiState = uiState,
                            onThemeModeChange = themeViewModel::setThemeMode,
                            onRequestPermissions = ::requestAllContactPermissions,
                            onOpenAppSettings = { openAppSettings(context) }
                        )
                    }
                }
            }
        }
    }

    uiState.pendingConfirmation?.let { pendingRequest ->
        DeleteConfirmationDialog(
            request = pendingRequest,
            onDismiss = contactsViewModel::dismissDeleteConfirmation,
            onConfirm = contactsViewModel::confirmPendingDeletion
        )
    }

    uiState.deletionProgress?.let { progress ->
        DeletionProgressDialog(progress = progress)
    }
}

private fun resolveUiMessage(context: Context, message: UiMessage): String {
    return when (message) {
        is UiMessage.DeletedSelected -> context.resources.getQuantityString(
            R.plurals.deleted_selected_contacts,
            message.count,
            message.count
        )
        is UiMessage.DeletedWithoutPhone -> context.resources.getQuantityString(
            R.plurals.deleted_contacts_without_phone,
            message.count,
            message.count
        )
        is UiMessage.DeletedAll -> context.resources.getQuantityString(
            R.plurals.deleted_all_contacts,
            message.count,
            message.count
        )
        UiMessage.DeleteFailed -> context.getString(R.string.contact_delete_failed)
        UiMessage.PermissionRequired -> context.getString(R.string.permission_required)
    }
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
