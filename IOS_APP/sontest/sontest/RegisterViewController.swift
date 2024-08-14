import UIKit

class RegisterViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    @IBOutlet weak var nameTextField: UITextField!
    @IBOutlet weak var surnameTextField: UITextField!
    @IBOutlet weak var phoneNumberTextField: UITextField! // msisdn
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var TCNumberTextField: UITextField!
    @IBOutlet weak var packagePicker: UIPickerView!
    
    var packages: [String] = []
    var packageIds: [Int] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        packagePicker.delegate = self
        packagePicker.dataSource = self
        
        fetchPackages()
    }
    
    func fetchPackages() {
        guard let url = URL(string: "http://35.242.205.201/v1/api/packages/getAllPackages") else {
            print("Invalid URL.")
            return
        }
        
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                print("Error fetching packages: \(error)")
                return
            }
            
            guard let data = data else {
                print("No data received.")
                return
            }
            
            do {
                if let jsonResponse = try JSONSerialization.jsonObject(with: data, options: []) as? [[String: Any]] {
                    self.packages.removeAll()
                    self.packageIds.removeAll()
                    
                    for package in jsonResponse {
                        if let packageName = package["packageName"] as? String,
                           let packageId = package["packageId"] as? Int {
                            self.packages.append(packageName)
                            self.packageIds.append(packageId)
                        }
                    }
                    
                    DispatchQueue.main.async {
                        self.packagePicker.reloadAllComponents()
                    }
                }
            } catch let jsonError {
                print("JSON error: \(jsonError)")
            }
        }
        task.resume()
    }
    
    // UIPickerView DataSource
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return packages.count
    }
    
    // UIPickerView Delegate
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return packages[row]
    }
    
    @IBAction func registerButtonPressed(_ sender: UIButton) {
        guard
            let name = nameTextField.text, !name.isEmpty,
            let surname = surnameTextField.text, !surname.isEmpty,
            let phoneNumber = phoneNumberTextField.text, !phoneNumber.isEmpty,
            let email = emailTextField.text, !email.isEmpty,
            let password = passwordTextField.text, !password.isEmpty,
            let TCNumber = TCNumberTextField.text, !TCNumber.isEmpty
        else {
            showAlert(message: "Lütfen tüm bilgileri doldurun.")
            return
        }
        
        let selectedRow = packagePicker.selectedRow(inComponent: 0)
        guard selectedRow >= 0 else {
            showAlert(message: "Lütfen bir paket seçin.")
            return
        }
        
        let selectedPackage = packages[selectedRow]
        
        let parameters: [String: Any] = [
            "name": name,
            "surname": surname,
            "msisdn": phoneNumber,
            "email": email,
            "password": password,
            "TCNumber": TCNumber,
            "packageName": selectedPackage
        ]
        
        registerUser(with: parameters)
    }
    
    func registerUser(with parameters: [String: Any]) {
        guard let url = URL(string: "http://35.242.205.201/v1/api/auth/register") else {
            print("Invalid URL.")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: parameters, options: [])
        } catch {
            print("Error: Unable to encode parameters to JSON.")
            return
        }
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error during registration: \(error)")
                DispatchQueue.main.async {
                    self.showAlert(message: "Kayıt sırasında bir hata oluştu.")
                }
                return
            }
            
            if let httpResponse = response as? HTTPURLResponse {
                print("HTTP Status Code: \(httpResponse.statusCode)")
            }
            
            guard let data = data else {
                DispatchQueue.main.async {
                    self.showAlert(message: "Sunucudan veri alınamadı.")
                }
                return
            }
            
            do {
                if let jsonResponse = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                    print("Response JSON: \(jsonResponse)")
                }
            } catch {
                print("JSON parsing error: \(error)")
            }
            
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 {
                    self.showAlert(message: "Kayıt başarılı!")
                } else {
                    self.showAlert(message: "Kayıt başarısız.")
                }
            }
        }
        
        task.resume()
    }
    
    func showAlert(message: String) {
        let alert = UIAlertController(title: "Bilgi", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Tamam", style: .default, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
}

