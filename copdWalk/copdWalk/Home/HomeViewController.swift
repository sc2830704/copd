//
//  HomeViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit
import HomeKit
import CoreData

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
    var progressCurrentStep:Int = 1000
    var progressTargetStep:Int?
    var progressScale:Float?
    
    /** Core Data **/
    let app = UIApplication.shared.delegate as! AppDelegate
    var viewContext: NSManagedObjectContext!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        //Core Data
        viewContext = app.persistentContainer.viewContext
        deleteUserData_OneByOne()
        insertUserData()
        queryUserData()
        // pregress view
        progressView.transform = progressView.transform.scaledBy(x: 1, y: 10)
        //progressCurrentStep += 1
        progressTargetStep = 7000
        CurrentStep.text = String(progressCurrentStep)
        TargetStep.text = String(progressTargetStep!)
        progressScale = Float(progressCurrentStep)/Float(progressTargetStep!)
        
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
        
        Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) {
            (timer) in
            self.refresh()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func refresh() {
        progressCurrentStep += 1
        CurrentStep.text = String(progressCurrentStep)
    }
    
    

}

extension HomeViewController { /** CoreData functions **/
    
    /** Insert Data **/
    func insertUserData() {
        //        let url = URL(string: "http://140.118.122.241/copd/apiv1/user/getbyid/\(user_account!)")
        //        if let data = try? Data(contentsOf: url!) {
        //            let new_data = "[\(String(decoding: data, as: UTF8.self))]"
        //            let data_obj = new_data.data(using: .utf8)
        //
        //            if let jsonObj = try? JSONSerialization.jsonObject(with: data_obj!, options: .allowFragments) {
        //                for user in jsonObj as! [[String: AnyObject]] {
        //                    let user_sex = user["sex"] as! String == "1" ? "男" : "女"
        //
        //                    let user_cell = ["\(user["id"] as! String)","\(user["fname"] as! String) \(user["lname"] as! String)","\(user["age"] as! String)","\(user_sex)","\(user["height"] as! String)","\(user["weight"] as! String)","\(user["bmi"] as! String)","\(user["drug"] as! String)","\(user["history"] as! String)","\(user["drug_other"] as! String)","\(user["history_other"] as! String)"]
        //
        //                    print(user_cell)
        //
        //                }
        //            }
        //        }
        let CoreData_UserData = NSEntityDescription.insertNewObject(forEntityName: "UserData", into: viewContext) as! UserData
        CoreData_UserData.account_id = "qwerty"
        CoreData_UserData.password = "as"
        CoreData_UserData.name = "楊 士逸"
        CoreData_UserData.age = 24
        CoreData_UserData.sex = "男"
        CoreData_UserData.height = 184.5
        CoreData_UserData.weight = 68.3
        CoreData_UserData.bmi = 18.9
        CoreData_UserData.drug = "None"
        CoreData_UserData.history = "None"
        CoreData_UserData.drug_other = "None"
        CoreData_UserData.history_other = "None"
        app.saveContext()
    }
    
    /** Query Data **/
    
    func queryUserData() {
        do {
            let allUsers = try viewContext.fetch(UserData.fetchRequest())
            for user in allUsers as! [UserData] {
                print("\(user.account_id!),\(user.name!),\(user.age),\(user.sex!),\(user.height),\(user.weight),\(user.bmi),\(user.drug!),\(user.history!),\(user.drug_other!),\(user.history_other!)")
            }
        } catch {
            print("error: \(error)")
        }
    }
    
    /** Delete Data **/
    
    func deleteUserData_OneByOne() {
        do {
            let allUsers = try viewContext.fetch(UserData.fetchRequest())
            for user in allUsers as! [UserData] {
                viewContext.delete(user)
            }
            app.saveContext()
        } catch {
            print("error: \(error)")
        }
    }
    
    func deleteUserData_Batch() {
        let batch = NSBatchDeleteRequest(fetchRequest: UserData.fetchRequest())
        do {
            try app.persistentContainer.persistentStoreCoordinator.execute(batch, with: viewContext)
        } catch {
            print("error: \(error)")
        }
    }
}
