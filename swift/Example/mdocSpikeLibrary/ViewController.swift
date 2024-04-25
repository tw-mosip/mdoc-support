//
//  ViewController.swift
//  mdocSpikeLibrary
//
//  Created by BalachandarG on 04/24/2024.
//  Copyright (c) 2024 BalachandarG. All rights reserved.
//

import UIKit
import mdocSpikeLibrary
import SwiftCBOR

class ViewController: UIViewController {
    let library = CborLibrayUtils()
    
    @IBOutlet weak var jsonResult: UILabel!
    @IBAction func parseMDocCBOR(_ sender: Any) {
        
        let parsedJson = library.decodeAndParseMdoc(base64EncodedString: base64EncodedString)
        print("Parsed json From iOS Library", parsedJson)
        jsonResult.text = "Parsed mDL VC in Json from iOS Library---->"+parsedJson
    }
    
    @IBOutlet weak var parseMDocCBOR: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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

