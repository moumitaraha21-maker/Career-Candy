package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.JobListing
import com.example.ui.theme.*
import com.example.ui.viewmodel.JobSearchViewModel

@Composable
fun SearchScreen(
    viewModel: JobSearchViewModel,
    modifier: Modifier = Modifier
) {
    val jobs by viewModel.jobs.collectAsState()
    val resume by viewModel.userResume.collectAsState()

    // Filter local list based on query AND selected horizontal filter + advanced filters
    val filteredJobs = remember(
        viewModel.activeSearchQuery,
        viewModel.selectedFilterCategory,
        viewModel.filterLocation,
        viewModel.filterWorkType,
        viewModel.filterSalaryRange,
        viewModel.filterSalaryMin,
        viewModel.filterSalaryMax,
        jobs
    ) {
        jobs.filter { job ->
            val matchQuery = viewModel.activeSearchQuery.isEmpty() ||
                    job.title.contains(viewModel.activeSearchQuery, ignoreCase = true) ||
                    job.company.contains(viewModel.activeSearchQuery, ignoreCase = true) ||
                    job.location.contains(viewModel.activeSearchQuery, ignoreCase = true)

            val matchCategory = when (viewModel.selectedFilterCategory) {
                "Remote" -> job.workType.equals("Remote", ignoreCase = true)
                "Full-time" -> job.tags.any { it.contains("Full-time", ignoreCase = true) }
                "25+ LPA" -> {
                    val digits = job.salary.filter { it.isDigit() }
                    val salaryInt = if (digits.length >= 2) digits.substring(0, 2).toIntOrNull() ?: 0 else digits.toIntOrNull() ?: 0
                    salaryInt >= 25 || job.salary.contains("35") || job.salary.contains("40") || job.salary.contains("50")
                }
                "Bengaluru" -> job.location.contains("Bengaluru", ignoreCase = true) || job.tags.any { it.contains("Bengaluru", ignoreCase = true) }
                "Mumbai" -> job.location.contains("Mumbai", ignoreCase = true) || job.tags.any { it.contains("Mumbai", ignoreCase = true) }
                "Delhi NCR" -> job.location.contains("Delhi", ignoreCase = true) || job.location.contains("Gurugram", ignoreCase = true) || job.location.contains("Noida", ignoreCase = true)
                else -> true
            }

            val matchLocation = viewModel.filterLocation == "All" || job.location.contains(viewModel.filterLocation, ignoreCase = true)
            val matchWorkType = viewModel.filterWorkType == "All" || job.workType.equals(viewModel.filterWorkType, ignoreCase = true)

            // Parse job's salary range
            val (jobMin, jobMax) = run {
                val digits = job.salary.split("-").map { part ->
                    part.filter { it.isDigit() }.toIntOrNull() ?: 0
                }
                if (digits.size >= 2) {
                    Pair(digits[0], digits[1])
                } else if (digits.size == 1 && digits[0] > 0) {
                    if (job.salary.contains("+")) {
                        Pair(digits[0], 100)
                    } else if (job.salary.contains("Under") || job.salary.contains("Below")) {
                        Pair(0, digits[0])
                    } else {
                        Pair(digits[0], digits[0])
                    }
                } else {
                    Pair(0, 100)
                }
            }

            // Salary match compares overlap with slider min and max filter bounds
            val matchSalary = (jobMax >= viewModel.filterSalaryMin && jobMin <= viewModel.filterSalaryMax)

            matchQuery && matchCategory && matchLocation && matchWorkType && matchSalary
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // App top header
        AppHeader(viewModel = viewModel)

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            // Welcome Header
            val userName = if (resume.fullName.isNotBlank()) resume.fullName.split(" ").first() else "Sarah"
            Text(
                text = "Hey $userName!",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                color = CandyOnBackground,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Find your sweet spot.",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                color = CandyPrimary,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar with advanced filters options
            OutlinedTextField(
                value = viewModel.searchQueryInput,
                onValueChange = { viewModel.searchQueryInput = it },
                placeholder = { Text("Search by title, skills or keywords...", color = CandyOutline) },
                leadingIcon = { 
                    IconButton(
                        onClick = { 
                            viewModel.activeSearchQuery = viewModel.searchQueryInput 
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search Action", tint = CandyPrimary) 
                    }
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (viewModel.searchQueryInput.isNotEmpty()) {
                            IconButton(onClick = { 
                                viewModel.searchQueryInput = "" 
                                viewModel.activeSearchQuery = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Search",
                                    tint = CandyOutline
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.isFilterPanelExpanded = !viewModel.isFilterPanelExpanded }) {
                            Icon(
                                imageVector = if (viewModel.isFilterPanelExpanded) Icons.Default.Close else Icons.Default.Tune,
                                contentDescription = "Advanced Filters",
                                tint = CandyPrimary
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.activeSearchQuery = viewModel.searchQueryInput
                    }
                ),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CandyPrimary,
                    unfocusedBorderColor = CandyOutlineVariant,
                    focusedContainerColor = Color(0xFFFFE8F3), // Soft pink background
                    unfocusedContainerColor = Color(0xFFFFE8F3),
                    focusedTextColor = Color.Black,             // Typed in Black as requested
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Dynamic Advanced Filters Expandable Shelf Panel
            AnimatedVisibility(
                visible = viewModel.isFilterPanelExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF7F9)),
                    border = BorderStroke(1.dp, CandyOutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Filter Opportunities (PAN INDIA)",
                            style = MaterialTheme.typography.titleSmall,
                            color = CandyPrimary,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Location drop selector
                        Text("Location Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CandyOutline)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("All", "Bengaluru", "Mumbai", "Delhi NCR", "Pune", "Hyderabad").forEach { loc ->
                                FilterChipMini(
                                    label = loc,
                                    isSelected = viewModel.filterLocation == loc,
                                    onClick = { viewModel.filterLocation = loc }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Work Type selector
                        Text("Work Environment", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CandyOutline)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("All", "Remote", "Hybrid", "On-site").forEach { type ->
                                FilterChipMini(
                                    label = type,
                                    isSelected = viewModel.filterWorkType == type,
                                    onClick = { viewModel.filterWorkType = type }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Salary Bracket selector
                        Text("Salary Package Presets", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CandyOutline)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                        ) {
                            listOf("All", "Under 15 LPA", "15 - 30 LPA", "30+ LPA").forEach { range ->
                                FilterChipMini(
                                    label = range,
                                    isSelected = viewModel.filterSalaryRange == range,
                                    onClick = { 
                                        viewModel.filterSalaryRange = range
                                        when (range) {
                                            "All" -> {
                                                viewModel.filterSalaryMin = 0f
                                                viewModel.filterSalaryMax = 60f
                                            }
                                            "Under 15 LPA" -> {
                                                viewModel.filterSalaryMin = 0f
                                                viewModel.filterSalaryMax = 15f
                                            }
                                            "15 - 30 LPA" -> {
                                                viewModel.filterSalaryMin = 15f
                                                viewModel.filterSalaryMax = 30f
                                            }
                                            "30+ LPA" -> {
                                                viewModel.filterSalaryMin = 30f
                                                viewModel.filterSalaryMax = 60f
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Continuous Range Slider Component
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Adjust Range (Continuous)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyOutline
                            )
                            Text(
                                text = "${viewModel.filterSalaryMin.toInt()} - ${viewModel.filterSalaryMax.toInt()} LPA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = CandyPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        RangeSlider(
                            value = viewModel.filterSalaryMin..viewModel.filterSalaryMax,
                            onValueChange = { range ->
                                viewModel.filterSalaryMin = range.start
                                viewModel.filterSalaryMax = range.endInclusive
                                // Since it is customized, deselect specific preset if it doesn't match
                                val minVal = range.start.toInt()
                                val maxVal = range.endInclusive.toInt()
                                viewModel.filterSalaryRange = when {
                                    minVal == 0 && maxVal == 60 -> "All"
                                    minVal == 0 && maxVal == 15 -> "Under 15 LPA"
                                    minVal == 15 && maxVal == 30 -> "15 - 30 LPA"
                                    minVal == 30 && maxVal == 60 -> "30+ LPA"
                                    else -> "Custom"
                                }
                            },
                            valueRange = 0f..60f,
                            steps = 11, // steps of 5 LPA (0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60)
                            colors = SliderDefaults.colors(
                                activeTrackColor = CandyPrimary,
                                inactiveTrackColor = CandyOutlineVariant,
                                thumbColor = CandyPrimary,
                                activeTickColor = CandyPrimaryContainer,
                                inactiveTickColor = CandyOutlineVariant
                            ),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Reset button and Slider endpoints indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFF0F5), RoundedCornerShape(8.dp))
                                        .border(1.dp, CandyOutlineVariant, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Min: ${viewModel.filterSalaryMin.toInt()} LPA", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.Black.copy(alpha = 0.7f))
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFF0F5), RoundedCornerShape(8.dp))
                                        .border(1.dp, CandyOutlineVariant, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Max: ${viewModel.filterSalaryMax.toInt()} LPA", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.Black.copy(alpha = 0.7f))
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.filterLocation = "All"
                                    viewModel.filterWorkType = "All"
                                    viewModel.filterSalaryRange = "All"
                                    viewModel.filterSalaryMin = 0f
                                    viewModel.filterSalaryMax = 60f
                                    viewModel.isFilterPanelExpanded = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CandySecondary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Clear Filters", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Top Matches Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Matches",
                    style = MaterialTheme.typography.titleMedium,
                    color = CandyOnBackground,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "See all",
                    color = CandyTertiary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal snappy Top Matches Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Prepopulated high matches
                val topMatchItems = jobs.sortedByDescending { it.matchPercentage }.take(3)
                items(topMatchItems) { job ->
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, CandyOutlineVariant),
                        modifier = Modifier
                            .width(260.dp)
                            .clickable { viewModel.applyForJob(job.id) }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(CandySecondaryContainer, RoundedCornerShape(12.dp))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = job.logoUrl,
                                        contentDescription = "Logo",
                                        modifier = Modifier.size(34.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(CandyPrimary, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "${job.matchPercentage}% Match",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = job.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = CandyOnBackground,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = job.company,
                                style = MaterialTheme.typography.bodySmall,
                                color = CandyOutline,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Display tags or friends social proof
                            if (job.id == "4" || job.id == "1") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy((-6).dp)
                                ) {
                                    Box(modifier = Modifier.size(24.dp).background(CandyPrimaryContainer, CircleShape))
                                    Box(modifier = Modifier.size(24.dp).background(CandySecondaryContainer, CircleShape))
                                    Box(modifier = Modifier.size(24.dp).background(CandyTertiaryContainer, CircleShape))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "12 friends work here",
                                        fontSize = 11.sp,
                                        color = CandyOutline,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .background(CandyTertiaryContainer, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "NEW OPPORTUNITY",
                                        color = CandyTertiary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Daily Picks Feed
            Text(
                text = "Daily Picks",
                style = MaterialTheme.typography.titleMedium,
                color = CandyOnBackground,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (filteredJobs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CandyOutlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No jobs match your query",
                            fontWeight = FontWeight.Bold,
                            color = CandyOutline
                        )
                    }
                }
            } else {
                filteredJobs.forEach { job ->
                    val isFav = job.isFavorite
                    DailyPickItem(
                        job = job,
                        isBookmarked = isFav,
                        onBookmarkToggle = { viewModel.toggleFavorite(job.id) },
                        onCardClick = { viewModel.applyForJob(job.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun DailyPickItem(
    job: JobListing,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CandyOutlineVariant),
        colors = CardDefaults.cardColors(containerColor = CandyBackground), // matches surface-container-low aesthetic
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = job.logoUrl,
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = CandyOnBackground,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = job.company,
                            color = CandyOutline,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onBookmarkToggle, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) CandyPrimary else CandyOutline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(CandyPrimaryContainer, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = job.salary,
                            color = CandyOnPrimaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(CandyTertiaryContainer, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = job.workType,
                            color = CandyOnTertiaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(CandySecondaryContainer, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (job.tags.contains("Full-time")) "Full-time" else "Hybrid",
                            color = CandyOnSecondaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipMini(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) CandyPrimaryContainer else Color.White
    Box(
        modifier = Modifier
            .border(
                1.dp,
                if (isSelected) CandyPrimary else CandyOutlineVariant,
                RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) CandyPrimary else Color.Black.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

