//
//  PersonalInfoViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class PersonalInfoViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    var CellTitle = ["帳號","姓名","年齡","性別","BMI","用藥","病史","其他用藥","其他病史"]
    var CellDetail = ["gg3be0","張嘉航","31","男","34.9","None","None","None","None"] // 未來改成去 server 取資料
    
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return CellTitle.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "PersonalInfo_Cell", for: indexPath)
        
        // cell setting
        cell.accessoryType = .none
        cell.textLabel?.textColor = UIColor.blue
        
        if let myTitle = cell.textLabel {
            myTitle.text = "\(CellTitle[indexPath.row])"
        }
        
        if let myDetail = cell.detailTextLabel {
            myDetail.text = "\(CellDetail[indexPath.row])"
        }
        
        return cell
    }
    
}
