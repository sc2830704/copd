//
//  Education_4.swift
//  copdWalk
//
//  Created by 41 on 2018/9/11.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class Education_4: UIViewController {
    
    @IBOutlet weak var Edu4_Label1: UILabel!
    @IBOutlet weak var Edu4_Label2: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        Edu4_Label1.layer.borderWidth = 2
        Edu4_Label1.layer.borderColor = UIColor(red: 5/255, green: 122/255, blue: 200/255, alpha: 1.0).cgColor
        Edu4_Label1.layer.cornerRadius = 20
        
        Edu4_Label2.layer.borderWidth = 2
        Edu4_Label2.layer.borderColor = UIColor(red: 5/255, green: 122/255, blue: 200/255, alpha: 1.0).cgColor
        Edu4_Label2.layer.cornerRadius = 20
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
