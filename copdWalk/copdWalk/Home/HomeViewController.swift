//
//  HomeViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class HomeViewController: UIViewController {
    // progress view
    @IBOutlet weak var progressView: UIProgressView!
    @IBOutlet weak var CurrentStep: UILabel!
    @IBOutlet weak var TargetStep: UILabel!
    // exercise advice
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var exercise_advice: UILabel!
    @IBAction func start_exercise(_ sender: Any) {
    }
    // environment information
    @IBOutlet weak var temperature: UILabel!
    @IBOutlet weak var humidity: UILabel!
    @IBOutlet weak var UV: UILabel!
    @IBOutlet weak var pm25: UILabel!
    @IBOutlet weak var update_time: UILabel!
    // weekly step
    @IBOutlet weak var weekly_step: UILabel!
    // variable
    var progressCurrentStep:Int?
    var progressTargetStep:Int?
    var progressScale:Float?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        // pregress view
        progressView.transform = progressView.transform.scaledBy(x: 1, y: 10)
        progressCurrentStep = 9532
        progressTargetStep = 7000
        CurrentStep.text = String(progressCurrentStep!)
        TargetStep.text = String(progressTargetStep!)
        progressScale = Float(progressCurrentStep!)/Float(progressTargetStep!)
        print(progressScale)
        print(progressScale!.isLessThanOrEqualTo(0.7))
        if progressScale!.isLessThanOrEqualTo(1.0) {
            if progressScale!.isLessThanOrEqualTo(0.7) {
                progressView.progressTintColor = UIColor.red
            } else {
                progressView.progressTintColor = UIColor.green
            }
        } else {
            progressScale = 1.0
            progressView.progressTintColor = UIColor.green
        }
        progressView.progress = progressScale!
        // exercise advice
        imageView.image = UIImage(named: "home_good.jpg")
        exercise_advice.text = "運動建議測試"
        // environment information
        temperature.text = "溫度(°C)：32"
        humidity.text = "濕度(%)：82"
        UV.text = "紫外線：5"
        pm25.text = "PM 2.5：7"
        update_time.text = "最後更新時間：2018/09/08 17:08:02"
        // weekly step
        weekly_step.text = "步數：3421"
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
