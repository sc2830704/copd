//
//  MedicineUsageViewController.swift
//  copdWalk
//
//  Created by yangfourone on 2019/4/12.
//  Copyright © 2019 41. All rights reserved.
//

import UIKit

class MedicineUsageViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    let medicine = ["Alvesco","AnoroEllipta","Atrovent","Berodual","Berotec","Duasma","FlixotideAccuhaler","FlixotideEvohaler","Foster","IncruseEllipta","OnbrezBreezhaler","PulmicortTurbuhaler","RelvarEllipta","Seretide","SpioltoRespimat","Spiriva","StriverdiRespimat","SymbicortTurbuhaler","UltibroBreezhaler","Ventolin"]
    let medicineEnglish = ["Alvesco","AnoroEllipta","Atrovent","Berodual","Berotec","Duasma","Flixotide Accuhaler","Flixotide Evohaler","Foster","Incruse Ellipta","Onbrez Breezhaler","Pulmicort Turbuhaler","Relvar Ellipta","Seretide","Spiolto Respimat","Spiriva","Striverdi Respimat","Symbicort Turbuhaler","Ultibro Breezhaler","Ventolin"]
    let medicineChinese = ["治喘樂","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥","藥"]
    let medicineProgress = ["0.34","0.27","0.56","0.87","0.63","0.34","0.27","0.56","0.87","0.63","0.34","0.27","0.56","0.87","0.63","0.34","0.27","0.56","0.87","0.63","0.93"]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        let Header = "藥品清單"
        return Header
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 200
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return medicine.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MedicineUsageTableViewCell", for: indexPath) as! MedicineUsageTableViewCell
        
        cell.MedicinePhoto.image = UIImage(named: "\(medicine[indexPath.row]).png")
        cell.MedicineEnglishName.text = medicineEnglish[indexPath.row]
        cell.MedicineChineseName.text = medicineChinese[indexPath.row]
        cell.MedicineUsageProgress.progress = Float(medicineProgress[indexPath.row])!
        cell.MedicineUsageProgress.transform = cell.MedicineUsageProgress.transform.scaledBy(x: 1, y: 8)
    
        return cell
    }
}
