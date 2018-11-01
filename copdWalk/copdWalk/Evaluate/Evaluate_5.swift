//
//  Evaluate_5.swift
//  copdWalk
//
//  Created by 41 on 2018/9/11.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class Evaluate_5: UIViewController {
    
    @IBOutlet weak var Evaluate5_1: UIImageView!
    @IBOutlet weak var Evaluate5_2: UIImageView!
    @IBOutlet weak var Evaluate5_3: UIImageView!
    @IBOutlet weak var Evaluate5_4: UIImageView!
    @IBOutlet weak var Evaluate5_5: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        Evaluate5_1.image = UIImage(named: "Evaluate5-1.png")
        Evaluate5_2.image = UIImage(named: "Evaluate5-2.png")
        Evaluate5_3.image = UIImage(named: "Evaluate5-3.png")
        Evaluate5_4.image = UIImage(named: "Evaluate5-4.png")
        Evaluate5_5.image = UIImage(named: "Evaluate5-5.png")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
