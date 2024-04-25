package com.spike.mdocmocklibrary.mock
//
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class IssuerAPIResponse(
    @SerializedName("credential")
    val credential: String,
)


@Serializable
data class ParsedVcResponse(
    @SerialName("given_name")
    val givenName: String,
    @SerialName("family_name")
    val familyName: String,
    @SerialName("issue_date")
    val issueDate: String,
    @SerialName("expiry_date")
    val expiryDate: String,
    @SerialName("birth_date")
    val birthDate: String,
    @SerialName("issuing_country")
    val issuingCountry: String,
    @SerialName("document_number")
    val documentNumber: String,
    @SerialName("driving_privileges")
    val drivingPrivileges: List<DrivingPrivileges>
)

@Serializable
data class DrivingPrivileges(
    @SerialName("vehicle_category_code")
    val vehicleCategoryCode: String,
    @SerialName("issue_date")
    val issueDateDP: String,
    @SerialName("expiry_date")
    val expiryDateDP: String
)