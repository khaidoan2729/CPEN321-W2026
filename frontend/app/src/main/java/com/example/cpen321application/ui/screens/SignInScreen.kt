package com.example.cpen321application.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.cpen321application.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.cpen321application.R
data class AuthTokens(val accessToken: String)

@Composable
fun SignInScreen(
    onSignInSuccess: (accessToken: String, givenName: String?, familyName: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            val googleIdTokenCredential = signInWithGoogle(context)
                            val tokens = exchangeGoogleToken(googleIdTokenCredential.idToken)
                            onSignInSuccess(
                                tokens.accessToken,
                                googleIdTokenCredential.givenName,
                                googleIdTokenCredential.familyName
                            )
                        } catch (e: Exception) {
                            Log.e("SignInScreen", "Sign-in failed", e)
                            errorMessage = "Sign-in failed: ${e.message ?: "unknown error"}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified  
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Sign in with Google")            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private suspend fun signInWithGoogle(context: android.content.Context): GoogleIdTokenCredential {
    val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_CLIENT_ID)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

    val credentialManager = CredentialManager.create(context)
    val result = credentialManager.getCredential(context, request)

    val googleIdTokenCredential = GoogleIdTokenCredential
        .createFrom(result.credential.data)

    return googleIdTokenCredential
}

private suspend fun exchangeGoogleToken(idToken: String): AuthTokens = withContext(Dispatchers.IO) {
    val json = JSONObject().apply { put("idToken", idToken) }.toString()
    val body = json.toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("${BuildConfig.API_BASE_URL}/auth/google")
        .post(body)
        .build()

    OkHttpClient().newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            error("Backend rejected sign-in: HTTP ${response.code}")
        }
        val responseBody = response.body?.string() ?: error("Empty response")
        val obj = JSONObject(responseBody)
        AuthTokens(
            accessToken = obj.getString("accessToken")
        )
    }
}