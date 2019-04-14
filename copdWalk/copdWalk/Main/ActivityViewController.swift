//
//  ActivityViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class ActivityViewController: UIViewController {
    
    @IBOutlet weak var watchState: UILabel!
    @IBOutlet weak var wristbandState: UILabel!
    @IBOutlet weak var start: UIButton!
    @IBOutlet weak var save: UIButton!
    @IBOutlet weak var state: UILabel!
    @IBOutlet weak var spo2: UILabel!
    @IBOutlet weak var heartRate: UILabel!
    @IBOutlet weak var sbp: UILabel!
    @IBOutlet weak var dbp: UILabel!
    @IBOutlet weak var exerciseTime: UILabel!
    @IBOutlet weak var preTest: UILabel!
    @IBOutlet weak var postTest: UILabel!
    
    var timer: Timer?
    var counter: Int = 0
    var exerciseState: Int = 0
    
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
    
    @IBAction func start(_ sender: Any) {
        if start.currentTitle == "開始" {
            start.setTitle("測量中", for: .normal)
            start.isEnabled = false
            timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { (_) in
                self.counter = self.counter + 1
                let now = Date()
                let formatter = DateFormatter()
                formatter.dateFormat = "HH:mm:ss"
                let time = formatter.string(from: now)
                self.exerciseTime.text = "運動時間：\(time)"
                if self.counter > 30 {
                    self.counter = 0
                    // stop the timer
                    self.timer?.invalidate()
                    // enable start button
                    self.start.isEnabled = true
                    self.start.setTitle("開始", for: .normal)
                }
            }
        }
    }
    
}
