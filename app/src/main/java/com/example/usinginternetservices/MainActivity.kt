package com.example.usinginternetservices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usinginternetservices.ui.theme.UsingInternetServicesTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// ==========================================
// 1. DATA MODEL
// ==========================================
data class User(
    val id: Int,
    val name: String,
    val email: String
)

// ==========================================
// 2. API CONTRACT
// ==========================================
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>
}

// ==========================================
// 3. SERVICE CLIENT (The Connection)
// ==========================================
object ServiceClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }
}

// ==========================================
// 4. UI IMPLEMENTATION
// ==========================================

// BITS Theme Colors
val BitsBlue = Color(0xFF2B2B88)
val BitsGold = Color(0xFFE99B2D)
val LightGold = Color(0xFFFFF8E1)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UsingInternetServicesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { HeaderSection() },
                    bottomBar = { FooterSection() }
                ) { innerPadding ->
                    InternetServiceScreen(paddingValues = innerPadding)
                }
            }
        }
    }
}

@Composable
fun InternetServiceScreen(paddingValues: PaddingValues) {
    // STATE: Holds ALL users fetched from the service
    var allUsers by remember { mutableStateOf<List<User>>(emptyList()) }

    // PAGINATION STATE
    var currentPage by remember { mutableIntStateOf(0) }
    val itemsPerPage = 3 // Show 3 users per page

    // LOADING STATE
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Client Ready") }
    val scope = rememberCoroutineScope()

    // --- PAGINATION LOGIC ---
    val totalPages = if (allUsers.isEmpty()) 0 else (allUsers.size + itemsPerPage - 1) / itemsPerPage
    val currentDisplayList = allUsers
        .drop(currentPage * itemsPerPage)
        .take(itemsPerPage)

    // --- MAIN LAYOUT ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. START SCREEN (Only show if we have NO data)
        if (allUsers.isEmpty() && !isLoading) {
            EducationalCard(
                title = "What is a Service?",
                description = "The Service (Backend) holds the Database and Logic.\n\n" +
                        "This App (Client) only requests data and displays it."
            )
            Spacer(modifier = Modifier.height(16.dp))

            ArchitectureCard()
            Spacer(modifier = Modifier.height(24.dp))

            // Connect Button
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        statusMessage = "Client: Calling Service..."
                        delay(1000) // Demo delay

                        try {
                            val fetchedUsers = ServiceClient.api.getUsers()
                            allUsers = fetchedUsers
                            statusMessage = "Client: Received ${allUsers.size} Users."
                            currentPage = 0 // Reset to page 1
                        } catch (e: Exception) {
                            statusMessage = "Error: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BitsBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect to Service")
            }
        }

        // 2. LOADING INDICATOR
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = BitsGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fetching Data...", color = BitsBlue)
                }
            }
        }

        // 3. DATA LIST SCREEN (Show only if we HAVE data)
        if (currentDisplayList.isNotEmpty()) {

            // --- NEW: Header with HOME Button ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "▼ Service Data (${currentPage + 1}/$totalPages)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BitsBlue
                )

                // THE "GO HOME" BUTTON
                Button(
                    onClick = {
                        // RESET LOGIC: Clear data to go back to start
                        allUsers = emptyList()
                        statusMessage = "Client Ready"
                        currentPage = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Close", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // The List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentDisplayList) { user ->
                    UserItem(user)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pagination Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { if (currentPage > 0) currentPage-- },
                    enabled = currentPage > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = BitsBlue)
                ) {
                    Text("< Previous")
                }

                Button(
                    onClick = { if (currentPage < totalPages - 1) currentPage++ },
                    enabled = currentPage < totalPages - 1,
                    colors = ButtonDefaults.buttonColors(containerColor = BitsBlue)
                ) {
                    Text("Next >")
                }
            }
        }

        // Status Message at bottom
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Status: $statusMessage",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun EducationalCard(title: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightGold),
        border = BorderStroke(1.dp, BitsGold),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = BitsBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, color = BitsBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ArchitectureCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Light Blue
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = BitsBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Architectural Stability", fontWeight = FontWeight.Bold, color = BitsBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Whether the backend runs on:\n1. Local Machine\n2. Production Server\n3. Cloud (AWS/Google App Engine)\n\nThe Android Client code remains IDENTICAL.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun UserItem(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = BitsBlue,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "${user.id}", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = user.name, fontWeight = FontWeight.Bold, color = BitsBlue)
                Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BitsBlue)
            .statusBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("SDPD CS #08", color = BitsGold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FooterSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BitsBlue)
            .navigationBarsPadding()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("WILP BITS PILANI", color = BitsGold, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    }
}