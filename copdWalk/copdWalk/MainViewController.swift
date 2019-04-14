//
//  MainViewController.swift
//  copdWalk
//
//  Created by yangfourone on 2018/11/26.
//  Copyright © 2018 41. All rights reserved.
//

import UIKit

class MainViewController: UIViewController {
    
    @IBOutlet weak var Personal_Info: UIButton!
    @IBOutlet weak var Daily_Step: UIButton!
    @IBOutlet weak var Environment: UIButton!
    @IBOutlet weak var Exercise: UIButton!
    @IBOutlet weak var Symptom_Manage: UIButton!
    @IBOutlet weak var Activity_Record: UIButton!
    @IBOutlet weak var Medicine_Box: UIButton!
    @IBOutlet weak var Health_Education: UIButton!
    @IBOutlet weak var Device_Setting: UIButton!
    @IBOutlet weak var Logout: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print(user_account)
        self.navigationItem.hidesBackButton = true
        // Do any additional setup after loading the view.
        Personal_Info.layer.cornerRadius = 10
        Personal_Info.layer.borderColor = UIColor.orange.cgColor
        Personal_Info.layer.borderWidth = 2
        
        Daily_Step.layer.cornerRadius = 10
        Daily_Step.layer.borderColor = UIColor.orange.cgColor
        Daily_Step.layer.borderWidth = 2
        
        Environment.layer.cornerRadius = 10
        Environment.layer.borderColor = UIColor.orange.cgColor
        Environment.layer.borderWidth = 2
        
        Exercise.layer.cornerRadius = 10
        Exercise.layer.borderColor = UIColor.orange.cgColor
        Exercise.layer.borderWidth = 2
        
        Symptom_Manage.layer.cornerRadius = 10
        Symptom_Manage.layer.borderColor = UIColor.orange.cgColor
        Symptom_Manage.layer.borderWidth = 2
        
        Activity_Record.layer.cornerRadius = 10
        Activity_Record.layer.borderColor = UIColor.orange.cgColor
        Activity_Record.layer.borderWidth = 2
        
        Medicine_Box.layer.cornerRadius = 10
        Medicine_Box.layer.borderColor = UIColor.orange.cgColor
        Medicine_Box.layer.borderWidth = 2
        
        Health_Education.layer.cornerRadius = 10
        Health_Education.layer.borderColor = UIColor.orange.cgColor
        Health_Education.layer.borderWidth = 2
        
        Device_Setting.layer.cornerRadius = 10
        Device_Setting.layer.borderColor = UIColor.orange.cgColor
        Device_Setting.layer.borderWidth = 2
        
        Logout.layer.cornerRadius = 10
        Logout.layer.borderColor = UIColor.gray.cgColor
        Logout.layer.borderWidth = 2
    }
    
    @IBAction func Logout(_ sender: Any) {
        UserDefaults.standard.set(false, forKey: "isLogin")
        self.navigationController?.popToRootViewController(animated: true)
    }
    
    @IBAction func Personal_Info(_ sender: Any) {
        let PersonalInfoViewController = self.storyboard?.instantiateViewController(withIdentifier: "personal_info") as! PersonalInfoViewController
        self.navigationController?.pushViewController(PersonalInfoViewController, animated: true)
    }
    
    @IBAction func Daily_Step(_ sender: Any) {
        let DailyStepViewController = self.storyboard?.instantiateViewController(withIdentifier: "dailystep") as! DailyStepViewController
        self.navigationController?.pushViewController(DailyStepViewController, animated: true)
    }
    
    @IBAction func Environment(_ sender: Any) {
        let EnvironmentViewController = self.storyboard?.instantiateViewController(withIdentifier: "environment") as! EnvironmentViewController
        self.navigationController?.pushViewController(EnvironmentViewController, animated: true)
    }
    
    @IBAction func Exercise(_ sender: Any) {
        let ActivityViewController = self.storyboard?.instantiateViewController(withIdentifier: "activity") as! ActivityViewController
        self.navigationController?.pushViewController(ActivityViewController, animated: true)
    }
    
    @IBAction func Symptom_Manage(_ sender: Any) {
        let SymptomManageViewController = self.storyboard?.instantiateViewController(withIdentifier: "SymptomManage") as! SymptomManageViewController
        self.navigationController?.pushViewController(SymptomManageViewController, animated: true)
    }
    
    @IBAction func Activity_Record(_ sender: Any) {
        let HistoryViewController = self.storyboard?.instantiateViewController(withIdentifier: "history") as! HistoryViewController
        self.navigationController?.pushViewController(HistoryViewController, animated: true)
    }
    
    @IBAction func Medicine_Box(_ sender: Any) {
        let MedicineBoxViewController = self.storyboard?.instantiateViewController(withIdentifier: "medicine") as! MedicineBoxViewController
        self.navigationController?.pushViewController(MedicineBoxViewController, animated: true)
    }
    
    @IBAction func Health_Education(_ sender: Any) {
        let EducationViewController = self.storyboard?.instantiateViewController(withIdentifier: "education") as! EducationViewController
        self.navigationController?.pushViewController(EducationViewController, animated: true)
    }
    
    @IBAction func Device_Setting(_ sender: Any) {
        let BleDeviceViewController = self.storyboard?.instantiateViewController(withIdentifier: "ble_device") as! BleDeviceViewController
        self.navigationController?.pushViewController(BleDeviceViewController, animated: true)
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
