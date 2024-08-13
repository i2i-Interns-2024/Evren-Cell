//
//  RemainingBalance.swift
//  sontest
//
//  Created by Cerebrum on 8/13/24.
//

import Foundation

struct RemainingBalance: Codable {
    let msisdn: String
    let balanceData: Int
    let balanceSms: Int
    let balanceMinutes: Int
    let sdate: String
    let edate: String
}
