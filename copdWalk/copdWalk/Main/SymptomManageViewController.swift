//
//  SymptomManageViewController.swift
//  copdWalk
//
//  Created by yangfourone on 2018/11/27.
//  Copyright © 2018 41. All rights reserved.
//

import UIKit

class SymptomManageViewController: UIViewController {

    @IBOutlet weak var fev1: UIButton!
    @IBOutlet weak var mMRC: UIButton!
    @IBOutlet weak var Cat: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        /** button style setting **/
        fev1.layer.cornerRadius = 10
        fev1.layer.borderColor = UIColor.orange.cgColor
        fev1.layer.borderWidth = 2
        
        mMRC.layer.cornerRadius = 10
        mMRC.layer.borderColor = UIColor.orange.cgColor
        mMRC.layer.borderWidth = 2
        
        Cat.layer.cornerRadius = 10
        Cat.layer.borderColor = UIColor.orange.cgColor
        Cat.layer.borderWidth = 2
    }
    
    
    @IBAction func mMRC(_ sender: Any) {
        // go to mMRC view controller
        let vc = storyboard?.instantiateViewController(withIdentifier: "mMRC")
        show(vc!, sender: self)
    }
    
    @IBAction func Cat(_ sender: Any) {
        // go to cat view controller
        let vc = storyboard?.instantiateViewController(withIdentifier: "Cat")
        show(vc!, sender: self)
    }
}
