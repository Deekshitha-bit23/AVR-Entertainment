# AVR Entertainment - Comprehensive Notification System

## Overview
Complete role-specific notification system implemented for AVR Entertainment's expense management app. The system provides targeted notifications based on user roles and ensures all stakeholders receive relevant information at the right time.

## Role-Based Notification Distribution

### 👤 USER Role Notifications
Users (team members who submit expenses) receive notifications about:

1. **Expense Approval/Rejection Alerts**
   - ✅ `EXPENSE_APPROVED`: "Your ₹X expense for [Project] has been approved by [Approver]"
   - ❌ `EXPENSE_REJECTED`: "Your ₹X expense for [Project] has been rejected by [Approver]"

2. **Budget Change Notifications**
   - 💰 `BUDGET_ADDED`: "₹X has been added to [Department] budget for [Project]"
   - 💸 `BUDGET_DEDUCTED`: "₹X has been deducted from [Department] budget for [Project]"
   - 📊 Both project-wise and department-wise budget changes

3. **Budget Exceeded Alerts**
   - ⚠️ `BUDGET_EXCEEDED_DEPARTMENT`: "[Department] budget exceeded by ₹X"
   - ⚠️ `BUDGET_EXCEEDED_PROJECT`: "[Project] budget exceeded by ₹X"

### 👨‍💼 APPROVER Role Notifications
Approvers receive notifications about:

1. **Pending Approval Alerts (Project-wise)**
   - 🔔 `EXPENSE_SUBMITTED`: "₹X expense for [Project] needs approval"
   - ⏰ `PENDING_APPROVAL_REMINDER`: "X expenses (₹Y total) awaiting approval for [Project]"
   - Detailed breakdown of top pending expenses

2. **Budget Exceeded Alerts (Both Roles)**
   - ⚠️ `BUDGET_EXCEEDED_DEPARTMENT`: Department-level budget overruns
   - ⚠️ `BUDGET_EXCEEDED_PROJECT`: Project-level budget overruns

3. **Budget Change Notifications**
   - 💰 `BUDGET_ADDED`: Budget increases for oversight
   - 💸 `BUDGET_DEDUCTED`: Budget reductions for planning

## System Features

### Smart Notification Management
1. **Duplicate Prevention**: Prevents multiple notifications for same event
2. **Spam Protection**: 5% threshold for budget exceeded notifications
3. **Automatic Cleanup**: Removes old budget exceeded notifications
4. **Batch Processing**: Groups related notifications efficiently

### Real-time Updates
1. **15-second Refresh**: For approvers when notification panel is open
2. **5-second Refresh**: For users when notification panel is open
3. **Automatic Monitoring**: 30-minute periodic checks for pending approvals
4. **Push Notification Simulation**: Local notifications for testing

### Visual Indicators
1. **Badge Counts**: Show unread notification counts
2. **Read/Unread Status**: Visual distinction with background colors
3. **Notification Limits**: Maximum 4 notifications in dropdown (with "View All" option)
4. **Role-Specific Icons**: Different notification icons for different types

## Implementation Status
✅ **Complete Implementation**
- All notification types implemented and tested
- Role-based filtering working correctly
- Real-time updates functioning
- Comprehensive logging and error handling
- Push notification simulation active
- Budget monitoring with configurable thresholds
- Duplicate prevention and spam protection
- Enhanced user experience with visual indicators

## Testing Configuration
- Test users: `+919876543210`, `+917654321098`
- Test approver: `+918765432109`
- Department budgets: Costumes (₹1L), Set Design (₹3L), Camera (₹1.5L)
- Project budget: ₹50L total
- Notification thresholds: 5% buffer for budget exceeded alerts

## System Architecture

### 1. Notification Types (Complete Enum)
```kotlin
enum class NotificationType {
    // Expense related
    EXPENSE_SUBMITTED,      // To approvers when new expense submitted
    EXPENSE_APPROVED,       // To users when expense approved
    EXPENSE_REJECTED,       // To users when expense rejected
    
    // Budget related
    BUDGET_ADDED,           // To all when budget increased
    BUDGET_DEDUCTED,        // To all when budget decreased
    BUDGET_EXCEEDED_PROJECT,    // To all when project budget exceeded
    BUDGET_EXCEEDED_DEPARTMENT, // To all when department budget exceeded
    
    // Approval related
    PENDING_APPROVAL_REMINDER   // To approvers for pending items
}
```

### 2. Notification Data Model
```kotlin
data class NotificationData(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.EXPENSE_SUBMITTED,
    val recipientId: String = "",
    val senderId: String = "",
    val expenseId: String? = null,
    val projectId: String? = null,
    val departmentId: String? = null,
    val budgetId: String? = null,
    val amount: Double? = null,
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)
```

### 3. Core Notification Methods

#### Budget Change Notifications
- **Recipients**: Both Users and Approvers
- **Triggers**: Budget modifications (manual or system-generated)
- **Scope**: Project-level and Department-level
- **Enhanced Logging**: Comprehensive tracking of budget changes

#### Expense Status Notifications
- **Recipients**: Users (expense submitters only)
- **Triggers**: Approval/rejection actions by approvers
- **Features**: Duplicate prevention, detailed logging
- **Push Notifications**: Simulated push notifications with local display

#### Pending Approval Reminders
- **Recipients**: Approvers only
- **Triggers**: Multiple pending expenses or expenses older than 6 hours
- **Features**: Expense breakdown, project-wise grouping
- **Smart Filtering**: Only sends when action is needed

#### Budget Exceeded Alerts
- **Recipients**: Both Users and Approvers
- **Triggers**: Spending exceeds budget limits (with 5% threshold to prevent spam)
- **Features**: Automatic cleanup of old alerts, detailed spending breakdown
- **Scope**: Both project and department level monitoring

### 4. Notification Orchestrator System

The `sendRoleSpecificNotifications()` method provides centralized notification management:

```kotlin
suspend fun sendRoleSpecificNotifications(
    notificationType: NotificationType,
    projectId: String,
    projectName: String,
    department: String? = null,
    amount: Double? = null,
    expenseId: String? = null,
    senderId: String = "system",
    additionalData: Map<String, Any> = emptyMap()
): Result<Unit>
```

### 5. Role-Based Filtering

The system automatically filters notifications based on user roles:

- **Users**: See approval results, budget changes, and budget alerts
- **Approvers**: See pending submissions, approval reminders, budget changes, and budget alerts

## Implementation Features

### Smart Notification Management
1. **Duplicate Prevention**: Prevents multiple notifications for same event
2. **Spam Protection**: 5% threshold for budget exceeded notifications
3. **Automatic Cleanup**: Removes old budget exceeded notifications
4. **Batch Processing**: Groups related notifications efficiently

### Real-time Updates
1. **15-second Refresh**: For approvers when notification panel is open
2. **5-second Refresh**: For users when notification panel is open
3. **Automatic Monitoring**: 30-minute periodic checks for pending approvals
4. **Push Notification Simulation**: Local notifications for testing

### Visual Indicators
1. **Badge Counts**: Show unread notification counts
2. **Read/Unread Status**: Visual distinction with background colors
3. **Notification Limits**: Maximum 4 notifications in dropdown (with "View All" option)
4. **Role-Specific Icons**: Different notification icons for different types

### Enhanced Logging
- Comprehensive logging throughout the notification pipeline
- Detailed tracking of notification creation, delivery, and user interaction
- Error handling and failure reporting
- Performance monitoring for notification operations

## Testing and Validation

### Mock Data System
- Test users: `+919876543210`, `+917654321098`
- Test approver: `+918765432109`
- Sample projects with realistic budgets and expenses
- Comprehensive test scenarios for all notification types

### Budget Monitoring
- Department budgets: Costumes (₹1L), Set Design (₹3L), Camera (₹1.5L), etc.
- Project budget: ₹50L total
- Automatic monitoring with configurable thresholds
- Real-time budget tracking and alerts

### Notification Testing
- `testAllNotificationTypes()` method for comprehensive testing
- Simulated budget changes and exceeded scenarios
- End-to-end notification flow validation
- Role-specific notification filtering verification

## Configuration

### Budget Limits (Configurable)
```kotlin
val departmentBudgets = mapOf(
    "Costumes" to 100000.0,
    "Set Design" to 300000.0,
    "Camera" to 150000.0,
    "Lighting" to 100000.0,
    "Sound" to 80000.0,
    // ... more departments
)
val projectBudgetLimit = 5000000.0 // 50 lakh total
```

### Notification Thresholds
- **Budget Exceeded Threshold**: 5% over limit to prevent spam
- **Pending Approval Reminder**: 6 hours for testing (configurable)
- **Notification Display Limit**: 4 notifications in dropdown
- **Refresh Intervals**: 5-15 seconds based on user activity

## Benefits

1. **Role-Specific Relevance**: Users only see notifications relevant to their role
2. **Comprehensive Coverage**: All expense and budget scenarios covered
3. **Real-time Updates**: Immediate notification of important events
4. **Spam Prevention**: Smart filtering prevents notification overload
5. **Enhanced User Experience**: Clean, organized notification system
6. **Audit Trail**: Complete logging for troubleshooting and analysis
7. **Scalable Architecture**: Easy to add new notification types and roles

## Future Enhancements

1. **Push Notification Integration**: Real FCM/APNS integration
2. **Email Notifications**: Backup notification channel
3. **Notification Preferences**: User-configurable notification settings
4. **Advanced Filtering**: Custom notification filters and categories
5. **Analytics**: Notification engagement and effectiveness tracking
6. **Multi-language Support**: Localized notification messages

This comprehensive notification system ensures that all stakeholders in the AVR Entertainment expense management system receive timely, relevant, and actionable information based on their specific roles and responsibilities. 