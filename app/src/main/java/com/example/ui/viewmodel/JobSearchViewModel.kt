package com.example.ui.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.JobListing
import com.example.data.model.Resume
import com.example.data.model.ChatMessage
import com.example.data.model.NotificationItem
import com.example.data.repository.ResumeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTab {
    DISCOVER, SEARCH, RESUME, STATUS, ASSISTANT
}

class JobSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val resumeDao = AppDatabase.getDatabase(application).resumeDao()
    private val repository = ResumeRepository(resumeDao)

    // Authentication State
    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    var authEmail by mutableStateOf("")
    var authPassword by mutableStateOf("")
    var authError by mutableStateOf<String?>(null)

    // Registration Additions
    var authModeRegister by mutableStateOf(false)
    var registerFullName by mutableStateOf("")
    var registerPhone by mutableStateOf("")

    // Premium Version for Resume Builder State
    private val _isPremiumUnlocked = MutableStateFlow(false)
    val isPremiumUnlocked: StateFlow<Boolean> = _isPremiumUnlocked.asStateFlow()

    // Interactive Notification history
    private val _notificationHistory = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notificationHistory: StateFlow<List<NotificationItem>> = _notificationHistory.asStateFlow()

    var showNotificationInbox by mutableStateOf(false)
    var showProfileModal by mutableStateOf(false)
    var profileEditMode by mutableStateOf(false)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Career Candy Alerts"
            val descriptionText = "Alerts about matching jobs and application status changes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("career_candy_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun postLocalNotification(title: String, message: String, type: String = "system") {
        val sdf = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
        val currentTime = sdf.format(Date())

        val newItem = NotificationItem(
            title = title,
            body = message,
            timestamp = currentTime,
            type = type
        )
        _notificationHistory.value = listOf(newItem) + _notificationHistory.value

        // Post status-bar android notification
        try {
            val context = getApplication<Application>()
            val builder = NotificationCompat.Builder(context, "career_candy_channel")
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(newItem.id.hashCode(), builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unlockPremium() {
        _isPremiumUnlocked.value = true
        postLocalNotification(
            title = "👑 Premium Unlocked!",
            message = "Congratulations! You have successfully upgraded to Resume Builder Premium (₹500). Enjoy unlimited AI enhancements and exclusive themes!",
            type = "premium"
        )
    }

    fun clearNotifications() {
        _notificationHistory.value = emptyList()
    }

    fun register() {
        if (authEmail.isBlank() || authPassword.isBlank() || registerFullName.isBlank() || registerPhone.isBlank()) {
            authError = "Please fill in all registration fields"
            return
        }
        if (!authEmail.contains("@")) {
            authError = "Please enter a valid email address"
            return
        }
        authError = null
        _isSignedIn.value = true
        viewModelScope.launch {
            updateResumeField { resume ->
                resume.copy(
                    fullName = registerFullName,
                    email = authEmail,
                    phone = registerPhone
                )
            }
            // Trigger a welcoming registration notification!
            postLocalNotification(
                title = "🎉 Account Registered!",
                message = "Welcome to Career Candy, $registerFullName! Your Indian professional tech carrier profile is active.",
                type = "system"
            )
        }
    }

    fun signIn() {
        if (authEmail.isBlank() || authPassword.isBlank()) {
            authError = "Please enter valid credentials"
            return
        }
        if (!authEmail.contains("@")) {
            authError = "Please enter a valid email address"
            return
        }
        authError = null
        _isSignedIn.value = true
        // Set profile name based on email to customize the experience!
        viewModelScope.launch {
            val shortName = authEmail.substringBefore("@")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            updateResumeField { resume ->
                resume.copy(fullName = "$shortName Candy", email = authEmail)
            }
        }
    }

    fun bypassSignIn() {
        authEmail = "sarah.candy@creative.com"
        authPassword = "password"
        authError = null
        _isSignedIn.value = true
    }

    fun signOut() {
        _isSignedIn.value = false
        authEmail = ""
        authPassword = ""
        authError = null
    }

    // Interactive Sidebar Trigger
    var isDrawerOpen by mutableStateOf(false)

    fun toggleDrawer() {
        isDrawerOpen = !isDrawerOpen
    }

    fun closeDrawer() {
        isDrawerOpen = false
    }

    // Current Navigation Tab
    var currentTab by mutableStateOf(AppTab.DISCOVER)
        private set

    fun setTab(tab: AppTab) {
        currentTab = tab
    }

    // Resume Stepper
    var currentResumeStep by mutableStateOf(1) // 1: Personal, 2: Experience, 3: Skills, 4: Education
        private set

    fun nextResumeStep() {
        if (currentResumeStep < 4) currentResumeStep++
    }

    fun prevResumeStep() {
        if (currentResumeStep > 1) currentResumeStep--
    }

    fun setResumeStep(step: Int) {
        if (step in 1..4) currentResumeStep = step
    }

    // Search and Filter Parameters
    var searchQueryInput by mutableStateOf("")
    var activeSearchQuery by mutableStateOf("")
    var selectedFilterCategory by mutableStateOf("Discover") // "Discover", "Remote", "Full-time", "25+ LPA", "Bengaluru", "Mumbai"

    // Advanced Filter State variables
    var filterLocation by mutableStateOf("All") // "All", "Bengaluru", "Mumbai", "Delhi NCR", "Pune", "Hyderabad", "Remote"
    var filterWorkType by mutableStateOf("All") // "All", "Remote", "Hybrid", "On-site"
    var filterSalaryRange by mutableStateOf("All") // "All", "Under 15 LPA", "15 - 30 LPA", "30+ LPA"
    var filterSalaryMin by mutableStateOf(0f)
    var filterSalaryMax by mutableStateOf(60f)
    var isFilterPanelExpanded by mutableStateOf(false)

    // Chat agent states
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("1", "Namaste! I'm Candy Agent, your Career Candy AI recruiter & career companion for India. 🍬 Ask me anything about landed tech roles in Bengaluru, Mumbai, Pune, Hyderabad, or Delhi NCR (at Swiggy, Zomato, Reliance Jio, Paytm, PhonePe, Razorpay, etc.)!", false)
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    var chatInput by mutableStateOf("")
    var isChatLoading by mutableStateOf(false)

    fun sendChatMessage() {
        val input = chatInput.trim()
        if (input.isBlank()) return

        chatInput = ""
        val userId = System.currentTimeMillis().toString()
        val userMsg = ChatMessage(id = userId, text = input, isUser = true)
        
        _chatMessages.value = _chatMessages.value + userMsg
        isChatLoading = true

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val apiKey = com.example.BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                kotlinx.coroutines.delay(1200)
                val responseText = generateSimulatedResponse(input)
                val botId = (System.currentTimeMillis() + 1).toString()
                _chatMessages.value = _chatMessages.value + ChatMessage(id = botId, text = responseText, isUser = false)
                isChatLoading = false
                return@launch
            }

            try {
                val payload = buildGeminiPayload(_chatMessages.value)
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody = payload.toRequestBody(mediaType)
                
                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val request = okhttp3.Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string() ?: ""
                    if (response.isSuccessful && bodyString.isNotBlank()) {
                        val responseText = parseGeminiText(bodyString)
                        val botId = (System.currentTimeMillis() + 1).toString()
                        _chatMessages.value = _chatMessages.value + ChatMessage(id = botId, text = responseText, isUser = false)
                    } else {
                        val errorText = "Oops! I encountered an error communicating with the AI. Error code: ${response.code}."
                        val botId = (System.currentTimeMillis() + 1).toString()
                        _chatMessages.value = _chatMessages.value + ChatMessage(id = botId, text = errorText, isUser = false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val fallbackText = "Sorry, I had trouble connecting to the network. Let's talk offline! (Local assistance: ${generateSimulatedResponse(input)})"
                val botId = (System.currentTimeMillis() + 1).toString()
                _chatMessages.value = _chatMessages.value + ChatMessage(id = botId, text = fallbackText, isUser = false)
            } finally {
                isChatLoading = false
            }
        }
    }

    private fun buildGeminiPayload(messages: List<ChatMessage>): String {
        val contentsJson = messages.joinToString(separator = ",") { msg ->
            val role = if (msg.isUser) "user" else "model"
            val escapedText = msg.text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
            """{"role": "$role", "parts": [{"text": "$escapedText"}]}"""
        }
        
        return """
        {
          "contents": [$contentsJson],
          "systemInstruction": {
            "parts": [
              {
                "text": "You are 'Candy Agent', Career Candy's expert tech recruiter and AI career mentor for India. You guide job seekers to land their sweet-spot careers in Indian organizations (like Swiggy, Zomato, Reliance Jio, Paytm, PhonePe, Razorpay, Flipkart, TCS, Infosys, Wipro) and thriving Indian tech hubs (Bengaluru, Gurgaon/Noida NCR, Mumbai, Hyderabad, Pune, Chennai). Your tone is always cheerful, encouraging, professional, and sweet. Keep replies helpful, well-structured with bullet points if applicable, and concise, under 3 short paragraphs."
              }
            ]
          }
        }
        """.trimIndent()
    }

    private fun parseGeminiText(jsonResponse: String): String {
        try {
            val parts = jsonResponse.split("\"text\":")
            if (parts.size > 1) {
                val candidatePart = parts[1].trim()
                if (candidatePart.startsWith("\"")) {
                    val endQuoteIndex = findEndQuote(candidatePart)
                    if (endQuoteIndex != -1) {
                        val escapedText = candidatePart.substring(1, endQuoteIndex)
                        return unescapeJsonString(escapedText)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return "I processed your request, but had issues parsing the response. How else can I assist you with your Indian tech job search?"
    }

    private fun findEndQuote(s: String): Int {
        var escaped = false
        for (i in 1 until s.length) {
            val c = s[i]
            if (escaped) {
                escaped = false
            } else if (c == '\\') {
                escaped = true
            } else if (c == '"') {
                return i
            }
        }
        return -1
    }

    private fun unescapeJsonString(s: String): String {
        return s.replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
    }

    private fun generateSimulatedResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("resume") -> {
                "For Indian tech roles, your resume should highlight direct impact (LPA scale, latency, users served). If applying to top-tier giants like Flipkart or Swiggy, highlight high-throughput architecture. For fintechs like Paytm and Razorpay, emphasize security, UPI/banking integrations, and scale. Ensure your 'Education' and 'Skills' sections are on top!"
            }
            q.contains("bengaluru") || q.contains("bangalore") -> {
                "Bengaluru is the Silicon Valley of India! 🍬 Hot spots include HSR Layout, Koramangala, and Outer Ring Road where companies like Flipkart, Swiggy, and Razorpay are headquartered. Excellent networking hubs are local tech meetups or filter coffee chats with UI/UX designers and engineers."
            }
            q.contains("interview") -> {
                "Mock Interviews can sweeten up your performance! Companies like Paytm, PhonePe, and Jio focus heavily on Core Android design patterns (MVVM, Coroutines, StateFlow) or React performance for web roles. Always be ready to talk about a project you scaled!"
            }
            else -> {
                "That's a sweet question! Land your dream job by matching your skills with top Indian companies. Remember to customize your resume step-by-step using our 'Resume' tab, check active applications in our 'Status' tab, and search for specific cities under the 'Search' tab!"
            }
        }
    }

    // AI enhancing loading indicators
    var isEnhancingResume by mutableStateOf(false)
        private set

    // Resume flow from Room Database
    private val _userResume = MutableStateFlow(Resume())
    val userResume: StateFlow<Resume> = _userResume.asStateFlow()

    // Interactive Jobs State
    private val _jobs = MutableStateFlow<List<JobListing>>(emptyList())
    val jobs: StateFlow<List<JobListing>> = _jobs.asStateFlow()

    init {
        // Initialize Job list with realistic matching mockups across PAN INDIA
        _jobs.value = listOf(
            JobListing(
                id = "1",
                title = "Senior UI/UX Designer",
                company = "Flipkart",
                location = "Bengaluru, Karnataka (Hybrid)",
                salary = "22 - 32 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiq_us2qxxuhAGTu8gwVc1yk_9sbPJBwmmYnm6BPfkt61nLMJTrHHfpR-Q-hp1GUBhXgB4DkPU_SaaCAkZfmw6RxB0-RwdQX3WowOIVFRH4gXonf2zIsnrJLA9JvrJRX-xtCntS_VZn4XRl-tzfJ2I_3hQlOiOnXc0sqp5Oe3E48b0W7Z3vWEs4tvpaZ2uN9aPQGXtbHjD7_vDuAtKM5_McrAAmSrcQ_BrrIuQKxFTsn7r2okmV_-EGXknueGbx80Tucd5aCgbeQ",
                workType = "Hybrid",
                tags = listOf("Full-time", "Design Lab", "Bengaluru", "Flipkart"),
                isHot = true,
                isFeatured = true,
                matchPercentage = 98,
                applicationStatus = "Offer"
            ),
            JobListing(
                id = "2",
                title = "AI Research Engineer",
                company = "Reliance Jio",
                location = "Mumbai, Maharashtra (On-site)",
                salary = "35 - 50 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsdoXs40PFj91LcMAeOIEN8wdNcvUI1kTffjSpu--vhV6xqboR6PW07Li6KFlRHq5ujwOyfjJBNqo18FAugrxpjYEIeRT6gB8wwL1UBQrARkDNLL-UC-YSGf7G1xdMx0zeCFLveUeuoRDxcUGsgtrlLWjNPxDefTImeJIrH-R7WGV-y51WAQWzB0JtTEdPr2ZF_Ku3OYyVE4Yzp9JKHHW2aFnR49El8XVrvPRBTQQ6EZaHtCpC-BxMseVB60w7gUn5yw_zkyVDJw",
                workType = "On-site",
                tags = listOf("Full-time", "On-site", "AI", "Mumbai"),
                isFeatured = true,
                matchPercentage = 94
            ),
            JobListing(
                id = "3",
                title = "Full Stack Engineer",
                company = "Razorpay",
                location = "Bengaluru, Karnataka (Remote)",
                salary = "18 - 25 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiq_us2qxxuhAGTu8gwVc1yk_9sbPJBwmmYnm6BPfkt61nLMJTrHHfpR-Q-hp1GUBhXgB4DkPU_SaaCAkZfmw6RxB0-RwdQX3WowOIVFRH4gXonf2zIsnrJLA9JvrJRX-xtCntS_VZn4XRl-tzfJ2I_3hQlOiOnXc0sqp5Oe3E48b0W7Z3vWEs4tvpaZ2uN9aPQGXtbHjD7_vDuAtKM5_McrAAmSrcQ_BrrIuQKxFTsn7r2okmV_-EGXknueGbx80Tucd5aCgbeQ",
                workType = "Remote",
                tags = listOf("Remote", "Kotlin", "Fintech", "Bengaluru"),
                matchPercentage = 88,
                applicationStatus = "Interviewing"
            ),
            JobListing(
                id = "4",
                title = "Senior iOS Developer",
                company = "Paytm",
                location = "Noida, Delhi NCR",
                salary = "24 - 30 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsdoXs40PFj91LcMAeOIEN8wdNcvUI1kTffjSpu--vhV6xqboR6PW07Li6KFlRHq5ujwOyfjJBNqo18FAugrxpjYEIeRT6gB8wwL1UBQrARkDNLL-UC-YSGf7G1xdMx0zeCFLveUeuoRDxcUGsgtrlLWjNPxDefTImeJIrH-R7WGV-y51WAQWzB0JtTEdPr2ZF_Ku3OYyVE4Yzp9JKHHW2aFnR49El8XVrvPRBTQQ6EZaHtCpC-BxMseVB60w7gUn5yw_zkyVDJw",
                workType = "On-site",
                tags = listOf("Full-time", "Delhi NCR", "iOS", "Paytm"),
                matchPercentage = 98
            ),
            JobListing(
                id = "5",
                title = "Product Designer",
                company = "PhonePe",
                location = "Pune, Maharashtra",
                salary = "15 - 22 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGztWwxQ3UMWrim36iw3KVXLyhFQuDv_71gRpC5ltcM7shkzThopSG5LVS_nVEKoNZRkjwEdk2kGHwn0XAgSlhWonqG2bM2kaRLcWKEo1ReSvmHB4OsS43WKgOKGrAGXEPOxcnfQ3bFtNsFCJsFAGnXgP9mEE50z2LdCT9bdfDTAtQrD1i9rodoiaJxVa835cSwGyJq7T3ZqYO1jpfHQV3x3NsQjCvZHU3uwFdpOESM4zgCJbjwk3f3syEr93p9tpn8oNrADt_vg",
                workType = "Hybrid",
                tags = listOf("Hybrid", "Fintech", "UX", "Pune"),
                matchPercentage = 85,
                applicationStatus = "Applied"
            ),
            JobListing(
                id = "6",
                title = "Lead Backend Architect",
                company = "Zomato",
                location = "Gurugram, Delhi NCR (Remote)",
                salary = "40 - 55 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD6rfR2rPfitPnMfsl8iES-KxOzSMFNDqmmSILYV9OjglVdob0DFm9erHGhR1szhRV7kzMaoIYFy3iWCJBDxKdHCoO2X4Hi9jctojpk4czJwRWOtglSVxMQHZwI_IWrcXoBw6sx0ha9Hdvt4h2XHwQJB3nqbfgaTJWn6903Mty6Q4eW-L1_COdUP8jEiiV7Rmw3MhKy-s-qOIlc5oH9zG1TvU2mseby1o_uacmXYcRZUulvIHtT441LGxQPbhuN95VQhKHgzDRNrA",
                workType = "Remote",
                tags = listOf("Remote", "Full-time", "Scalability", "Zomato"),
                matchPercentage = 91,
                applicationStatus = "Interviewing"
            ),
            JobListing(
                id = "7",
                title = "Senior Android Engineer",
                company = "Swiggy",
                location = "Bengaluru, Karnataka (On-site)",
                salary = "28 - 38 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGNHURekhJdSPETuW_4b-Z9nxIbEXoXfs7pAcioUaaSXFYfeGVC9iWr6wbqzz8LgREr2_T0yMbRe-CYEm4PhmGJeEBd4qzRw5JW3bK5s3a6YFqCLDMURSKhpUEZNNd2XycqutOWKBbfbt0iqOz0uIjl2T96n9FzS7o_ldWAEHX4XBs97jDljQQsuZs2V9MkEBVUVlg_PGJKc3PhahijjtJ7ntnJ_20W442fIqVRhEQg1dNsYAOhO7uq3MNtzM5d-NmXZ3Y3weG6g",
                workType = "On-site",
                tags = listOf("Bengaluru", "Full-time", "Android", "Swiggy"),
                matchPercentage = 95
            ),
            JobListing(
                id = "8",
                title = "Technical Consultant",
                company = "TCS",
                location = "Hyderabad, Telangana",
                salary = "10 - 14 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiq_us2qxxuhAGTu8gwVc1yk_9sbPJBwmmYnm6BPfkt61nLMJTrHHfpR-Q-hp1GUBhXgB4DkPU_SaaCAkZfmw6RxB0-RwdQX3WowOIVFRH4gXonf2zIsnrJLA9JvrJRX-xtCntS_VZn4XRl-tzfJ2I_3hQlOiOnXc0sqp5Oe3E48b0W7Z3vWEs4tvpaZ2uN9aPQGXtbHjD7_vDuAtKM5_McrAAmSrcQ_BrrIuQKxFTsn7r2okmV_-EGXknueGbx80Tucd5aCgbeQ",
                workType = "On-site",
                tags = listOf("Hyderabad", "Consulting", "TCS"),
                matchPercentage = 87
            ),
            JobListing(
                id = "9",
                title = "Data Analyst",
                company = "Infosys",
                location = "Pune, Maharashtra",
                salary = "8 - 12 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsdoXs40PFj91LcMAeOIEN8wdNcvUI1kTffjSpu--vhV6xqboR6PW07Li6KFlRHq5ujwOyfjJBNqo18FAugrxpjYEIeRT6gB8wwL1UBQrARkDNLL-UC-YSGf7G1xdMx0zeCFLveUeuoRDxcUGsgtrlLWjNPxDefTImeJIrH-R7WGV-y51WAQWzB0JtTEdPr2ZF_Ku3OYyVE4Yzp9JKHHW2aFnR49El8XVrvPRBTQQ6EZaHtCpC-BxMseVB60w7gUn5yw_zkyVDJw",
                workType = "Hybrid",
                tags = listOf("Pune", "Analytics", "Infosys"),
                matchPercentage = 83
            ),
            JobListing(
                id = "10",
                title = "Software Engineer",
                company = "Wipro",
                location = "Chennai, Tamil Nadu",
                salary = "6 - 9 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGztWwxQ3UMWrim36iw3KVXLyhFQuDv_71gRpC5ltcM7shkzThopSG5LVS_nVEKoNZRkjwEdk2kGHwn0XAgSlhWonqG2bM2kaRLcWKEo1ReSvmHB4OsS43WKgOKGrAGXEPOxcnfQ3bFtNsFCJsFAGnXgP9mEE50z2LdCT9bdfDTAtQrD1i9rodoiaJxVa835cSwGyJq7T3ZqYO1jpfHQV3x3NsQjCvZHU3uwFdpOESM4zgCJbjwk3f3syEr93p9tpn8oNrADt_vg",
                workType = "On-site",
                tags = listOf("Chennai", "Software", "Wipro"),
                matchPercentage = 81
            ),
            JobListing(
                id = "11",
                title = "Senior Product Engineer",
                company = "Zoho",
                location = "Chennai, Tamil Nadu (On-site)",
                salary = "16 - 28 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiq_us2qxxuhAGTu8gwVc1yk_9sbPJBwmmYnm6BPfkt61nLMJTrHHfpR-Q-hp1GUBhXgB4DkPU_SaaCAkZfmw6RxB0-RwdQX3WowOIVFRH4gXonf2zIsnrJLA9JvrJRX-xtCntS_VZn4XRl-tzfJ2I_3hQlOiOnXc0sqp5Oe3E48b0W7Z3vWEs4tvpaZ2uN9aPQGXtbHjD7_vDuAtKM5_McrAAmSrcQ_BrrIuQKxFTsn7r2okmV_-EGXknueGbx80Tucd5aCgbeQ",
                workType = "On-site",
                tags = listOf("Chennai", "SaaS", "Product", "Zoho"),
                matchPercentage = 92
            ),
            JobListing(
                id = "12",
                title = "Staff Frontend Engineer",
                company = "Freshworks",
                location = "Bengaluru, Karnataka (Remote)",
                salary = "30 - 45 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsdoXs40PFj91LcMAeOIEN8wdNcvUI1kTffjSpu--vhV6xqboR6PW07Li6KFlRHq5ujwOyfjJBNqo18FAugrxpjYEIeRT6gB8wwL1UBQrARkDNLL-UC-YSGf7G1xdMx0zeCFLveUeuoRDxcUGsgtrlLWjNPxDefTImeJIrH-R7WGV-y51WAQWzB0JtTEdPr2ZF_Ku3OYyVE4Yzp9JKHHW2aFnR49El8XVrvPRBTQQ6EZaHtCpC-BxMseVB60w7gUn5yw_zkyVDJw",
                workType = "Remote",
                tags = listOf("Remote", "SaaS", "Frontend", "Freshworks"),
                matchPercentage = 89
            ),
            JobListing(
                id = "13",
                title = "Lead Android Developer",
                company = "Ola Cabs",
                location = "Bengaluru, Karnataka (On-site)",
                salary = "35 - 48 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGztWwxQ3UMWrim36iw3KVXLyhFQuDv_71gRpC5ltcM7shkzThopSG5LVS_nVEKoNZRkjwEdk2kGHwn0XAgSlhWonqG2bM2kaRLcWKEo1ReSvmHB4OsS43WKgOKGrAGXEPOxcnfQ3bFtNsFCJsFAGnXgP9mEE50z2LdCT9bdfDTAtQrD1i9rodoiaJxVa835cSwGyJq7T3ZqYO1jpfHQV3x3NsQjCvZHU3uwFdpOESM4zgCJbjwk3f3syEr93p9tpn8oNrADt_vg",
                workType = "On-site",
                tags = listOf("Bengaluru", "Mobility", "Android", "Ola"),
                matchPercentage = 97,
                isHot = true
            ),
            JobListing(
                id = "14",
                title = "Systems Architect",
                company = "Zerodha",
                location = "Bengaluru, Karnataka (Hybrid)",
                salary = "45 - 60 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiq_us2qxxuhAGTu8gwVc1yk_9sbPJBwmmYnm6BPfkt61nLMJTrHHfpR-Q-hp1GUBhXgB4DkPU_SaaCAkZfmw6RxB0-RwdQX3WowOIVFRH4gXonf2zIsnrJLA9JvrJRX-xtCntS_VZn4XRl-tzfJ2I_3hQlOiOnXc0sqp5Oe3E48b0W7Z3vWEs4tvpaZ2uN9aPQGXtbHjD7_vDuAtKM5_McrAAmSrcQ_BrrIuQKxFTsn7r2okmV_-EGXknueGbx80Tucd5aCgbeQ",
                workType = "Hybrid",
                tags = listOf("Hybrid", "Fintech", "Go", "Zerodha"),
                matchPercentage = 95,
                isFeatured = true
            ),
            JobListing(
                id = "15",
                title = "Mobile App Developer",
                company = "Groww",
                location = "Bengaluru, Karnataka (Hybrid)",
                salary = "20 - 32 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsdoXs40PFj91LcMAeOIEN8wdNcvUI1kTffjSpu--vhV6xqboR6PW07Li6KFlRHq5ujwOyfjJBNqo18FAugrxpjYEIeRT6gB8wwL1UBQrARkDNLL-UC-YSGf7G1xdMx0zeCFLveUeuoRDxcUGsgtrlLWjNPxDefTImeJIrH-R7WGV-y51WAQWzB0JtTEdPr2ZF_Ku3OYyVE4Yzp9JKHHW2aFnR49El8XVrvPRBTQQ6EZaHtCpC-BxMseVB60w7gUn5yw_zkyVDJw",
                workType = "Hybrid",
                tags = listOf("Hybrid", "Wealthtech", "Flutter", "Groww"),
                matchPercentage = 91
            ),
            JobListing(
                id = "16",
                title = "Senior Product Designer",
                company = "CRED",
                location = "Bengaluru, Karnataka (On-site)",
                salary = "32 - 45 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGztWwxQ3UMWrim36iw3KVXLyhFQuDv_71gRpC5ltcM7shkzThopSG5LVS_nVEKoNZRkjwEdk2kGHwn0XAgSlhWonqG2bM2kaRLcWKEo1ReSvmHB4OsS43WKgOKGrAGXEPOxcnfQ3bFtNsFCJsFAGnXgP9mEE50z2LdCT9bdfDTAtQrD1i9rodoiaJxVa835cSwGyJq7T3ZqYO1jpfHQV3x3NsQjCvZHU3uwFdpOESM4zgCJbjwk3f3syEr93p9tpn8oNrADt_vg",
                workType = "On-site",
                tags = listOf("Bengaluru", "Fintech", "Design", "CRED"),
                matchPercentage = 94,
                isFeatured = true
            ),
            JobListing(
                id = "17",
                title = "Vice President - Digital Hub",
                company = "HDFC Bank",
                location = "Mumbai, Maharashtra (On-site)",
                salary = "35 - 55 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiq_us2qxxuhAGTu8gwVc1yk_9sbPJBwmmYnm6BPfkt61nLMJTrHHfpR-Q-hp1GUBhXgB4DkPU_SaaCAkZfmw6RxB0-RwdQX3WowOIVFRH4gXonf2zIsnrJLA9JvrJRX-xtCntS_VZn4XRl-tzfJ2I_3hQlOiOnXc0sqp5Oe3E48b0W7Z3vWEs4tvpaZ2uN9aPQGXtbHjD7_vDuAtKM5_McrAAmSrcQ_BrrIuQKxFTsn7r2okmV_-EGXknueGbx80Tucd5aCgbeQ",
                workType = "On-site",
                tags = listOf("Mumbai", "Finance", "Banking", "HDFC"),
                matchPercentage = 86
            ),
            JobListing(
                id = "18",
                title = "Senior Software Engineer",
                company = "Tata 1mg",
                location = "Gurugram, Delhi NCR (Hybrid)",
                salary = "22 - 34 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsdoXs40PFj91LcMAeOIEN8wdNcvUI1kTffjSpu--vhV6xqboR6PW07Li6KFlRHq5ujwOyfjJBNqo18FAugrxpjYEIeRT6gB8wwL1UBQrARkDNLL-UC-YSGf7G1xdMx0zeCFLveUeuoRDxcUGsgtrlLWjNPxDefTImeJIrH-R7WGV-y51WAQWzB0JtTEdPr2ZF_Ku3OYyVE4Yzp9JKHHW2aFnR49El8XVrvPRBTQQ6EZaHtCpC-BxMseVB60w7gUn5yw_zkyVDJw",
                workType = "Hybrid",
                tags = listOf("Delhi NCR", "Healthcare", "Backend", "Tata 1mg"),
                matchPercentage = 93
            ),
            JobListing(
                id = "19",
                title = "Engineering Manager",
                company = "Zepto",
                location = "Mumbai, Maharashtra (On-site)",
                salary = "40 - 58 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGztWwxQ3UMWrim36iw3KVXLyhFQuDv_71gRpC5ltcM7shkzThopSG5LVS_nVEKoNZRkjwEdk2kGHwn0XAgSlhWonqG2bM2kaRLcWKEo1ReSvmHB4OsS43WKgOKGrAGXEPOxcnfQ3bFtNsFCJsFAGnXgP9mEE50z2LdCT9bdfDTAtQrD1i9rodoiaJxVa835cSwGyJq7T3ZqYO1jpfHQV3x3NsQjCvZHU3uwFdpOESM4zgCJbjwk3f3syEr93p9tpn8oNrADt_vg",
                workType = "On-site",
                tags = listOf("Mumbai", "Quick Commerce", "Logistics", "Zepto"),
                matchPercentage = 90,
                isHot = true
            ),
            JobListing(
                id = "20",
                title = "Senior Frontend Architect",
                company = "Blinkit",
                location = "Gurugram, Delhi NCR (Hybrid)",
                salary = "28 - 42 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiq_us2qxxuhAGTu8gwVc1yk_9sbPJBwmmYnm6BPfkt61nLMJTrHHfpR-Q-hp1GUBhXgB4DkPU_SaaCAkZfmw6RxB0-RwdQX3WowOIVFRH4gXonf2zIsnrJLA9JvrJRX-xtCntS_VZn4XRl-tzfJ2I_3hQlOiOnXc0sqp5Oe3E48b0W7Z3vWEs4tvpaZ2uN9aPQGXtbHjD7_vDuAtKM5_McrAAmSrcQ_BrrIuQKxFTsn7r2okmV_-EGXknueGbx80Tucd5aCgbeQ",
                workType = "Hybrid",
                tags = listOf("Delhi NCR", "Quick Commerce", "Frontend", "Blinkit"),
                matchPercentage = 91
            ),
            JobListing(
                id = "21",
                title = "Lead UX Researcher",
                company = "Lenskart",
                location = "Faridabad, Delhi NCR (Hybrid)",
                salary = "18 - 26 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsdoXs40PFj91LcMAeOIEN8wdNcvUI1kTffjSpu--vhV6xqboR6PW07Li6KFlRHq5ujwOyfjJBNqo18FAugrxpjYEIeRT6gB8wwL1UBQrARkDNLL-UC-YSGf7G1xdMx0zeCFLveUeuoRDxcUGsgtrlLWjNPxDefTImeJIrH-R7WGV-y51WAQWzB0JtTEdPr2ZF_Ku3OYyVE4Yzp9JKHHW2aFnR49El8XVrvPRBTQQ6EZaHtCpC-BxMseVB60w7gUn5yw_zkyVDJw",
                workType = "Hybrid",
                tags = listOf("Delhi NCR", "D2C", "User Research", "Lenskart"),
                matchPercentage = 85
            ),
            JobListing(
                id = "22",
                title = "Android Developer Intern",
                company = "Nykaa",
                location = "Mumbai, Maharashtra (Hybrid)",
                salary = "6 - 10 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGztWwxQ3UMWrim36iw3KVXLyhFQuDv_71gRpC5ltcM7shkzThopSG5LVS_nVEKoNZRkjwEdk2kGHwn0XAgSlhWonqG2bM2kaRLcWKEo1ReSvmHB4OsS43WKgOKGrAGXEPOxcnfQ3bFtNsFCJsFAGnXgP9mEE50z2LdCT9bdfDTAtQrD1i9rodoiaJxVa835cSwGyJq7T3ZqYO1jpfHQV3x3NsQjCvZHU3uwFdpOESM4zgCJbjwk3f3syEr93p9tpn8oNrADt_vg",
                workType = "Hybrid",
                tags = listOf("Mumbai", "E-commerce", "Kotlin", "Nykaa"),
                matchPercentage = 80
            ),
            JobListing(
                id = "23",
                title = "Systems Analyst",
                company = "HCLTech",
                location = "Noida, Delhi NCR (On-site)",
                salary = "8 - 13 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiq_us2qxxuhAGTu8gwVc1yk_9sbPJBwmmYnm6BPfkt61nLMJTrHHfpR-Q-hp1GUBhXgB4DkPU_SaaCAkZfmw6RxB0-RwdQX3WowOIVFRH4gXonf2zIsnrJLA9JvrJRX-xtCntS_VZn4XRl-tzfJ2I_3hQlOiOnXc0sqp5Oe3E48b0W7Z3vWEs4tvpaZ2uN9aPQGXtbHjD7_vDuAtKM5_McrAAmSrcQ_BrrIuQKxFTsn7r2okmV_-EGXknueGbx80Tucd5aCgbeQ",
                workType = "On-site",
                tags = listOf("Delhi NCR", "Consulting", "Service", "HCLTech"),
                matchPercentage = 87
            ),
            JobListing(
                id = "24",
                title = "QA Automation Lead",
                company = "Cognizant",
                location = "Hyderabad, Telangana (Hybrid)",
                salary = "12 - 18 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsdoXs40PFj91LcMAeOIEN8wdNcvUI1kTffjSpu--vhV6xqboR6PW07Li6KFlRHq5ujwOyfjJBNqo18FAugrxpjYEIeRT6gB8wwL1UBQrARkDNLL-UC-YSGf7G1xdMx0zeCFLveUeuoRDxcUGsgtrlLWjNPxDefTImeJIrH-R7WGV-y51WAQWzB0JtTEdPr2ZF_Ku3OYyVE4Yzp9JKHHW2aFnR49El8XVrvPRBTQQ6EZaHtCpC-BxMseVB60w7gUn5yw_zkyVDJw",
                workType = "Hybrid",
                tags = listOf("Hyderabad", "QA", "Automation", "Cognizant"),
                matchPercentage = 84
            ),
            JobListing(
                id = "25",
                title = "Full Stack Developer",
                company = "Tech Mahindra",
                location = "Pune, Maharashtra (On-site)",
                salary = "10 - 15 LPA",
                logoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAGztWwxQ3UMWrim36iw3KVXLyhFQuDv_71gRpC5ltcM7shkzThopSG5LVS_nVEKoNZRkjwEdk2kGHwn0XAgSlhWonqG2bM2kaRLcWKEo1ReSvmHB4OsS43WKgOKGrAGXEPOxcnfQ3bFtNsFCJsFAGnXgP9mEE50z2LdCT9bdfDTAtQrD1i9rodoiaJxVa835cSwGyJq7T3ZqYO1jpfHQV3x3NsQjCvZHU3uwFdpOESM4zgCJbjwk3f3syEr93p9tpn8oNrADt_vg",
                workType = "On-site",
                tags = listOf("Pune", "Consulting", "Web", "Tech Mahindra"),
                matchPercentage = 82
            )
        )

        // Sync with Room DB state
        viewModelScope.launch {
            repository.resumeFlow
                .catch { e -> e.printStackTrace() }
                .collectLatest { resume ->
                    if (resume != null) {
                        _userResume.value = resume
                    } else {
                        // Prepopulate default high-fidelity placeholder state as seen in mockups adjusted for India
                        val defaultResume = Resume(
                            fullName = "Sarah Candy",
                            email = "sarah.candy@creative.com",
                            phone = "+91 98765 43210",
                            portfolio = "creativecandy.design",
                            jobTitle = "Senior UI Designer",
                            company = "Flipkart",
                            startDate = "2024-01",
                            endDate = "2026-05",
                            keyAchievements = "Designed UPI flows that processed over 5 Million transactions daily. Reduced app-crash rates on low-end devices by 35% using Jetpack Compose optimizations.",
                            skills = "UI Design, User Research, Prototyping, Figma, Design Systems, Jetpack Compose",
                            degree = "BTech in Computer Science",
                            school = "IIT Bombay",
                            graduationYear = "2023"
                        )
                        repository.updateResume(defaultResume)
                    }
                }
        }
    }

    // Toggle Favorite Opportunity
    fun toggleFavorite(jobId: String) {
        _jobs.value = _jobs.value.map { job ->
            if (job.id == jobId) {
                job.copy(isFavorite = !job.isFavorite)
            } else {
                job
            }
        }
    }

    // Apply for Job
    fun applyForJob(jobId: String, status: String = "Applied") {
        var updatedJobName = ""
        _jobs.value = _jobs.value.map { job ->
            if (job.id == jobId) {
                updatedJobName = job.title + " at " + job.company
                job.copy(applicationStatus = status)
            } else {
                job
            }
        }

        if (updatedJobName.isNotEmpty()) {
            postLocalNotification(
                title = "Application Submitted ✉️",
                message = "Your application for $updatedJobName is now marked as '$status'. Checking with recruiter...",
                type = "status"
            )

            // Dynamic Simulator: After 6 seconds, the recruiter schedules an interview!
            if (status == "Applied") {
                viewModelScope.launch {
                    kotlinx.coroutines.delay(6000)
                    _jobs.value = _jobs.value.map { job ->
                        if (job.id == jobId) {
                            job.copy(applicationStatus = "Interviewing")
                        } else {
                            job
                        }
                    }
                    postLocalNotification(
                        title = "🎉 Status Update: Interview Scheduled!",
                        message = "Awesome! $updatedJobName was impressed by your resume and upgraded your tracker status to Interviewing! Checking slot...",
                        type = "status"
                    )
                }
            }
        }
    }

    // Check for matching talent criteria
    fun checkNewJobMatches(skills: String) {
        if (skills.isBlank()) return
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            val skillList = skills.split(",").map { it.trim().lowercase() }
            val matchedJob = _jobs.value.firstOrNull { job ->
                job.tags.any { tag -> tag.lowercase() in skillList } && job.applicationStatus == null
            }
            if (matchedJob != null) {
                postLocalNotification(
                    title = "✨ New Relevant Job Matched!",
                    message = "Candy Matcher found: '${matchedJob.title}' at ${matchedJob.company} (${matchedJob.salary}) based on your skill alignment!",
                    type = "match"
                )
            }
        }
    }

    // Update Resume Field
    fun updateResumeField(onUpdate: (Resume) -> Resume) {
        viewModelScope.launch {
            val oldSkills = _userResume.value.skills
            val updated = onUpdate(_userResume.value)
            _userResume.value = updated
            repository.updateResume(updated)

            if (updated.skills != oldSkills) {
                checkNewJobMatches(updated.skills)
            }
        }
    }

    // AI Resume Enhancer Call
    fun triggerAIEnhance() {
        val achievements = _userResume.value.keyAchievements
        isEnhancingResume = true
        viewModelScope.launch {
            val enhanced = repository.enhanceKeyAchievements(achievements)
            updateResumeField { resume ->
                resume.copy(keyAchievements = enhanced)
            }
            isEnhancingResume = false
        }
    }
    
    // Quick Reset Resume Demo
    fun resetResumeDemo() {
        viewModelScope.launch {
            repository.clearResume()
            currentResumeStep = 1
        }
    }
}
