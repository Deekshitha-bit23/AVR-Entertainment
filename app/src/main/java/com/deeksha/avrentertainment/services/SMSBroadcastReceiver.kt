package com.deeksha.avrentertainment.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern
import kotlinx.coroutines.*
import android.os.Handler
import android.os.Looper
import java.util.concurrent.ConcurrentHashMap
import java.security.MessageDigest

class SMSBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SMSReceiver"
        private const val OTP_TIMEOUT_MS = 300000L // 5 minutes
        private const val MAX_OTP_ATTEMPTS = 5
        private const val RATE_LIMIT_WINDOW_MS = 60000L // 1 minute
        
        // Security: Track OTP attempts to prevent abuse
        private val otpAttempts = ConcurrentHashMap<String, MutableList<Long>>()
        
        // Secure listener management
        private var otpListener: ((String) -> Unit)? = null
        private var isListenerActive = false
        private var timeoutHandler: Handler? = null
        private var timeoutRunnable: Runnable? = null
        
        // Trusted sender patterns for enhanced security
        private val trustedSenderPatterns = listOf(
            "FIREBASE", "GOOGLE", "VERIFY", "OTP", "NOREPLY",
            "AUTHENTICATION", "SECURITY", "LOGIN"
        )
        
        fun setOTPListener(listener: (String) -> Unit) {
            synchronized(this) {
                // Clear any existing listener
                removeOTPListener()
                
            otpListener = listener
                isListenerActive = true
                
                // Set timeout for auto-fill session
                setupTimeout()
                
                android.util.Log.d(TAG, "🔐 Secure OTP listener activated with timeout")
            }
        }
        
        fun removeOTPListener() {
            synchronized(this) {
            otpListener = null
                isListenerActive = false
                
                // Clear timeout
                timeoutHandler?.removeCallbacks(timeoutRunnable ?: return)
                timeoutHandler = null
                timeoutRunnable = null
                
                android.util.Log.d(TAG, "🔒 OTP listener deactivated")
            }
        }
        
        private fun setupTimeout() {
            timeoutHandler = Handler(Looper.getMainLooper())
            timeoutRunnable = Runnable {
                android.util.Log.d(TAG, "⏰ OTP auto-fill session timed out")
                removeOTPListener()
            }
            timeoutHandler?.postDelayed(timeoutRunnable!!, OTP_TIMEOUT_MS)
        }
        
        private fun isRateLimited(phoneNumber: String): Boolean {
            val currentTime = System.currentTimeMillis()
            val attempts = otpAttempts.getOrPut(phoneNumber) { mutableListOf() }
            
            // Remove old attempts outside the rate limit window
            attempts.removeAll { it < currentTime - RATE_LIMIT_WINDOW_MS }
            
            // Check if exceeded max attempts
            if (attempts.size >= MAX_OTP_ATTEMPTS) {
                android.util.Log.w(TAG, "🚫 Rate limit exceeded for phone number")
                return true
            }
            
            // Add current attempt
            attempts.add(currentTime)
            return false
        }
        
        private fun validateSender(message: String): Boolean {
            val upperMessage = message.uppercase()
            return trustedSenderPatterns.any { pattern ->
                upperMessage.contains(pattern)
            }
        }
        
        // Generate app signature hash for verification (if needed)
        fun getAppSignature(context: Context): String? {
            try {
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.GET_SIGNATURES
                )
                val signatures = packageInfo.signatures
                if (signatures != null && signatures.isNotEmpty()) {
                    val signature = signatures[0]
                    val md = MessageDigest.getInstance("SHA-256")
                    md.update(signature.toByteArray())
                    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP)
                }
                return null
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Failed to get app signature", e)
                return null
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            android.util.Log.d(TAG, "📱 SMS received for OTP extraction")
            
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    try {
                    // Get SMS message contents
                        val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as? String
                        
                        if (message != null) {
                            android.util.Log.d(TAG, "📨 Processing SMS message for OTP")
                            
                            // Security: Validate sender (basic check)
                            if (!validateSender(message)) {
                                android.util.Log.w(TAG, "⚠️ SMS from untrusted sender, skipping auto-fill")
                                return
                            }
                    
                    // Extract OTP from message
                            val extractedOtp = extractOTP(message)
                            if (extractedOtp != null) {
                                synchronized(this) {
                                    if (isListenerActive && otpListener != null) {
                                        // Security: Check rate limiting
                                        val phoneNumber = extractPhoneFromMessage(message) ?: "unknown"
                                        if (!isRateLimited(phoneNumber)) {
                                            // Notify the listener about the OTP
                                            otpListener?.invoke(extractedOtp)
                                            android.util.Log.d(TAG, "✅ OTP auto-filled successfully: ${extractedOtp.take(2)}****")
                                            
                                            // Auto-remove listener after successful extraction
                                            removeOTPListener()
                                        }
                                    } else {
                                        android.util.Log.d(TAG, "🔇 No active OTP listener")
                                    }
                                }
                            } else {
                                android.util.Log.d(TAG, "❌ No valid OTP found in message")
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "❌ Error processing SMS", e)
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    android.util.Log.d(TAG, "⏰ SMS retriever timeout")
                }
                else -> {
                    android.util.Log.d(TAG, "❌ SMS retriever failed with status: ${status?.statusCode}")
                }
            }
        }
    }

    private fun extractOTP(message: String): String? {
        android.util.Log.d(TAG, "🔍 Extracting OTP from message")
        
        // Enhanced OTP patterns with priority order
        val patterns = listOf(
            // Firebase/Google specific patterns (highest priority)
            "(?:Firebase|FIREBASE)\\s*(?:verification|Verification)?\\s*(?:code|Code)?\\s*:?\\s*(\\d{6})",
            "(?:Google|GOOGLE)\\s*(?:verification|Verification)?\\s*(?:code|Code)?\\s*:?\\s*(\\d{6})",
            
            // Common OTP patterns with context
            "(?:Your|your)\\s*(?:OTP|otp|code|Code|verification code)\\s*(?:is|:)?\\s*(\\d{4,8})",
            "(?:OTP|otp|Code|code)\\s*(?:is|:)?\\s*(\\d{4,8})",
            "(\\d{4,8})\\s*(?:is|Is)\\s*(?:your|Your)\\s*(?:OTP|otp|code|Code|verification code)",
            "(?:verification|Verification)\\s*(?:code|Code)\\s*:?\\s*(\\d{4,8})",
            "(?:login|Login)\\s*(?:code|Code)\\s*:?\\s*(\\d{4,8})",
            "(?:security|Security)\\s*(?:code|Code)\\s*:?\\s*(\\d{4,8})",
            
            // Pattern for messages with specific keywords
            "(?:use|Use)\\s*(\\d{4,8})\\s*(?:to|for)\\s*(?:verify|login|authenticate)",
            "(?:enter|Enter)\\s*(\\d{4,8})\\s*(?:to|for)\\s*(?:verify|complete|login)",
            
            // Generic patterns (lower priority)
            "\\b(\\d{6})\\b",  // 6 digit numbers (most common)
            "\\b(\\d{4})\\b",  // 4 digit numbers
            "\\b(\\d{8})\\b",  // 8 digit numbers (less common)
            "\\b(\\d{5})\\b"   // 5 digit numbers (least common)
        )

        for (pattern in patterns) {
            try {
                val matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message)
            if (matcher.find()) {
                val otp = if (matcher.groupCount() > 0) {
                    matcher.group(1) // Get the captured group
                } else {
                    matcher.group(0) // Get the entire match
                }
                
                    // Enhanced validation
                    if (otp != null && isValidOTP(otp, message)) {
                        android.util.Log.d(TAG, "✅ Valid OTP extracted using pattern: $pattern")
                    return otp
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error applying pattern: $pattern", e)
                continue
            }
        }
        
        android.util.Log.d(TAG, "❌ No valid OTP pattern matched")
        return null
    }
    
    private fun isValidOTP(otp: String, fullMessage: String): Boolean {
        // Basic validation
        if (!otp.all { it.isDigit() }) return false
        if (otp.length !in 4..8) return false
        
        // Avoid common false positives
        val invalidPatterns = listOf(
            "0000", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999",
            "1234", "4321", "0123", "9876"
        )
        
        if (invalidPatterns.contains(otp)) {
            android.util.Log.d(TAG, "⚠️ Rejected common false positive: $otp")
            return false
        }
        
        // Context validation - ensure it's actually an OTP message
        val contextKeywords = listOf(
            "otp", "code", "verify", "verification", "authenticate", "login", 
            "firebase", "google", "security", "confirm", "validation"
        )
        
        val hasContext = contextKeywords.any { keyword ->
            fullMessage.lowercase().contains(keyword)
        }
        
        if (!hasContext) {
            android.util.Log.d(TAG, "⚠️ Rejected OTP without proper context: $otp")
            return false
        }
        
        android.util.Log.d(TAG, "✅ OTP validation passed: ${otp.take(2)}****")
        return true
    }
    
    private fun extractPhoneFromMessage(message: String): String? {
        val phonePattern = "\\+?\\d{10,15}"
        val matcher = Pattern.compile(phonePattern).matcher(message)
        return if (matcher.find()) matcher.group(0) else null
    }
} 