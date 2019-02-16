//
//  Pdo+CoreDataProperties.swift
//  copdWalk
//
//  Created by yangfourone on 2018/10/25.
//  Copyright © 2018 41. All rights reserved.
//
//

import Foundation
import CoreData


extension Pdo {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Pdo> {
        return NSFetchRequest<Pdo>(entityName: "Pdo")
    }

    @NSManaged public var date: Int32
    @NSManaged public var step: Int16

}
