//
//  Evaluate_1.swift
//  copdWalk
//
//  Created by 41 on 2018/9/11.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class Evaluate_1: UIViewController {
    
    @IBOutlet weak var Evaluate1_1: UIImageView!
    @IBOutlet weak var Evaluate1_2: UIImageView!
    @IBOutlet weak var Evaluate1_3: UIImageView!
    @IBOutlet weak var Evaluate1_4: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        Evaluate1_1.image = UIImage(named: "Evaluate1-1.png")
        Evaluate1_2.image = UIImage(named: "Evaluate1-2.png")
        Evaluate1_3.image = UIImage(named: "Evaluate1-3.png")
        Evaluate1_4.image = UIImage(named: "Evaluate1-4.png")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
