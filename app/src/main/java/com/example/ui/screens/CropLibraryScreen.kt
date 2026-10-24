package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AgriCareDatabase
import com.example.data.local.GuideArticleDao
import com.example.data.local.GuideArticleEntity
import com.example.ui.components.AgriCareCard
import com.example.ui.components.AgriCareEmptyState
import com.example.ui.components.AgriCarePill
import com.example.ui.components.AgriCareTopBar
import com.example.ui.components.NavigationIconType
import com.example.ui.theme.ForestGreenDeep
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LeafGreenAccent
import com.example.ui.theme.MintLight
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SageSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmOffWhite

@Composable
fun CropLibraryScreen(
    guideArticleDao: GuideArticleDao,
    database: AgriCareDatabase,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Ensure starter articles are seeded if empty
    LaunchedEffect(Unit) {
        database.preseedIfEmpty()
    }

    val allArticles by guideArticleDao.getAllArticles().collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCropFilter by remember { mutableStateOf("All") }
    var selectedArticleForDetail by remember { mutableStateOf<GuideArticleEntity?>(null) }

    val cropFilters = listOf("All", "Rice", "Potato", "Tomato", "Eggplant", "Chili")

    val filteredArticles = allArticles.filter { article ->
        val matchesCrop = selectedCropFilter == "All" || article.crop.equals(selectedCropFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                article.title.contains(searchQuery, ignoreCase = true) ||
                article.crop.contains(searchQuery, ignoreCase = true) ||
                article.symptoms.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCrop && matchesSearch
    }

    if (selectedArticleForDetail != null) {
        GuideDetailScreen(
            article = selectedArticleForDetail!!,
            onBack = { selectedArticleForDetail = null }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(WarmOffWhite)
                .testTag("crop_library_screen")
        ) {
            AgriCareTopBar(
                title = stringResource(R.string.title_crop_library),
                navigationIcon = NavigationIconType.BACK,
                onNavigationClick = onBack
            )

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.library_search_hint),
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.action_close),
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = ForestGreenPrimary,
                        unfocusedBorderColor = OutlineLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("library_search_input")
                )
            }

            // Crop Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cropFilters) { filter ->
                    val isSelected = selectedCropFilter == filter
                    val label = if (filter == "All") stringResource(R.string.library_filter_all) else filter
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ForestGreenPrimary else PureWhite)
                            .border(1.dp, if (isSelected) ForestGreenPrimary else OutlineLight, RoundedCornerShape(12.dp))
                            .clickable { selectedCropFilter = filter }
                            .padding(horizontal = 14.dp)
                            .testTag("filter_chip_$filter"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) PureWhite else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Articles List or Empty State
            if (filteredArticles.isEmpty()) {
                AgriCareEmptyState(
                    title = stringResource(R.string.library_empty_title),
                    description = stringResource(R.string.library_empty_desc),
                    icon = Icons.Default.Search,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredArticles, key = { it.id }) { article ->
                        GuideArticleRowItem(
                            article = article,
                            onClick = { selectedArticleForDetail = article }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideArticleRowItem(
    article: GuideArticleEntity,
    onClick: () -> Unit
) {
    AgriCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("library_item_${article.id}"),
        backgroundColor = PureWhite,
        borderColor = OutlineLight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MintLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!article.scientificName.isNullOrBlank()) {
                    Text(
                        text = "(${article.scientificName})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            color = TextSecondary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AgriCarePill(
                        text = article.category,
                        backgroundColor = SageSurface,
                        textColor = ForestGreenDeep
                    )
                    AgriCarePill(
                        text = article.crop,
                        backgroundColor = MintLight,
                        textColor = ForestGreenPrimary
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Guide Detail Screen matching Master Mockup Screen 8
 */
@Composable
private fun GuideDetailScreen(
    article: GuideArticleEntity,
    onBack: () -> Unit
) {
    var isBookmarked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .testTag("guide_detail_screen")
    ) {
        AgriCareTopBar(
            title = article.crop,
            navigationIcon = NavigationIconType.BACK,
            onNavigationClick = onBack,
            actions = {
                IconButton(
                    onClick = { isBookmarked = !isBookmarked },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("guide_bookmark_btn")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = ForestGreenPrimary
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Image Banner Container matching mockup Screen 8
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2EA86E), Color(0xFF176B4D))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        )
                        if (!article.scientificName.isNullOrBlank()) {
                            Text(
                                text = "(${article.scientificName})",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontStyle = FontStyle.Italic,
                                    color = Color(0xFFD4EBDC)
                                )
                            )
                        }
                    }
                }
            }

            // Badges row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AgriCarePill(
                        text = article.category,
                        backgroundColor = MintLight,
                        textColor = ForestGreenDeep
                    )
                    AgriCarePill(
                        text = article.crop,
                        backgroundColor = SageSurface,
                        textColor = ForestGreenPrimary
                    )
                }
            }

            // About Section
            item {
                DetailSectionCard(
                    title = stringResource(R.string.library_section_about),
                    content = article.description
                )
            }

            // Symptoms Section
            if (article.symptoms.isNotEmpty()) {
                item {
                    DetailListCard(
                        title = stringResource(R.string.library_section_symptoms),
                        items = article.symptoms
                    )
                }
            }

            // Look-alikes / Similar Problems
            if (article.lookAlikes.isNotEmpty()) {
                item {
                    DetailListCard(
                        title = stringResource(R.string.library_section_lookalikes),
                        items = article.lookAlikes
                    )
                }
            }

            // Safe Management
            if (article.management.isNotEmpty()) {
                item {
                    DetailListCard(
                        title = stringResource(R.string.library_section_management),
                        items = article.management
                    )
                }
            }

            // Prevention
            if (article.prevention.isNotEmpty()) {
                item {
                    DetailListCard(
                        title = stringResource(R.string.library_section_prevention),
                        items = article.prevention
                    )
                }
            }

            // Source Attribution (PRD 6.11: Never invent source citations)
            if (!article.sources.isNullOrBlank()) {
                item {
                    AgriCareCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = SageSurface,
                        borderColor = OutlineLight
                    ) {
                        Text(
                            text = stringResource(R.string.library_source_attribution),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDeep
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = article.sources,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        if (!article.reviewedAt.isNullOrBlank()) {
                            Text(
                                text = "Last Reviewed: ${article.reviewedAt}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    content: String
) {
    AgriCareCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureWhite,
        borderColor = OutlineLight
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                lineHeight = 22.sp
            )
        )
    }
}

@Composable
private fun DetailListCard(
    title: String,
    items: List<String>
) {
    AgriCareCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureWhite,
        borderColor = OutlineLight
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        items.forEachIndexed { index, itemText ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ForestGreenPrimary)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = itemText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}
