package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.ResumeScreen
import com.example.ui.screens.StatusScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.ProfileDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.JobSearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: JobSearchViewModel = viewModel()
                val isSignedIn by viewModel.isSignedIn.collectAsState()

                if (!isSignedIn) {
                    SignInScreen(viewModel = viewModel)
                } else {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    // Synchronize VM status to local drawer state
                    LaunchedEffect(viewModel.isDrawerOpen) {
                        if (viewModel.isDrawerOpen) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }

                    LaunchedEffect(drawerState.isOpen) {
                        viewModel.isDrawerOpen = drawerState.isOpen
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = Color.White,
                                modifier = Modifier.width(300.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(24.dp)
                                ) {
                                    // Drawer Header Logo
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(CandyPrimaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🍬", fontSize = 18.sp)
                                        }
                                        Text(
                                            text = "Career Candy",
                                            color = CandyPrimary,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Profile state in drawer
                                    val resume by viewModel.userResume.collectAsState()
                                    Card(
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(containerColor = CandySurfaceVariant),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.showProfileModal = true
                                                viewModel.profileEditMode = false
                                                viewModel.closeDrawer()
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(CandyPrimaryContainer)
                                                    .padding(2.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape)
                                                ) {
                                                    AsyncImage(
                                                        model = resume.profilePictureUrl.ifBlank { "https://lh3.googleusercontent.com/aida-public/AB6AXuAGNHURekhJdSPETuW_4b-Z9nxIbEXoXfs7pAcioUaaSXFYfeGVC9iWr6wbqzz8LgREr2_T0yMbRe-CYEm4PhmGJeEBd4qzRw5JW3bK5s3a6YFqCLDMURSKhpUEZNNd2XycqutOWKBbfbt0iqOz0uIjl2T96n9FzS7o_ldWAEHX4XBs97jDljQQsuZs2V9MkEBVUVlg_PGJKc3PhahijjtJ7ntnJ_20W442fIqVRhEQg1dNsYAOhO7uq3MNtzM5d-NmXZ3Y3weG6g" },
                                                        contentDescription = "User Avatar",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                }
                                            }
                                            Column {
                                                Text(
                                                    text = resume.fullName.ifBlank { "Sarah Candy" },
                                                    fontWeight = FontWeight.Bold,
                                                    color = CandyOnBackground,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = resume.jobTitle.ifBlank { "Professional Techie" },
                                                    color = CandyOutline,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                    HorizontalDivider(color = CandyOutlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "QUICK LINKS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CandySecondary,
                                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                                    )

                                    DrawerMenuItem(
                                        label = "My Profile",
                                        icon = Icons.Default.Person,
                                        isSelected = viewModel.showProfileModal,
                                        onClick = {
                                            viewModel.showProfileModal = true
                                            viewModel.profileEditMode = false
                                            viewModel.closeDrawer()
                                        }
                                    )

                                    DrawerMenuItem(
                                        label = "Explore Jobs",
                                        icon = Icons.Default.Explore,
                                        isSelected = viewModel.currentTab == AppTab.DISCOVER,
                                        onClick = {
                                            viewModel.setTab(AppTab.DISCOVER)
                                            viewModel.closeDrawer()
                                        }
                                    )

                                    DrawerMenuItem(
                                        label = "Search Positions",
                                        icon = Icons.Default.Search,
                                        isSelected = viewModel.currentTab == AppTab.SEARCH,
                                        onClick = {
                                            viewModel.setTab(AppTab.SEARCH)
                                            viewModel.closeDrawer()
                                        }
                                    )

                                    DrawerMenuItem(
                                        label = "Resume Builder",
                                        icon = Icons.Default.Description,
                                        isSelected = viewModel.currentTab == AppTab.RESUME,
                                        onClick = {
                                            viewModel.setTab(AppTab.RESUME)
                                            viewModel.closeDrawer()
                                        }
                                    )

                                    DrawerMenuItem(
                                        label = "Application Trackers",
                                        icon = Icons.Default.CheckCircle,
                                        isSelected = viewModel.currentTab == AppTab.STATUS,
                                        onClick = {
                                            viewModel.setTab(AppTab.STATUS)
                                            viewModel.closeDrawer()
                                        }
                                    )

                                    DrawerMenuItem(
                                        label = "AI Career Advisor",
                                        icon = Icons.Default.AutoAwesome,
                                        isSelected = viewModel.currentTab == AppTab.ASSISTANT,
                                        onClick = {
                                            viewModel.setTab(AppTab.ASSISTANT)
                                            viewModel.closeDrawer()
                                        }
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    HorizontalDivider(color = CandyOutlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(14.dp))

                                    DrawerMenuItem(
                                        label = "Sign Out Account",
                                        icon = Icons.Default.ExitToApp,
                                        isSelected = false,
                                        colorAccent = Color(0xFFBA1A1A),
                                        onClick = {
                                            viewModel.signOut()
                                            viewModel.closeDrawer()
                                        }
                                    )
                                }
                            }
                        }
                    ) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                CandyBottomBar(
                                    currentTab = viewModel.currentTab,
                                    onTabSelected = { viewModel.setTab(it) }
                                )
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                AnimatedContent(
                                    targetState = viewModel.currentTab,
                                    transitionSpec = {
                                        fadeIn() togetherWith fadeOut()
                                    }
                                ) { targetTab ->
                                    when (targetTab) {
                                        AppTab.DISCOVER -> DiscoverScreen(viewModel = viewModel)
                                        AppTab.SEARCH -> SearchScreen(viewModel = viewModel)
                                        AppTab.RESUME -> ResumeScreen(viewModel = viewModel)
                                        AppTab.STATUS -> StatusScreen(viewModel = viewModel)
                                        AppTab.ASSISTANT -> AssistantScreen(viewModel = viewModel)
                                    }
                                }
                            }
                        }

                        // Profile View and Edit Overlay
                                        if (viewModel.showProfileModal) {
                                            ProfileDialog(
                                                viewModel = viewModel,
                                                onDismiss = { viewModel.showProfileModal = false }
                                            )
                                        }

                                        // Notification Inbox / Alert Center Dialog Overlay
                        if (viewModel.showNotificationInbox) {
                            val notifications by viewModel.notificationHistory.collectAsState()

                            AlertDialog(
                                onDismissRequest = { viewModel.showNotificationInbox = false },
                                confirmButton = {
                                    TextButton(onClick = { viewModel.showNotificationInbox = false }) {
                                        Text("Dismiss", color = CandyPrimary, fontWeight = FontWeight.Black)
                                    }
                                },
                                dismissButton = {
                                    if (notifications.isNotEmpty()) {
                                        TextButton(onClick = { viewModel.clearNotifications() }) {
                                            Text("Clear All", color = CandyOutline, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                },
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("🍬", fontSize = 24.sp)
                                        Text(
                                            "Candy Alerts",
                                            color = CandyOnBackground,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 20.sp
                                        )
                                    }
                                },
                                text = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 400.dp)
                                    ) {
                                        if (notifications.isEmpty()) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 40.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    "🍭",
                                                    fontSize = 48.sp,
                                                    modifier = Modifier.padding(bottom = 12.dp)
                                                )
                                                Text(
                                                    "No alerts yet",
                                                    fontWeight = FontWeight.Bold,
                                                    color = CandyOnBackground,
                                                    fontSize = 16.sp
                                                )
                                                Text(
                                                    "Apply to jobs or update skills in your Resume to trigger simulated match & tracker notifications!",
                                                    textAlign = TextAlign.Center,
                                                    fontSize = 12.sp,
                                                    color = CandyOutline,
                                                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp)
                                                )
                                            }
                                        } else {
                                            LazyColumn(
                                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                items(notifications) { item ->
                                                    Card(
                                                        shape = RoundedCornerShape(16.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = when (item.type) {
                                                                "match" -> Color(0xFFFFF7EA) // gold matched shade
                                                                "premium" -> Color(0xFFF7F2FA) // violet premium shade
                                                                else -> Color(0xFFFFECEF) // soft sweet pink shade
                                                            }
                                                        ),
                                                        border = BorderStroke(
                                                            1.dp,
                                                            when (item.type) {
                                                                "match" -> Color(0xFFFFD485)
                                                                "premium" -> CandyPrimary.copy(alpha = 0.3f)
                                                                else -> CandyOutlineVariant
                                                            }
                                                        ),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                if (item.type == "match") {
                                                                    viewModel.setTab(AppTab.DISCOVER)
                                                                } else if (item.type == "status") {
                                                                    viewModel.setTab(AppTab.STATUS)
                                                                }
                                                                viewModel.showNotificationInbox = false
                                                            }
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(14.dp),
                                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                            verticalAlignment = Alignment.Top
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(32.dp)
                                                                    .background(
                                                                        color = when (item.type) {
                                                                            "match" -> Color(0xFFFFB74D)
                                                                            "premium" -> CandySecondary
                                                                            else -> CandyPrimary
                                                                        },
                                                                        shape = CircleShape
                                                                    ),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = when (item.type) {
                                                                        "match" -> Icons.Default.AutoAwesome
                                                                        "premium" -> Icons.Default.Star
                                                                        else -> Icons.Default.Work
                                                                    },
                                                                    contentDescription = null,
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                            }

                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Text(
                                                                        text = item.title,
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 13.sp,
                                                                        color = CandyOnBackground,
                                                                        maxLines = 1,
                                                                        overflow = TextOverflow.Ellipsis,
                                                                        modifier = Modifier.weight(1f)
                                                                    )
                                                                    Text(
                                                                        text = item.timestamp,
                                                                        style = MaterialTheme.typography.bodySmall,
                                                                        color = CandyOutline,
                                                                        fontSize = 10.sp
                                                                    )
                                                                }
                                                                Spacer(modifier = Modifier.height(3.dp))
                                                                Text(
                                                                    text = item.body,
                                                                    fontSize = 12.sp,
                                                                    color = CandyOnBackground.copy(alpha = 0.82f),
                                                                    lineHeight = 16.sp
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(28.dp),
                                containerColor = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    colorAccent: Color? = null,
    onClick: () -> Unit
) {
    val bg = if (isSelected) CandyPrimaryContainer else Color.Transparent
    val textAndIconColor = colorAccent ?: if (isSelected) CandyPrimary else CandyOnBackground.copy(alpha = 0.8f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textAndIconColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                color = textAndIconColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
            )
        }
    }
}

@Composable
fun CandyBottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // mandate safe-insets boundary handling
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Discover
            BottomTabItem(
                label = "Discover",
                icon = Icons.Default.Explore,
                activeIcon = Icons.Default.Explore,
                isActive = currentTab == AppTab.DISCOVER,
                onClick = { onTabSelected(AppTab.DISCOVER) }
            )

            // Tab 2: Search
            BottomTabItem(
                label = "Search",
                icon = Icons.Default.Search,
                activeIcon = Icons.Default.Search,
                isActive = currentTab == AppTab.SEARCH,
                onClick = { onTabSelected(AppTab.SEARCH) }
            )

            // Tab 3: Resume
            BottomTabItem(
                label = "Resume",
                icon = Icons.Default.Description,
                activeIcon = Icons.Default.Description,
                isActive = currentTab == AppTab.RESUME,
                onClick = { onTabSelected(AppTab.RESUME) }
            )

            // Tab 4: Status
            BottomTabItem(
                label = "Status",
                icon = Icons.Default.CheckCircle,
                activeIcon = Icons.Default.CheckCircle,
                isActive = currentTab == AppTab.STATUS,
                onClick = { onTabSelected(AppTab.STATUS) }
            )

            // Tab 5: Assistant / AI Advisor
            BottomTabItem(
                label = "AI Advisor",
                icon = Icons.Default.AutoAwesome,
                activeIcon = Icons.Default.AutoAwesome,
                isActive = currentTab == AppTab.ASSISTANT,
                onClick = { onTabSelected(AppTab.ASSISTANT) }
            )
        }
    }
}

@Composable
fun BottomTabItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isActive) CandyOnPrimaryContainer else CandyOutline
    val containerColor = if (isActive) CandyPrimaryContainer else Color.Transparent

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isActive) activeIcon else icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
