# Project-Specific and Approver Notification System

## Overview

This document outlines the implementation of the project-specific notification system for AVR Entertainment, including approver-specific notifications in the Project Selection page and project-specific notifications after project selection.

## Features Implemented

### 1. Approver-Specific Notifications in Project Selection Page

**Location**: `ProjectSelectionScreen.kt`

**Features**:
- Shows pending notifications for approvers before they log in
- Displays notifications in an expandable panel
- Color-coded notification indicators based on priority
- Badge showing notification count
- Only shows unread notifications to avoid clutter

**Notification Types Shown**:
- `EXPENSE_SUBMITTED` - New expense submissions awaiting approval
- `PENDING_APPROVAL_REMINDER` - Reminders for pending approvals
- `BUDGET_EXCEEDED_PROJECT` - Project budget exceeded alerts
- `BUDGET_EXCEEDED_DEPARTMENT` - Department budget exceeded alerts

### 2. Project-Specific Notifications After Project Selection

**Location**: `ProjectDetailsWithNotificationsScreen.kt`

**Features**:
- Shows notifications specific to the selected project
- Displays project information along with notifications
- Automatic project selection notification creation
- Expandable notification panel
- Navigation to full project dashboard

**Notification Types Shown**:
- `EXPENSE_APPROVED` - Expense approval confirmations
- `EXPENSE_REJECTED` - Expense rejection notifications
- `BUDGET_ADDED` - Budget additions to project/departments
- `BUDGET_DEDUCTED` - Budget deductions from project/departments
- `BUDGET_EXCEEDED_PROJECT` - Project budget exceeded alerts
- `BUDGET_EXCEEDED_DEPARTMENT` - Department budget exceeded alerts
- `PROJECT_UPDATED` - Project selection confirmations

## Implementation Details

### NotificationRepository Enhancements

Added three new methods to `NotificationRepository` class:

#### 1. `getProjectSpecificNotifications()`
```kotlin
suspend fun getProjectSpecificNotifications(
    userId: String,
    projectId: String,
    userRole: UserRole
): Result<List<NotificationData>>
```
- Filters notifications by project ID and user role
- Returns up to 10 most recent project-specific notifications
- Role-based filtering ensures users see appropriate notification types

#### 2. `getApproverNotificationsPreLogin()`
```kotlin
suspend fun getApproverNotificationsPreLogin(
    approverId: String,
    limitCount: Int = 5
): Result<List<NotificationData>>
```
- Fetches unread notifications for approvers
- Shows only approval-related notifications
- Limited count to prevent UI overflow

#### 3. `sendProjectSelectionNotification()`
```kotlin
suspend fun sendProjectSelectionNotification(
    userId: String,
    projectId: String,
    projectName: String
): Result<Unit>
```
- Creates a confirmation notification when user selects a project
- Helps track project selection events

### UI Components

#### NotificationItem Component
- Displays individual notification with title, message, and type indicator
- Color-coded based on notification type:
  - **Blue**: Expense submissions (`EXPENSE_SUBMITTED`)
  - **Red**: Budget exceeded alerts (`BUDGET_EXCEEDED_*`)
  - **Orange**: Pending approval reminders (`PENDING_APPROVAL_REMINDER`)
- Shows read/unread status with different opacity

#### NotificationPanel Component
- Expandable container for multiple notifications
- Shows notification count badge
- Handles empty states with helpful messages
- Scrollable list for multiple notifications

### Navigation Flow Updates

#### Updated NavGraph
- Added `ProjectDetailsWithNotificationsScreen` route
- Supports project ID parameter passing
- Includes navigation to `ProductionHeadHomeScreen` with project context

#### Navigation Routes
```kotlin
"project_details/{projectId}" -> ProjectDetailsWithNotificationsScreen
"production_head_home/{projectId}" -> ProductionHeadHomeScreen
```

## Usage Instructions

### For Approvers (Project Selection Page)

1. **Viewing Notifications**: Notifications automatically load when the approver accesses the project selection page
2. **Expanding Panel**: Click on the notification panel header to expand/collapse
3. **Notification Count**: Badge shows the number of pending notifications
4. **Selecting Project**: Choose a project to continue to project-specific notifications

### For Users (After Project Selection)

1. **Project Selection**: Select a project from the project list
2. **Notification Display**: Project-specific notifications automatically load
3. **Project Information**: View project details alongside notifications
4. **Dashboard Access**: Use "Open Dashboard" button for full project management

### For Developers

#### Creating Sample Notifications
```kotlin
// Call this method to create sample notifications for testing
createSampleProjectNotifications()
```

#### Testing the System
```kotlin
// Test all notification types including project-specific ones
testAllNotificationTypes()
```

## Sample Data

The system includes sample project-specific notifications:

1. **Expense Approved**: Camera equipment expense approval (₹15,000)
2. **Budget Added**: Sound department budget addition (₹50,000)
3. **New Expense Submitted**: Lighting equipment submission for approval (₹8,500)

## Configuration

### Default User IDs
- **User**: `+919876543210`
- **Approver/Production Head**: `+918765432109`

### Project ID
- **Sample Project**: `project_001` (Movie Production A)

## Color Scheme

### Notification Types
- **Expense Submitted**: Blue (`#2E5CFF`)
- **Budget Exceeded**: Red (`#E53E3E`)
- **Pending Approval**: Orange (`#FF9500`)
- **General**: Gray (`#808080`)

### UI Elements
- **Primary**: Blue (`#4169E1`)
- **Success**: Green (`#34C759`)
- **Warning**: Orange (`#FF9500`)
- **Error**: Red (`#E53E3E`)

## Performance Considerations

1. **Notification Limiting**: Project notifications limited to 10 most recent
2. **Role-based Filtering**: Only relevant notifications shown per user role
3. **Lazy Loading**: Notifications loaded only when screens are accessed
4. **Caching**: Notifications cached in component state to avoid repeated API calls

## Future Enhancements

1. **Real-time Updates**: WebSocket integration for live notification updates
2. **Mark as Read**: Functionality to mark notifications as read
3. **Notification Categories**: Additional filtering by notification categories
4. **Push Notifications**: Integration with FCM for background notifications
5. **Notification History**: Archive system for older notifications

## Troubleshooting

### Common Issues

1. **Notifications Not Loading**: Check user ID and project ID configuration
2. **Empty Notification Panel**: Verify sample data creation
3. **Navigation Issues**: Ensure proper route configuration in NavGraph

### Debug Logs
Monitor these log tags for debugging:
- `ProjectNotifications`
- `ApproverNotifications`
- `NotificationTest`
- `ProjectSelection`
- `ProjectDetails`

## Summary

The implemented notification system provides:
- ✅ Approver-specific notifications in Project Selection page
- ✅ Project-specific notifications after project selection
- ✅ Role-based notification filtering
- ✅ Expandable notification panels with proper UI/UX
- ✅ Sample data for testing
- ✅ Proper navigation flow integration
- ✅ Color-coded notification indicators
- ✅ Performance optimizations

The system enhances user experience by providing contextual, relevant notifications at the right time in the user journey. 