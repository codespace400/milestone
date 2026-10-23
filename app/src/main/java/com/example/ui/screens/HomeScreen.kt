package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalWithMilestones
import com.example.ui.MotivationalQuote
import com.example.ui.components.CategoryBadge
import com.example.ui.components.GoalCategories
import com.example.ui.components.LinearGradientProgressBar
import com.example.ui.components.ProgressRing
import com.example.ui.theme.AccentPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PurplePinkGradient
import com.example.ui.theme.SuccessGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    goals: List<GoalWithMilestones>,
    allActiveGoals: List<GoalWithMilestones>,
    selectedCategory: String?,
    quote: MotivationalQuote,
    showDailyQuotes: Boolean,
    onCategorySelected: (String?) -> Unit,
    onGoalClick: (Long) -> Unit,
    onAddGoalClick: () -> Unit,
    onRotateQuote: () -> Unit
) {
    // Toggle between Linear and Circular progress display style
    var useCircularDisplay by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddGoalClick,
                shape = CircleShape,
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("add_goal_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Goal")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "New Goal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header Section
            item {
                HomeHeader(
                    totalActive = allActiveGoals.size,
                    avgProgress = if (allActiveGoals.isEmpty()) 0 else (allActiveGoals.sumOf { it.progressPercentage } / allActiveGoals.size),
                    totalMilestones = allActiveGoals.sumOf { it.completedMilestones }
                )
            }

            // Motivational Quote Banner
            if (showDailyQuotes) {
                item {
                    QuoteBanner(
                        quote = quote,
                        onRotateQuote = onRotateQuote
                    )
                }
            }

            // Category Filter Chips
            item {
                CategoryFilterBar(
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected,
                    useCircular = useCircularDisplay,
                    onToggleProgressStyle = { useCircularDisplay = !useCircularDisplay }
                )
            }

            // Goal Cards
            if (goals.isEmpty()) {
                item {
                    EmptyGoalsState(onAddGoalClick = onAddGoalClick)
                }
            } else {
                items(goals, key = { it.goal.id }) { goalItem ->
                    GoalCard(
                        goalItem = goalItem,
                        useCircular = useCircularDisplay,
                        onClick = { onGoalClick(goalItem.goal.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    totalActive: Int,
    avgProgress: Int,
    totalMilestones: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Milestone",
                    style = MaterialTheme.typography.displayMedium,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Track your ambitions, one step at a time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Flag,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Stats Strip
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatColumn(
                    value = "$totalActive",
                    label = "Active Goals",
                    tint = PrimaryPurple
                )
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                )
                StatColumn(
                    value = "$avgProgress%",
                    label = "Avg Progress",
                    tint = AccentPink
                )
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                )
                StatColumn(
                    value = "$totalMilestones",
                    label = "Steps Done",
                    tint = SuccessGold
                )
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = tint
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuoteBanner(
    quote: MotivationalQuote,
    onRotateQuote: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onRotateQuote() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryPurple.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "\"${quote.quote}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                onClick = onRotateQuote,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "New Quote",
                    tint = PrimaryPurple.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilterBar(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    useCircular: Boolean,
    onToggleProgressStyle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Goals",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (useCircular) "Ring View" else "Bar View",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleProgressStyle() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null || selectedCategory == "All",
                    onClick = { onCategorySelected("All") },
                    label = { Text("All", fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryPurple,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(GoalCategories.ALL) { cat ->
                FilterChip(
                    selected = selectedCategory.equals(cat.name, ignoreCase = true),
                    onClick = { onCategorySelected(cat.name) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(cat.defaultEmoji)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(cat.name, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryPurple,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    goalItem: GoalWithMilestones,
    useCircular: Boolean = false,
    onClick: () -> Unit
) {
    val goal = goalItem.goal
    val daysLeft = goalItem.daysRemaining

    val countdownText = when {
        goal.isCompleted -> "Completed"
        daysLeft < 0 -> "Overdue by ${-daysLeft}d"
        daysLeft == 0L -> "Due today!"
        daysLeft == 1L -> "1 day left"
        else -> "$daysLeft days left"
    }

    val countdownColor = when {
        goal.isCompleted -> SuccessGold
        daysLeft < 0 -> Color(0xFFEF4444)
        daysLeft <= 3 -> AccentPink
        else -> PrimaryPurple
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 7.dp)
            .clickable(onClick = onClick)
            .testTag("goal_card_${goal.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Top Row: Category Tag, Emoji, Countdown Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = goal.emoji,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CategoryBadge(categoryName = goal.category)
                }

                // Deadline Countdown Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(countdownColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint = countdownColor,
                        modifier = Modifier.size(13.dp)
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

            Spacer(modifier = Modifier.height(12.dp))

            // Title & Description
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (goal.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Section
            if (useCircular) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${goalItem.completedMilestones} of ${goalItem.totalMilestones} milestones",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (goalItem.progressPercentage == 100) "Goal Achieved!" else "Keep pushing forward",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    ProgressRing(
                        progress = goalItem.progress,
                        size = 54.dp,
                        strokeWidth = 6.dp,
                        isCompleted = goalItem.progressPercentage == 100
                    )
                }
            } else {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${goalItem.completedMilestones}/${goalItem.totalMilestones} Milestones",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${goalItem.progressPercentage}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (goalItem.progressPercentage == 100) SuccessGold else PrimaryPurple
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearGradientProgressBar(
                        progress = goalItem.progress,
                        height = 10.dp,
                        isCompleted = goalItem.progressPercentage == 100
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Tap Prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyGoalsState(onAddGoalClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🎯", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No active goals in this category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Set an ambitious goal and break it down into actionable milestones.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            androidx.compose.material3.Button(
                onClick = onAddGoalClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create First Goal", fontWeight = FontWeight.Bold)
            }
        }
    }
}
