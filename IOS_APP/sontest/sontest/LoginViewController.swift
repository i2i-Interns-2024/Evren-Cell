import UIKit

class LoginViewController: UIViewController {
    @IBOutlet weak var msisdnTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    @IBAction func loginButtonPressed(_ sender: UIButton) {
        guard let msisdn = msisdnTextField.text, !msisdn.isEmpty,
              let password = passwordTextField.text, !password.isEmpty else {
            showAlert(message: "Lütfen telefon numarası ve şifrenizi girin.")
            return
        }
        
        authenticateUser(msisdn: msisdn, password: password) { [weak self] (isValidUser, error) in
            guard let self = self else { return }
            
            if let error = error {
                DispatchQueue.main.async {
                    self.showAlert(message: "Giriş yapılamadı. Lütfen tekrar deneyin.")
                }
                print("API Hatası: \(error.localizedDescription)")
                return
            }
            
            if isValidUser {
                DispatchQueue.main.async {
                    // Başarılı giriş, UsageViewController'a geçiş yap
                    self.performSegue(withIdentifier: "showUsageViewController", sender: self)
                }
            } else {
                DispatchQueue.main.async {
                    self.showAlert(message: "Telefon numarası veya şifre yanlış.")
                }
            }
        }
    }
    
    func authenticateUser(msisdn: String, password: String, completion: @escaping (Bool, Error?) -> Void) {
        let urlString = "http://35.242.205.201/v1/api/auth/login"
        guard let url = URL(string: urlString) else {
            completion(false, NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "Geçersiz URL"]))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body: [String: Any] = ["msisdn": msisdn, "password": password]
        request.httpBody = try? JSONSerialization.data(withJSONObject: body, options: [])
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(false, error)
                return
            }
            
            guard let data = data else {
                completion(false, NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "Veri bulunamadı"]))
                return
            }
            
            do {
                if let jsonResponse = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                    // Yanıtın "body" kısmında "Login successful" var mı kontrol et
                    if let body = jsonResponse["body"] as? String, body == "Login successful" {
                        completion(true, nil)
                    } else {
                        completion(false, NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "Giriş başarısız"]))
                    }
                } else {
                    completion(false, NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "Geçersiz JSON yanıtı"]))
                }
            } catch {
                completion(false, error)
            }
        }
        task.resume()
    }
    
    func showAlert(message: String) {
        let alert = UIAlertController(title: "Hata", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Tamam", style: .default, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showUsageViewController",
           let usageVC = segue.destination as? UsageViewController {
            usageVC.msisdn = msisdnTextField.text // msisdn'yi UsageViewController'a geçiyoruz
        }
    }
}

