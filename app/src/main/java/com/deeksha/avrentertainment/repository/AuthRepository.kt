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

data class OTPResult(
    val isSuccess: Boolean,
    val verificationId: String? = null,
    val autoVerificationCode: String? = null,
    val error: String? = null
)

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    
    private var currentVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    // Enhanced OTP sending with better callback handling
    suspend fun sendOtp(
        activity: Activity, 
        phoneNumber: String, 
        onVerificationComplete: (OTPResult) -> Unit, 
        onVerificationFailed: (Exception) -> Unit
    ) = withContext(Dispatchers.Main) {
        try {
            android.util.Log.d("AuthRepository", "🔥 Sending OTP to: $phoneNumber")
            
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Instant verification (auto-retrieval or Google Play services verification)
                    android.util.Log.d("AuthRepository", "✅ Verification completed automatically")
                    val smsCode = credential.smsCode
                    onVerificationComplete(
                        OTPResult(
                            isSuccess = true,
                            autoVerificationCode = smsCode,
                            verificationId = currentVerificationId
                        )
                    )
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    android.util.Log.e("AuthRepository", "❌ Verification failed: ${e.message}", e)
                    onVerificationFailed(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    android.util.Log.d("AuthRepository", "📱 OTP sent successfully. VerificationId: ${verificationId.take(10)}...")
                    currentVerificationId = verificationId
                    resendToken = token
                    
                    onVerificationComplete(
                        OTPResult(
                            isSuccess = true,
                            verificationId = verificationId
                        )
                    )
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                .setActivity(activity) // Activity for callback binding
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
            
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ Exception in sendOtp: ${e.message}", e)
            onVerificationFailed(e)
        }
    }

    // Resend OTP functionality
    suspend fun resendOtp(
        activity: Activity,
        phoneNumber: String,
        onVerificationComplete: (OTPResult) -> Unit,
        onVerificationFailed: (Exception) -> Unit
    ) = withContext(Dispatchers.Main) {
        try {
            if (resendToken == null) {
                // If no resend token, fall back to regular send
                sendOtp(activity, phoneNumber, onVerificationComplete, onVerificationFailed)
                return@withContext
            }

            android.util.Log.d("AuthRepository", "🔄 Resending OTP to: $phoneNumber")

            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    android.util.Log.d("AuthRepository", "✅ Resend verification completed automatically")
                    val smsCode = credential.smsCode
                    onVerificationComplete(
                        OTPResult(
                            isSuccess = true,
                            autoVerificationCode = smsCode,
                            verificationId = currentVerificationId
                        )
                    )
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    android.util.Log.e("AuthRepository", "❌ Resend verification failed: ${e.message}", e)
                    onVerificationFailed(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    android.util.Log.d("AuthRepository", "📱 OTP resent successfully")
                    currentVerificationId = verificationId
                    resendToken = token
                    
                    onVerificationComplete(
                        OTPResult(
                            isSuccess = true,
                            verificationId = verificationId
                        )
                    )
                }
            }

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(resendToken!!) // Use the resend token
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)

        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ Exception in resendOtp: ${e.message}", e)
            onVerificationFailed(e)
        }
    }

    // Enhanced OTP verification
    suspend fun verifyOtpAndGetUserRole(verificationId: String, otp: String, phoneNumber: String): Result<Pair<UserRole?, Boolean>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "🔐 Verifying OTP: ${otp.take(2)}**** for ${phoneNumber}")
            
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val authResult = auth.signInWithCredential(credential).await()
            
            android.util.Log.d("AuthRepository", "✅ Firebase authentication successful for: ${authResult.user?.phoneNumber}")
            
            // After successful authentication, check if user exists and get role
            val userResult = getUserWithRole(phoneNumber)
            android.util.Log.d("AuthRepository", "👤 User lookup result: ${userResult.getOrNull()}")
            
            Result.success(userResult.getOrNull() ?: (null to false))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ OTP verification failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getUserRole(phoneNumber: String): Result<UserRole?> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "👤 Getting user role for: $phoneNumber")
            val doc = usersCollection.document(phoneNumber).get().await()
            val user = doc.toObject(User::class.java)
            android.util.Log.d("AuthRepository", "📋 User role found: ${user?.role}")
            Result.success(user?.role)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ Error getting user role: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Enhanced user lookup with better logging
    suspend fun getUserWithRole(phoneNumber: String): Result<Pair<UserRole?, Boolean>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "🔍 Looking up user: $phoneNumber")
            val doc = usersCollection.document(phoneNumber).get().await()
            
            if (doc.exists()) {
                android.util.Log.d("AuthRepository", "✅ User found in database")
                // Get role as string from Firestore and map to enum
                val roleString = doc.getString("role")
                android.util.Log.d("AuthRepository", "📋 Role string from DB: $roleString")
                
                val userRole = when (roleString) {
                    "USER" -> UserRole.USER
                    "APPROVER" -> UserRole.APPROVER
                    "Production Head", "PRODUCTION_HEAD" -> UserRole.PRODUCTION_HEAD
                    else -> {
                        android.util.Log.w("AuthRepository", "⚠️ Unknown role: $roleString")
                        null
                    }
                }
                // User exists, return role (could be null if no role assigned) and true for userExists
                Result.success(userRole to true)
            } else {
                android.util.Log.w("AuthRepository", "❌ User not found in database")
                // User doesn't exist in the system
                Result.success(null to false)
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ Error in getUserWithRole: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun createUserWithRole(phoneNumber: String, role: UserRole, createdBy: String = "Admin"): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "👤 Creating user: $phoneNumber with role: $role")
            val user = User(
                phoneNumber = phoneNumber,
                role = role,
                createdBy = createdBy,
                timestamp = Timestamp.now()
            )
            usersCollection.document(phoneNumber).set(user).await()
            android.util.Log.d("AuthRepository", "✅ User created successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ Error creating user: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Create sample users for testing
    suspend fun createSampleUsers(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "🔧 Creating sample users for testing")
            
            // Check if sample users already exist
            val testUserDoc = usersCollection.document("+919876543210").get().await()
            if (testUserDoc.exists()) {
                android.util.Log.d("AuthRepository", "📋 Sample users already exist")
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
                ),
                User(
                    phoneNumber = "+919999999999", // For testing production head
                    role = UserRole.PRODUCTION_HEAD,
                    createdBy = "System",
                    timestamp = Timestamp.now()
                )
            )

            sampleUsers.forEach { user ->
                usersCollection.document(user.phoneNumber).set(user).await()
                android.util.Log.d("AuthRepository", "✅ Created sample user: ${user.phoneNumber} with role: ${user.role}")
            }

            android.util.Log.d("AuthRepository", "🎉 Created ${sampleUsers.size} sample users")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ Failed to create sample users: ${e.message}", e)
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
        val isLoggedIn = auth.currentUser != null
        android.util.Log.d("AuthRepository", "🔐 User logged in status: $isLoggedIn")
        return isLoggedIn
    }

    fun getCurrentUser() = auth.currentUser

    fun getCurrentUserPhoneNumber(): String? {
        val phoneNumber = auth.currentUser?.phoneNumber
        android.util.Log.d("AuthRepository", "📱 Current user phone: $phoneNumber")
        return phoneNumber
    }

    fun signOut() {
        android.util.Log.d("AuthRepository", "🚪 Signing out user")
        auth.signOut()
        currentVerificationId = null
        resendToken = null
    }

    // Clear verification state (useful for new login attempts)
    fun clearVerificationState() {
        android.util.Log.d("AuthRepository", "🧹 Clearing verification state")
        currentVerificationId = null
        resendToken = null
    }

    // Get current verification ID (useful for debugging)
    fun getCurrentVerificationId(): String? = currentVerificationId
} 