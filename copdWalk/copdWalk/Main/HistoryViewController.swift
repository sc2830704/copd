//
//  HistoryViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class HistoryViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    var activity_datetime = ["2018/08/24 19:32","2018/08/30 21:49","2018/09/07 14:22","2018/09/24 19:32","2018/09/30 21:49","2018/10/07 14:22"]
    var exercise_time = ["00:20:16","00:25:07","00:24:45","00:20:16","00:25:07","00:24:45"]
    var h_i_time = ["17","21","17","17","21","17"]
    var step = ["2097","2662","2496","2097","2662","2496"]
    var distance = ["733","931","873","733","931","873"]
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        imageView.image = UIImage(named: "History_1.png")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return activity_datetime.count
    }
    
    /*func tableView(_ tableView: UITableView, titleForFooterInSection section: Int) -> String? {
     var Footer = ["Personal Information","BLE & Wi-Fi Device Setting and Connecting"]
     return Footer[section]
     }
 
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        let Header = "COPD (Chronic Obstructive Pulmonary Disease) 是一種呼吸道長期發炎導致無法呼吸之呼吸道阻塞，使得氣體無法通暢地進出呼吸道的疾病。"
        return Header
    }*/
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Activity_Cell", for: indexPath) as! TableviewCellViewController
        
        cell.selectionStyle = UITableViewCell.SelectionStyle.none
        cell.accessoryType = .none
    
        if let myDatetime = cell.activity_datetime {
            myDatetime.text = "\(activity_datetime[indexPath.row])"
        }
        if let myExerciseTime = cell.exercise_time {
            myExerciseTime.text = "\(exercise_time[indexPath.row])"
        }
        if let myHITime = cell.h_i_time {
            myHITime.text = "\(h_i_time[indexPath.row])"
        }
        if let myStep = cell.step {
            myStep.text = "\(step[indexPath.row])"
        }
        if let myDistance = cell.distance {
            myDistance.text = "\(distance[indexPath.row])"
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 100
    }
    
    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        //var cellToDeSelect:UITableViewCell = tableView.cellForRow(at: indexPath)!
        //cellToDeSelect.contentView.backgroundColor = UIColor.clear
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //var selectCell:UITableViewCell = tableView.cellForRow(at: indexPath)!
        //selectCell.selectionStyle = UITableViewCellSelectionStyle.none
        //selectCell.contentView.backgroundColor = UIColor.clear
        //let vc = storyboard?.instantiateViewController(withIdentifier: "Evaluate_\(indexPath.row + 1)")
        //show(vc!, sender: self)
    }
    
}
