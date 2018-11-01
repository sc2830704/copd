//
//  MoreViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class MoreViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    var info = [
        ["我的資料"],
        ["藍牙裝置"],
        ["系統登出"]
    ]
    var Footer = ["Personal Information","BLE & Wi-Fi Device Setting and Connecting","System Log Out"]
    var Header = ["個人資訊","設定","登出"]
    
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
        return info.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return info[section].count
    }
    
    func tableView(_ tableView: UITableView, titleForFooterInSection section: Int) -> String? {
        return Footer[section]
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return Header[section]
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "More_1stCell", for: indexPath)
        
        cell.selectionStyle = UITableViewCell.SelectionStyle.none
        cell.accessoryType = .disclosureIndicator
        
        if let myLabel = cell.textLabel {
            myLabel.text = "\(info[indexPath.section][indexPath.row])"
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        let cellToDeSelect:UITableViewCell = tableView.cellForRow(at: indexPath)!
        cellToDeSelect.contentView.backgroundColor = UIColor.white
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let selectCell:UITableViewCell = tableView.cellForRow(at: indexPath)!
        selectCell.contentView.backgroundColor = UIColor.white
        if indexPath.section == 0 {
            let vc = storyboard?.instantiateViewController(withIdentifier: "personal_info")
            show(vc!, sender: self)
        } else if indexPath.section == 1 {
            let vc = storyboard?.instantiateViewController(withIdentifier: "ble_device")
            show(vc!, sender: self)
        } else {
            let vc = storyboard?.instantiateViewController(withIdentifier: "login")
            show(vc!, sender: self)
        }
    }
}
