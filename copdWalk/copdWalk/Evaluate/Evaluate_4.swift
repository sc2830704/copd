//
//  Evaluate_4.swift
//  copdWalk
//
//  Created by 41 on 2018/9/11.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class Evaluate_4: UIViewController {
    
    @IBOutlet weak var Evaluate4_1: UIImageView!
    @IBOutlet weak var Evaluate4_2: UIImageView!
    @IBOutlet weak var Evaluate4_3: UIImageView!
    @IBOutlet weak var Evaluate4_4: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        Evaluate4_1.image = UIImage(named: "Evaluate4-1.png")
        Evaluate4_2.image = UIImage(named: "Evaluate4-2.png")
        Evaluate4_3.image = UIImage(named: "Evaluate4-3.png")
        Evaluate4_4.image = UIImage(named: "Evaluate4-4.png")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
