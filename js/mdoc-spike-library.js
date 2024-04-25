import cbor from 'cbor'



/** Methods to Decode Base64 URL */
const decodeBase64 = (encoded) => {
  return new Uint8Array(atob(encoded)
      .split('')
      .map((c) => c.charCodeAt(0)));
};

const decodeBase64URL = (input) => {
  try {
      return decodeBase64(input.replace(/-/g, '+').replace(/_/g, '/').replace(/\s/g, ''));
  }
  catch (_a) {
      throw new TypeError('The input to be decoded is not correctly encoded.');
  }
};



export const decodeAndParseCborData = (base64url) => {
  

    var decodedByteArray = decodeBase64URL(base64url)
    var decodedCborData = cbor.decode(decodedByteArray)

    const resultObject = {}

    var credentialSubject = parseCredentialSubject(decodedCborData)
    resultObject["credentialSubject"] = credentialSubject

    var validityInfo = parseValidityInfo(decodedCborData)
    resultObject["validityInfo"] = validityInfo
    
    return resultObject
      
}
function parseCredentialSubject(decodedCborData){

  var credentialSubjectObject = {}
  var elements = decodedCborData.issuerSigned.nameSpaces["org.iso.18013.5.1"]

    
  for (let i = 0; i < elements.length; i++) {
    var decodedElement = cbor.decode(elements[i].value)

    var elementIdentifier = decodedElement["elementIdentifier"].toString()
    var elementValue = decodedElement["elementValue"]


    switch (typeof elementValue) {
      case 'string':
        credentialSubjectObject[`${elementIdentifier}`] = elementValue
        break;

        case 'object':
          if(elementValue[0] == null){
            credentialSubjectObject[`${elementIdentifier}`] = elementValue.value
          } else {
            const arr = []
            for(let i=0;i<elementValue.length;i++){
              const drivingPrivilegeObject = {}
              arr[i] = {
                "issue_date": elementValue[i]["issue_date"].value,
                "expiry_date": elementValue[i]["expiry_date"].value,
                "vehicle_category_code": elementValue[i]["vehicle_category_code"]
              }
            }
            credentialSubjectObject[`${elementIdentifier}`] = arr
          }
          break;

      default:
        credentialSubjectObject[`${elementIdentifier}`] = elementValue
        break;
    }

   

  }
  return credentialSubjectObject

}

function parseValidityInfo(decodedCborData){
  const validityJsonObjectResult = { }

      var issuerAuth = decodedCborData.issuerSigned.issuerAuth

      for(let i = 0; i<issuerAuth.length; i++){
        
        if(issuerAuth[i].value){
          let decodedElement = cbor.decode(issuerAuth[i].value)
          if(decodedElement["validityInfo"]["signed"]) {
            validityJsonObjectResult["signed"] = decodedElement["validityInfo"]["signed"]
            validityJsonObjectResult["validFrom"] = decodedElement["validityInfo"]["validFrom"]
            validityJsonObjectResult["validUntil"] = decodedElement["validityInfo"]["validUntil"]
          }
         
        }
        
      }
      return validityJsonObjectResult
}