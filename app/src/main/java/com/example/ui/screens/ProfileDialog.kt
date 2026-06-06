package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Resume
import com.example.ui.theme.*
import com.example.ui.viewmodel.JobSearchViewModel

@Composable
fun ProfileDialog(
    viewModel: JobSearchViewModel,
    onDismiss: () -> Unit
) {
    val currentResume by viewModel.userResume.collectAsState()

    // Form states
    var fullName by remember(currentResume) { mutableStateOf(currentResume.fullName) }
    var jobTitle by remember(currentResume) { mutableStateOf(currentResume.jobTitle) }
    var email by remember(currentResume) { mutableStateOf(currentResume.email) }
    var phone by remember(currentResume) { mutableStateOf(currentResume.phone) }
    var portfolio by remember(currentResume) { mutableStateOf(currentResume.portfolio) }
    var profilePictureUrl by remember(currentResume) { mutableStateOf(currentResume.profilePictureUrl) }
    
    var company by remember(currentResume) { mutableStateOf(currentResume.company) }
    var startDate by remember(currentResume) { mutableStateOf(currentResume.startDate) }
    var endDate by remember(currentResume) { mutableStateOf(currentResume.endDate) }
    var keyAchievements by remember(currentResume) { mutableStateOf(currentResume.keyAchievements) }
    
    var skills by remember(currentResume) { mutableStateOf(currentResume.skills) }
    var degree by remember(currentResume) { mutableStateOf(currentResume.degree) }
    var school by remember(currentResume) { mutableStateOf(currentResume.school) }
    var graduationYear by remember(currentResume) { mutableStateOf(currentResume.graduationYear) }

    val presetAvatars = listOf(
        "https://lh3.googleusercontent.com/aida-public/AB6AXuD6rfR2rPfitPnMfsl8iES-KxOzSMFNDqmmSILYV9OjglVdob0DFm9erHGhR1szhRV7kzMaoIYFy3iWCJBDxKdHCoO2X4Hi9jctojpk4czJwRWOtglSVxMQHZwI_IWrcXoBw6sx0ha9Hdvt4h2XHwQJB3nqbfgaTJWn6903Mty6Q4eW-L1_COdUP8jEiiV7Rmw3MhKy-s-qOIlc5oH9zG1TvU2mseby1o_uacmXYcRZUulvIHtT441LGxQPbhuN95VQhKHgzDRNrA",
        "https://lh3.googleusercontent.com/aida-public/AB6AXuAGNHURekhJdSPETuW_4b-Z9nxIbEXoXfs7pAcioUaaSXFYfeGVC9iWr6wbqzz8LgREr2_T0yMbRe-CYEm4PhmGJeEBd4qzRw5JW3bK5s3a6YFqCLDMURSKhpUEZNNd2XycqutOWKBbfbt0iqOz0uIjl2T96n9FzS7o_ldWAEHX4XBs97jDljQQsuZs2V9MkEBVUVlg_PGJKc3PhahijjtJ7ntnJ_20W442fIqVRhEQg1dNsYAOhO7uq3MNtzM5d-NmXZ3Y3weG6g",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=256",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=256",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=256",
        "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&q=80&w=256"
    )

    // Scroll state for view content
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            shape = RoundedCornerShape(24.dp),
            color = CandyBackground,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Toolbar Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .background(CandySurfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close profile editor",
                            tint = CandySecondary
                        )
                    }

                    Text(
                        text = if (viewModel.profileEditMode) "Edit Career Profile" else "My Sweet Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = CandyTertiary,
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { 
                            viewModel.profileEditMode = !viewModel.profileEditMode 
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (viewModel.profileEditMode) CandyPrimaryContainer else CandySurfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (viewModel.profileEditMode) Icons.Default.Visibility else Icons.Default.Edit,
                            contentDescription = "Toggle edit mode",
                            tint = if (viewModel.profileEditMode) CandyOnPrimaryContainer else CandySecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content Pane
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(bottom = 12.dp)
                ) {
                    
                    // Profile Frame & Portrait
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(CandyPrimaryContainer)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            ) {
                                AsyncImage(
                                    model = profilePictureUrl.ifBlank { "https://lh3.googleusercontent.com/aida-public/AB6AXuD6rfR2rPfitPnMfsl8iES-KxOzSMFNDqmmSILYV9OjglVdob0DFm9erHGhR1szhRV7kzMaoIYFy3iWCJBDxKdHCoO2X4Hi9jctojpk4czJwRWOtglSVxMQHZwI_IWrcXoBw6sx0ha9Hdvt4h2XHwQJB3nqbfgaTJWn6903Mty6Q4eW-L1_COdUP8jEiiV7Rmw3MhKy-s-qOIlc5oH9zG1TvU2mseby1o_uacmXYcRZUulvIHtT441LGxQPbhuN95VQhKHgzDRNrA" },
                                    contentDescription = "User Portrait",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!viewModel.profileEditMode) {
                            Text(
                                text = fullName.ifBlank { "Unspecified Candidate" },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyOnBackground
                            )
                            Text(
                                text = jobTitle.ifBlank { "Dreamer / Job Seeker" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyPrimary
                            )
                        } else {
                            Text(
                                text = "Choose Profile Avatar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyOutline
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            // Row of prest image URLs
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                            ) {
                                presetAvatars.forEach { url ->
                                    val isSelected = profilePictureUrl == url
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) CandyPrimary else CandyOutlineVariant,
                                                shape = CircleShape
                                            )
                                            .clickable { profilePictureUrl = url }
                                    ) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "Preset Avatar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (viewModel.profileEditMode) {
                        // EDIT FORM
                        ProfileSectionCard(title = "IDENTITY DETAILS", icon = Icons.Default.Badge) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = jobTitle,
                                onValueChange = { jobTitle = it },
                                label = { Text("Desired Job Title") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = profilePictureUrl,
                                onValueChange = { profilePictureUrl = it },
                                label = { Text("Custom Avatar URL") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ProfileSectionCard(title = "CONTACT CHANNELS", icon = Icons.Default.ContactMail) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone Number") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = portfolio,
                                onValueChange = { portfolio = it },
                                label = { Text("Portfolio or LinkedIn Link") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ProfileSectionCard(title = "RECENT POSITION", icon = Icons.Default.Work) {
                            OutlinedTextField(
                                value = company,
                                onValueChange = { company = it },
                                label = { Text("Company Name") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = startDate,
                                    onValueChange = { startDate = it },
                                    label = { Text("Start Date") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = endDate,
                                    onValueChange = { endDate = it },
                                    label = { Text("End Date") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = keyAchievements,
                                onValueChange = { keyAchievements = it },
                                label = { Text("Key Achievements (AI advice ready)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ProfileSectionCard(title = "SKILLS & EDUCATION", icon = Icons.Default.School) {
                            OutlinedTextField(
                                value = skills,
                                onValueChange = { skills = it },
                                label = { Text("Core Skills (comma separated)") },
                                placeholder = { Text("Kotlin, Compose, Retrofit, Git") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = degree,
                                onValueChange = { degree = it },
                                label = { Text("Degree / Field of Study") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = school,
                                    onValueChange = { school = it },
                                    label = { Text("Institution / School") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1.5f)
                                )
                                OutlinedTextField(
                                    value = graduationYear,
                                    onValueChange = { graduationYear = it },
                                    label = { Text("Grad Year") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CandyPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(0.8f)
                                )
                            }
                        }
                    } else {
                        // VIEW PROFILE INFORMATION (SLEEK METRIC DETAILS)
                        ProfileViewFieldCard(title = "Contact Channels", icon = Icons.Default.ContactMail) {
                            ProfileDetailRow(icon = Icons.Default.Email, label = "Email", value = email.ifBlank { "Not set" })
                            ProfileDetailRow(icon = Icons.Default.Phone, label = "Phone", value = phone.ifBlank { "Not set" })
                            ProfileDetailRow(icon = Icons.Default.Link, label = "Portfolio Space", value = portfolio.ifBlank { "Not set" })
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ProfileViewFieldCard(title = "Professional Background", icon = Icons.Default.Work) {
                            ProfileDetailRow(icon = Icons.Default.CorporateFare, label = "Target/Active Company", value = company.ifBlank { "Not set" })
                            ProfileDetailRow(icon = Icons.Default.CalendarToday, label = "Duration", value = if (startDate.isNotBlank() || endDate.isNotBlank()) "$startDate - $endDate" else "Not set")
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Key Achievements", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CandyPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CandyBackground, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = keyAchievements.ifBlank { "Showcase your top milestones here! Click edit to add them." },
                                    fontSize = 12.sp,
                                    color = CandyOnBackground,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ProfileViewFieldCard(title = "Core Skills", icon = Icons.Default.AutoAwesome) {
                            if (skills.isBlank()) {
                                Text("No skills saved. Add them to find better matching jobs!", fontSize = 12.sp, color = CandyOutline)
                            } else {
                                val list = skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    list.forEach { skillTag ->
                                        Box(
                                            modifier = Modifier
                                                .background(CandyPrimaryContainer, RoundedCornerShape(20.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = skillTag,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CandyOnPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ProfileViewFieldCard(title = "Education Degree", icon = Icons.Default.School) {
                            ProfileDetailRow(icon = Icons.Default.HistoryEdu, label = "Degree", value = degree.ifBlank { "Not set" })
                            ProfileDetailRow(icon = Icons.Default.AccountBalance, label = "Insititution", value = school.ifBlank { "Not set" })
                            ProfileDetailRow(icon = Icons.Default.Event, label = "Graduation", value = graduationYear.ifBlank { "Not set" })
                        }
                    }
                }

                // Footers with CTA Save / Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (viewModel.profileEditMode) {
                        OutlinedButton(
                            onClick = { 
                                // Revert states to clean values
                                fullName = currentResume.fullName
                                jobTitle = currentResume.jobTitle
                                email = currentResume.email
                                phone = currentResume.phone
                                portfolio = currentResume.portfolio
                                profilePictureUrl = currentResume.profilePictureUrl
                                company = currentResume.company
                                startDate = currentResume.startDate
                                endDate = currentResume.endDate
                                keyAchievements = currentResume.keyAchievements
                                skills = currentResume.skills
                                degree = currentResume.degree
                                school = currentResume.school
                                graduationYear = currentResume.graduationYear
                                
                                viewModel.profileEditMode = false 
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, CandyOutlineVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Discard", color = CandySecondary, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.updateResumeField { resume ->
                                    resume.copy(
                                        fullName = fullName,
                                        jobTitle = jobTitle,
                                        email = email,
                                        phone = phone,
                                        portfolio = portfolio,
                                        profilePictureUrl = profilePictureUrl,
                                        company = company,
                                        startDate = startDate,
                                        endDate = endDate,
                                        keyAchievements = keyAchievements,
                                        skills = skills,
                                        degree = degree,
                                        school = school,
                                        graduationYear = graduationYear
                                    )
                                }
                                viewModel.profileEditMode = false
                                viewModel.postLocalNotification(
                                    title = "Sweeet! Profile Saved",
                                    message = "Your active student profile details were updated and synchronized across Career Candy!",
                                    type = "success"
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CandyPrimary),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Save Profile", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    } else {
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CandySecondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("All Sweeet!", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CandyOutlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = CandyPrimary, modifier = Modifier.size(16.dp))
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Black, color = CandySecondary)
            }
            content()
        }
    }
}

@Composable
fun ProfileViewFieldCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CandyOutlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = CandyPrimary, modifier = Modifier.size(16.dp))
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Black, color = CandySecondary)
            }
            content()
        }
    }
}

@Composable
fun ProfileDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CandyOutline,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CandyOutline)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = CandyOnBackground)
        }
    }
}
