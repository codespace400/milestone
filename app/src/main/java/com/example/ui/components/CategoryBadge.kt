package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CategoryCareer
import com.example.ui.theme.CategoryCreative
import com.example.ui.theme.CategoryFinance
import com.example.ui.theme.CategoryFitness
import com.example.ui.theme.CategoryHealth
import com.example.ui.theme.CategoryLearning
import com.example.ui.theme.CategoryPersonal

data class CategoryMeta(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val defaultEmoji: String
)

object GoalCategories {
    val ALL = listOf(
        CategoryMeta("Career", Icons.Rounded.Work, CategoryCareer, "🚀"),
        CategoryMeta("Fitness", Icons.Rounded.Bolt, CategoryFitness, "🏃‍♂️"),
        CategoryMeta("Learning", Icons.Rounded.MenuBook, CategoryLearning, "📚"),
        CategoryMeta("Health", Icons.Rounded.Favorite, CategoryHealth, "❤️"),
        CategoryMeta("Finance", Icons.Rounded.Savings, CategoryFinance, "💰"),
        CategoryMeta("Creative", Icons.Rounded.Palette, CategoryCreative, "🎨"),
        CategoryMeta("Personal", Icons.Rounded.AutoAwesome, CategoryPersonal, "✨")
    )

    fun getMeta(name: String): CategoryMeta {
        return ALL.find { it.name.equals(name, ignoreCase = true) }
            ?: CategoryMeta(name, Icons.Rounded.AccountCircle, CategoryPersonal, "🎯")
    }
}

@Composable
fun CategoryBadge(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    val meta = GoalCategories.getMeta(categoryName)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(meta.color.copy(alpha = 0.12f))
            .border(1.dp, meta.color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = meta.icon,
            contentDescription = null,
            tint = meta.color,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = meta.name,
            color = meta.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
