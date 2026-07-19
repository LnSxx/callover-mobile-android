package com.callover.android.core.calls

enum class CallEndReason {
    Local,
    Remote,
    Declined,
    Cancelled,
    Timeout,
    Failed,
    PermissionDenied,
}