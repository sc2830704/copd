//
//  TableviewCellViewController.swift
//  copdWalk
//
//  Created by 41 on 2018/9/12.
//  Copyright © 2018年 41. All rights reserved.
//

import UIKit

class TableviewCellViewController: UITableViewCell {
    
    @IBOutlet weak var activity_datetime: UILabel!
    @IBOutlet weak var exercise_time: UILabel!
    @IBOutlet weak var h_i_time: UILabel!
    @IBOutlet weak var step: UILabel!
    @IBOutlet weak var distance: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}
