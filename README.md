After checking out the code, follow the below steps according to the platforms,
## Android
- Go to **kotlin** directory
- Run the app module which is the example app which has the library(mdocmocklibrary) included in it.
- On running the app, Sample screen will be displayed and it has two buttons **API call to Issuer** and **Decode and Parse**.
- **API call to Issuer** button will get the base64URL string from the Issuer.(For now the sample base64url is hardcoded).
- **Decode and Parse** will call the method from the library and decodeAndParseMDocData will take the Base64 URL as parameter `CborUtils.decodeAndParseMDocData(Base64URL)`
-  We are using `implementation("co.nstant.in:cbor:0.9")` maven dependency to decode the CBOR data.
- Library decodes the Base64 URL first , then decodes CBOR data and parses the mDoc/mDL VC.
- Once parsing is done, we are constructing json with **credentialSubject** and **validityInfo** as keys which will be returned to the Application and displayed on the Sample screen.

## iOS
 - Go to **swift** directory
 - Open `swift/Example/mdocSpikeLibrary.xcworkspace` in XCode.
 - Run `pod install` command.
 - Run the Example App which has the library(mdocmocklibrary) included in it.
 - On running the app, Sample screen will be displayed and it has one button **Decode and Parse**.
 - **Decode and Parse** will call the method from the library and decodeAndParseMDocData will take the Base64 URL as parameter `CborLibrayUtils().decodeAndParseMDocData(Base64URL)`
- We are using `pod SwiftCBOR` pod to decode the CBOR data.
- Library decodes the Base64 URL first , then decodes CBOR data and parses the mDoc/mDL VC.
- Once parsing is done, we are constructing json with **credentialSubject** and **validityInfo** as keys which will be returned to the Application and displayed on the Sample screen.

## JS
 - Go to **js** directory
 - Run `npm install` command.
 - Run the Sample Node App using the command `node example-app.js` which has the library(`./mdoc-spike-library.js`) imported in it.
 - On running the app, `const resultJson = decodeAndParseCborData(base64url)` will be called.
 - We are using `import cbor from 'cbor` npm library to decode the CBOR data.
- Library decodes the Base64 URL first , then decodes CBOR data and parses the mDoc/mDL VC.
- Once parsing is done, we are constructing json with **credentialSubject** and **validityInfo** as keys which will be returned to the Application and printed on the Console.

For all 3 libraries, Result will be of below json

```
{
    "validityInfo": {
        "validFrom": "2024-01-12T00:10:05Z",
        "validUntil": "2025-01-12T00:10:05Z",
        "signed": "2024-01-12T00:10:05Z"
    },
    "credentialSubject": {
        "issuing_country": "US",
        "given_name": "Inga",
        "family_name": "Silverstone",
        "issue_date": "2024-01-12",
        "driving_privileges": [
            {
                "vehicle_category_code": "A",
                "issue_date": "2023-01-01",
                "expiry_date": "2043-01-01"
            }
        ],
        "birth_date": "1991-11-06",
        "expiry_date": "2025-01-12",
        "document_number": "12345678"
    }
}
```
