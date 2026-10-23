package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.NoteAlt
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Goal
import com.example.data.model.GoalWithMilestones
import com.example.data.model.Milestone
import com.example.ui.components.CategoryBadge
import com.example.ui.components.ProgressRing
import com.example.ui.theme.AccentPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessGold
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalWithMilestones: GoalWithMilestones?,
    onBack: () -> Unit,
    onEditGoal: (Goal) -> Unit,
    onDeleteGoal: (Goal) -> Unit,
    onToggleMilestone: (Milestone) -> Unit,
    onDeleteMilestone: (Milestone) -> Unit,
    onAddMilestoneClick: () -> Unit,
    onToggleGoalCompleted: (Boolean) -> Unit,
    onSaveNotes: (String) -> Unit
) {
    if (goalWithMilestones == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Goal not found")
        }
        return
    }

    val goal = goalWithMilestones.goal
    val milestones = goalWithMilestones.milestones
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Notes editing state
    var notesText by remember(goal.notes) { mutableStateOf(goal.notes) }
    var isNotesDirty by remember(goal.notes) { mutableStateOf(false) }

    val daysLeft = goalWithMilestones.daysRemaining
    val targetDate = LocalDate.ofEpochDay(goal.targetDateEpochDay)
    val formattedTargetDate = targetDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

    val countdownText = when {
        goal.isCompleted -> "Goal Completed 🎉"
        daysLeft < 0 -> "Overdue by ${-daysLeft} days"
        daysLeft == 0L -> "Target Date is Today!"
        daysLeft == 1L -> "1 day remaining"
        else -> "$daysLeft days left"
    }

    val countdownColor = when {
        goal.isCompleted -> SuccessGold
        daysLeft < 0 -> Color(0xFFEF4444)
        daysLeft <= 3 -> AccentPink
        else -> PrimaryPurple
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Goal Details",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditGoal(goal) }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Goal",
                            tint = PrimaryPurple
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete Goal",
                            tint = Color(0xFFEF4444)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            // Large Top Hero with Progress Ring
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Category & Emoji Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryBadge(categoryName = goal.category)

                            // Countdown Tag
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(countdownColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Schedule,
                                    contentDescription = null,
                                    tint = countdownColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = countdownText,
                                    color = countdownColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Large Progress Ring
                        ProgressRing(
                            progress = goalWithMilestones.progress,
                            size = 170.dp,
                            strokeWidth = 16.dp,
                            isCompleted = goalWithMilestones.progressPercentage == 100
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Goal Title & Description
                        Text(
                            text = "${goal.emoji} ${goal.title}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        if (goal.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = goal.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Target Date Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Target: $formattedTargetDate",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Complete Goal Toggle Button
                        Button(
                            onClick = { onToggleGoalCompleted(!goal.isCompleted) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (goal.isCompleted) SuccessGold else PrimaryPurple
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("toggle_goal_complete_btn")
                        ) {
                            Icon(
                                imageVector = if (goal.isCompleted) Icons.Rounded.EmojiEvents else Icons.Rounded.CheckCircle,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (goal.isCompleted) "Completed! (Tap to Reopen)" else "Mark Goal as Completed",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Milestone Checklist Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Milestones Checklist",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${goalWithMilestones.completedMilestones} of ${goalWithMilestones.totalMilestones} finished",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // + Add Milestone button
                    Button(
                        onClick = onAddMilestoneClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple.copy(alpha = 0.12f),
                            contentColor = PrimaryPurple
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_milestone_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Milestone",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Milestone Checklist Items
            if (milestones.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🪜", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Break this goal into smaller milestones",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = onAddMilestoneClick,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add First Milestone")
                            }
                        }
                    }
                }
            } else {
                items(milestones, key = { it.id }) { milestone ->
                    MilestoneChecklistItem(
                        milestone = milestone,
                        onToggle = { onToggleMilestone(milestone) },
                        onDelete = { onDeleteMilestone(milestone) }
                    )
                }
            }

            // Notes / Reflections Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.NoteAlt,
                                    contentDescription = null,
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Notes & Reflections",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (isNotesDirty) {
                                Button(
                                    onClick = {
                                        onSaveNotes(notesText)
                                        isNotesDirty = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryPurple
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Save Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = notesText,
                            onValueChange = {
                                notesText = it
                                isNotesDirty = true
                            },
                            placeholder = {
                                Text("Record your thoughts, lessons learned, or strategy for this goal...")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .testTag("goal_notes_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Goal?") },
            text = { Text("Are you sure you want to delete \"${goal.title}\" and all of its milestones? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteGoal(goal)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MilestoneChecklistItem(
    milestone: Milestone,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val dueDate = LocalDate.ofEpochDay(milestone.dueDateEpochDay)
    val formattedDate = dueDate.format(DateTimeFormatter.ofPattern("MMM dd"))

    val isOverdue = !milestone.isDone && milestone.dueDateEpochDay < LocalDate.now().toEpochDay()
    val isDueToday = !milestone.isDone && milestone.dueDateEpochDay == LocalDate.now().toEpochDay()

    val boxBg by animateColorAsState(
        targetValue = if (milestone.isDone) PrimaryPurple else Color.Transparent,
        label = "boxBg"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable(onClick = onToggle)
            .testTag("milestone_item_${milestone.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isDone)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Custom Animated Checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(boxBg)
                    .border(
                        width = 2.dp,
                        color = if (milestone.isDone) PrimaryPurple else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (milestone.isDone) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Due Date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (milestone.isDone) FontWeight.Normal else FontWeight.SemiBold,
                    color = if (milestone.isDone)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (milestone.isDone) TextDecoration.LineThrough else null
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = when {
                            milestone.isDone -> MaterialTheme.colorScheme.onSurfaceVariant
                            isOverdue -> Color(0xFFEF4444)
                            isDueToday -> AccentPink
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when {
                            milestone.isDone -> "Completed"
                            isOverdue -> "Overdue ($formattedDate)"
                            isDueToday -> "Due today!"
                            else -> "Due $formattedDate"
                        },
                        fontSize = 11.sp,
                        fontWeight = if (isOverdue || isDueToday) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            milestone.isDone -> MaterialTheme.colorScheme.onSurfaceVariant
                            isOverdue -> Color(0xFFEF4444)
                            isDueToday -> AccentPink
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Delete action
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete milestone",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
