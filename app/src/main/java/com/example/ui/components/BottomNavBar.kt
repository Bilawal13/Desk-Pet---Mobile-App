package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppNavTab
import com.example.ui.theme.*

@Composable
fun BottomNavBar(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = CozyCardBackground.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = Primary.copy(alpha = 0.25f),
                    ambientColor = Primary.copy(alpha = 0.15f)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppNavTab.entries.forEach { tab ->
                    val isSelected = tab == currentTab
                    NavTabItem(
                        tab = tab,
                        isSelected = isSelected,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    tab: AppNavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(),
        label = "tab_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Primary else Color.Transparent,
        label = "tab_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else OnSurfaceVariant,
        label = "tab_content_color"
    )

    val icon: ImageVector = when (tab) {
        AppNavTab.ROOM -> if (isSelected) Icons.Filled.Cottage else Icons.Outlined.Cottage
        AppNavTab.FOCUS -> if (isSelected) Icons.Filled.Timer else Icons.Outlined.Timer
        AppNavTab.MISSIONS -> if (isSelected) Icons.Filled.TaskAlt else Icons.Outlined.TaskAlt
        AppNavTab.PETS -> if (isSelected) Icons.Filled.CrueltyFree else Icons.Outlined.CrueltyFree
        AppNavTab.SHOP -> if (isSelected) Icons.Filled.Storefront else Icons.Outlined.Storefront
    }

    Box(
        modifier = modifier
            .scale(scale)
            .height(52.dp)
            .background(bgColor, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("nav_tab_${tab.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tab.title,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = tab.title,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontSize = 10.sp
            )
        }
    }
}
