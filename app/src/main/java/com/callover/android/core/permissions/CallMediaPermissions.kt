package com.callover.android.core.permissions

import com.callover.android.core.domain.models.CallType

data class CallMediaPermissions(
    val hasMicrophone: Boolean,
    val hasCamera: Boolean,
) {
    fun isGrantedFor(callType: CallType): Boolean {
        return when (callType) {
            CallType.Audio -> hasMicrophone
            CallType.Video -> hasMicrophone && hasCamera
        }
    }
}

fun CallType.requiredPermissions(): Array<String> {
    return when (this) {
        CallType.Audio -> arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
        )

        CallType.Video -> arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
        )
    }
}