//
//  AlvescoSettingViewController.swift
//  copdWalk
//
//  Created by yangfourone on 2019/4/13.
//  Copyright © 2019 41. All rights reserved.
//

import UIKit

class AlvescoSettingViewController: UIViewController {
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var time: UILabel!
    @IBOutlet weak var per: UILabel!
    @IBOutlet weak var current: UILabel!
    @IBOutlet weak var edit: UIButton!
    
    var MedicineTakeTimeHour:String = "9"
    var MedicineTakeTimeMinute:String = "30"
    var MedicinePerTime:Int = 2
    var MedicineAll:Int = 200
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        /** button styling **/
        edit.layer.cornerRadius = 10
        edit.layer.borderColor = UIColor.orange.cgColor
        edit.layer.borderWidth = 2

        
        /** image setting **/
        imageView.image = UIImage(named: "Alvesco.png")
        
        /** label stting**/
        time.text = "提醒時間： \(MedicineTakeTimeHour)時\(MedicineTakeTimeMinute)分"
        per.text = "劑量：每次按壓 \(MedicinePerTime) 下"
        current.text = "藥品總量：\(MedicineAll) 下"
    }
    
    @IBAction func edit(_ sender: Any) {
        editNotification()
    }
    
    func editNotification() {
        let alertController = UIAlertController(title: "編輯用藥提醒", message: "請做你的資料更新", preferredStyle: .alert)
        let EditAction = UIAlertAction(title: "確定", style: .default) {
            (action) in
            let EditMedicineTakeTimeHour = alertController.textFields![0].text
            let EditMedicineTakeTimeMinute = alertController.textFields![1].text
            let EditMedicinePerTime = alertController.textFields![2].text
            let EditMedicineAll = alertController.textFields![3].text
            
            if (EditMedicineTakeTimeHour == "") && (EditMedicineTakeTimeMinute == "") && (EditMedicinePerTime == "") && (EditMedicineAll == "") {
                // 時間為空
            } else if Int(EditMedicineTakeTimeHour!)! > 23 && Int(EditMedicineTakeTimeHour!)! < 0 && Int(EditMedicineTakeTimeMinute!)! > 59 && Int(EditMedicineTakeTimeMinute!)! < 0 {
                // 時間不合理
            } else {
                self.time.text = "提醒時間： \(EditMedicineTakeTimeHour ?? "00")時\(EditMedicineTakeTimeMinute ?? "00")分"
                self.per.text = "劑量：每次按壓 \(EditMedicinePerTime ?? "1") 下"
                self.current.text = "藥品總量：\(EditMedicineAll ?? "100") 下"
            }
        }
        let Cancel = UIAlertAction(title: "取消", style: .cancel)
        
        alertController.addTextField {
            (textField) in
            textField.placeholder = "提醒時間之小時 (0~23)"
        }
        alertController.addTextField {
            (textField) in
            textField.placeholder = "提醒時間之分鐘 (0~59)"
        }
        alertController.addTextField {
            (textField) in
            textField.placeholder = "每次服用次數"
        }
        alertController.addTextField {
            (textField) in
            textField.placeholder = "藥品總量"
        }
        
        alertController.addAction(EditAction)
        alertController.addAction(Cancel)
        
        present(alertController, animated: true, completion: nil)
    }
}
