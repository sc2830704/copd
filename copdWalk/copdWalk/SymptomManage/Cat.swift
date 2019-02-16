//
//  Cat.swift
//  copdWalk
//
//  Created by yangfourone on 2018/10/28.
//  Copyright © 2018 41. All rights reserved.
//

import UIKit

class Cat: UIViewController, SSRadioButtonControllerDelegate {
    // cat1
    @IBOutlet weak var cat1_0: UIButton!
    @IBOutlet weak var cat1_1: UIButton!
    @IBOutlet weak var cat1_2: UIButton!
    @IBOutlet weak var cat1_3: UIButton!
    @IBOutlet weak var cat1_4: UIButton!
    @IBOutlet weak var cat1_5: UIButton!
    // cat2
    @IBOutlet weak var cat2_0: UIButton!
    @IBOutlet weak var cat2_1: UIButton!
    @IBOutlet weak var cat2_2: UIButton!
    @IBOutlet weak var cat2_3: UIButton!
    @IBOutlet weak var cat2_4: UIButton!
    @IBOutlet weak var cat2_5: UIButton!
    // cat3
    @IBOutlet weak var cat3_0: UIButton!
    @IBOutlet weak var cat3_1: UIButton!
    @IBOutlet weak var cat3_2: UIButton!
    @IBOutlet weak var cat3_3: UIButton!
    @IBOutlet weak var cat3_4: UIButton!
    @IBOutlet weak var cat3_5: UIButton!
    // cat4
    @IBOutlet weak var cat4_0: UIButton!
    @IBOutlet weak var cat4_1: UIButton!
    @IBOutlet weak var cat4_2: UIButton!
    @IBOutlet weak var cat4_3: UIButton!
    @IBOutlet weak var cat4_4: UIButton!
    @IBOutlet weak var cat4_5: UIButton!
    // cat5
    @IBOutlet weak var cat5_0: UIButton!
    @IBOutlet weak var cat5_1: UIButton!
    @IBOutlet weak var cat5_2: UIButton!
    @IBOutlet weak var cat5_3: UIButton!
    @IBOutlet weak var cat5_4: UIButton!
    @IBOutlet weak var cat5_5: UIButton!
    // cat6
    @IBOutlet weak var cat6_0: UIButton!
    @IBOutlet weak var cat6_1: UIButton!
    @IBOutlet weak var cat6_2: UIButton!
    @IBOutlet weak var cat6_3: UIButton!
    @IBOutlet weak var cat6_4: UIButton!
    @IBOutlet weak var cat6_5: UIButton!
    // cat7
    @IBOutlet weak var cat7_0: UIButton!
    @IBOutlet weak var cat7_1: UIButton!
    @IBOutlet weak var cat7_2: UIButton!
    @IBOutlet weak var cat7_3: UIButton!
    @IBOutlet weak var cat7_4: UIButton!
    @IBOutlet weak var cat7_5: UIButton!
    // cat8
    @IBOutlet weak var cat8_0: UIButton!
    @IBOutlet weak var cat8_1: UIButton!
    @IBOutlet weak var cat8_2: UIButton!
    @IBOutlet weak var cat8_3: UIButton!
    @IBOutlet weak var cat8_4: UIButton!
    @IBOutlet weak var cat8_5: UIButton!
    
    var cat1: SSRadioButtonsController?
    var cat2: SSRadioButtonsController?
    var cat3: SSRadioButtonsController?
    var cat4: SSRadioButtonsController?
    var cat5: SSRadioButtonsController?
    var cat6: SSRadioButtonsController?
    var cat7: SSRadioButtonsController?
    var cat8: SSRadioButtonsController?
    
    var cat1_point: UIButton?
    var cat2_point: UIButton?
    var cat3_point: UIButton?
    var cat4_point: UIButton?
    var cat5_point: UIButton?
    var cat6_point: UIButton?
    var cat7_point: UIButton?
    var cat8_point: UIButton?
    
    @IBOutlet weak var submit: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        /** button style setting **/
        submit.layer.cornerRadius = 10
        submit.layer.borderColor = UIColor.orange.cgColor
        submit.layer.borderWidth = 2
        
        // Do any additional setup after loading the view, typically from a nib.
        
        cat1 = SSRadioButtonsController(buttons: cat1_0, cat1_1, cat1_2, cat1_3, cat1_4, cat1_5)
        cat1!.delegate = self
        
        cat2 = SSRadioButtonsController(buttons: cat2_0, cat2_1, cat2_2, cat2_3, cat2_4, cat2_5)
        cat2!.delegate = self
        
        cat3 = SSRadioButtonsController(buttons: cat3_0, cat3_1, cat3_2, cat3_3, cat3_4, cat3_5)
        cat3!.delegate = self
        
        cat4 = SSRadioButtonsController(buttons: cat4_0, cat4_1, cat4_2, cat4_3, cat4_4, cat4_5)
        cat4!.delegate = self
        
        cat5 = SSRadioButtonsController(buttons: cat5_0, cat5_1, cat5_2, cat5_3, cat5_4, cat5_5)
        cat5!.delegate = self
        
        cat6 = SSRadioButtonsController(buttons: cat6_0, cat6_1, cat6_2, cat6_3, cat6_4, cat6_5)
        cat6!.delegate = self
        
        cat7 = SSRadioButtonsController(buttons: cat7_0, cat7_1, cat7_2, cat7_3, cat7_4, cat7_5)
        cat7!.delegate = self
        
        cat8 = SSRadioButtonsController(buttons: cat8_0, cat8_1, cat8_2, cat8_3, cat8_4, cat8_5)
        cat8!.delegate = self
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func didSelectButton(selectedButton: UIButton?) {
        cat1_point = cat1!.selectedButton()
        cat2_point = cat2!.selectedButton()
        cat3_point = cat3!.selectedButton()
        cat4_point = cat4!.selectedButton()
        cat5_point = cat5!.selectedButton()
        cat6_point = cat6!.selectedButton()
        cat7_point = cat7!.selectedButton()
        cat8_point = cat8!.selectedButton()
        
    }
    
    @IBAction func submit(_ sender: Any) {
        if ((cat1_point?.currentTitle) != nil) && ((cat1_point?.currentTitle) != nil) && ((cat1_point?.currentTitle) != nil) && ((cat1_point?.currentTitle) != nil) && ((cat1_point?.currentTitle) != nil) && ((cat1_point?.currentTitle) != nil) && ((cat1_point?.currentTitle) != nil) && ((cat1_point?.currentTitle) != nil) {
            print("cat1： \((cat1_point?.currentTitle)!) points")
            print("cat2： \((cat2_point?.currentTitle)!) points")
            print("cat3： \((cat3_point?.currentTitle)!) points")
            print("cat4： \((cat4_point?.currentTitle)!) points")
            print("cat5： \((cat5_point?.currentTitle)!) points")
            print("cat6： \((cat6_point?.currentTitle)!) points")
            print("cat7： \((cat7_point?.currentTitle)!) points")
            print("cat8： \((cat8_point?.currentTitle)!) points")
            // upload to server
            // back to evaluate view controller
            let vc = storyboard?.instantiateViewController(withIdentifier: "MainViewController")
            show(vc!, sender: self)
        } else {
            print("ERROR: Empty Answer!")
        }
    }
    
}
