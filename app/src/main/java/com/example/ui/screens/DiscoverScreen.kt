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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.JobSearchViewModel

@Composable
fun DiscoverScreen(
    viewModel: JobSearchViewModel,
    modifier: Modifier = Modifier
) {
    val jobs by viewModel.jobs.collectAsState()
    val filterOptions = listOf("Discover", "Remote", "Full-time", "25+ LPA", "Bengaluru", "Mumbai", "Delhi NCR")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp) // Cushion to avoid navigation bar overlapping
    ) {
        // Top App Header
        AppHeader(viewModel = viewModel)

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            // Search Input Row
            OutlinedTextField(
                value = viewModel.searchQueryInput,
                onValueChange = { 
                    viewModel.searchQueryInput = it
                },
                placeholder = { 
                    Text("Search for 'Product Designer'...", color = CandyOutline) 
                },
                leadingIcon = { 
                    IconButton(
                        onClick = {
                            viewModel.activeSearchQuery = viewModel.searchQueryInput
                            viewModel.setTab(AppTab.SEARCH)
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search Action", tint = CandyPrimary) 
                    }
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 6.dp)) {
                        if (viewModel.searchQueryInput.isNotEmpty()) {
                            IconButton(onClick = { 
                                viewModel.searchQueryInput = ""
                                viewModel.activeSearchQuery = ""
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Search", tint = CandyOutline)
                            }
                        }
                        IconButton(
                            onClick = { viewModel.setTab(AppTab.SEARCH) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CandySecondary)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.activeSearchQuery = viewModel.searchQueryInput
                        viewModel.setTab(AppTab.SEARCH)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { option ->
                    val isActive = viewModel.selectedFilterCategory == option
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            1.dp, 
                            if (isActive) CandyPrimary else CandyOutlineVariant
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) CandyPrimary else Color.White
                        ),
                        modifier = Modifier
                            .clickable {
                                viewModel.selectedFilterCategory = option
                                if (option != "Discover") {
                                    viewModel.setTab(AppTab.SEARCH) // Pivot search filter view
                                }
                            }
                    ) {
                        Text(
                            text = option,
                            color = if (isActive) Color.White else CandyOnBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Featured Opportunity Bento Grid (Asymmetric layout)
            Text(
                text = "Featured Opportunity",
                style = MaterialTheme.typography.titleMedium,
                color = CandyOnBackground,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Principal UX Architect Left featured card
                Box(
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            val fJob = jobs.find { it.id == "1" }
                            if (fJob != null) {
                                viewModel.applyForJob("1")
                            }
                        }
                ) {
                    // Office environment photo
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDGJ9IgTj2MUHQdQamwLplfLOawICGTYaYD83LOkYWTf_H2z5hdCUyeetoN2BS8TRyn1zinS2-ROs6hEhYkqfb8qiup4VubdBjEH1M_FQyTU7nNtTgR3p8-FfPU4I8Ytvpglz6k25cb6Ja9e71h9YfKN9JnHFXpQVvSMtuCfP3NjZ6L5u00lQdhv6e2jY5IwoaJSNxs0H9M9AhBF2WWPC4MUDwIdip6pnGhyVuzHVhsvwMykzrbP1fxZNetpO6kMLYmBCMQO17VBQ",
                        contentDescription = "Modern workspace",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                    startY = 180f
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .background(CandyTertiary, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "HOT OPPORTUNITY",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Principal UX Architect",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "CandyDesign Lab • $160k - $220k",
                            color = CandyPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Research Right featured card
                val isArrApplied = jobs.any { it.id == "2" && it.applicationStatus != null }
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CandySecondaryContainer),
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .size(38.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = "AI Powered",
                                    tint = CandySecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.toggleFavorite("2") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                val isFav2 = jobs.find { it.id == "2" }?.isFavorite == true
                                Icon(
                                    if (isFav2) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFav2) CandyPrimary else CandySecondary
                                )
                            }
                        }

                        Column {
                            Text(
                                "AI Research Engineer",
                                style = MaterialTheme.typography.titleMedium,
                                color = CandyOnSecondaryContainer,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "Neural Pop • Remote",
                                color = CandySecondary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.applyForJob("2") },
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isArrApplied) CandyOutline else CandySecondary
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                            ) {
                                Text(
                                    if (isArrApplied) "Applied" else "Apply Now",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Suggested Jobs Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Recommended Jobs",
                    style = MaterialTheme.typography.titleMedium,
                    color = CandyOnBackground,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "See all",
                    color = CandyPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { viewModel.setTab(AppTab.SEARCH) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Multi-job vertical aggregation list
            jobs.filter { it.id != "1" && it.id != "2" }.take(3).forEach { job ->
                JobListingCard(
                    job = job,
                    onFavoriteToggle = { viewModel.toggleFavorite(job.id) },
                    onApplyClick = { viewModel.applyForJob(job.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Gradient Banner - Talent Match
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(204.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CandyTertiary, CandyPrimary)
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1.3f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Your perfect match is waiting!",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Based on your resume, we found 14 new opportunities that fit you perfectly.",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { 
                                    viewModel.searchQueryInput = ""
                                    viewModel.activeSearchQuery = ""
                                    viewModel.selectedFilterCategory = "Discover"
                                    viewModel.setTab(AppTab.SEARCH) 
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    "View Matches",
                                    color = CandyTertiary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(0.7f)
                                .size(90.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "Spark Matching",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeader(viewModel: JobSearchViewModel) {
    val resume by viewModel.userResume.collectAsState()
    val profilePic = resume.profilePictureUrl

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Menu circular container from the Sleek Theme
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CandySurfaceVariant)
                    .clickable { viewModel.toggleDrawer() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = CandySecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Career Candy",
                color = CandyOnBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.01).sp
            )
        }

        // Beautiful Bell and Avatar in a lavender styled ring on the right
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val notifications by viewModel.notificationHistory.collectAsState()
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CandySurfaceVariant)
                    .clickable { viewModel.showNotificationInbox = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = CandySecondary,
                    modifier = Modifier.size(20.dp)
                )
                if (notifications.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .background(CandyPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notifications.size.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CandyPrimaryContainer)
                    .clickable { 
                        viewModel.showProfileModal = true
                        viewModel.profileEditMode = false
                    }
                    .padding(2.dp) // Ring stroke effect
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                ) {
                    AsyncImage(
                        model = profilePic.ifBlank { "https://lh3.googleusercontent.com/aida-public/AB6AXuD6rfR2rPfitPnMfsl8iES-KxOzSMFNDqmmSILYV9OjglVdob0DFm9erHGhR1szhRV7kzMaoIYFy3iWCJBDxKdHCoO2X4Hi9jctojpk4czJwRWOtglSVxMQHZwI_IWrcXoBw6sx0ha9Hdvt4h2XHwQJB3nqbfgaTJWn6903Mty6Q4eW-L1_COdUP8jEiiV7Rmw3MhKy-s-qOIlc5oH9zG1TvU2mseby1o_uacmXYcRZUulvIHtT441LGxQPbhuN95VQhKHgzDRNrA" },
                        contentDescription = "Sarah Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun JobListingCard(
    job: JobListing,
    onFavoriteToggle: () -> Unit,
    onApplyClick: () -> Unit
) {
    val isApplied = job.applicationStatus != null
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CandyOutlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(CandyBackground, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, CandyOutlineVariant, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = job.logoUrl,
                            contentDescription = "Logo",
                            modifier = Modifier.size(34.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column {
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = CandyOnBackground,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${job.company} • ${job.location}",
                            color = CandyOutline,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (job.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (job.isFavorite) CandyPrimary else CandyOutline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata tag rows
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                job.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(CandySurfaceVariant, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag.uppercase(),
                            color = CandySecondary,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action footer
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = job.salary,
                    color = CandyPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Button(
                    onClick = onApplyClick,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApplied) CandyOutline else CandyPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (isApplied) "Applied" else "Quick Apply",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
