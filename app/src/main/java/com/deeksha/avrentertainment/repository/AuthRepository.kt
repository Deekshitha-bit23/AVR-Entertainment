package com.deeksha.avrentertainment.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.deeksha.avrentertainment.models.User
import com.deeksha.avrentertainment.models.UserRole
import java.util.concurrent.TimeUnit

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun sendOtp(activity: Activity, phoneNumber: String, onVerificationComplete: (String) -> Unit, onVerificationFailed: (Exception) -> Unit) = withContext(Dispatchers.IO) {
        try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification completed
                    onVerificationComplete(credential.smsCode ?: "")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onVerificationFailed(e)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Store verificationId for later use
                    onVerificationComplete(verificationId)
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            onVerificationFailed(e)
        }
    }

    suspend fun verifyOtpAndGetUserRole(verificationId: String, otp: String, phoneNumber: String): Result<Pair<UserRole?, Boolean>> = withContext(Dispatchers.IO) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            auth.signInWithCredential(credential).await()
            
            // After successful authentication, check if user exists and get role
            val userResult = getUserWithRole(phoneNumber)
            Result.success(userResult.getOrNull() ?: (null to false))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(phoneNumber: String): Result<UserRole?> = withContext(Dispatchers.IO) {
        try {
            val doc = usersCollection.document(phoneNumber).get().await()
            val user = doc.toObject(User::class.java)
            Result.success(user?.role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // New method to check if user exists and get role
    suspend fun getUserWithRole(phoneNumber: String): Result<Pair<UserRole?, Boolean>> = withContext(Dispatchers.IO) {
        try {
            val doc = usersCollection.document(phoneNumber).get().await()
            if (doc.exists()) {
                // Get role as string from Firestore and map to enum
                val roleString = doc.getString("role")
                val userRole = when (roleString) {
                    "USER" -> UserRole.USER
                    "APPROVER" -> UserRole.APPROVER
                    "Production Head" -> UserRole.PRODUCTION_HEAD
                    else -> null
                }
                // User exists, return role (could be null if no role assigned) and true for userExists
                Result.success(userRole to true)
            } else {
                // User doesn't exist in the system
                Result.success(null to false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserWithRole(phoneNumber: String, role: UserRole, createdBy: String = "Admin"): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = User(
                phoneNumber = phoneNumber,
                role = role,
                createdBy = createdBy,
                timestamp = Timestamp.now()
            )
            usersCollection.document(phoneNumber).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Create sample users for testing
    suspend fun createSampleUsers(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Check if sample users already exist
            val testUserDoc = usersCollection.document("+919876543210").get().await()
            if (testUserDoc.exists()) {
                android.util.Log.d("AuthRepository", "Sample users already exist")
                return@withContext Result.success(Unit)
            }

            // Create sample users
            val sampleUsers = listOf(
                User(
                    phoneNumber = "+919876543210",
                    role = UserRole.USER,
                    createdBy = "System",
                    timestamp = Timestamp.now()
                ),
                User(
                    phoneNumber = "+918765432109",
                    role = UserRole.APPROVER,
                    createdBy = "System",
                    timestamp = Timestamp.now()
                ),
                User(
                    phoneNumber = "+917654321098",
                    role = UserRole.USER,
                    createdBy = "System",
                    timestamp = Timestamp.now()
                )
            )

            sampleUsers.forEach { user ->
                usersCollection.document(user.phoneNumber).set(user).await()
                android.util.Log.d("AuthRepository", "Created sample user: ${user.phoneNumber} with role: ${user.role}")
            }

            android.util.Log.d("AuthRepository", "Created ${sampleUsers.size} sample users")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Failed to create sample users: ${e.message}")
            Result.failure(e)
        }
    }

    // Note: Users should be added manually through Firebase Console
    // Go to Firestore Database > users collection > Add document
    // Document ID: +91XXXXXXXXXX (full phone number with country code)
    // Fields: role (string): "USER" or "APPROVER", createdBy (string): "Admin", timestamp (timestamp): now
    
    // Test scenarios:
    // Known users with roles: +919876543210 (USER), +918765432109 (APPROVER), +917654321098 (USER)
    // Unknown user (not in system): Any other phone number like +911234567890

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser() = auth.currentUser

    fun getCurrentUserPhoneNumber(): String? {
        return auth.currentUser?.phoneNumber
    }

    fun signOut() {
        auth.signOut()
    }
} 