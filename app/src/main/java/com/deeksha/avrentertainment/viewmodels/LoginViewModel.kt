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
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit

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
    val isUnknownUser: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    // Users should be added manually through Firebase Console

    fun updatePhoneNumber(phoneNumber: String) {
        _state.value = _state.value.copy(phoneNumber = phoneNumber)
    }

    fun updateOtp(otp: String) {
        _state.value = _state.value.copy(otp = otp)
    }

    fun sendOtp(activity: Activity) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val phoneNumber = "+91${_state.value.phoneNumber}"
            
            try {
                authRepository.sendOtp(
                    activity = activity,
                    phoneNumber = phoneNumber,
                    onVerificationComplete = { verificationId ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            otpSent = true,
                            verificationId = verificationId
                        )
                    },
                    onVerificationFailed = { exception ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to send OTP"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val verificationId = _state.value.verificationId
                val otp = _state.value.otp
                val phoneNumber = "+91${_state.value.phoneNumber}"
                
                if (verificationId != null) {
                    authRepository.verifyOtpAndGetUserRole(verificationId, otp, phoneNumber).fold(
                        onSuccess = { (userRole, userExists) ->
                            when {
                                !userExists -> {
                                    // User doesn't exist in the system at all
                                    _state.value = _state.value.copy(
                                        isLoading = false,
                                        isUnknownUser = true,
                                        isAuthenticated = true,
                                        hasNoRole = false
                                    )
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
                                }
                                else -> {
                                    // User exists but has no role assigned
                                    _state.value = _state.value.copy(
                                        isLoading = false,
                                        hasNoRole = true,
                                        isAuthenticated = true,
                                        isUnknownUser = false
                                    )
                                }
                            }
                        },
                        onFailure = { exception ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to verify OTP"
                            )
                        }
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Verification ID not found"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    // Development bypass for testing
    fun bypassAuthentication() {
        viewModelScope.launch {
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
        _state.value = LoginState()
    }
} 