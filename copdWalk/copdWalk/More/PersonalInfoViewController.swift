//
//  PersonalInfoViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class PersonalInfoViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    let url = URL(string: "http://140.118.122.241/copd/apiv1/user/getbyid/\(user_account!)")
    var cellTitle = ["帳號","姓名","年齡","性別","身高","體重","BMI","用藥","病史","其他用藥","其他病史"]
    var user_cell:[String]!
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        if let data = try? Data(contentsOf: url!) {
            let new_data = "[\(String(decoding: data, as: UTF8.self))]"
            let data_obj = new_data.data(using: .utf8)
            
            if let jsonObj = try? JSONSerialization.jsonObject(with: data_obj!, options: .allowFragments) {
                for user in jsonObj as! [[String: AnyObject]] {
                    let user_sex = user["sex"] as! String == "1" ? "男" : "女"
                    user_cell = ["\(user["id"] as! String)","\(user["fname"] as! String) \(user["lname"] as! String)","\(user["age"] as! String)","\(user_sex)","\(user["height"] as! String)","\(user["weight"] as! String)","\(user["bmi"] as! String)","\(user["drug"] as! String)","\(user["history"] as! String)","\(user["drug_other"] as! String)","\(user["history_other"] as! String)"]
                    
                    print(user_cell!)
                }
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cellTitle.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "PersonalInfo_Cell", for: indexPath)
        
        // cell setting
        cell.accessoryType = .none
        cell.textLabel?.textColor = UIColor.blue
        
        if let myTitle = cell.textLabel {
            myTitle.text = "\(cellTitle[indexPath.row])"
        }
        
        if let myDetail = cell.detailTextLabel {
            myDetail.text = "\(user_cell[indexPath.row])"
        }
        
        return cell
    }
    
}
