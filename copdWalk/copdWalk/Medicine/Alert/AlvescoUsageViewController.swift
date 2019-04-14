//
//  AlvescoUsageViewController.swift
//  copdWalk
//
//  Created by yangfourone on 2019/4/13.
//  Copyright © 2019 41. All rights reserved.
//

import UIKit

class AlvescoUsageViewController: UIViewController {

    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var time: UILabel!
    @IBOutlet weak var per: UILabel!
    @IBOutlet weak var current: UILabel!
    @IBOutlet weak var progress: UIProgressView!
    @IBOutlet weak var cancel: UIButton!
    @IBOutlet weak var eat: UIButton!
    
    var MedicineTakeTime:String = "09:00"
    var MedicinePerTime:Int = 2
    var MedicineCurrent:Int = 50
    var MedicineAll:Int = 200
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        /** button styling **/
        cancel.layer.cornerRadius = 10
        cancel.layer.borderColor = UIColor.orange.cgColor
        cancel.layer.borderWidth = 2
        
        eat.layer.cornerRadius = 10
        eat.layer.borderColor = UIColor.orange.cgColor
        eat.layer.borderWidth = 2
        
        /** progress bar styling **/
        progress.transform = progress.transform.scaledBy(x: 1, y: 10)
        progress.progress = Float(MedicineCurrent)/Float(MedicineAll)
        
        /** image setting **/
        imageView.image = UIImage(named: "Alvesco.png")
        
        /** label stting**/
        time.text = "時間： \(MedicineTakeTime)"
        per.text = "劑量：每次按壓 \(MedicinePerTime) 下"
        current.text = "藥品已用：\(MedicineCurrent) / \(MedicineAll) 下"
    }
    

    func eatNotification() {
        let alertController = UIAlertController(title: "確定服用?", message: "服用後使用量將-1", preferredStyle: .actionSheet)
        let okAction = UIAlertAction(title: "服用", style: .default) {
            (action) in
            self.dismiss (animated: true, completion: nil)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .cancel)
        
        alertController.addAction(okAction)
        alertController.addAction(cancel)
        
        present(alertController, animated: true, completion: nil)
    }

    func cancelNotification() {
        
    }
    
    func whyCancelNotification() {
        
    }
    
    @IBAction func eat(_ sender: Any) {
        eatNotification()
    }
    
    @IBAction func cancel(_ sender: Any) {
        
    }
}
