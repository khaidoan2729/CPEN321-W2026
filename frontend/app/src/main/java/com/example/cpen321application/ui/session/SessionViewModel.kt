package com.example.cpen321application.ui.session

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class SessionViewModel : ViewModel() {
    var accessToken by mutableStateOf<String?>(null)
        private set
    var givenName by mutableStateOf<String?>(null)
        private set
    var familyName by mutableStateOf<String?>(null)
        private set

    fun setSession(accessToken: String, givenName: String?, familyName: String?) {
        this.accessToken = accessToken
        this.givenName = givenName
        this.familyName = familyName
    }
}