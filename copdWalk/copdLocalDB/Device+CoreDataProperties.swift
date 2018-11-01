//
//  Device+CoreDataProperties.swift
//  copdWalk
//
//  Created by yangfourone on 2018/10/25.
//  Copyright © 2018 41. All rights reserved.
//
//

import Foundation
import CoreData


extension Device {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Device> {
        return NSFetchRequest<Device>(entityName: "Device")
    }

    @NSManaged public var env: String?
    @NSManaged public var spo2: String?
    @NSManaged public var watch: String?

}
