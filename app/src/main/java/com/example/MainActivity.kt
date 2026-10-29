package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import com.example.ui.AppNavTab
import com.example.ui.DeskPetViewModel
import com.example.ui.DeskPetViewModelFactory
import com.example.ui.auth.AuthViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.DailyMoodCheckInSheet
import com.example.ui.components.PetOverviewSheet
import com.example.ui.components.ProfileSheet
import com.example.ui.components.ToastNotification
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.*
import com.example.ui.theme.Background
import com.example.ui.theme.DeskPetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            app.rive.runtime.kotlin.core.Rive.init(applicationContext)
        } catch (e: Throwable) {
            android.util.Log.w("DeskPet", "Rive runtime init: ${e.message}")
        }
        enableEdgeToEdge()
        setContent {
            DeskPetTheme {
                DeskPetRoot()
            }
        }
    }
}

@Composable
fun DeskPetRoot(
    authViewModel: AuthViewModel = viewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = isLoggedIn,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "auth_screen_switch"
    ) { loggedIn ->
        if (!loggedIn) {
            LoginScreen(authViewModel = authViewModel)
        } else {
            val userKey = currentUser?.id?.ifBlank { currentUser?.email } ?: "guest"
            key(userKey) {
                val context = LocalContext.current.applicationContext as Application
                val userDeskPetViewModel: DeskPetViewModel = viewModel(
                    key = "desk_pet_vm_$userKey",
                    factory = DeskPetViewModelFactory(context, currentUser)
                )
                DeskPetApp(
                    viewModel = userDeskPetViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun DeskPetApp(
    viewModel: DeskPetViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activePet by viewModel.activePet.collectAsStateWithLifecycle()
    val allPets by viewModel.allPets.collectAsStateWithLifecycle()
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val missions by viewModel.allMissions.collectAsStateWithLifecycle()
    val weeklyQuest by viewModel.weeklyQuest.collectAsStateWithLifecycle()
    val shopProducts by viewModel.shopProducts.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val showProfileSheet by viewModel.showProfileSheet.collectAsStateWithLifecycle()
    val showPetOverviewSheet by viewModel.showPetOverviewSheet.collectAsStateWithLifecycle()
    val showMoodCheckInDialog by viewModel.showMoodCheckInDialog.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val authProvider by authViewModel.authProvider.collectAsStateWithLifecycle()

    // BackHandler: Return to Room if in another tab
    BackHandler(enabled = currentTab != AppNavTab.ROOM) {
        viewModel.selectTab(AppNavTab.ROOM)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Background,
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        topBar = {
            TopHeaderBar(
                petLevel = activePet.level,
                coins = profile.coins,
                petDrawableResName = activePet.drawableResName,
                userAvatarResId = profile.getAvatarDrawableResId(),
                onPetClick = { viewModel.setPetOverviewSheetVisible(true) },
                onProfileClick = { viewModel.setProfileSheetVisible(true) }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { targetTab ->
                when (targetTab) {
                    AppNavTab.ROOM -> RoomScreen(
                        viewModel = viewModel,
                        profile = profile,
                        pet = activePet
                    )
                    AppNavTab.FOCUS -> FocusTimerScreen(
                        viewModel = viewModel,
                        profile = profile,
                        tasks = tasks
                    )
                    AppNavTab.MISSIONS -> DailyMissionsScreen(
                        viewModel = viewModel,
                        missions = missions,
                        weeklyQuest = weeklyQuest
                    )
                    AppNavTab.PETS -> PetSanctuaryScreen(
                        viewModel = viewModel,
                        activePet = activePet,
                        allPets = allPets
                    )
                    AppNavTab.SHOP -> CozyShopScreen(
                        viewModel = viewModel,
                        profile = profile,
                        products = shopProducts
                    )
                }
            }

            // Toast Alert Overlay
            ToastNotification(message = toastMessage)

            // Pet Overview Pop-up Sheet (Triggered by Navbar Pet Avatar Icon)
            if (showPetOverviewSheet) {
                PetOverviewSheet(
                    pet = activePet,
                    onFeed = { viewModel.feedPet() },
                    onPet = { viewModel.petCompanion() },
                    onPlay = { viewModel.playWithPet() },
                    onOpenSanctuary = {
                        viewModel.setPetOverviewSheetVisible(false)
                        viewModel.selectTab(AppNavTab.PETS)
                    },
                    onDismiss = { viewModel.setPetOverviewSheetVisible(false) }
                )
            }

            // User Profile Sheet & Customization Panel (Triggered by Profile Avatar Icon)
            if (showProfileSheet) {
                ProfileSheet(
                    profile = profile,
                    authProvider = authProvider,
                    email = currentUser?.email ?: "",
                    onUpdateProfile = { newName, newAvatar ->
                        viewModel.updateUserProfile(newName, newAvatar)
                    },
                    onSignOut = {
                        viewModel.setProfileSheetVisible(false)
                        authViewModel.signOut()
                    },
                    onDismiss = { viewModel.setProfileSheetVisible(false) }
                )
            }

            // Daily Mood Check-In Bottom Sheet
            if (showMoodCheckInDialog) {
                DailyMoodCheckInSheet(
                    onRecordMood = { moodKey, note ->
                        viewModel.recordMood(moodKey, note)
                    },
                    onDismiss = { viewModel.setMoodCheckInDialogVisible(false) }
                )
            }
        }
    }
}
