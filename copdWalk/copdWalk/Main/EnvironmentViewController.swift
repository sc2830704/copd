//
//  EnvironmentViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit
import HomeKit
import CoreData
import CoreLocation

class EnvironmentViewController: UIViewController, CLLocationManagerDelegate {
    
    // location
    var LM = CLLocationManager()
    var Lat:CLLocationDegrees?
    var Long:CLLocationDegrees?
    
    // exercise advice
    @IBOutlet weak var UVimageView: UIImageView!
    @IBOutlet weak var PM25imageView: UIImageView!
    @IBOutlet weak var exercise_advice: UILabel!
    @IBOutlet weak var start_exercise: UIButton!
    @IBAction func start_exercise(_ sender: Any) {
    }
    // environment information
    @IBOutlet weak var UserLocation: UILabel!
    @IBOutlet weak var temperature: UILabel!
    @IBOutlet weak var humidity: UILabel!
    @IBOutlet weak var UV: UILabel!
    @IBOutlet weak var pm25: UILabel!
    @IBOutlet weak var update_time: UILabel!
    // variable
    var progressCurrentStep:Int = 1000
    var progressTargetStep:Int?
    var progressScale:Float?
    
    /** Core Data **/
    let app = UIApplication.shared.delegate as! AppDelegate
    var viewContext: NSManagedObjectContext!
    
    var env_information:[String:AnyObject]?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        /** button & label style setting **/
        start_exercise.layer.cornerRadius = 10
        start_exercise.layer.borderColor = UIColor.orange.cgColor
        start_exercise.layer.borderWidth = 2
        
        temperature.layer.cornerRadius = 5
        temperature.layer.borderColor = UIColor.orange.cgColor
        temperature.layer.borderWidth = 2
        
        humidity.layer.cornerRadius = 5
        humidity.layer.borderColor = UIColor.orange.cgColor
        humidity.layer.borderWidth = 2
        
        UV.layer.cornerRadius = 5
        UV.layer.borderColor = UIColor.orange.cgColor
        UV.layer.borderWidth = 2
        
        pm25.layer.cornerRadius = 5
        pm25.layer.borderColor = UIColor.orange.cgColor
        pm25.layer.borderWidth = 2
        
        
        /** ask location request **/
        self.LM.requestAlwaysAuthorization()
        
        if CLLocationManager.locationServicesEnabled() {
            LM.delegate = self
            LM.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
            LM.startUpdatingLocation()
        }
        
        // Core Data
//        viewContext = app.persistentContainer.viewContext
//        deleteUserData_OneByOne()
//        insertUserData()
//        queryUserData()
        
        /** get environment data from server **/
        let url = URL(string: "http://140.118.122.241/copd/apiv1/env/getbyuser/qwerty")
        
        if let data = try? Data(contentsOf: url!) {
            if let jsonObj = try? JSONSerialization.jsonObject(with: data, options: .allowFragments) {
                env_information = (jsonObj as! [[String: AnyObject]])[(jsonObj as! [[String: AnyObject]]).count - 1]
            }
        }
        
        // exercise advice
        UVimageView.image = UIImage(named: "Env_good.png")
        PM25imageView.image = UIImage(named: "Env_bad_pm25.png")
        exercise_advice.text = "運動建議測試"
        // environment information
        temperature.text = "  溫度(°C)：\(env_information?["temperature"] ?? "No Data" as AnyObject)"
        humidity.text = "  濕度(%)：\(env_information?["humidity"] ?? "No Data" as AnyObject)"
        UV.text = "  紫外線：\(env_information?["uv"] ?? "No Data" as AnyObject)"
        pm25.text = "  PM 2.5：\(env_information?["pm25"] ?? "No Data" as AnyObject)"
        update_time.text = "最後更新時間：\(env_information?["datetime"] ?? "No Data" as AnyObject)"
        
        Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) {
            (timer) in
            //self.refresh()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let currentLocation: CLLocationCoordinate2D = manager.location?.coordinate else { return }
        print("locations = \(currentLocation.latitude) \(currentLocation.longitude)")
        Lat = currentLocation.latitude
        Long = currentLocation.longitude
        ToAddress()
    }
    
    func ToAddress() {
        let location = CLLocation(latitude: Lat!, longitude: Long!)
        let geocoder = CLGeocoder()
        
        geocoder.reverseGeocodeLocation(location) { (placemarks, error) in
            guard error == nil, placemarks != nil else {
                return
            }
            
            for placemark in placemarks! {
                self.UserLocation.text = "所在地： \(placemark.administrativeArea!), \(placemark.locality!)"
                print(placemark.administrativeArea!)
                print(placemark.locality!)
            }
        }
    }
}

extension EnvironmentViewController { /** CoreData functions **/
    
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
