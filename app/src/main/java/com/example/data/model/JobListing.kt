package com.example.data.model

data class JobListing(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val logoUrl: String,
    val workType: String, // e.g. Remote, Hybrid, On-site
    val tags: List<String>, // e.g. Full-time, Unlimited PTO, Stock Options, Bonus Equity
    val isHot: Boolean = false,
    val isFeatured: Boolean = false,
    val isFavorite: Boolean = false,
    val matchPercentage: Int = 85,
    val applicationStatus: String? = null // e.g. "Applied", "Interview Scheduled", "Under Review"
)
