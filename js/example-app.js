

import { decodeAndParseCborData } from "./mdoc-spike-library.js";

const resultJson = decodeAndParseCborData()

console.log("Parsed mDL VC in Json from JS Library---->", resultJson)