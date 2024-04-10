package com.spike.mdocmocklibrary.parse

import co.nstant.`in`.cbor.CborDecoder
import co.nstant.`in`.cbor.model.DataItem
import co.nstant.`in`.cbor.model.MajorType
import com.google.gson.Gson
import com.spike.mdocmocklibrary.mock.ParsedVcResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import java.io.ByteArrayInputStream
import co.nstant.`in`.cbor.model.Array as CborArray
import co.nstant.`in`.cbor.model.ByteString as CborByteString
import co.nstant.`in`.cbor.model.Map as CborMap
import co.nstant.`in`.cbor.model.UnicodeString as CborUnicodeString

class CborUtils {

    companion object {


        fun parseCborToGetJsonResponse(cborBytes: ByteArray): JsonObject {
            val jsonObjectResult = buildJsonObject {

                val cbors = CborDecoder(ByteArrayInputStream(cborBytes)).decode()

                val elements =
                    cbors[0]["issuerSigned"]["nameSpaces"]["org.iso.18013.5.1"] as CborArray

                for (item in elements.dataItems) {
                    val decoded =
                        CborDecoder(ByteArrayInputStream((item as CborByteString).bytes)).decode()

                    val identifier = decoded[0]["elementIdentifier"].toString()
                    val value = decoded[0]["elementValue"]

                    when (value.majorType) {
                        MajorType.UNICODE_STRING -> put(identifier, value.toString())
                        MajorType.ARRAY -> {
                            val dpJsonObject = buildJsonObject {
                                val drivingPrivileges = value.get(0)
                                put("issue_date", drivingPrivileges["issue_date"].toString())
                                put("expiry_date", drivingPrivileges["expiry_date"].toString())
                                put("vehicle_category_code", drivingPrivileges["vehicle_category_code"].toString())

                            }
                            putJsonArray(identifier) {
                                add(dpJsonObject)
                            }
                        }
                        else -> {
                            put(identifier, value.toString())
                        }
                    }
                }
            }


            //Optional to map json string to the data class
            val vcJsonObject = Json.decodeFromString<ParsedVcResponse>(jsonObjectResult.toString())

            val gson = Gson()
            val jsonString = gson.toJson(vcJsonObject)
            System.out.println("Json String-->: $jsonString")

           // val jsonString = vcJsonObject.toString()
            System.out.println("Json Object-->: ${vcJsonObject.toString()}")

            System.out.println("Json Object-driving-Vehicle category code->: ${vcJsonObject.drivingPrivileges.get(0).vehicleCategoryCode}")
            System.out.println("Json Object-expiry-->: ${vcJsonObject.drivingPrivileges.get(0).expiryDateDP}")

            return jsonObjectResult
        }
    }



}

operator fun DataItem.get(name: String): DataItem {
    check(this.majorType == MajorType.MAP)
    this as CborMap
    return this.get(CborUnicodeString(name))
}

operator fun DataItem.get(index: Int): DataItem {
    check(this.majorType == MajorType.ARRAY)
    this as CborArray
    return this.dataItems[index]
}