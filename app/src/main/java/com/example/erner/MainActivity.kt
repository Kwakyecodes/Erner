package com.example.erner

import android.os.Bundle
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.erner.ui.theme.ErnerTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : androidx.fragment.app.FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val googleAuthClient = GoogleAuthClient(this)

        setContent {
            ErnerTheme {
                var isSignedIn by rememberSaveable {
                    mutableStateOf(googleAuthClient.isSingedIn())
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isSignedIn) {
                        HomeScreen ( activity=this,
                            onSignOutClick = {
                                lifecycleScope.launch {
                                    googleAuthClient.signOut()
                                    isSignedIn = !isSignedIn
                                }
                            })
                    }
                    else {
                        WelcomeScreen(
                            Modifier.padding(innerPadding),
                            onSignInClick = {
                                if (hasInternetConnection(this)) {
                                    lifecycleScope.launch {
                                        isSignedIn = googleAuthClient.signIn()
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Please check your internet connection and try again.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )

                    }

                }
            }
        }
    }
}

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}
