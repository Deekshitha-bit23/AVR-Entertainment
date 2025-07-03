package com.deeksha.avrentertainment.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deeksha.avrentertainment.repository.AuthRepository
import com.deeksha.avrentertainment.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val otpSent: Boolean = false,
    val verificationId: String? = null,
    val phoneNumber: String = "",
    val otp: String = "",
    val userRole: UserRole? = null,
    val isAuthenticated: Boolean = false,
    val hasNoRole: Boolean = false,
    val isUnknownUser: Boolean = false,
    val canResend: Boolean = false,
    val resendCountdown: Int = 0,
    val autoFilledOtp: String? = null,
    val isAutoFilling: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        // Create sample users on init
        viewModelScope.launch {
            authRepository.createSampleUsers()
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _state.value = _state.value.copy(
            phoneNumber = phoneNumber,
            error = null // Clear error when user starts typing
        )
    }

    fun updateOtp(otp: String) {
        _state.value = _state.value.copy(
            otp = otp,
            error = null, // Clear error when user starts typing
            isAutoFilling = false // Clear auto-fill indicator when user manually types
        )
    }

    fun sendOtp(activity: Activity) {
        viewModelScope.launch {
            val phoneNumber = _state.value.phoneNumber.trim()
            
            // Validate phone number
            if (phoneNumber.isEmpty()) {
                _state.value = _state.value.copy(error = "Please enter phone number")
                return@launch
            }
            
            if (phoneNumber.length != 10) {
                _state.value = _state.value.copy(error = "Please enter valid 10-digit phone number")
                return@launch
            }

            _state.value = _state.value.copy(
                isLoading = true, 
                error = null,
                otpSent = false,
                canResend = false
            )
            
            val fullPhoneNumber = "+91$phoneNumber"
            android.util.Log.d("LoginViewModel", "📱 Sending OTP to: $fullPhoneNumber")
            
            try {
                authRepository.sendOtp(
                    activity = activity,
                    phoneNumber = fullPhoneNumber,
                    onVerificationComplete = { otpResult ->
                        android.util.Log.d("LoginViewModel", "🎯 OTP Result: ${otpResult}")
                        
                        if (otpResult.isSuccess) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                otpSent = true,
                                verificationId = otpResult.verificationId,
                                autoFilledOtp = otpResult.autoVerificationCode,
                                isAutoFilling = otpResult.autoVerificationCode != null,
                                otp = otpResult.autoVerificationCode ?: _state.value.otp
                            )
                            
                            // Start resend countdown
                            startResendCountdown()
                            
                            // If auto-verification happened, verify immediately
                            if (otpResult.autoVerificationCode != null && otpResult.verificationId != null) {
                                android.util.Log.d("LoginViewModel", "🚀 Auto-verifying OTP")
                                verifyOtp()
                            }
                        } else {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = otpResult.error ?: "Failed to send OTP"
                            )
                        }
                    },
                    onVerificationFailed = { exception ->
                        android.util.Log.e("LoginViewModel", "❌ OTP Send Failed: ${exception.message}", exception)
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = when {
                                exception.message?.contains("TOO_MANY_REQUESTS") == true -> 
                                    "Too many requests. Please try again later."
                                exception.message?.contains("INVALID_PHONE_NUMBER") == true -> 
                                    "Invalid phone number format"
                                exception.message?.contains("QUOTA_EXCEEDED") == true -> 
                                    "SMS quota exceeded. Please try again later."
                                else -> exception.message ?: "Failed to send OTP"
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "❌ Exception in sendOtp: ${e.message}", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred while sending OTP"
                )
            }
        }
    }

    fun resendOtp(activity: Activity) {
        viewModelScope.launch {
            if (!_state.value.canResend) {
                android.util.Log.w("LoginViewModel", "⏳ Resend not available yet")
                return@launch
            }

            _state.value = _state.value.copy(
                isLoading = true, 
                error = null,
                canResend = false
            )
            
            val fullPhoneNumber = "+91${_state.value.phoneNumber}"
            android.util.Log.d("LoginViewModel", "🔄 Resending OTP to: $fullPhoneNumber")
            
            try {
                authRepository.resendOtp(
                    activity = activity,
                    phoneNumber = fullPhoneNumber,
                    onVerificationComplete = { otpResult ->
                        android.util.Log.d("LoginViewModel", "🎯 Resend OTP Result: ${otpResult}")
                        
                        if (otpResult.isSuccess) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                verificationId = otpResult.verificationId,
                                autoFilledOtp = otpResult.autoVerificationCode,
                                isAutoFilling = otpResult.autoVerificationCode != null,
                                otp = otpResult.autoVerificationCode ?: ""
                            )
                            
                            // Start resend countdown again
                            startResendCountdown()
                            
                            // If auto-verification happened, verify immediately
                            if (otpResult.autoVerificationCode != null && otpResult.verificationId != null) {
                                android.util.Log.d("LoginViewModel", "🚀 Auto-verifying resent OTP")
                                verifyOtp()
                            }
                        } else {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = otpResult.error ?: "Failed to resend OTP"
                            )
                        }
                    },
                    onVerificationFailed = { exception ->
                        android.util.Log.e("LoginViewModel", "❌ OTP Resend Failed: ${exception.message}", exception)
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to resend OTP"
                        )
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "❌ Exception in resendOtp: ${e.message}", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred while resending OTP"
                )
            }
        }
    }

    private fun startResendCountdown() {
        viewModelScope.launch {
            var countdown = 30 // 30 seconds countdown
            _state.value = _state.value.copy(resendCountdown = countdown)
            
            while (countdown > 0) {
                delay(1000) // Wait 1 second
                countdown--
                _state.value = _state.value.copy(resendCountdown = countdown)
            }
            
            _state.value = _state.value.copy(canResend = true, resendCountdown = 0)
            android.util.Log.d("LoginViewModel", "✅ Resend now available")
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            val currentState = _state.value
            val otp = currentState.otp.trim()
            val verificationId = currentState.verificationId
            
            // Validate OTP
            if (otp.isEmpty()) {
                _state.value = _state.value.copy(error = "Please enter OTP")
                return@launch
            }
            
            if (otp.length != 6) {
                _state.value = _state.value.copy(error = "OTP must be 6 digits")
                return@launch
            }
            
            if (verificationId == null) {
                _state.value = _state.value.copy(error = "Verification ID not found. Please request OTP again.")
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val phoneNumber = "+91${currentState.phoneNumber}"
            android.util.Log.d("LoginViewModel", "🔐 Verifying OTP for: $phoneNumber")
            
            try {
                    authRepository.verifyOtpAndGetUserRole(verificationId, otp, phoneNumber).fold(
                        onSuccess = { (userRole, userExists) ->
                        android.util.Log.d("LoginViewModel", "✅ OTP Verification Success - Role: $userRole, Exists: $userExists")
                        
                            when {
                                !userExists -> {
                                    // User doesn't exist in the system at all
                                    _state.value = _state.value.copy(
                                        isLoading = false,
                                        isUnknownUser = true,
                                        isAuthenticated = true,
                                        hasNoRole = false
                                    )
                                android.util.Log.d("LoginViewModel", "👤 Unknown user authenticated")
                                }
                                userRole != null -> {
                                    // User exists and has a role
                                    _state.value = _state.value.copy(
                                        isLoading = false,
                                        userRole = userRole,
                                        isAuthenticated = true,
                                        hasNoRole = false,
                                        isUnknownUser = false
                                    )
                                android.util.Log.d("LoginViewModel", "🎉 User authenticated with role: $userRole")
                                }
                                else -> {
                                    // User exists but has no role assigned
                                    _state.value = _state.value.copy(
                                        isLoading = false,
                                        hasNoRole = true,
                                        isAuthenticated = true,
                                        isUnknownUser = false
                                    )
                                android.util.Log.d("LoginViewModel", "⚠️ User authenticated but no role assigned")
                                }
                            }
                        },
                        onFailure = { exception ->
                        android.util.Log.e("LoginViewModel", "❌ OTP Verification Failed: ${exception.message}", exception)
                        
                        val errorMessage = when {
                            exception.message?.contains("INVALID_VERIFICATION_CODE") == true -> 
                                "Invalid OTP. Please check and try again."
                            exception.message?.contains("SESSION_EXPIRED") == true -> 
                                "OTP expired. Please request a new OTP."
                            exception.message?.contains("TOO_MANY_REQUESTS") == true -> 
                                "Too many attempts. Please try again later."
                            else -> exception.message ?: "Invalid OTP. Please try again."
                        }
                        
                    _state.value = _state.value.copy(
                        isLoading = false,
                            error = errorMessage
                    )
                }
                )
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "❌ Exception in verifyOtp: ${e.message}", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred during verification"
                )
            }
        }
    }

    // Development bypass for testing (only use in debug builds)
    fun bypassAuthentication() {
        viewModelScope.launch {
            android.util.Log.d("LoginViewModel", "🔧 DEVELOPMENT: Bypassing authentication")
            
            val phoneNumber = "+91${_state.value.phoneNumber}"
            val userResult = authRepository.getUserWithRole(phoneNumber).getOrNull()
            
            if (userResult != null) {
                val (userRole, userExists) = userResult
                when {
                    !userExists -> {
                        _state.value = _state.value.copy(
                            isUnknownUser = true,
                            isAuthenticated = true,
                            hasNoRole = false,
                            isLoading = false
                        )
                    }
                    userRole != null -> {
                        _state.value = _state.value.copy(
                            userRole = userRole,
                            isAuthenticated = true,
                            hasNoRole = false,
                            isUnknownUser = false,
                            isLoading = false
                        )
                    }
                    else -> {
                        _state.value = _state.value.copy(
                            hasNoRole = true,
                            isAuthenticated = true,
                            isUnknownUser = false,
                            isLoading = false
                        )
                    }
                }
            } else {
                _state.value = _state.value.copy(
                    isUnknownUser = true,
                    isAuthenticated = true,
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun resetState() {
        android.util.Log.d("LoginViewModel", "🔄 Resetting login state")
        authRepository.clearVerificationState()
        _state.value = LoginState()
    }

    // Handle auto-filled OTP from SMS
    fun handleAutoFilledOtp(otp: String) {
        android.util.Log.d("LoginViewModel", "📲 Auto-filled OTP received: ${otp.take(2)}****")
        _state.value = _state.value.copy(
            otp = otp,
            isAutoFilling = true,
            autoFilledOtp = otp,
            error = null
        )
        
        // Auto-verify if we have verification ID
        if (_state.value.verificationId != null) {
            android.util.Log.d("LoginViewModel", "🚀 Auto-verifying filled OTP")
            verifyOtp()
        }
    }

    // Check if current user is logged in
    fun checkAuthState() {
        if (authRepository.isUserLoggedIn()) {
            val phoneNumber = authRepository.getCurrentUserPhoneNumber()
            android.util.Log.d("LoginViewModel", "🔐 User already logged in: $phoneNumber")
            
            if (phoneNumber != null) {
                viewModelScope.launch {
                    val userResult = authRepository.getUserWithRole(phoneNumber).getOrNull()
                    if (userResult != null) {
                        val (userRole, userExists) = userResult
                        when {
                            !userExists -> {
                                _state.value = _state.value.copy(
                                    isUnknownUser = true,
                                    isAuthenticated = true,
                                    phoneNumber = phoneNumber.removePrefix("+91")
                                )
                            }
                            userRole != null -> {
                                _state.value = _state.value.copy(
                                    userRole = userRole,
                                    isAuthenticated = true,
                                    phoneNumber = phoneNumber.removePrefix("+91")
                                )
                            }
                            else -> {
                                _state.value = _state.value.copy(
                                    hasNoRole = true,
                                    isAuthenticated = true,
                                    phoneNumber = phoneNumber.removePrefix("+91")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 