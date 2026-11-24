package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ShopProduct
import com.example.data.model.UserProfile
import com.example.ui.DeskPetViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CozyShopScreen(
    viewModel: DeskPetViewModel,
    profile: UserProfile,
    products: List<ShopProduct>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf("accessories") }
    val previewProduct by viewModel.previewProduct.collectAsState()

    val dailyDeal = products.find { it.isDailyDeal }
    val catalogProducts = remember(products, selectedCategory) {
        products.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Currency Bar HUD
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CozyCardBackground,
            border = BorderStroke(1.dp, CozyCardBorder),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Coins
                    Surface(shape = CircleShape, color = TertiaryFixed) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("🪙", fontSize = 16.sp)
                            Column {
                                Text("COINS", style = MaterialTheme.typography.labelSmall, color = OnTertiaryFixedVariant, fontSize = 9.sp)
                                Text(
                                    text = String.format("%,d", profile.coins),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnTertiaryFixed
                                )
                            }
                        }
                    }

                    // Star Gems
                    Surface(shape = CircleShape, color = PrimaryFixed) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("💎", fontSize = 16.sp)
                            Column {
                                Text("STAR GEMS", style = MaterialTheme.typography.labelSmall, color = OnPrimaryFixedVariant, fontSize = 9.sp)
                                Text(
                                    text = "${profile.starGems}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnPrimaryFixed
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.showToast("Top up: Complete focus marathons to earn free star gems!") },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(38.dp).testTag("top_up_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(16.dp))
                        Text("Top Up", style = MaterialTheme.typography.labelMedium, color = OnSecondaryContainer)
                    }
                }
            }
        }

        // Daily Deal Banner
        dailyDeal?.let { deal ->
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = CozyCardBackground,
                border = BorderStroke(1.dp, CozyCardBorder),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = Tertiary) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("🔥", fontSize = 12.sp)
                                Text(
                                    text = "DEAL OF THE DAY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Outline, modifier = Modifier.size(16.dp))
                            Text("Ends in 08:42:19", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.shop_beanbag),
                            contentDescription = deal.name,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurfaceContainerLowest),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(shape = CircleShape, color = TertiaryFixed) {
                                    Text(
                                        text = "${deal.discountPercent}% OFF",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnTertiaryFixed,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "${deal.originalPriceCoins} 🪙",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Outline,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }

                            Text(
                                text = deal.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = deal.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                maxLines = 2
                            )
                            Text(
                                text = "${deal.priceCoins} Coins",
                                style = MaterialTheme.typography.titleSmall,
                                color = Tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.previewProductInRoom(deal) },
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, Primary),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🛋️", fontSize = 14.sp)
                                Text("Try in Room", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.purchaseProduct(deal.id) },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("claim_daily_deal_btn")
                        ) {
                            Text("Claim ${deal.priceCoins} 🪙", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Category Filter Tabs
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Browse Catalog", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("120+ ITEMS", style = MaterialTheme.typography.labelSmall, color = Outline)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShopCategoryChip("Pets 🐾", selectedCategory == "pets") {
                    selectedCategory = "pets"
                }
                ShopCategoryChip("Accessories 🎀", selectedCategory == "accessories") {
                    selectedCategory = "accessories"
                }
                ShopCategoryChip("Furniture 🛋️", selectedCategory == "furniture") {
                    selectedCategory = "furniture"
                }
                ShopCategoryChip("Themes 🏡", selectedCategory == "themes") {
                    selectedCategory = "themes"
                }
                ShopCategoryChip("Pet Snacks 🍓", selectedCategory == "snacks") {
                    selectedCategory = "snacks"
                }
            }
        }

        // Products Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val nonDealProducts = catalogProducts.filter { !it.isDailyDeal }
            val chunked = nonDealProducts.chunked(2)
            chunked.forEach { rowProducts ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowProducts.forEach { product ->
                        ProductItemCard(
                            product = product,
                            userLevel = profile.level,
                            onTryOn = { viewModel.openProductPreview(product) },
                            onBuy = { viewModel.purchaseProduct(product.id) },
                            onEquip = {
                                val petId = product.id.removePrefix("shop_pet_")
                                viewModel.equipPet(petId)
                            },
                            onLockedClick = {
                                viewModel.showToast("Unlocks at Level ${product.requiredLevel}! Complete focus sessions to level up ⭐")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowProducts.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // Try-on / Purchase / Equip Bottom Sheet Modal
    previewProduct?.let { product ->
        val isPet = product.category == "pets"
        val petId = if (isPet) product.id.removePrefix("shop_pet_") else ""

        ModalBottomSheet(
            onDismissRequest = { viewModel.closeProductPreview() },
            containerColor = SurfaceContainerLowest,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fitting Room Hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) {
                    val previewRes = com.example.data.model.getShopDrawableResByName(product.drawableResName, product.category)

                    Image(
                        painter = painterResource(id = previewRes),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentScale = ContentScale.Fit
                    )

                    Surface(
                        shape = CircleShape,
                        color = SurfaceContainerLowest.copy(alpha = 0.9f),
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("🐾", fontSize = 12.sp)
                            Text(
                                text = if (isPet) "Pet Companion" else "Mochi Previewing",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = SecondaryFixed,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                    ) {
                        Text(
                            text = if (isPet) product.effectBonus else "Mood: Delighted ✨",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSecondaryFixedVariant,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${product.priceCoins} 🪙",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Tertiary
                    )
                }

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )

                // Balance indicator
                if (!product.isOwned) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Remaining after purchase:", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            Text(
                                text = "${(profile.coins - product.priceCoins).coerceAtLeast(0)} Coins",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!isPet && (product.category == "furniture" || product.category == "themes" || product.category == "accessories")) {
                    OutlinedButton(
                        onClick = {
                            viewModel.closeProductPreview()
                            viewModel.previewProductInRoom(product)
                        },
                        shape = CircleShape,
                        border = BorderStroke(1.5.dp, Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🛋️", fontSize = 16.sp)
                            Text(
                                text = "Try in Room (Live 3D Preview)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                }

                val isLevelLocked = profile.level < product.requiredLevel && !product.isOwned

                // Action Button (Locked, Equip, or Buy)
                if (isLevelLocked) {
                    Button(
                        onClick = { viewModel.showToast("Reach Level ${product.requiredLevel} to unlock this item! 🔒") },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Outline)
                            Text(
                                text = "Locked • Requires Level ${product.requiredLevel}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Outline
                            )
                        }
                    }
                } else if (isPet && product.isOwned) {
                    Button(
                        onClick = {
                            viewModel.equipPet(petId)
                            viewModel.closeProductPreview()
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("equip_modal_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Pets, contentDescription = null)
                            Text("Equip as Active Desk Pet", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.purchaseProduct(product.id) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_purchase_modal_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null)
                            Text(
                                text = if (isPet) "Adopt Companion (${product.priceCoins} 🪙)" else "Confirm Purchase (${product.priceCoins} 🪙)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                Text(
                    text = "Instant delivery to your Room Habitat & Pet Sanctuary",
                    style = MaterialTheme.typography.labelSmall,
                    color = Outline,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ShopCategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) Primary else SurfaceContainerLow,
        shadowElevation = if (isSelected) 3.dp else 0.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Color.White else OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ProductItemCard(
    product: ShopProduct,
    userLevel: Int = 1,
    onTryOn: () -> Unit,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
    onLockedClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isPet = product.category == "pets"
    val petId = if (isPet) product.id.removePrefix("shop_pet_") else ""
    val isLocked = userLevel < product.requiredLevel && !product.isOwned

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CozyCardBackground,
        border = BorderStroke(1.2.dp, CozyCardBorder),
        shadowElevation = 3.dp,
        modifier = modifier.height(312.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper section: Product Preview Image & Tag
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(134.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, CozyCardBorder, RoundedCornerShape(16.dp))
                        .clickable(onClick = if (isLocked) onLockedClick else onTryOn),
                    contentAlignment = Alignment.Center
                ) {
                    val resId = com.example.data.model.getShopDrawableResByName(product.drawableResName, product.category)

                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isPet) 6.dp else 10.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Tag badge
                    if (isLocked) {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceContainerHighest.copy(alpha = 0.95f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Outline, modifier = Modifier.size(11.dp))
                                Text(
                                    text = "Lvl ${product.requiredLevel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Outline,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    } else if (product.tagText.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = if (product.isOwned) SecondaryFixed else PrimaryFixed,
                            modifier = Modifier
                                .align(if (product.isOwned) Alignment.TopEnd else Alignment.TopStart)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = product.tagText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (product.isOwned) OnSecondaryFixedVariant else OnPrimaryFixedVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Text Description Block (symmetric vertical height)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = product.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = "${product.priceCoins} Coins",
                        style = MaterialTheme.typography.labelMedium,
                        color = Tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Lower Action Row (fixed 36.dp height for all states)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Button(
                        onClick = onLockedClick,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                        modifier = Modifier.fillMaxSize().testTag("locked_${product.id}"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Outline, modifier = Modifier.size(13.dp))
                            Text("Lvl ${product.requiredLevel} Req", style = MaterialTheme.typography.labelSmall, color = Outline, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else if (isPet) {
                    if (product.isOwned) {
                        if (product.isEquipped) {
                            Surface(
                                shape = CircleShape,
                                color = SecondaryFixed,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = OnSecondaryFixedVariant, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Equipped (Active)", style = MaterialTheme.typography.labelSmall, color = OnSecondaryFixedVariant, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Button(
                                onClick = onEquip,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                modifier = Modifier.fillMaxSize().testTag("equip_${product.id}"),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("Equip Pet", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = onBuy,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier.fillMaxSize().testTag("adopt_${product.id}"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🐾", fontSize = 12.sp)
                                Text("Adopt ${product.priceCoins} 🪙", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                } else if (product.isOwned) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceContainerHighest,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Owned", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }
                    }
                } else if (product.category == "accessories") {
                    Button(
                        onClick = onTryOn,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.fillMaxSize().testTag("try_on_${product.id}"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("✨", fontSize = 12.sp)
                            Text("Try on Mochi", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                } else {
                    Button(
                        onClick = onBuy,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                        modifier = Modifier.fillMaxSize().testTag("buy_${product.id}"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
                            Text("Buy Now", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}
