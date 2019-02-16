//
//  DailyStepViewController.swift
//  copdWalk
//
//  Created by yangfourone on 2018/11/27.
//  Copyright © 2018 41. All rights reserved.
//

import UIKit
import HealthKit

class DailyStepViewController: UIViewController {

    @IBOutlet weak var progressBar: CircularProgressBar!
    @IBOutlet weak var WeeklyTargetStep: UILabel!
    
    var currentStep:Int!
    var targetStep:Int!
    
    let healthStore = HKHealthStore()
    
    func getTodaysSteps(completion: @escaping (Double) -> Void) {
        let stepsQuantityType = HKQuantityType.quantityType(forIdentifier: .stepCount)!
        
        let now = Date()
        let startOfDay = Calendar.current.startOfDay(for: now)
        let predicate = HKQuery.predicateForSamples(withStart: startOfDay, end: now, options: .strictStartDate)
        
        let query = HKStatisticsQuery(quantityType: stepsQuantityType, quantitySamplePredicate: predicate, options: .cumulativeSum) { _, result, _ in
            guard let result = result, let sum = result.sumQuantity() else {
                completion(0.0)
                return
            }
            completion(sum.doubleValue(for: HKUnit.count()))
        }
        
        healthStore.execute(query)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        currentStep = 3851
        targetStep = 5500
        
        // Do any additional setup after loading the view.
        progressBar.lineWidth = 20
        progressBar.labelSize = 30
        progressBar.safePercent = 85
        progressBar.setProgress(to: Double(currentStep)/Double(targetStep), withAnimation: false, CurrentStep: currentStep)
        
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
