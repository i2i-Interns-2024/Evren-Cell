//
//  PackageDetails.swift
//  sontest
//
//  Created by Cerebrum on 8/13/24.
//

import Foundation

struct PackageDetails: Codable {
    let packageId: Int
    let packageName: String
    let amountMinutes: Int
    let price: Double
    let amountData: Int
    let amountSms: Int
    let period: Int
}
