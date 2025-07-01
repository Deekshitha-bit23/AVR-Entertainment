package com.deeksha.avrentertainment.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SMSBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private var otpListener: ((String) -> Unit)? = null
        
        fun setOTPListener(listener: (String) -> Unit) {
            otpListener = listener
        }
        
        fun removeOTPListener() {
            otpListener = null
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    
                    // Extract OTP from message
                    val otp = extractOTP(message)
                    if (otp != null) {
                        // Notify the listener (LoginScreen) about the OTP
                        otpListener?.invoke(otp)
                        android.util.Log.d("SMSReceiver", "OTP extracted: $otp")
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    android.util.Log.d("SMSReceiver", "SMS retriever timeout")
                }
                else -> {
                    android.util.Log.d("SMSReceiver", "SMS retriever failed with status: ${status.statusCode}")
                }
            }
        }
    }

    private fun extractOTP(message: String): String? {
        // Common OTP patterns
        val patterns = listOf(
            "\\b\\d{6}\\b",           // 6 digit number
            "\\b\\d{4}\\b",           // 4 digit number
            "(?:OTP|otp|code|Code)\\s*:?\\s*(\\d{4,6})", // OTP: 123456
            "(\\d{4,6})\\s*(?:is|Is)\\s*(?:your|Your)\\s*(?:OTP|otp|code|Code)", // 123456 is your OTP
            "(?:verification|Verification)\\s*(?:code|Code)\\s*:?\\s*(\\d{4,6})", // Verification code: 123456
        )

        for (pattern in patterns) {
            val matcher = Pattern.compile(pattern).matcher(message)
            if (matcher.find()) {
                val otp = if (matcher.groupCount() > 0) {
                    matcher.group(1) // Get the captured group
                } else {
                    matcher.group(0) // Get the entire match
                }
                
                // Validate OTP length (4-6 digits)
                if (otp != null && otp.length in 4..6 && otp.all { it.isDigit() }) {
                    return otp
                }
            }
        }
        
        return null
    }
} 