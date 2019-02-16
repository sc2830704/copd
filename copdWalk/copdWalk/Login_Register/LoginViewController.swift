//
//  LoginViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/7/23.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit
import CoreData

public var user_account:String?

class LoginViewController: UIViewController {
    
    @IBOutlet weak var account: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var login: UIButton!
    @IBOutlet weak var register: UIButton!
    @IBOutlet weak var forget_password: UIButton!
    
    
    /** Core Data **/
    let app = UIApplication.shared.delegate as! AppDelegate
    var viewContext: NSManagedObjectContext!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        //self.navigationController?.navigationBar.isHidden = true
        //viewContext = app.persistentContainer.viewContext
        
        if UserDefaults.standard.bool(forKey: "isLogin") {
            let MainViewController = self.storyboard?.instantiateViewController(withIdentifier: "MainViewController") as! MainViewController
            self.navigationController?.pushViewController(MainViewController, animated: false)
        }
        
        /** close keyboard when click anywhere **/
        self.hideKeyboardWhenTappedAround()
        
        /** button style setting **/
        login.layer.cornerRadius = 10
        login.layer.borderColor = UIColor.orange.cgColor
        login.layer.borderWidth = 2
        
        register.layer.cornerRadius = 10
        register.layer.borderColor = UIColor.gray.cgColor
        register.layer.borderWidth = 2
        
        forget_password.layer.cornerRadius = 10
        forget_password.layer.borderColor = UIColor.gray.cgColor
        forget_password.layer.borderWidth = 2
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
       
    }
    
    @IBAction func register(_ sender: Any) {
        let vc = storyboard?.instantiateViewController(withIdentifier: "register")
        show(vc!, sender: self)
    }
    
    @IBAction func login(_ sender: Any) {
        if account.text == "qwerty" && password.text == "" {
            user_account = "qwerty"
            //insertLoginData()
            
            UserDefaults.standard.set(true, forKey: "isLogin")
            let MainViewController = self.storyboard?.instantiateViewController(withIdentifier: "MainViewController") as! MainViewController
            self.navigationController?.pushViewController(MainViewController, animated: true)
            /*
            let vc = storyboard?.instantiateViewController(withIdentifier: "tabbar")
            show(vc!, sender: self)
            */
            
        } else {
            //設定為 alert action
            let alertController = UIAlertController(title: "ERROR", message: "帳號或密碼有錯誤", preferredStyle: .actionSheet)
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
    func queryLoginData() -> String {
        do {
            let allUsers = try viewContext.fetch(Login.fetchRequest())
            for user in allUsers as! [Login] {
                user_account = user.account
            }
        } catch {
            print("error: \(error)")
        }
        return user_account ?? "nil"
    }
    func insertLoginData() {
        let CoreData_Login = NSEntityDescription.insertNewObject(forEntityName: "Login", into: viewContext) as! Login
        CoreData_Login.account = user_account
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
