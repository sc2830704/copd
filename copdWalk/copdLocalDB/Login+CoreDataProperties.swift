//
//  Login+CoreDataProperties.swift
//  copdWalk
//
//  Created by yangfourone on 2018/11/6.
//  Copyright © 2018 41. All rights reserved.
//
//

import Foundation
import CoreData


extension Login {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Login> {
        return NSFetchRequest<Login>(entityName: "Login")
    }

    @NSManaged public var account: String?

}
