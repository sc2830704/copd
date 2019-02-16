//
//  Daily+CoreDataProperties.swift
//  copdWalk
//
//  Created by yangfourone on 2018/10/25.
//  Copyright © 2018 41. All rights reserved.
//
//

import Foundation
import CoreData


extension Daily {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Daily> {
        return NSFetchRequest<Daily>(entityName: "Daily")
    }

    @NSManaged public var daily_sync_flag: Bool
    @NSManaged public var date: NSDate?
    @NSManaged public var distance: Int16
    @NSManaged public var h_i_time: Int16
    @NSManaged public var step: Int16

}
