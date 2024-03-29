//
//  HistoryViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class HistoryViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    var activity_datetime = [""]
    var exercise_time = ["00:20:16","00:25:07","00:24:45","00:20:16","00:25:07","00:24:45","00:25:07","00:25:07","00:25:07","00:25:07",""]
    var h_i_time = [""]
    var step = [""]
    var distance = [""]
    
    let url = URL(string: "http://140.118.122.241/copd/apiv1/activity/getbyuser/qwerty")
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        imageView.image = UIImage(named: "History_1.png")
        
        if let data = try? Data(contentsOf: url!) {
            let new_data = String(decoding: data, as: UTF8.self)
            let data_obj = new_data.data(using: .utf8)

            if let jsonObj = try? JSONSerialization.jsonObject(with: data_obj!, options: .allowFragments) {
                for history in jsonObj as! [[String: AnyObject]] {
                    activity_datetime.insert(history["start_time"] as! String, at: 0)
                    h_i_time.insert(history["h_i_time"] as! String, at: 0)
                    step.insert(history["step"] as! String, at: 0)
                    distance.insert(history["distance"] as! String, at: 0)
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
        return activity_datetime.count
    }
    
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
