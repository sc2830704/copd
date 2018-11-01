//
//  UserData+CoreDataProperties.swift
//  copdWalk
//
//  Created by yangfourone on 2018/10/25.
//  Copyright © 2018 41. All rights reserved.
//
//

import Foundation
import CoreData


extension UserData {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<UserData> {
        return NSFetchRequest<UserData>(entityName: "UserData")
    }

    @NSManaged public var account_id: String?
    @NSManaged public var age: Int16
    @NSManaged public var bmi: Float
    @NSManaged public var drug: String?
    @NSManaged public var drug_other: String?
    @NSManaged public var height: Float
    @NSManaged public var history: String?
    @NSManaged public var history_other: String?
    @NSManaged public var name: String?
    @NSManaged public var password: String?
    @NSManaged public var sex: String?
    @NSManaged public var weight: Float

}
