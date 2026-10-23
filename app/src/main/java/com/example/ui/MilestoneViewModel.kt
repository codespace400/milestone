package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MilestoneDatabase
import com.example.data.model.Goal
import com.example.data.model.GoalWithMilestones
import com.example.data.model.Milestone
import com.example.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AppScreen {
    HOME,
    COMPLETED,
    SETTINGS
}

data class MotivationalQuote(
    val quote: String,
    val author: String
)

class MilestoneViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GoalRepository

    init {
        val database = MilestoneDatabase.getDatabase(application, viewModelScope)
        repository = GoalRepository(database.goalDao(), database.milestoneDao())
    }

    // Navigation and UI states
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedGoalId = MutableStateFlow<Long?>(null)
    val selectedGoalId: StateFlow<Long?> = _selectedGoalId.asStateFlow()

    private val _editingGoal = MutableStateFlow<Goal?>(null)
    val editingGoal: StateFlow<Goal?> = _editingGoal.asStateFlow()

    private val _showAddGoalScreen = MutableStateFlow(false)
    val showAddGoalScreen: StateFlow<Boolean> = _showAddGoalScreen.asStateFlow()

    private val _addMilestoneForGoalId = MutableStateFlow<Long?>(null)
    val addMilestoneForGoalId: StateFlow<Long?> = _addMilestoneForGoalId.asStateFlow()

    private val _confettiTrigger = MutableStateFlow(false)
    val confettiTrigger: StateFlow<Boolean> = _confettiTrigger.asStateFlow()

    // Filters and Settings
    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _reminderFrequency = MutableStateFlow("Daily at 8:00 AM")
    val reminderFrequency: StateFlow<String> = _reminderFrequency.asStateFlow()

    private val _themeMode = MutableStateFlow("system") // system, light, dark
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _showDailyQuotes = MutableStateFlow(true)
    val showDailyQuotes: StateFlow<Boolean> = _showDailyQuotes.asStateFlow()

    private val quotes = listOf(
        MotivationalQuote("Small disciplines repeated with consistency every day lead to great achievements gained slowly over time.", "John C. Maxwell"),
        MotivationalQuote("You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"),
        MotivationalQuote("What you get by achieving your goals is not as important as what you become by achieving your goals.", "Zig Ziglar"),
        MotivationalQuote("The secret of getting ahead is getting started. The secret of getting started is breaking your complex overwhelming tasks into small manageable tasks.", "Mark Twain"),
        MotivationalQuote("A goal is a dream with a deadline.", "Napoleon Hill"),
        MotivationalQuote("Energy flows where attention goes. Focus on your milestones today.", "Tony Robbins")
    )

    private val _currentQuote = MutableStateFlow(quotes.first())
    val currentQuote: StateFlow<MotivationalQuote> = _currentQuote.asStateFlow()

    // All active goals
    val activeGoals: StateFlow<List<GoalWithMilestones>> = repository.activeGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered active goals
    val filteredActiveGoals: StateFlow<List<GoalWithMilestones>> = combine(
        repository.activeGoals,
        _selectedCategory
    ) { goals, category ->
        if (category == null || category == "All") {
            goals
        } else {
            goals.filter { it.goal.category.equals(category, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Completed goals
    val completedGoals: StateFlow<List<GoalWithMilestones>> = repository.completedGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All goals for dropdowns
    val allGoals: StateFlow<List<GoalWithMilestones>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently viewed goal details
    val selectedGoalWithMilestones: StateFlow<GoalWithMilestones?> = combine(
        repository.allGoals,
        _selectedGoalId
    ) { goals, goalId ->
        if (goalId == null) null
        else goals.find { it.goal.id == goalId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        _selectedGoalId.value = null
        _showAddGoalScreen.value = false
    }

    fun openGoalDetail(goalId: Long) {
        _selectedGoalId.value = goalId
    }

    fun closeGoalDetail() {
        _selectedGoalId.value = null
    }

    fun openAddGoal(existingGoal: Goal? = null) {
        _editingGoal.value = existingGoal
        _showAddGoalScreen.value = true
    }

    fun closeAddGoal() {
        _editingGoal.value = null
        _showAddGoalScreen.value = false
    }

    fun openAddMilestone(goalId: Long) {
        _addMilestoneForGoalId.value = goalId
    }

    fun closeAddMilestone() {
        _addMilestoneForGoalId.value = null
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun rotateQuote() {
        val nextIdx = (quotes.indexOf(_currentQuote.value) + 1) % quotes.size
        _currentQuote.value = quotes[nextIdx]
    }

    fun saveGoal(
        id: Long = 0,
        title: String,
        description: String,
        category: String,
        emoji: String,
        targetDateEpochDay: Long,
        initialMilestones: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                // New Goal
                val newGoal = Goal(
                    title = title,
                    description = description,
                    category = category,
                    emoji = emoji,
                    targetDateEpochDay = targetDateEpochDay
                )
                val newGoalId = repository.insertGoal(newGoal)

                if (initialMilestones.isNotEmpty()) {
                    initialMilestones.filter { it.isNotBlank() }.forEachIndexed { idx, milestoneTitle ->
                        repository.insertMilestone(
                            Milestone(
                                goalId = newGoalId,
                                title = milestoneTitle.trim(),
                                dueDateEpochDay = targetDateEpochDay,
                                orderIndex = idx
                            )
                        )
                    }
                }
            } else {
                // Update Goal
                val existing = _editingGoal.value
                val updated = Goal(
                    id = id,
                    title = title,
                    description = description,
                    category = category,
                    emoji = emoji,
                    targetDateEpochDay = targetDateEpochDay,
                    isCompleted = existing?.isCompleted ?: false,
                    completedDateEpochDay = existing?.completedDateEpochDay,
                    notes = existing?.notes ?: "",
                    createdAt = existing?.createdAt ?: System.currentTimeMillis()
                )
                repository.updateGoal(updated)
            }
            closeAddGoal()
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            if (_selectedGoalId.value == goal.id) {
                closeGoalDetail()
            }
        }
    }

    fun toggleMilestone(milestone: Milestone, goalId: Long) {
        viewModelScope.launch {
            val newDone = !milestone.isDone
            repository.toggleMilestone(milestone.id, newDone, goalId)

            // Check if this action triggers celebration
            if (newDone) {
                val goalWithM = repository.allGoals
                // Check if all milestones are completed or this was completed
                _confettiTrigger.value = true
            }
        }
    }

    fun addMilestone(goalId: Long, title: String, dueDateEpochDay: Long) {
        viewModelScope.launch {
            repository.insertMilestone(
                Milestone(
                    goalId = goalId,
                    title = title.trim(),
                    dueDateEpochDay = dueDateEpochDay
                )
            )
            closeAddMilestone()
        }
    }

    fun deleteMilestone(milestone: Milestone) {
        viewModelScope.launch {
            repository.deleteMilestone(milestone)
        }
    }

    fun toggleGoalCompleted(goalId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setGoalCompleted(goalId, isCompleted)
            if (isCompleted) {
                _confettiTrigger.value = true
            }
        }
    }

    fun updateGoalNotes(goalId: Long, notes: String) {
        viewModelScope.launch {
            repository.updateGoalNotes(goalId, notes)
        }
    }

    fun dismissConfetti() {
        _confettiTrigger.value = false
    }

    fun setReminderFrequency(freq: String) {
        _reminderFrequency.value = freq
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    fun setShowDailyQuotes(show: Boolean) {
        _showDailyQuotes.value = show
    }

    fun resetWithSampleData() {
        viewModelScope.launch {
            repository.resetWithSampleData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedGoalId.value = null
        }
    }
}
