package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resume_table")
data class Resume(
    @PrimaryKey val id: Int = 1, // Single profile focus
    
    // Step 1: Personal
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val portfolio: String = "",
    val profilePictureUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuD6rfR2rPfitPnMfsl8iES-KxOzSMFNDqmmSILYV9OjglVdob0DFm9erHGhR1szhRV7kzMaoIYFy3iWCJBDxKdHCoO2X4Hi9jctojpk4czJwRWOtglSVxMQHZwI_IWrcXoBw6sx0ha9Hdvt4h2XHwQJB3nqbfgaTJWn6903Mty6Q4eW-L1_COdUP8jEiiV7Rmw3MhKy-s-qOIlc5oH9zG1TvU2mseby1o_uacmXYcRZUulvIHtT441LGxQPbhuN95VQhKHgzDRNrA",
    
    // Step 2: Experience
    val jobTitle: String = "",
    val company: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val keyAchievements: String = "",
    
    // Step 3: Skills (Comma separated list)
    val skills: String = "",
    
    // Step 4: Education
    val degree: String = "",
    val school: String = "",
    val graduationYear: String = ""
)
