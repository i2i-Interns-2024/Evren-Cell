import UIKit

class UsageViewController: UIViewController {

    @IBOutlet weak var packageNameLabel: UILabel!
    @IBOutlet weak var expirationDateLabel1: UILabel!
    @IBOutlet weak var expirationDateLabel2: UILabel!
    @IBOutlet weak var expirationDateLabel3: UILabel!
    @IBOutlet weak var remainingMinutesLabel: UILabel!
    @IBOutlet weak var remainingSmsLabel: UILabel!
    @IBOutlet weak var remainingDataLabel: UILabel!
    
    @IBOutlet weak var minutesRemainingPercentageLabel: UILabel!
    @IBOutlet weak var smsRemainingPercentageLabel: UILabel!
    @IBOutlet weak var dataRemainingPercentageLabel: UILabel!
    
    var msisdn: String?  // msisdn parametresini class içinde bir property olarak tanımladık
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if let msisdn = msisdn {
            fetchPackageDetails(msisdn: msisdn)
            fetchRemainingBalance(msisdn: msisdn)
        }
    }
    
    func fetchPackageDetails(msisdn: String) {
        let packageURL = "http://35.242.205.201/v1/api/packages/getUserPackageByMsisdn?msisdn=\(msisdn)"
        
        fetch(from: packageURL) { data in
            guard let package = data else { return }
            
            let amountMinutes = package["amountMinutes"] as? Int ?? 0
            let amountSms = package["amountSms"] as? Int ?? 0
            let amountData = package["amountData"] as? Int ?? 0
            let packageName = package["packageName"] as? String ?? ""
            
            // Label güncellemeleri ana iş parçacığında yapılmalı
            DispatchQueue.main.async {
                self.packageNameLabel.text = packageName
                self.remainingMinutesLabel.text = "Toplam Dakika: \(amountMinutes)"
                self.remainingSmsLabel.text = "Toplam SMS: \(amountSms)"
                self.remainingDataLabel.text = "Toplam Veri: \(amountData) MB"
                
                // Kalan yüzde hesaplama işlemi
                self.calculateRemainingPercentage(amountMinutes: amountMinutes, amountSms: amountSms, amountData: amountData)
            }
        }
    }
    
    func fetchRemainingBalance(msisdn: String) {
        let balanceURL = "http://35.242.205.201/v1/api/balance/remainingBalance?msisdn=\(msisdn)"
        
        fetch(from: balanceURL) { data in
            guard let balance = data else { return }
            
            let balanceData = balance["balanceData"] as? Int ?? 0
            let balanceSms = balance["balanceSms"] as? Int ?? 0
            let balanceMinutes = balance["balanceMinutes"] as? Int ?? 0
            let sdate = balance["sdate"] as? String ?? ""
            
            // Sdate'den sadece tarihi ayıklama
            let dateOnly = sdate.split(separator: "T").first ?? ""
            
            // Label güncellemeleri ana iş parçacığında yapılmalı
            DispatchQueue.main.async {
                self.expirationDateLabel1.text = " \(dateOnly)"
                self.expirationDateLabel2.text = " \(dateOnly)"
                self.expirationDateLabel3.text = " \(dateOnly)"
                self.remainingMinutesLabel.text = "\(balanceMinutes) Minutes"
                self.remainingSmsLabel.text = "\(balanceSms) Sms"
                self.remainingDataLabel.text = "\(balanceData) MB"
            }
        }
    }
    
    func calculateRemainingPercentage(amountMinutes: Int, amountSms: Int, amountData: Int) {
        guard let msisdn = msisdn else { return }
        
        let balanceURL = "http://35.242.205.201/v1/api/balance/remainingBalance?msisdn=\(msisdn)"
        
        fetch(from: balanceURL) { data in
            guard let balance = data else { return }
            
            let balanceMinutes = balance["balanceMinutes"] as? Int ?? 0
            let balanceSms = balance["balanceSms"] as? Int ?? 0
            let balanceData = balance["balanceData"] as? Int ?? 0
            
            // Kalan yüzdeleri hesapla
            let minutesRemainingPercentage = (amountMinutes > 0) ? (Double(balanceMinutes) / Double(amountMinutes)) * 100 : 0
            let smsRemainingPercentage = (amountSms > 0) ? (Double(balanceSms) / Double(amountSms)) * 100 : 0
            let dataRemainingPercentage = (amountData > 0) ? (Double(balanceData) / Double(amountData)) * 100 : 0
            
            // Yüzde bilgilerini ana iş parçacığında güncelle
            DispatchQueue.main.async {
                self.minutesRemainingPercentageLabel.text = " \(String(format: "%.1f", minutesRemainingPercentage))%"
                self.smsRemainingPercentageLabel.text = " \(String(format: "%.1f", smsRemainingPercentage))%"
                self.dataRemainingPercentageLabel.text = " \(String(format: "%.1f", dataRemainingPercentage))%"
            }
        }
    }
    
    func fetch(from urlString: String, completion: @escaping ([String: Any]?) -> Void) {
        guard let url = URL(string: urlString) else {
            completion(nil)
            return
        }
        
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            guard let data = data, error == nil else {
                completion(nil)
                return
            }
            
            do {
                if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                    completion(json)
                } else {
                    completion(nil)
                }
            } catch {
                completion(nil)
            }
        }
        
        task.resume()
    }
}

