package com.easebill.app.network.model

object UserRequestBodies {
    data class SignInBody(
        val emailAddress: String,
        val credential: String,
        val credentialType: String,
        val deviceDetails: DeviceInfoBody? = null,
    )

    data class SignUpBody(
        val firstName: String,
        val lastName: String,
        val userName: String,
        val phoneNumber: String,
        val emailAddress: String,
        val password: String,
        val role: String = "USER",
    )

    data class ResetPasswordBody(
        val emailAddress: String,
        val newPassword: String
    )

    data class ForgotPassword(
        val emailAddress: String
    )

    data class VerifyEmailOTP(
        val otp: String
    )

    data class ValidateForgotPassword(
        val emailAddress: String,
        val otp: String
    )

    data class DeleteDeviceBody(
        val id: String
    )

    data class DeviceInfoBody(
        val deviceToken: String,
        val deviceOS: String,
        val deviceType: String,
        val deviceID: String
    )


    data class ResetTransactionPINBody(
        val oldTransactionPin: String,
        val newTransactionPin: String
    )

    data class ChangePasswordBody(
        val currentPassword: String,
        val newPassword: String
    )

    data class TransactionPINBody(
        val transactionPin: String
    )
}
