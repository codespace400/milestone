package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MilestoneViewModel
import com.example.ui.components.ConfettiCelebration
import com.example.ui.screens.AddEditGoalScreen
import com.example.ui.screens.AddMilestoneDialog
import com.example.ui.screens.CompletedGoalsScreen
import com.example.ui.screens.GoalDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MilestoneTheme
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessGold

class MainActivity : ComponentActivity() {

    private val viewModel: MilestoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            MilestoneTheme(darkTheme = isDark) {
                MilestoneApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MilestoneApp(viewModel: MilestoneViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedGoalId by viewModel.selectedGoalId.collectAsState()
    val showAddGoalScreen by viewModel.showAddGoalScreen.collectAsState()
    val editingGoal by viewModel.editingGoal.collectAsState()
    val addMilestoneGoalId by viewModel.addMilestoneForGoalId.collectAsState()
    val confettiTrigger by viewModel.confettiTrigger.collectAsState()

    val filteredActiveGoals by viewModel.filteredActiveGoals.collectAsState()
    val allActiveGoals by viewModel.activeGoals.collectAsState()
    val completedGoals by viewModel.completedGoals.collectAsState()
    val allGoals by viewModel.allGoals.collectAsState()
    val selectedGoalWithMilestones by viewModel.selectedGoalWithMilestones.collectAsState()

    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val currentQuote by viewModel.currentQuote.collectAsState()
    val reminderFrequency by viewModel.reminderFrequency.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val showDailyQuotes by viewModel.showDailyQuotes.collectAsState()

    // Handle Android system back gesture smoothly
    BackHandler(enabled = showAddGoalScreen || selectedGoalId != null || currentScreen != AppScreen.HOME) {
        when {
            showAddGoalScreen -> viewModel.closeAddGoal()
            selectedGoalId != null -> viewModel.closeGoalDetail()
            currentScreen != AppScreen.HOME -> viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // Screen 2: Add/Edit Goal Full Screen
            showAddGoalScreen -> {
                AddEditGoalScreen(
                    existingGoal = editingGoal,
                    onBack = { viewModel.closeAddGoal() },
                    onSave = { id, title, desc, cat, emoji, targetDate, initialMilestones ->
                        viewModel.saveGoal(id, title, desc, cat, emoji, targetDate, initialMilestones)
                    }
                )
            }

            // Screen 3: Goal Detail Screen
            selectedGoalId != null -> {
                val currentGoalId = selectedGoalId!!
                GoalDetailScreen(
                    goalWithMilestones = selectedGoalWithMilestones,
                    onBack = { viewModel.closeGoalDetail() },
                    onEditGoal = { goal ->
                        viewModel.openAddGoal(goal)
                    },
                    onDeleteGoal = { goal ->
                        viewModel.deleteGoal(goal)
                    },
                    onToggleMilestone = { milestone ->
                        viewModel.toggleMilestone(milestone, currentGoalId)
                    },
                    onDeleteMilestone = { milestone ->
                        viewModel.deleteMilestone(milestone)
                    },
                    onAddMilestoneClick = {
                        viewModel.openAddMilestone(currentGoalId)
                    },
                    onToggleGoalCompleted = { isCompleted ->
                        viewModel.toggleGoalCompleted(currentGoalId, isCompleted)
                    },
                    onSaveNotes = { notes ->
                        viewModel.updateGoalNotes(currentGoalId, notes)
                    }
                )
            }

            // Main App with Bottom Navigation (Screens 1, 5, 6)
            else -> {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("main_bottom_nav"),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.HOME,
                                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Flag,
                                        contentDescription = "Goals"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Goals",
                                        fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    indicatorColor = PrimaryPurple,
                                    selectedTextColor = PrimaryPurple
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.COMPLETED,
                                onClick = { viewModel.navigateTo(AppScreen.COMPLETED) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.EmojiEvents,
                                        contentDescription = "Completed"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Completed",
                                        fontWeight = if (currentScreen == AppScreen.COMPLETED) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    indicatorColor = SuccessGold,
                                    selectedTextColor = SuccessGold
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreen == AppScreen.SETTINGS,
                                onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = "Settings"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Settings",
                                        fontWeight = if (currentScreen == AppScreen.SETTINGS) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    indicatorColor = PrimaryPurple,
                                    selectedTextColor = PrimaryPurple
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            AppScreen.HOME -> {
                                HomeScreen(
                                    goals = filteredActiveGoals,
                                    allActiveGoals = allActiveGoals,
                                    selectedCategory = selectedCategory,
                                    quote = currentQuote,
                                    showDailyQuotes = showDailyQuotes,
                                    onCategorySelected = { viewModel.setCategoryFilter(it) },
                                    onGoalClick = { goalId -> viewModel.openGoalDetail(goalId) },
                                    onAddGoalClick = { viewModel.openAddGoal() },
                                    onRotateQuote = { viewModel.rotateQuote() }
                                )
                            }
                            AppScreen.COMPLETED -> {
                                CompletedGoalsScreen(
                                    completedGoals = completedGoals,
                                    onGoalClick = { goalId -> viewModel.openGoalDetail(goalId) },
                                    onTriggerCelebration = {
                                        viewModel.toggleGoalCompleted(0L, false)
                                    }
                                )
                            }
                            AppScreen.SETTINGS -> {
                                SettingsScreen(
                                    reminderFrequency = reminderFrequency,
                                    themeMode = themeMode,
                                    showDailyQuotes = showDailyQuotes,
                                    onReminderFrequencyChange = { viewModel.setReminderFrequency(it) },
                                    onThemeModeChange = { viewModel.setThemeMode(it) },
                                    onToggleShowQuotes = { viewModel.setShowDailyQuotes(it) },
                                    onResetSampleData = { viewModel.resetWithSampleData() },
                                    onClearAllData = { viewModel.clearAllData() }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Screen 4: Add Milestone Dialog
        if (addMilestoneGoalId != null) {
            AddMilestoneDialog(
                initialGoalId = addMilestoneGoalId,
                availableGoals = allGoals,
                onDismiss = { viewModel.closeAddMilestone() },
                onSave = { goalId, title, dueDate ->
                    viewModel.addMilestone(goalId, title, dueDate)
                }
            )
        }

        // Celebratory Confetti Burst Overlay
        ConfettiCelebration(
            isActive = confettiTrigger,
            onFinished = { viewModel.dismissConfetti() }
        )
    }
}
