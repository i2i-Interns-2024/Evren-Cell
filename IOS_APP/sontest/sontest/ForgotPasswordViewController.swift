import UIKit

class ForgotPasswordViewController: UIViewController {

    @IBOutlet weak var tcNumberTextField: UITextField!
    @IBOutlet weak var emailTextField: UITextField!

    override func viewDidLoad() {
        super.viewDidLoad()
    }

    @IBAction func forgotPasswordButtonTapped(_ sender: UIButton) {
        // TC kimlik numarası ve email'in girilip girilmediğini kontrol et
        guard let tcNumber = tcNumberTextField.text, !tcNumber.isEmpty,
              let email = emailTextField.text, !email.isEmpty else {
            displayAlert(message: "Lütfen TC kimlik numarası ve email adresini giriniz.")
            return
        }

        // API isteği için gerekli parametreler
        let parameters = [
            "email": email,
            "TCNumber": tcNumber
        ]

        // Şifre sıfırlama isteği gönder
        sendForgotPasswordRequest(parameters: parameters)
    }

    func sendForgotPasswordRequest(parameters: [String: String]) {
        // URL doğrulama
        guard let url = URL(string: "http://35.242.205.201/v1/api/forgetPassword/reset") else {
            displayAlert(message: "Geçersiz URL.")
            return
        }

        // URL isteği yapılandırma
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        // JSON verisini isteğe dönüştürme
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: parameters, options: [])
        } catch {
            displayAlert(message: "İstek oluşturulurken bir hata oluştu.")
            return
        }

        // URL isteğini başlat
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            // Hata kontrolü
            guard error == nil else {
                DispatchQueue.main.async {
                    self.displayAlert(message: "Hata oluştu: \(error!.localizedDescription)")
                }
                return
            }

            // Yanıt ve veri kontrolü
            guard let data = data, let httpResponse = response as? HTTPURLResponse else {
                DispatchQueue.main.async {
                    self.displayAlert(message: "Sunucudan geçerli bir yanıt alınamadı.")
                }
                return
            }

            DispatchQueue.main.async {
                // Yanıt kodunu kontrol et
                if httpResponse.statusCode == 200 {
                    self.displayAlert(message: "Şifrenizi sıfırlama isteği başarılı.")
                } else {
                    self.displayAlert(message: "Şifrenizi sıfırlama isteği başarısız: \(httpResponse.statusCode)")
                }
            }
        }

        task.resume()
    }

    func displayAlert(message: String) {
        // Uyarı mesajını göster
        let alert = UIAlertController(title: "Bilgi", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Tamam", style: .default, handler: nil))
        present(alert, animated: true, completion: nil)
    }
}

