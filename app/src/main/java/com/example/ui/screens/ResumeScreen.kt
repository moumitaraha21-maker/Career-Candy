package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Resume
import com.example.ui.theme.*
import com.example.ui.viewmodel.JobSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeScreen(
    viewModel: JobSearchViewModel,
    modifier: Modifier = Modifier
) {
    val resume by viewModel.userResume.collectAsState()
    var showPreviewModal by remember { mutableStateOf(false) }

    // Premium Version Features (500 Rupee Purchase)
    val isPremium by viewModel.isPremiumUnlocked.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }
    var selectedTemplateTheme by remember { mutableStateOf("Sleek Minimal") }

    // Simulated UPI Payment Gateway
    var isPaying by remember { mutableStateOf(false) }
    var upiInput by remember { mutableStateOf("") }
    var upiError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // App top header
        AppHeader(viewModel = viewModel)

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            
            // Progress Stepper Component
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CandyBackground),
                border = BorderStroke(1.dp, CandyOutlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stepper Segment 1: Personal
                    StepNode(
                        stepNumber = 1,
                        stepTitle = "Personal",
                        activeStep = viewModel.currentResumeStep,
                        onClick = { viewModel.setResumeStep(1) }
                    )

                    // Line
                    Divider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = CandyOutlineVariant)

                    // Stepper Segment 2: Experience
                    StepNode(
                        stepNumber = 2,
                        stepTitle = "Experience",
                        activeStep = viewModel.currentResumeStep,
                        onClick = { viewModel.setResumeStep(2) }
                    )

                    // Line
                    Divider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = CandyOutlineVariant)

                    // Stepper Segment 3: Skills
                    StepNode(
                        stepNumber = 3,
                        stepTitle = "Skills",
                        activeStep = viewModel.currentResumeStep,
                        onClick = { viewModel.setResumeStep(3) }
                    )

                    // Line
                    Divider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = CandyOutlineVariant)

                    // Stepper Segment 4: Education
                    StepNode(
                        stepNumber = 4,
                        stepTitle = "Education",
                        activeStep = viewModel.currentResumeStep,
                        onClick = { viewModel.setResumeStep(4) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Info Banner Card (500 Rupee Plan)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremium) Color(0xFFF3E8FF) else Color(0xFFFFF4D4)
                ),
                border = BorderStroke(1.dp, if (isPremium) CandySecondary.copy(alpha = 0.4f) else Color(0xFFFFD485)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (isPremium) CandySecondary else Color(0xFFFFA000),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPremium) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                            contentDescription = "Status",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPremium) "👑 Resume Builder Pro Active" else "🛡️ Unlock Premium Resume Builder",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = if (isPremium) CandySecondary else Color(0xFF7A5C00)
                        )
                        Text(
                            text = if (isPremium) "Enjoy premium 'Cosmic Sapphire' & 'Royal Emerald' templates with unlimited AI optimizations!" else "Gain access to premium executive layouts & AI content enhancer for just ₹500",
                            fontSize = 11.sp,
                            color = if (isPremium) CandySecondary.copy(alpha = 0.8f) else Color(0xFF7A5C00).copy(alpha = 0.8f),
                            lineHeight = 15.sp
                        )
                    }

                    if (!isPremium) {
                        Button(
                            onClick = { showPaymentDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Upgrade ₹500", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(CandySecondary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Pro: $selectedTemplateTheme",
                                fontWeight = FontWeight.Bold,
                                color = CandySecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stepper Form Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Form entry section
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = when (viewModel.currentResumeStep) {
                            1 -> "Workplace Profile"
                            2 -> "Work Experience"
                            3 -> "Professional Skills"
                            else -> "Education History"
                        },
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                        color = CandyOnBackground,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = when (viewModel.currentResumeStep) {
                            1 -> "Let us get to know your professional background contact details."
                            2 -> "Tell us about your professional journey. Add your most recent roles first."
                            3 -> "Declare the tech stack tags and certifications that define your expertise."
                            else -> "Where did you study? Recruiters appreciate seeing historic degrees."
                        },
                        color = CandyOutline,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Current step form renderer
                    AnimatedContent(
                        targetState = viewModel.currentResumeStep,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { targetStep ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CandyOutlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                when (targetStep) {
                                    1 -> {
                                        // Profile Photo Picker Section
                                        Text(
                                            text = "Professional Profile Picture",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp,
                                            color = CandySecondary
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(CircleShape)
                                                    .background(CandyBackground)
                                                    .border(2.dp, CandyPrimary, CircleShape)
                                            ) {
                                                AsyncImage(
                                                    model = resume.profilePictureUrl.ifBlank { "https://lh3.googleusercontent.com/aida-public/AB6AXuD6rfR2rPfitPnMfsl8iES-KxOzSMFNDqmmSILYV9OjglVdob0DFm9erHGhR1szhRV7kzMaoIYFy3iWCJBDxKdHCoO2X4Hi9jctojpk4czJwRWOtglSVxMQHZwI_IWrcXoBw6sx0ha9Hdvt4h2XHwQJB3nqbfgaTJWn6903Mty6Q4eW-L1_COdUP8jEiiV7Rmw3MhKy-s-qOIlc5oH9zG1TvU2mseby1o_uacmXYcRZUulvIHtT441LGxQPbhuN95VQhKHgzDRNrA" },
                                                    contentDescription = "Chosen Profile Picture",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Select an elegant corporate avatar, or supply your own photo URL to show recruiters.",
                                                    fontSize = 11.sp,
                                                    color = CandyOutline,
                                                    lineHeight = 15.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }

                                        // Horizontal scroll of classic default avatars
                                        val avatarOptions = listOf(
                                            "https://lh3.googleusercontent.com/aida-public/AB6AXuD6rfR2rPfitPnMfsl8iES-KxOzSMFNDqmmSILYV9OjglVdob0DFm9erHGhR1szhRV7kzMaoIYFy3iWCJBDxKdHCoO2X4Hi9jctojpk4czJwRWOtglSVxMQHZwI_IWrcXoBw6sx0ha9Hdvt4h2XHwQJB3nqbfgaTJWn6903Mty6Q4eW-L1_COdUP8jEiiV7Rmw3MhKy-s-qOIlc5oH9zG1TvU2mseby1o_uacmXYcRZUulvIHtT441LGxQPbhuN95VQhKHgzDRNrA", // Sarah Portrait
                                            "https://lh3.googleusercontent.com/aida-public/AB6AXuAGNHURekhJdSPETuW_4b-Z9nxIbEXoXfs7pAcioUaaSXFYfeGVC9iWr6wbqzz8LgREr2_T0yMbRe-CYEm4PhmGJeEBd4qzRw5JW3bK5s3a6YFqCLDMURSKhpUEZNNd2XycqutOWKBbfbt0iqOz0uIjl2T96n9FzS7o_ldWAEHX4XBs97jDljQQsuZs2V9MkEBVUVlg_PGJKc3PhahijjtJ7ntnJ_20W442fIqVRhEQg1dNsYAOhO7uq3MNtzM5d-NmXZ3Y3weG6g", // Sarah Smiley
                                            "https://lh3.googleusercontent.com/aida-public/AB6AXuCd3z7-taYgpUinb4EuYQf1eooBT-oGKRwAAeCRSe3-wEtPL8fMKOjerFl-2aLndEHC6YF1Az8sMFgi3kQwqXSRPGLQdOMZ0I4XQl2t27NFa9_m0KTWZFF5pcW98XXomuSwVf9gIjY9jL2XciSSpWdOBeokJ5exa9oGR3YfOG4EquS34xql3VZ-BfqcEU8L_2uwBL2euw64sg_T9mOrV-FW_cgO1xDElZl9tzQSf06RcEIlQo7dT3KGjxPRmSPxbOSUxG0dpROgOg", // Standard Tech Female
                                            "https://lh3.googleusercontent.com/aida-public/AB6AXuDGJ9IgTj2MUHQdQamwLplfLOawICGTYaYD83LOkYWTf_H2z5hdCUyeetoN2BS8TRyn1zinS2-ROs6hEhYkqfb8qiup4VubdBjEH1M_FQyTU7nNtTgR3p8-FfPU4I8Ytvpglz6k25cb6Ja9e71h9YfKN9JnHFXpQVvSMtuCfP3NjZ6L5u00lQdhv6e2jY5IwoaJSNxs0H9M9AhBF2WWPC4MUDwIdip6pnGhyVuzHVhsvwMykzrbP1fxZNetpO6kMLYmBCMQO17VBQ", // Classic Tech Male
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState())
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            avatarOptions.forEachIndexed { index, avatarUrl ->
                                                val isSelected = resume.profilePictureUrl == avatarUrl
                                                Box(
                                                    modifier = Modifier
                                                        .size(54.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) CandyPrimaryContainer else Color.Transparent)
                                                        .border(
                                                            width = if (isSelected) 3.dp else 1.dp,
                                                            color = if (isSelected) CandyPrimary else CandyOutlineVariant,
                                                            shape = CircleShape
                                                        )
                                                        .clickable {
                                                            viewModel.updateResumeField { it.copy(profilePictureUrl = avatarUrl) }
                                                        }
                                                        .padding(if (isSelected) 3.dp else 0.dp)
                                                ) {
                                                    AsyncImage(
                                                        model = avatarUrl,
                                                        contentDescription = "Avatar Dynamic ${index + 1}",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(CircleShape)
                                                    )
                                                }
                                            }
                                        }

                                        ResumeInputField(
                                            label = "Or Custom Image URL Link",
                                            value = resume.profilePictureUrl,
                                            placeholder = "e.g. https://yourphoto.com/image.jpg",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(profilePictureUrl = newValue) }
                                            }
                                        )

                                        Divider(color = CandyOutlineVariant, modifier = Modifier.padding(vertical = 4.dp))

                                        // Personal Form
                                        ResumeInputField(
                                            label = "Full Name",
                                            value = resume.fullName,
                                            placeholder = "e.g. Sarah Candy",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(fullName = newValue) }
                                            }
                                        )
                                        ResumeInputField(
                                            label = "Email Address",
                                            value = resume.email,
                                            placeholder = "e.g. sarah.candy@creative.com",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(email = newValue) }
                                            }
                                        )
                                        ResumeInputField(
                                            label = "Phone Number",
                                            value = resume.phone,
                                            placeholder = "e.g. +1 (555) 727-2263",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(phone = newValue) }
                                            }
                                        )
                                        ResumeInputField(
                                            label = "Portfolio/Website",
                                            value = resume.portfolio,
                                            placeholder = "e.g. creativecandy.design",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(portfolio = newValue) }
                                            }
                                        )
                                    }
                                    2 -> {
                                        // Experience Form
                                        ResumeInputField(
                                            label = "Job Title",
                                            value = resume.jobTitle,
                                            placeholder = "e.g. Senior UI Designer",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(jobTitle = newValue) }
                                            }
                                        )
                                        ResumeInputField(
                                            label = "Company Name",
                                            value = resume.company,
                                            placeholder = "e.g. Creative Candy Co.",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(company = newValue) }
                                            }
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                ResumeInputField(
                                                    label = "Start Date",
                                                    value = resume.startDate,
                                                    placeholder = "2024-01",
                                                    onValueChange = { newValue ->
                                                        viewModel.updateResumeField { it.copy(startDate = newValue) }
                                                    }
                                                )
                                            }
                                            Box(modifier = Modifier.weight(1f)) {
                                                ResumeInputField(
                                                    label = "End Date",
                                                    value = resume.endDate,
                                                    placeholder = "Present",
                                                    onValueChange = { newValue ->
                                                        viewModel.updateResumeField { it.copy(endDate = newValue) }
                                                    }
                                                )
                                            }
                                        }

                                        // Achievements block with AI trigger
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Key Achievements", fontWeight = FontWeight.Bold, color = CandyOnBackground, fontSize = 14.sp)
                                                
                                                if (viewModel.isEnhancingResume) {
                                                    CircularProgressIndicator(
                                                        color = CandyPrimary,
                                                        modifier = Modifier.size(16.dp),
                                                        strokeWidth = 2.dp
                                                    )
                                                } else {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                        modifier = Modifier
                                                            .clickable { 
                                                                if (isPremium) {
                                                                    viewModel.triggerAIEnhance()
                                                                } else {
                                                                    showPaymentDialog = true
                                                                }
                                                            }
                                                            .background(if (isPremium) CandyPrimaryContainer else Color(0xFFFFF1C5), RoundedCornerShape(12.dp))
                                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        if (!isPremium) {
                                                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFFC49000), modifier = Modifier.size(11.dp))
                                                        }
                                                        Text(
                                                            text = if (isPremium) "AI Enhancer active" else "AI Enhancer (₹500 Premium)",
                                                            color = if (isPremium) CandyPrimary else Color(0xFFC49000),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                }
                                            }
                                            OutlinedTextField(
                                                value = resume.keyAchievements,
                                                onValueChange = { newValue ->
                                                    viewModel.updateResumeField { it.copy(keyAchievements = newValue) }
                                                },
                                                placeholder = { Text("Briefly describe your impact...", color = CandyOutline) },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = CandyPrimary,
                                                    unfocusedBorderColor = CandyOutlineVariant,
                                                    focusedContainerColor = CandyBackground,
                                                    unfocusedContainerColor = CandyBackground
                                                ),
                                                shape = RoundedCornerShape(16.dp),
                                                minLines = 4,
                                                maxLines = 6,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                    3 -> {
                                        // Skills Form
                                        ResumeInputField(
                                            label = "Technical Skills (comma-separated)",
                                            value = resume.skills,
                                            placeholder = "e.g. UX Design, Prototyping, Figma, React",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(skills = newValue) }
                                            }
                                        )

                                        Text("Preview Skill Badges", color = CandyOutline, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        val skillsList = resume.skills.split(",").filter { it.isNotBlank() }
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (skillsList.isEmpty()) {
                                                Text("Skills will render as color badges here...", color = CandyOutline.copy(alpha = 0.6f), fontSize = 11.sp)
                                            } else {
                                                skillsList.forEach { skill ->
                                                    Box(
                                                        modifier = Modifier
                                                            .background(CandySecondaryContainer, RoundedCornerShape(12.dp))
                                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                                    ) {
                                                        Text(skill.trim(), color = CandySecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        // Education Form
                                        ResumeInputField(
                                            label = "Degree / Course name",
                                            value = resume.degree,
                                            placeholder = "e.g. Master of Business Administration",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(degree = newValue) }
                                            }
                                        )
                                        ResumeInputField(
                                            label = "School / University",
                                            value = resume.school,
                                            placeholder = "e.g. Harvard University",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(school = newValue) }
                                            }
                                        )
                                        ResumeInputField(
                                            label = "Graduation Year",
                                            value = resume.graduationYear,
                                            placeholder = "e.g. 2023",
                                            onValueChange = { newValue ->
                                                viewModel.updateResumeField { it.copy(graduationYear = newValue) }
                                            }
                                        )
                                    }
                                }

                                // Stepper navigation buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextButton(
                                        onClick = { viewModel.prevResumeStep() },
                                        enabled = viewModel.currentResumeStep > 1,
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Previous", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { 
                                            if (viewModel.currentResumeStep == 4) {
                                                showPreviewModal = true
                                            } else {
                                                viewModel.nextResumeStep()
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CandyPrimary)
                                    ) {
                                        Text(
                                            if (viewModel.currentResumeStep == 4) "Preview Form" else "Continue",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sidebar tip & mini preview
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Neon Expert Tip
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CandyTertiary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Lightbulb, contentDescription = "Tip", tint = Color.White)
                        }

                        Column {
                            Text(
                                "Expert Tip",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Focus on quantifiable achievements. Use phrases like \"Increased sales by 20%\" or \"Optimized loading metrics by 35%\" to hook recruiters.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // AI and Grammar mini Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CandySecondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Enhancer", tint = CandySecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("AI Resume Enhancer", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CandyOnSecondaryContainer)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CandyPrimaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Spellcheck, contentDescription = "Spellcheck", tint = CandyPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Grammar Check Active", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CandyOnPrimaryContainer)
                        }
                    }
                }

                // Tablet Graphic Resume Preview
                Text(
                    "Live Resume Preview",
                    style = MaterialTheme.typography.titleMedium,
                    color = CandyOnBackground,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, CandyOutlineVariant, RoundedCornerShape(24.dp))
                        .clickable { showPreviewModal = true }
                ) {
                    // tablet mockup device photo
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCd3z7-taYgpUinb4EuYQf1eooBT-oGKRwAAeCRSe3-wEtPL8fMKOjerFl-2aLndEHC6YF1Az8sMFgi3kQwqXSRPGLQdOMZ0I4XQl2t27NFa9_m0KTWZFF5pcW98XXomuSwVf9gIjY9jL2XciSSpWdOBeokJ5exa9oGR3YfOG4EquS34xql3VZ-BfqcEU8L_2uwBL2euw64sg_T9mOrV-FW_cgO1xDElZl9tzQSf06RcEIlQo7dT3KGjxPRmSPxbOSUxG0dpROgOg",
                        contentDescription = "Tablet preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Touch to Expand Mode",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(Icons.Default.ZoomIn, contentDescription = "Expand", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Floating Expandable interactive dialog
    if (showPreviewModal) {
        // Theme color calculations
        val themeBgColor = when (selectedTemplateTheme) {
            "Cosmic Sapphire" -> Color(0xFF0F1E36)
            "Royal Emerald" -> Color(0xFF0F3622)
            else -> CandyPrimaryContainer
        }
        val themeTextColor = when (selectedTemplateTheme) {
            "Cosmic Sapphire" -> Color.White
            "Royal Emerald" -> Color.White
            else -> CandyOnPrimaryContainer
        }
        val themeLabelColor = when (selectedTemplateTheme) {
            "Cosmic Sapphire" -> Color(0xFF4CC9F0)
            "Royal Emerald" -> Color(0xFFE2C044)
            else -> CandyPrimary
        }
        val themeSecondaryLabelColor = when (selectedTemplateTheme) {
            "Cosmic Sapphire" -> Color(0xFFBDC9D6)
            "Royal Emerald" -> Color(0xFFD4E2D7)
            else -> CandySecondary
        }

        AlertDialog(
            onDismissRequest = { showPreviewModal = false },
            confirmButton = {
                Button(
                    onClick = { showPreviewModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CandyPrimary)
                ) {
                    Text("Close Preview", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Elegant Template Switcher segment
                    Text(
                        "SELECT EXECUTIVE TEMPLATE STYLE",
                        fontWeight = FontWeight.Bold,
                        color = CandyOutline,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val templates = listOf("Sleek Minimal", "Cosmic Sapphire", "Royal Emerald")
                        templates.forEach { temp ->
                            val isSelected = selectedTemplateTheme == temp
                            val locked = temp != "Sleek Minimal" && !isPremium

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) CandyPrimaryContainer else Color(0xFFFAFAFA)
                                ),
                                border = BorderStroke(1.dp, if (isSelected) CandyPrimary else CandyOutlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (locked) {
                                            showPaymentDialog = true
                                        } else {
                                            selectedTemplateTheme = temp
                                        }
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (locked) {
                                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFFC49000), modifier = Modifier.size(13.dp))
                                    } else {
                                        Icon(Icons.Default.Star, contentDescription = "Active", tint = CandyPrimary, modifier = Modifier.size(13.dp))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = temp,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isSelected) CandyPrimary else CandyOnBackground,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Styled Dynamic Resume PDF header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(themeBgColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        // Dynamic Profile Picture on Visual Mockup
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(themeTextColor.copy(alpha = 0.15f))
                                .border(2.dp, themeLabelColor, CircleShape)
                                .padding(2.dp)
                        ) {
                            AsyncImage(
                                model = resume.profilePictureUrl.ifBlank { "https://lh3.googleusercontent.com/aida-public/AB6AXuD6rfR2rPfitPnMfsl8iES-KxOzSMFNDqmmSILYV9OjglVdob0DFm9erHGhR1szhRV7kzMaoIYFy3iWCJBDxKdHCoO2X4Hi9jctojpk4czJwRWOtglSVxMQHZwI_IWrcXoBw6sx0ha9Hdvt4h2XHwQJB3nqbfgaTJWn6903Mty6Q4eW-L1_COdUP8jEiiV7Rmw3MhKy-s-qOIlc5oH9zG1TvU2mseby1o_uacmXYcRZUulvIHtT441LGxQPbhuN95VQhKHgzDRNrA" },
                                contentDescription = "Live PDF Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = resume.fullName.ifBlank { "Sarah Candy" },
                            style = MaterialTheme.typography.titleLarge,
                            color = themeTextColor,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = resume.jobTitle.ifBlank { "Senior UI Designer" },
                            style = MaterialTheme.typography.bodySmall,
                            color = themeLabelColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${resume.email.ifBlank { "sarah.candy@creative.com" }} • ${resume.phone.ifBlank { "+91 98765 43210" }}",
                            color = themeTextColor.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = resume.portfolio.ifBlank { "creativecandy.design" },
                            color = themeSecondaryLabelColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Section: Experience
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("WORK EXPERIENCE", fontWeight = FontWeight.Black, color = themeLabelColor, fontSize = 12.sp)
                        Divider(color = themeLabelColor.copy(alpha = 0.3f))
                        Text(
                            text = resume.jobTitle.ifBlank { "Senior UI Designer" },
                            fontWeight = FontWeight.Bold,
                            color = CandyOnBackground
                        )
                        Text(
                            text = "${resume.company.ifBlank { "Creative Candy Co." }} | ${resume.startDate.ifBlank { "2024-01" }} - ${resume.endDate.ifBlank { "Present" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CandyOutline,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = resume.keyAchievements.ifBlank { "Focus on quantifiable achievements. Use phrases like \"Increased sales by 20%\" or \"Reduced churn by 15%\" to catch recruiters' eyes." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = CandyOnBackground
                        )
                    }

                    // Section: Skills
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("PROFESSIONAL SKILLS", fontWeight = FontWeight.Black, color = themeSecondaryLabelColor, fontSize = 12.sp)
                        Divider(color = themeSecondaryLabelColor.copy(alpha = 0.3f))
                        val skillList = resume.skills.split(",").filter { it.isNotBlank() }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (skillList.isEmpty()) {
                                Text("Figma, UX Architecture, Jetpack Compose, User Interface Research", style = MaterialTheme.typography.bodySmall)
                            } else {
                                skillList.forEach { skill ->
                                    Box(
                                        modifier = Modifier
                                            .background(CandyBackground, RoundedCornerShape(12.dp))
                                            .border(1.dp, themeSecondaryLabelColor, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(skill.trim(), color = themeSecondaryLabelColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Section: Education
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("EDUCATION BACKGROUND", fontWeight = FontWeight.Black, color = themeLabelColor, fontSize = 12.sp)
                        Divider(color = themeLabelColor.copy(alpha = 0.3f))
                        Text(
                            text = resume.degree.ifBlank { "BTech in Computer Science" },
                            fontWeight = FontWeight.Bold,
                            color = CandyOnBackground
                        )
                        Text(
                            text = "${resume.school.ifBlank { "IIT Bombay" }} | Class of ${resume.graduationYear.ifBlank { "2023" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CandyOutline
                        )
                    }
                }
            }
        )
    }

    // UPI Payment Gateway Dialog Overlay (One-time ₹500 pay)
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (upiInput.isBlank() || !upiInput.contains("@")) {
                            upiError = "Please enter a valid UPI ID (e.g. name@okhdfcbank)"
                        } else {
                            upiError = null
                            isPaying = true
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(2000)
                                isPaying = false
                                viewModel.unlockPremium()
                                showPaymentDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CandyPrimary),
                    enabled = !isPaying,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    if (isPaying) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Pay ₹500 via Secure UPI 🛡️", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPaymentDialog = false; upiError = null },
                    enabled = !isPaying
                ) {
                    Text("Cancel", color = CandyOutline)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Premium Upgrade",
                        tint = CandyPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Upgrade to Premium",
                        color = CandyOnBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Unlock high-fidelity 'Cosmic Sapphire' & 'Royal Emerald' templates, advanced AI metrics optimization, and premium recruiter visibility!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CandyOnBackground.copy(alpha = 0.8f)
                    )

                    Divider(color = CandyOutlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Subtotal Plan", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("₹500.00", fontWeight = FontWeight.Black, color = CandyPrimary)
                    }

                    OutlinedTextField(
                        value = upiInput,
                        onValueChange = { upiInput = it; upiError = null },
                        placeholder = { Text("e.g. sarahcandy@paytm") },
                        label = { Text("Enter your UPI Address", fontWeight = FontWeight.Bold) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CandyPrimary,
                            unfocusedBorderColor = CandyOutlineVariant,
                            focusedContainerColor = CandyBackground,
                            unfocusedContainerColor = CandyBackground
                        ),
                        leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = "Card", tint = CandyPrimary) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (upiError != null) {
                        Text(upiError!!, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = "🛡️ 100% Secure transaction powered by NPCI-certified UPI systems.",
                            fontSize = 11.sp,
                            color = CandyOutline
                        )
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun StepNode(
    stepNumber: Int,
    stepTitle: String,
    activeStep: Int,
    onClick: () -> Unit
) {
    val isCompleted = stepNumber < activeStep
    val isActive = stepNumber == activeStep

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = when {
                        isCompleted -> CandySecondaryContainer
                        isActive -> CandyPrimary
                        else -> CandySurfaceVariant
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = "Completed", tint = CandySecondary, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = stepNumber.toString(),
                    color = if (isActive) Color.White else CandyOutline,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stepTitle,
            color = if (isActive) CandyPrimary else CandyOutline,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ResumeInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = CandyOnBackground,
            fontSize = 14.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = CandyOutline.copy(alpha = 0.5f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CandyPrimary,
                unfocusedBorderColor = CandyOutlineVariant,
                focusedContainerColor = CandyBackground,
                unfocusedContainerColor = CandyBackground
            ),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    horizontalArrangement: Arrangement.Horizontal,
    verticalArrangement: Arrangement.Vertical,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}
