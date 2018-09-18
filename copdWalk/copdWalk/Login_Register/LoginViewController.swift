//
//  LoginViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    
    @IBOutlet weak var account: UITextField!
    @IBOutlet weak var password: UITextField!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        /** close keyboard when click anywhere **/
        self.hideKeyboardWhenTappedAround()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func register(_ sender: Any) {
        let vc = storyboard?.instantiateViewController(withIdentifier: "register")
        show(vc!, sender: self)
    }
    
    @IBAction func login(_ sender: Any) {
        if account.text == "", password.text == "" {
            let vc = storyboard?.instantiateViewController(withIdentifier: "tabbar")
            show(vc!, sender: self)
        } else {
            //設定為 alert action
            let alertController = UIAlertController(title: "ERROR", message: "帳號或密碼有錯誤", preferredStyle: .alert)
            let okAction = UIAlertAction(title: "OK", style: .default) {
                (action) in
                self.dismiss (animated: true, completion: nil)
            }
            //增加"OK"按鍵
            alertController.addAction(okAction)
            //顯示提醒
            show(alertController, sender: self)
        }
    }
}
/** Close Keyboard **/
extension UIViewController {
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}
