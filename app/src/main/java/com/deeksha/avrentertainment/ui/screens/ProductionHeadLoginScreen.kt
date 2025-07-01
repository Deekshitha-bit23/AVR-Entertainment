package com.deeksha.avrentertainment.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionHeadLoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Production Head Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (!isOtpSent) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    
                    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            isLoading = false
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        checkUserRoleAndNavigate(auth.currentUser?.uid, db, onLoginSuccess)
                                    } else {
                                        errorMessage = "Authentication failed"
                                    }
                                }
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            isLoading = false
                            errorMessage = e.message
                        }

                        override fun onCodeSent(
                            verId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            isLoading = false
                            verificationId = verId
                            isOtpSent = true
                        }
                    }

                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(context as Activity)
                        .setCallbacks(callbacks)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isLoading && phoneNumber.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send OTP")
                }
            }
        } else {
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Enter OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    
                    val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                checkUserRoleAndNavigate(auth.currentUser?.uid, db, onLoginSuccess)
                            } else {
                                errorMessage = "Invalid OTP"
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isLoading && otp.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Verify OTP")
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

private fun checkUserRoleAndNavigate(
    uid: String?,
    db: FirebaseFirestore,
    onLoginSuccess: () -> Unit
) {
    if (uid == null) return
    
    db.collection("users")
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val role = document.getString("role")
                if (role == "Production Head") {
                    onLoginSuccess()
                }
            }
        }
} 