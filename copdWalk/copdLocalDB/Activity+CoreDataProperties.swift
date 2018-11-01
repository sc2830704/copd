//
//  Activity+CoreDataProperties.swift
//  copdWalk
//
//  Created by yangfourone on 2018/10/25.
//  Copyright © 2018 41. All rights reserved.
//
//

import Foundation
import CoreData


extension Activity {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Activity> {
        return NSFetchRequest<Activity>(entityName: "Activity")
    }

    @NSManaged public var activity_id: String?
    @NSManaged public var activity_sync_flag: Bool
    @NSManaged public var bp: String?
    @NSManaged public var data: String?
    @NSManaged public var distance: Int16
    @NSManaged public var end_time: String?
    @NSManaged public var h_i_time: Int16
    @NSManaged public var start_time: String?
    @NSManaged public var step: Int16

}
