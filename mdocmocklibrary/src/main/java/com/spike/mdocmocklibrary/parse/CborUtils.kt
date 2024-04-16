package com.spike.mdocmocklibrary.parse

import co.nstant.`in`.cbor.CborDecoder
import co.nstant.`in`.cbor.model.Array
import co.nstant.`in`.cbor.model.DataItem
import co.nstant.`in`.cbor.model.MajorType
import com.google.gson.Gson
import com.spike.mdocmocklibrary.mock.MDLVCResponse
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

            val mDocVcJsonObject = buildJsonObject {

                val cbors = CborDecoder(ByteArrayInputStream(cborBytes)).decode()

                val validityInfoJsonObject = parseIssuerAuth(cbors)

                put("validityInfo", validityInfoJsonObject)

                val elements =
                    cbors[0]["issuerSigned"]["nameSpaces"]["org.iso.18013.5.1"] as CborArray

                val credentialSubjectJsonObject = buildJsonObject {
                    for (item in elements.dataItems) {
                        val decoded =
                            CborDecoder(ByteArrayInputStream((item as CborByteString).bytes)).decode()

                        val identifier = decoded[0]["elementIdentifier"].toString()
                        val value = decoded[0]["elementValue"]

                        when (value.majorType) {
                            MajorType.UNICODE_STRING -> put(identifier, value.toString())
                            MajorType.ARRAY -> {
                                var dpJsonObject = buildJsonObject {  }
                                (value as Array).dataItems.forEach {drivingPrivileges ->
                                    dpJsonObject = buildJsonObject {
                                        put("issue_date", drivingPrivileges["issue_date"].toString())
                                        put("expiry_date", drivingPrivileges["expiry_date"].toString())
                                        put("vehicle_category_code", drivingPrivileges["vehicle_category_code"].toString())

                                    }
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
                put("credentialSubject", credentialSubjectJsonObject)

            }




/*
            //Optional to map json string to the data class
            val vcJsonObject = Json.decodeFromString<MDLVCResponse>(mDocVcJsonObject)

            val gson = Gson()
            val jsonString = gson.toJson(vcJsonObject)
            System.out.println("Json String-->: $jsonString")

            // val jsonString = vcJsonObject.toString()
            System.out.println("Json Object-->: ${vcJsonObject.toString()}")
*/

            return mDocVcJsonObject
        }

        private fun parseIssuerAuth(cbors: MutableList<DataItem>): JsonObject {


            val validityJsonObjectResult = buildJsonObject {

                val elements = cbors[0]["issuerSigned"]["issuerAuth"] as CborArray

                elements.dataItems.forEachIndexed { index, dataItem ->
                    if(index==2){
                        val decodedissuerAuth =
                            CborDecoder(ByteArrayInputStream((dataItem as CborByteString).bytes)).decode()
                        var validityInfo = decodedissuerAuth[0]["validityInfo"]

                        put("signed", validityInfo["signed"].toString())
                        put("validFrom", validityInfo["validFrom"].toString())
                        put("validUntil", validityInfo["validUntil"].toString())
                    }

                }
            }
            return validityJsonObjectResult
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