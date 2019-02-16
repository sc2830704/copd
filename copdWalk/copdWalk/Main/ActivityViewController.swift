//
//  ActivityViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class ActivityViewController: UIViewController {
    
    @IBOutlet weak var start: UIButton!
    @IBOutlet weak var save: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        /** button style setting **/
        start.layer.cornerRadius = 10
        start.layer.borderColor = UIColor.orange.cgColor
        start.layer.borderWidth = 2
        
        save.layer.cornerRadius = 10
        save.layer.borderColor = UIColor.orange.cgColor
        save.layer.borderWidth = 2
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
