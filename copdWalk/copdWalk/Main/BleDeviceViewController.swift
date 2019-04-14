//
//  BleDeviceViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class BleDeviceViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    var DevicePhoto = ["Watch.png","Box.png","Wristband.png"]
    var DeviceName = ["智慧手錶 ZoeS2","環境盒子 Env_Box","血氧手環 SPO2"]
    var DeviceUUID = ["----------","----------","68:8E:00:12:A2:01"]
    var DeviceState = ["未連接","未連接","已連接"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 190
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return DevicePhoto.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DeviceTableViewCell", for: indexPath) as! DeviceTableViewCell
        
        cell.DevicePhoto.image = UIImage(named: "\(DevicePhoto[indexPath.row])")
        cell.DeviceName.text = DeviceName[indexPath.row]
        cell.DeviceUUID.text = DeviceUUID[indexPath.row]
        cell.DeviceState.text = DeviceState[indexPath.row]
        if cell.DeviceState.text == "未連接" {
            cell.DeviceState.textColor = UIColor.red
        } else {
            cell.DeviceState.textColor = UIColor.green
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        // click event
        if DeviceState[indexPath.row] == "未連接" {
            connectAlert()
        } else {
            disconnectAlert()
        }
    }
    
    func connectAlert() {
        let alertController = UIAlertController(title: "搜尋藍牙裝置?", message: "請確認您的藍芽已經開啟", preferredStyle: .alert)
        let scanAction = UIAlertAction(title: "搜尋", style: .default) {
            (action) in
            // connect
        }
        let cancel = UIAlertAction(title: "取消", style: .cancel)
        
        alertController.addAction(scanAction)
        alertController.addAction(cancel)
        
        present(alertController, animated: true, completion: nil)
    }
    
    func disconnectAlert() {
        let alertController = UIAlertController(title: "中斷藍牙裝置連接?", message: "確認要中斷藍牙裝置連接請按斷線", preferredStyle: .alert)
        let disconnectAction = UIAlertAction(title: "斷線", style: .default) {
            (action) in
            // disconnect
        }
        let cancel = UIAlertAction(title: "取消", style: .cancel)
        
        alertController.addAction(disconnectAction)
        alertController.addAction(cancel)
        
        present(alertController, animated: true, completion: nil)
    }
}
