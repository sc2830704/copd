//
//  Evaluate+CoreDataProperties.swift
//  copdWalk
//
//  Created by yangfourone on 2018/10/25.
//  Copyright © 2018 41. All rights reserved.
//
//

import Foundation
import CoreData


extension Evaluate {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Evaluate> {
        return NSFetchRequest<Evaluate>(entityName: "Evaluate")
    }

    @NSManaged public var cat1: Int16
    @NSManaged public var cat2: Int16
    @NSManaged public var cat3: Int16
    @NSManaged public var cat4: Int16
    @NSManaged public var cat5: Int16
    @NSManaged public var cat6: Int16
    @NSManaged public var cat7: Int16
    @NSManaged public var cat8: Int16
    @NSManaged public var mmrc: Int16

}
