# Unknown User Functionality - Testing Guide

## Overview
The AVR Entertainment app now handles unknown users (users not registered in the system) by showing a dedicated screen with instructions to contact the administrator.

## How It Works

### User Types Handled:
1. **Known Users with Roles** - Navigate to their respective home screens
2. **Known Users without Roles** - Navigate to "no role assigned" screen  
3. **Unknown Users** - Navigate to "unknown user" screen ⭐ **NEW**

### Implementation Details:

#### AuthRepository Changes:
- Added `getUserWithRole()` method that returns `Pair<UserRole?, Boolean>`
- The Boolean indicates if the user exists in the system
- Modified `verifyOtpAndGetUserRole()` to return user existence status

#### LoginViewModel Changes:
- Added `isUnknownUser` state to `LoginState`
- Updated authentication logic to handle three user states
- Modified both `verifyOtp()` and `bypassAuthentication()` methods

#### UI Changes:
- Created `UnknownUserScreen` composable with professional design
- Added "unknown_user" route to navigation
- Updated login navigation logic to handle unknown users

## Testing Scenarios

### Test Users (Created automatically on app startup):
- **+919876543210** - USER role ✅
- **+918765432109** - APPROVER role ✅  
- **+917654321098** - USER role ✅

### Test Unknown User:
- **+911234567890** - Not in system ⚠️ (Will show Unknown User screen)
- **+910000000000** - Not in system ⚠️ (Will show Unknown User screen)
- Any other phone number not in the test users list

## How to Test:

1. **Install and run the app**
2. **Test Known User**: Enter `9876543210` → Should login as USER
3. **Test Unknown User**: Enter `1234567890` → Should show Unknown User screen
4. **Verify Navigation**: Click "Back to Login" → Should return to login screen

## Unknown User Screen Features:

- ⚠️ Warning icon with professional styling
- Clear "Access Restricted" title
- Informative message about unregistered phone number
- Instructions to contact administrator
- Contact information card (placeholder)
- "Back to Login" button for easy navigation
- Consistent design with app theme

## Code Flow:

```
Login with Phone Number
         ↓
    Send/Verify OTP
         ↓
  AuthRepository.getUserWithRole()
         ↓
┌─────────────────────────────────┐
│  User exists?                   │
├─────────────────────────────────┤
│  YES → Has role?                │
│    YES → Navigate to home       │
│    NO  → "No role assigned"     │
│                                 │
│  NO  → "Unknown user" screen ⭐  │
└─────────────────────────────────┘
```

## Production Notes:

- In production, administrators should add users through Firebase Console
- Users collection structure: Document ID = phone number, Fields = role, createdBy, timestamp
- The unknown user screen provides clear next steps for access
- Contact information should be updated with real admin details

## Security Benefits:

- Prevents unauthorized access attempts
- Provides clear user feedback
- Maintains professional user experience
- Gives administrators control over user access
- Logs authentication attempts for monitoring 