package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun TopHeaderBar(
    petLevel: Int,
    coins: Int,
    petDrawableResName: String = "mochi_featured",
    userAvatarResId: Int = R.drawable.avatar_user,
    onPetClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val petIconRes = com.example.data.model.getPetDrawableResByName(petDrawableResName)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App icon and Level pill (Clickable -> Opens Pet Overview Pop-up)
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onPetClick)
                .testTag("pet_header_avatar_button")
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = petIconRes),
                contentDescription = "Active Pet Avatar",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, PrimaryFixedDim, CircleShape),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .background(PrimaryFixed.copy(alpha = 0.85f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = OnPrimaryFixedVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "LVL $petLevel",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnPrimaryFixedVariant,
                    fontSize = 11.sp
                )
            }
        }

        // Coins pill & Profile avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Coins Pill
            Row(
                modifier = Modifier
                    .background(CozyCardBackground, CircleShape)
                    .border(1.dp, CozyCardBorder, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🪙",
                    fontSize = 14.sp
                )
                Text(
                    text = String.format("%,d", coins),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Profile Avatar (Clickable -> Opens Full User Panel)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(2.dp, Primary.copy(alpha = 0.35f), CircleShape)
                    .clickable(onClick = onProfileClick)
                    .testTag("profile_avatar_button")
            ) {
                Image(
                    painter = painterResource(id = userAvatarResId),
                    contentDescription = "User Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
