package com.example.cpen321application.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cpen321application.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.Inet4Address
import java.net.NetworkInterface
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import androidx.compose.material3.OutlinedCard
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding

data class DashboardInfo(
    val serverPublicIp: String,
    val serverTime: String,
    val developerFirstName: String,
    val developerLastName: String,
    val userFirstName: String,
    val userLastName: String
)

@Composable
fun DashboardScreen(
    accessToken: String?,
    clientFirstName: String?,
    clientLastName: String?,
    modifier: Modifier = Modifier
) {
    var dashboardInfo by remember { mutableStateOf<DashboardInfo?>(null) }
    var clientIp by remember { mutableStateOf("") }
    var clientTime by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(accessToken) {
        clientIp = getClientIpAddress()
        clientTime = getClientTimeFormatted()
        try {
            val token = accessToken ?: error("accessToken is missing")
            val firstName = clientFirstName ?: error("clientFirstName is missing")
            val lastName = clientLastName ?: error("clientLastName is missing")
            dashboardInfo = fetchDashboard(token, firstName, lastName)
        } catch (e: Exception) {
            errorMessage = "Failed to load dashboard: ${e.message ?: "unknown error"}"
        }
    }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineSmall)

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),

        shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                dashboardInfo?.let { info ->
                    Text("Server public IP: ${info.serverPublicIp}")
                    Text("Client IP: $clientIp")
                    Text("Server time: ${info.serverTime}")
                    Text("Client time: $clientTime")
                    Text("Developer: ${info.developerFirstName} ${info.developerLastName}")
                    Text("Logged in as: ${info.userFirstName} ${info.userLastName}")
                } ?: if (errorMessage == null) {
                    CircularProgressIndicator()
                } else {
                    Text("Something is wrong...")
                }
            }
        }
    }
}

private fun getClientIpAddress(): String {
    return try {
        NetworkInterface.getNetworkInterfaces().asSequence()
            .flatMap { it.inetAddresses.asSequence() }
            .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
            ?.hostAddress ?: "Unavailable"
    } catch (e: Exception) {
        "Unavailable"
    }
}

private fun getClientTimeFormatted(): String {
    val now = OffsetDateTime.now()
    val timePart = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    val offset = now.offset.id.let { if (it == "Z") "+00:00" else it }
    return "$timePart GMT$offset"
}

private suspend fun fetchDashboard(
    accessToken: String,
    clientFirstName: String,
    clientLastName:String,
): DashboardInfo = withContext(Dispatchers.IO) {
    val client = OkHttpClient()

    // Function to get each endpoint
    fun getFromBackend(path: String): JSONObject {
        val request = Request.Builder()
            .url("${BuildConfig.API_BASE_URL}/api/dashboard$path")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Backend returned HTTP ${response.code} for $path")
            }
            val body = response.body?.string() ?: error("Empty response for $path")
            return JSONObject(body)
        }
    }

    // Run all three concurrently instead of one after another
    val ipDeferred = async { getFromBackend("/ip") }
    val timeDeferred = async { getFromBackend("/time") }
    val nameDeferred = async { getFromBackend("/name") }

    val ipObj = ipDeferred.await()
    val timeObj = timeDeferred.await()
    val nameObj = nameDeferred.await()

    DashboardInfo(
        serverPublicIp = ipObj.getString("serverPublicIp"),
        serverTime = timeObj.getString("serverTime"),
        developerFirstName = nameObj.getString("firstName"),
        developerLastName = nameObj.getString("lastName"),
        userFirstName = clientFirstName,
        userLastName = clientLastName,
    )
}