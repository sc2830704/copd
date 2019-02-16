//
//  mMRC.swift
//  copdWalk
//
//  Created by yangfourone on 2019/1/18.
//  Copyright © 2019 41. All rights reserved.
//

import UIKit

class mMRC: UIViewController, SSRadioButtonControllerDelegate {
    // mMRC
    @IBOutlet weak var mMRC_0: UIButton!
    @IBOutlet weak var mMRC_1: UIButton!
    @IBOutlet weak var mMRC_2: UIButton!
    @IBOutlet weak var mMRC_3: UIButton!
    @IBOutlet weak var mMRC_4: UIButton!
    
    var mMRC: SSRadioButtonsController?
    
    var mMRC_point: UIButton?
    
    @IBOutlet weak var submit: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        /** button style setting **/
        submit.layer.cornerRadius = 10
        submit.layer.borderColor = UIColor.orange.cgColor
        submit.layer.borderWidth = 2
        
        // Do any additional setup after loading the view, typically from a nib.
        mMRC = SSRadioButtonsController(buttons: mMRC_0, mMRC_1, mMRC_2, mMRC_3, mMRC_4)
        mMRC!.delegate = self
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func didSelectButton(selectedButton: UIButton?) {
        mMRC_point = mMRC!.selectedButton()
        
    }
    
    @IBAction func submit(_ sender: Any) {
        if ((mMRC_point?.currentTitle) != nil) {
            print("mMRC： \((mMRC_point?.currentTitle)!) points")
            // upload to server
            
            // back to evaluate view controller
            let vc = storyboard?.instantiateViewController(withIdentifier: "MainViewController")
            show(vc!, sender: self)
        } else {
            print("ERROR: Empty Answer!")
        }
    }
    
}
