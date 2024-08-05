package com.easebill.app.network.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

@Serializable
data class Auth(
    val statusCode: Int?,
    val message: String?,
    val data: UserInfo?
)

@Serializer(forClass = BigDecimal::class)
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }
}

@Serializable
data class UserProfile(
    val statusCode: Int?,
    val message: String?,
    val user: User?
)

@Serializable
data class UserInfo(
    val statusCode: Int?,
    val message: String?,
    val token: String?,
    val user: User?,
    val isDeviceNew: Boolean?,
    val expireAt: String?
)


@Serializable
data class CustomResponse(
    val statusCode: Int?,
    val message: String?
)

@Serializable
data class User(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val userName: String,
    val gender: String,
    val role: String,
    val dateOfBirth: String?,
    val hasSetTransactionPin: String,
    val hasVerifiedPhone: String?,
    val referralCode: String?,
)


@Serializable
data class Token(
    val token: String?,
    val expireAt: String?,
)

@Serializable
data class RefreshToken(
    val statusCode: Int?,
    val message: String?,
    val token: Token?,
) : java.io.Serializable

@Serializable
data class DeviceMapping(
    val statusCode: Int?,
    val message: String?,
    val data: ArrayList<Devices>
)

@Serializable
data class Devices(
    val deviceID: String?,
    val deviceOS: String?,
    val id: String?,
    val deviceToken: String?,
    val deviceType: String?
)

