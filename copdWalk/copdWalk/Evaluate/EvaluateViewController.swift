//
//  EvaluateViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class EvaluateViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    var info = ["慢性阻塞性肺病","診斷與評估","患者身體活動原則","藥物治療","疾病症狀","危險因子","危險因子控制"]
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
        return info.count
    }
    
    /*func tableView(_ tableView: UITableView, titleForFooterInSection section: Int) -> String? {
        var Footer = ["Personal Information","BLE & Wi-Fi Device Setting and Connecting"]
        return Footer[section]
    }
    */
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        let Header = "COPD (Chronic Obstructive Pulmonary Disease) 是一種呼吸道長期發炎導致無法呼吸之呼吸道阻塞，使得氣體無法通暢地進出呼吸道的疾病。"
        return Header
    }
 
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Evaluate_Cell", for: indexPath)

        cell.selectionStyle = UITableViewCellSelectionStyle.none
        cell.accessoryType = .disclosureIndicator
    
        if let myLabel = cell.textLabel {
            myLabel.text = "\(info[indexPath.row])"
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        //var cellToDeSelect:UITableViewCell = tableView.cellForRow(at: indexPath)!
        //cellToDeSelect.contentView.backgroundColor = UIColor.clear
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //var selectCell:UITableViewCell = tableView.cellForRow(at: indexPath)!
        //selectCell.selectionStyle = UITableViewCellSelectionStyle.none
        //selectCell.contentView.backgroundColor = UIColor.clear
        let vc = storyboard?.instantiateViewController(withIdentifier: "Evaluate_\(indexPath.row + 1)")
        show(vc!, sender: self)
    }
    
}
