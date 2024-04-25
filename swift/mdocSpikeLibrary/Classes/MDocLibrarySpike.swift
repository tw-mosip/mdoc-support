

import Foundation
import SwiftCBOR

public class CborLibrayUtils{
    public init() {}
    public func greet(){
        print("Hello world")
    }
    
    public func decodeAndParseCBOR() -> String{
        guard let decodedBase64Data = Data(base64EncodedURLSafe: base64EncodedString) else {
          // handle errors in decoding base64 string here
          print("error data ---->")
          return ""
        }

        let inputToCBORDecode = Array(decodedBase64Data)

        let decodedCBORData = try! CBOR.decode(inputToCBORDecode)

        var jsonResult = [String: Any]()

        jsonResult["validityInfo"] = decodeAndParseIssuerAuth(decodedCBORData: decodedCBORData)
        jsonResult["credentialSubject"] = decodeAndParseCredentialSubject(
          decodedCBORData: decodedCBORData)

        // Convert the dictionary to JSON data
        do {
          let jsonData = try JSONSerialization.data(withJSONObject: jsonResult, options: [])

          // Print the JSON data
            let jsonObject = String(data: jsonData, encoding: .utf8)!
            
            print("Final JSON Result ---->", jsonObject)
            return jsonObject
        } catch {
          print("Error converting to JSON: \(error)")
            return ""
        }
       
    }
    
    func decodeAndParseIssuerAuth(decodedCBORData: CBOR?) -> [String: Any] {
      let issuerAuth = decodedCBORData?["issuerSigned"]?["issuerAuth"]

      var validityInfoJson = [String: Any]()

      if case .array(let issuerAuthArray)? = issuerAuth {
        for index in issuerAuthArray.indices {
          if index == 2 {
            if case .tagged(_, let taggedValue) = issuerAuthArray[index] {
              if case .byteString(let value) = taggedValue {
                if let issuerAuthElement = try? CBOR.decode(value) {
                    let validityInfo = issuerAuthElement?["validityInfo"]
                  if case .tagged(_, let taggedValue) = validityInfo?["signed"] {
                    if case .utf8String(let actualValue) = taggedValue {
                      validityInfoJson["signed"] = actualValue
                    }

                  }
                  if case .tagged(_, let taggedValue) = validityInfo?["validFrom"] {
                    if case .utf8String(let actualValue) = taggedValue {
                      validityInfoJson["validFrom"] = actualValue
                    }

                  }
                  if case .tagged(_, let taggedValue) = validityInfo?["validUntil"] {
                    if case .utf8String(let actualValue) = taggedValue {
                      validityInfoJson["validUntil"] = actualValue
                    }

                  }
                }
              }

            }
          }
        }
      }

      return validityInfoJson
    }

    func decodeAndParseCredentialSubject(decodedCBORData: CBOR?) -> [String: Any] {
      var credentialSubjectJson = [String: Any]()

      let credentialSubject = decodedCBORData?["issuerSigned"]?["nameSpaces"]?[
        "org.iso.18013.5.1"]

      if case .array(let itemArray)? = credentialSubject {

        for item in itemArray {
          if case .tagged(_, let cBOR) = item {

            if case let .byteString(inputByteString) = cBOR {
              if let nameSpaceElement = try? CBOR.decode(inputByteString) {

                  let elementID = nameSpaceElement?["elementIdentifier"]
                  var jsonKey: String?

                  let elementValue = nameSpaceElement?["elementValue"]
                var jsonValue: Any?

                if case .utf8String(let unwrappedID) = elementID {
                  jsonKey = unwrappedID
                }

                if case .utf8String(let unwrappedUTF8String) = elementValue {
                  jsonValue = unwrappedUTF8String
                }

                if case .tagged(_, let taggedValue) = elementValue {
                  if case .utf8String(let unwrappedUTF8String) = taggedValue {
                    jsonValue = unwrappedUTF8String
                  }
                }
                if case .array(let drivingPrivilegeArray)? = elementValue {

                  var dpJsonArray = [[String: String]]()
                    
                  for dpItem in drivingPrivilegeArray {

                    var dpJson = [String: String]()

                    if case .utf8String(let unwrappedUTF8String) = dpItem["vehicle_category_code"] {
                      dpJson["vehicle_category_code"] = unwrappedUTF8String
                    }
                    if case .tagged(_, let taggedValue) = dpItem["expiry_date"] {
                      if case .utf8String(let unwrappedUTF8String) = taggedValue {
                        dpJson["expiry_date"] = unwrappedUTF8String
                      }
                    }
                    if case .tagged(_, let taggedItem) = dpItem["issue_date"] {
                      if case .utf8String(let unwrappedUTF8String) = taggedItem {
                        dpJson["issue_date"] = unwrappedUTF8String
                      }
                    }
                    dpJsonArray.append(dpJson)

                  }
                  jsonValue = dpJsonArray
                }

                  credentialSubjectJson[jsonKey ?? ""] = jsonValue

              }
            }
          }
        }
      }
      return credentialSubjectJson

    }

}
extension Data {
  init?(base64EncodedURLSafe string: String, options: Base64DecodingOptions = []) {
    let string =
      string
      .replacingOccurrences(of: "-", with: "+")
      .replacingOccurrences(of: "_", with: "/")

    self.init(base64Encoded: string, options: options)
  }
}

