//
//  Evaluate_6.swift
//  copdWalk
//
//  Created by 41 on 2018/9/11.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class Evaluate_6: UIViewController {
    
    @IBOutlet weak var Evaluate6_1: UIImageView!
    @IBOutlet weak var Evaluate6_2: UIImageView!
    @IBOutlet weak var Evaluate6_3: UIImageView!
    @IBOutlet weak var Evaluate6_4: UIImageView!
    @IBOutlet weak var Evaluate6_5: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        Evaluate6_1.image = UIImage(named: "Evaluate6-1.png")
        Evaluate6_2.image = UIImage(named: "Evaluate6-2.png")
        Evaluate6_3.image = UIImage(named: "Evaluate6-3.png")
        Evaluate6_4.image = UIImage(named: "Evaluate6-4.png")
        Evaluate6_5.image = UIImage(named: "Evaluate6-5.png")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
