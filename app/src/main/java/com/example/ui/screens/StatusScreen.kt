package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.JobListing
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.JobSearchViewModel

// Helper to normalize different application status names to core flows
fun getNormalizedStatus(status: String?): String? {
    return when (status?.lowercase()?.trim()) {
        "applied", "under review" -> "Applied"
        "interviewing", "interview scheduled", "interview" -> "Interviewing"
        "offer", "offered" -> "Offer"
        else -> status
    }
}

@Composable
fun StatusScreen(
    viewModel: JobSearchViewModel,
    modifier: Modifier = Modifier
) {
    val jobs by viewModel.jobs.collectAsState()

    // Calculate dynamic stats based on user applications
    val activeApplications = remember(jobs) {
        jobs.filter { it.applicationStatus != null }
    }
    
    val totalApplied = activeApplications.count { getNormalizedStatus(it.applicationStatus) == "Applied" }
    val totalInterviews = activeApplications.count { getNormalizedStatus(it.applicationStatus) == "Interviewing" }
    val totalOffered = activeApplications.count { getNormalizedStatus(it.applicationStatus) == "Offer" }
    val totalApplicationsCount = activeApplications.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // App top header
        AppHeaderWithAvatar(viewModel = viewModel)

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            
            // Your Progress Header
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.titleMedium,
                color = CandyOnBackground,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Stat Grid Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Applied Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    count = totalApplied.toString(),
                    label = "Applied",
                    borderColor = CandyPrimary
                )

                // Interviews Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    count = totalInterviews.toString(),
                    label = "Interviews",
                    borderColor = CandySecondary
                )

                // Offered Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    count = totalOffered.toString(),
                    label = "Offered",
                    borderColor = CandyTertiary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PIPELINE PROGRESS BAR COMPONENT
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CandySurfaceVariant),
                border = BorderStroke(1.dp, CandyOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pipeline_progress_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Compute custom overall weighted success score:
                    // Applied is 33%, Interviewing is 66%, Offer is 100%
                    val maxPoints = totalApplicationsCount * 3
                    val currentPoints = activeApplications.sumOf {
                        when (getNormalizedStatus(it.applicationStatus)) {
                            "Applied" -> 1
                            "Interviewing" -> 2
                            "Offer" -> 3
                            else -> 1
                        }
                    }
                    val pct = if (maxPoints > 0) (currentPoints.toFloat() / maxPoints.toFloat() * 100).toInt() else 0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Pipeline Progress",
                            color = CandyOnBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        
                        Text(
                            text = "$pct%",
                            color = CandyPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            modifier = Modifier.testTag("pipeline_progress_percentage")
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val progressVal = if (totalApplicationsCount > 0) {
                        currentPoints.toFloat() / maxPoints.toFloat()
                    } else {
                        0f
                    }
                    
                    LinearProgressIndicator(
                        progress = { progressVal },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .testTag("pipeline_progress_bar"),
                        color = CandyPrimary,
                        trackColor = CandyPrimaryContainer.copy(alpha = 0.4f)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = when {
                            totalOffered > 0 -> "Brilliant work! You secured $totalOffered Offer${if (totalOffered > 1) "s" else ""}! Keep tracking your progress or prepare for interviews."
                            totalInterviews > 0 -> "Awesome! You have $totalInterviews active interview stages in progress. Stay sharp!"
                            totalApplied > 0 -> "Submitted $totalApplied job application${if (totalApplied > 1) "s" else ""}. Check back here for state transitions."
                            else -> "No active applications currently tracked. Jump over to the Explore tab to apply!"
                        },
                        color = CandyOutline,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp,
                        modifier = Modifier.testTag("pipeline_progress_motivation")
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Application Trackers List Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Application Trackers",
                    style = MaterialTheme.typography.titleMedium,
                    color = CandyOnBackground,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "${totalApplicationsCount} Total",
                    color = CandyPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.testTag("trackers_count_badge")
                )
            }

            // Interactive list of application trackers
            if (activeApplications.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CandyOutlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("empty_trackers_view")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Empty Tracker",
                            tint = CandyOutline.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No applications under tracking yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CandyOnBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Go to Discover or Search to apply to jobs.",
                            color = CandyOutline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.setTab(AppTab.DISCOVER) },
                            colors = ButtonDefaults.buttonColors(containerColor = CandyPrimary)
                        ) {
                            Text("Find Openings", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                activeApplications.forEach { job ->
                    val normalizedStatus = getNormalizedStatus(job.applicationStatus) ?: "Applied"
                    
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, CandyOutlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("application_card_${job.id}")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Card Header: Logo, Title, and Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(CandySurfaceVariant, CircleShape)
                                            .clip(CircleShape)
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = job.logoUrl,
                                            contentDescription = job.company,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    
                                    Column {
                                        Text(
                                            text = job.title,
                                            fontWeight = FontWeight.Bold,
                                            color = CandyOnBackground,
                                            fontSize = 16.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${job.company} • ${job.location}",
                                            color = CandyOutline,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                val (badgeBg, badgeText) = when (normalizedStatus) {
                                    "Offer" -> Pair(CandyTertiaryContainer, CandyTertiary)
                                    "Interviewing" -> Pair(CandySecondaryContainer, CandySecondary)
                                    else -> Pair(CandyPrimaryContainer, CandyPrimary)
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .background(badgeBg, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                        .testTag("badge_${job.id}")
                                ) {
                                    Text(
                                        text = normalizedStatus,
                                        color = badgeText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // LOCAL PROGRESS BAR COMPONENT Visualizer
                            val localProgress = when (normalizedStatus) {
                                "Applied" -> 0.33f
                                "Interviewing" -> 0.66f
                                "Offer" -> 1.0f
                                else -> 0.33f
                            }
                            
                            val localColor = when (normalizedStatus) {
                                "Offer" -> CandyTertiary
                                "Interviewing" -> CandySecondary
                                else -> CandyPrimary
                            }

                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tracking Stage",
                                        color = CandyOutline,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = when (normalizedStatus) {
                                            "Applied" -> "Stage 1: Applied"
                                            "Interviewing" -> "Stage 2: Interviewing"
                                            "Offer" -> "Stage 3: Offer Secured!"
                                            else -> "Stage 1: Applied"
                                        },
                                        color = localColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.testTag("stage_title_${job.id}")
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { localProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .testTag("progress_bar_${job.id}"),
                                    color = localColor,
                                    trackColor = CandySurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))
                            HorizontalDivider(color = CandyOutlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Transition chip controls
                            Column {
                                Text(
                                    text = "Transition Recruitment Status:",
                                    color = CandyOnBackground.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val statusOptions = listOf("Applied", "Interviewing", "Offer")
                                    statusOptions.forEach { statusOption ->
                                        val isCurrent = normalizedStatus == statusOption
                                        
                                        val buttonBg = if (isCurrent) {
                                            when (statusOption) {
                                                "Offer" -> CandyTertiary
                                                "Interviewing" -> CandySecondary
                                                else -> CandyPrimary
                                            }
                                        } else {
                                            Color.White
                                        }

                                        val buttonText = if (isCurrent) Color.White else CandyOutline
                                        val buttonBorder = if (isCurrent) Color.Transparent else CandyOutlineVariant

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(buttonBg)
                                                .border(1.dp, buttonBorder, RoundedCornerShape(16.dp))
                                                .clickable {
                                                    viewModel.applyForJob(job.id, statusOption)
                                                }
                                                .padding(vertical = 8.dp)
                                                .testTag("status_chip_${job.id}_${statusOption.lowercase()}"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = statusOption,
                                                color = buttonText,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Beautiful Call-to-action Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CandyPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("cta_explore_card")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ready for your next move?",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Discover 50+ new job openings tailored just for your skills.",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(220.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.setTab(AppTab.DISCOVER) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.testTag("cta_discover_button")
                        ) {
                            Text(
                                "Find More Jobs",
                                color = CandyPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeaderWithAvatar(viewModel: JobSearchViewModel) {
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
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                ) {
                    AsyncImage(
                        model = profilePic.ifBlank { "https://lh3.googleusercontent.com/aida-public/AB6AXuAGNHURekhJdSPETuW_4b-Z9nxIbEXoXfs7pAcioUaaSXFYfeGVC9iWr6wbqzz8LgREr2_T0yMbRe-CYEm4PhmGJeEBd4qzRw5JW3bK5s3a6YFqCLDMURSKhpUEZNNd2XycqutOWKBbfbt0iqOz0uIjl2T96n9FzS7o_ldWAEHX4XBs97jDljQQsuZs2V9MkEBVUVlg_PGJKc3PhahijjtJ7ntnJ_20W442fIqVRhEQg1dNsYAOhO7uq3MNtzM5d-NmXZ3Y3weG6g" },
                        contentDescription = "Sarah Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    count: String,
    label: String,
    borderColor: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CandyBackground),
        border = BorderStroke(2.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count,
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = borderColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = CandyOnBackground.copy(alpha = 0.6f)
            )
        }
    }
}
