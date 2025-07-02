package com.deeksha.avrentertainment.services

import android.content.Context
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

data class OTPState(
    val isAutoFillActive: Boolean = false,
    val lastOTPReceived: String? = null,
    val isOTPValid: Boolean = false,
    val autoFillSource: String? = null,
    val timeRemaining: Long = 0L,
    val error: String? = null
)

class SecureOTPManager private constructor() {
    
    companion object {
        private const val TAG = "SecureOTPManager"
        private const val SESSION_TIMEOUT_MS = 300000L // 5 minutes
        private const val OTP_VALIDITY_MS = 120000L // 2 minutes
        
        @Volatile
        private var INSTANCE: SecureOTPManager? = null
        
        fun getInstance(): SecureOTPManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecureOTPManager().also { INSTANCE = it }
            }
        }
    }
    
    private val _otpState = MutableStateFlow(OTPState())
    val otpState: StateFlow<OTPState> = _otpState.asStateFlow()
    
    private var sessionScope: CoroutineScope? = null
    private var encryptionKey: ByteArray? = null
    private var currentContext: Context? = null
    private var smsReceiver: SMSBroadcastReceiver? = null
    
    // Secure session management
    private var sessionStartTime: Long = 0L
    private var lastOTPTime: Long = 0L
    
    fun initializeSecureSession(context: Context) {
        android.util.Log.d(TAG, "🔐 Initializing secure OTP session")
        
        currentContext = context.applicationContext
        sessionScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        
        // Generate encryption key for this session
        generateEncryptionKey()
        
        // Initialize SMS retriever
        initializeSmsRetriever(context)
        
        sessionStartTime = System.currentTimeMillis()
        
        // Start session timeout monitoring
        startSessionTimeoutMonitoring()
        
        _otpState.value = _otpState.value.copy(
            isAutoFillActive = true,
            error = null
        )
    }
    
    private fun generateEncryptionKey() {
        encryptionKey = ByteArray(16).apply {
            SecureRandom().nextBytes(this)
        }
    }
    
    private fun initializeSmsRetriever(context: Context) {
        try {
            val client = SmsRetriever.getClient(context)
            val task = client.startSmsRetriever()
            
            task.addOnSuccessListener {
                android.util.Log.d(TAG, "✅ SMS Retriever started successfully")
                setupOTPListener()
            }
            
            task.addOnFailureListener { exception ->
                android.util.Log.e(TAG, "❌ Failed to start SMS Retriever", exception)
                _otpState.value = _otpState.value.copy(
                    error = "Failed to initialize auto-fill: ${exception.message}"
                )
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Exception initializing SMS Retriever", e)
            _otpState.value = _otpState.value.copy(
                error = "Auto-fill initialization error: ${e.message}"
            )
        }
    }
    
    private fun setupOTPListener() {
        SMSBroadcastReceiver.setOTPListener { otp ->
            handleOTPReceived(otp)
        }
    }
    
    private fun handleOTPReceived(otp: String) {
        sessionScope?.launch {
            try {
                // Encrypt OTP for secure storage
                val encryptedOTP = encryptOTP(otp)
                lastOTPTime = System.currentTimeMillis()
                
                android.util.Log.d(TAG, "🔐 OTP received and encrypted: ${otp.take(2)}****")
                
                _otpState.value = _otpState.value.copy(
                    lastOTPReceived = encryptedOTP,
                    isOTPValid = true,
                    autoFillSource = "SMS",
                    error = null
                )
                
                // Start OTP validity countdown
                startOTPValidityCountdown()
                
            } catch (e: Exception) {
                android.util.Log.e(TAG, "❌ Error handling received OTP", e)
                _otpState.value = _otpState.value.copy(
                    error = "Error processing OTP: ${e.message}"
                )
            }
        }
    }
    
    private fun startSessionTimeoutMonitoring() {
        sessionScope?.launch {
            delay(SESSION_TIMEOUT_MS)
            
            if (_otpState.value.isAutoFillActive) {
                android.util.Log.d(TAG, "⏰ OTP session timeout reached")
                endSession("Session timeout")
            }
        }
    }
    
    private fun startOTPValidityCountdown() {
        sessionScope?.launch {
            val startTime = System.currentTimeMillis()
            
            while (_otpState.value.isOTPValid && 
                   (System.currentTimeMillis() - startTime) < OTP_VALIDITY_MS) {
                
                val timeRemaining = OTP_VALIDITY_MS - (System.currentTimeMillis() - startTime)
                _otpState.value = _otpState.value.copy(timeRemaining = timeRemaining)
                
                delay(1000) // Update every second
            }
            
            // OTP validity expired
            if (_otpState.value.isOTPValid) {
                android.util.Log.d(TAG, "⏰ OTP validity expired")
                clearOTP("OTP expired")
            }
        }
    }
    
    fun getDecryptedOTP(): String? {
        return try {
            val encrypted = _otpState.value.lastOTPReceived
            if (encrypted != null && _otpState.value.isOTPValid) {
                decryptOTP(encrypted)
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error decrypting OTP", e)
            null
        }
    }
    
    private fun encryptOTP(otp: String): String {
        return try {
            if (encryptionKey == null) {
                throw IllegalStateException("Encryption key not initialized")
            }
            
            val cipher = Cipher.getInstance("AES/ECB/PKCS1Padding")
            val secretKey = SecretKeySpec(encryptionKey, "AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val encryptedBytes = cipher.doFinal(otp.toByteArray())
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Encryption failed", e)
            throw e
        }
    }
    
    private fun decryptOTP(encryptedOTP: String): String {
        return try {
            if (encryptionKey == null) {
                throw IllegalStateException("Encryption key not initialized")
            }
            
            val cipher = Cipher.getInstance("AES/ECB/PKCS1Padding")
            val secretKey = SecretKeySpec(encryptionKey, "AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            
            val encryptedBytes = Base64.decode(encryptedOTP, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Decryption failed", e)
            throw e
        }
    }
    
    fun clearOTP(reason: String) {
        android.util.Log.d(TAG, "🧹 Clearing OTP: $reason")
        
        _otpState.value = _otpState.value.copy(
            lastOTPReceived = null,
            isOTPValid = false,
            autoFillSource = null,
            timeRemaining = 0L
        )
        
        // Clear from memory
        encryptionKey?.fill(0)
    }
    
    fun endSession(reason: String = "User ended session") {
        android.util.Log.d(TAG, "🔒 Ending OTP session: $reason")
        
        // Remove SMS listener
        SMSBroadcastReceiver.removeOTPListener()
        
        // Clear encrypted data
        clearOTP(reason)
        
        // Cancel all coroutines
        sessionScope?.cancel()
        sessionScope = null
        
        // Clear encryption key from memory
        encryptionKey?.fill(0)
        encryptionKey = null
        
        _otpState.value = OTPState() // Reset to default state
        
        currentContext = null
    }
    
    fun validateSessionSecurity(): Boolean {
        val currentTime = System.currentTimeMillis()
        
        // Check session timeout
        if (sessionStartTime > 0 && (currentTime - sessionStartTime) > SESSION_TIMEOUT_MS) {
            endSession("Session security timeout")
            return false
        }
        
        // Check if encryption key is still valid
        if (encryptionKey == null) {
            endSession("Security key compromised")
            return false
        }
        
        return true
    }
    
    fun getSessionInfo(): Map<String, Any> {
        return mapOf(
            "isActive" to _otpState.value.isAutoFillActive,
            "sessionDuration" to (System.currentTimeMillis() - sessionStartTime),
            "hasValidOTP" to _otpState.value.isOTPValid,
            "autoFillSource" to (_otpState.value.autoFillSource ?: "None"),
            "timeRemaining" to _otpState.value.timeRemaining
        )
    }
    
    // Clean up on app destroy
    fun onApplicationDestroy() {
        android.util.Log.d(TAG, "🧹 Application destroy - cleaning up OTP manager")
        endSession("Application destroyed")
    }
} 