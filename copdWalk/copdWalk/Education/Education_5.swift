//
//  Education_5.swift
//  copdWalk
//
//  Created by 41 on 2018/9/11.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class Education_5: UIViewController {
    
    @IBOutlet weak var Edu5_Label1: UILabel!
    @IBOutlet weak var Edu5_Label2: UILabel!
    @IBOutlet weak var Edu5_Label3: UILabel!
    @IBOutlet weak var Edu5_Label4: UILabel!
    @IBOutlet weak var Edu5_Label5: UILabel!
    @IBOutlet weak var Edu5_Image1: UIImageView!
    @IBOutlet weak var Edu5_Image2: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        Edu5_Label1.layer.borderWidth = 2
        Edu5_Label1.layer.borderColor = UIColor(red: 5/255, green: 122/255, blue: 200/255, alpha: 1.0).cgColor
        Edu5_Label1.layer.cornerRadius = 20
        
        Edu5_Label2.layer.borderWidth = 2
        Edu5_Label2.layer.borderColor = UIColor(red: 5/255, green: 122/255, blue: 200/255, alpha: 1.0).cgColor
        Edu5_Label2.layer.cornerRadius = 20
        
        Edu5_Label3.layer.borderWidth = 2
        Edu5_Label3.layer.borderColor = UIColor(red: 5/255, green: 122/255, blue: 200/255, alpha: 1.0).cgColor
        Edu5_Label3.layer.cornerRadius = 20
        
        Edu5_Label4.layer.borderWidth = 2
        Edu5_Label4.layer.borderColor = UIColor(red: 5/255, green: 122/255, blue: 200/255, alpha: 1.0).cgColor
        Edu5_Label4.layer.cornerRadius = 20
        
        Edu5_Label5.layer.borderWidth = 2
        Edu5_Label5.layer.borderColor = UIColor(red: 5/255, green: 122/255, blue: 200/255, alpha: 1.0).cgColor
        Edu5_Label5.layer.cornerRadius = 20
        
        Edu5_Image1.image = UIImage(named: "Edu5_1.png")
        Edu5_Image2.image = UIImage(named: "Edu5_2.png")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
