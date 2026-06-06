package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.JobSearchViewModel

@Composable
fun SignInScreen(
    viewModel: JobSearchViewModel,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp)
        ) {
            // App Branding Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CandyPrimaryContainer)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍬",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Welcome Text
            Text(
                text = "Career Candy",
                color = CandyPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            
            Text(
                text = "Find your sweet spot in tech.",
                color = CandyOutline,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CandySurfaceVariant),
                border = BorderStroke(1.dp, CandyOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signin_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Registration / Login switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { viewModel.authModeRegister = false; viewModel.authError = null },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!viewModel.authModeRegister) CandyPrimary else Color.Transparent,
                                contentColor = if (!viewModel.authModeRegister) Color.White else CandyOutline
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { viewModel.authModeRegister = true; viewModel.authError = null },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.authModeRegister) CandyPrimary else Color.Transparent,
                                contentColor = if (viewModel.authModeRegister) Color.White else CandyOutline
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Register", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Text(
                        text = if (viewModel.authModeRegister) "Create Free Account" else "Welcome Back",
                        color = CandyOnBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Conditional Registration fields
                    if (viewModel.authModeRegister) {
                        // Full Name Field
                        Text(
                            text = "FULL NAME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CandySecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.registerFullName,
                            onValueChange = { viewModel.registerFullName = it },
                            placeholder = { Text("e.g. Sarah Candy", color = CandyOutline) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = CandyPrimary) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CandyPrimary,
                                unfocusedBorderColor = CandyOutlineVariant,
                                focusedContainerColor = Color(0xFFFFE8F3), // Sleek Pink Background
                                unfocusedContainerColor = Color(0xFFFFE8F3),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("register_fullname")
                        )

                        // Phone Number Field
                        Text(
                            text = "PHONE NUMBER (INDIA)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CandySecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = viewModel.registerPhone,
                            onValueChange = { viewModel.registerPhone = it },
                            placeholder = { Text("e.g. +91 98765 43210", color = CandyOutline) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = CandyPrimary) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CandyPrimary,
                                unfocusedBorderColor = CandyOutlineVariant,
                                focusedContainerColor = Color(0xFFFFE8F3), // Sleek Pink Background
                                unfocusedContainerColor = Color(0xFFFFE8F3),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("register_phone")
                        )
                    }

                    // Email Field
                    Text(
                        text = "EMAIL ADDRESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CandySecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.authEmail,
                        onValueChange = { viewModel.authEmail = it },
                        placeholder = { Text("you@career.com", color = CandyOutline) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = CandyPrimary) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CandyPrimary,
                            unfocusedBorderColor = CandyOutlineVariant,
                            focusedContainerColor = Color(0xFFFFE8F3), // Sleek Pink Background
                            unfocusedContainerColor = Color(0xFFFFE8F3),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("signin_email")
                    )

                    // Password Field
                    Text(
                        text = "PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CandySecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.authPassword,
                        onValueChange = { viewModel.authPassword = it },
                        placeholder = { Text("••••••••", color = CandyOutline) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = CandyPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = CandySecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CandyPrimary,
                            unfocusedBorderColor = CandyOutlineVariant,
                            focusedContainerColor = Color(0xFFFFE8F3), // Sleek Pink Background
                            unfocusedContainerColor = Color(0xFFFFE8F3),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signin_password")
                    )

                    // Error feedback
                    viewModel.authError?.let { error ->
                        Text(
                            text = error,
                            color = Color(0xFFBA1A1A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .testTag("signin_error")
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Button(
                        onClick = { 
                            if (viewModel.authModeRegister) {
                                viewModel.register()
                            } else {
                                viewModel.signIn()
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CandyPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("signin_submit")
                    ) {
                        Text(
                            text = if (viewModel.authModeRegister) "Complete Registration 🍬" else "Authenticate",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Demo Bypass
            TextButton(
                onClick = { viewModel.bypassSignIn() },
                modifier = Modifier.testTag("signin_bypass")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Sign In as Sarah Candy",
                        color = CandyPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "→",
                        color = CandyPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
