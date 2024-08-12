document.getElementById("forgotPasswordForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const nationalId = document.getElementById("nationalid").value;
    const email = document.getElementById("email").value;

    try {
        const response = await fetch('http://35.242.205.201/v1/api/forgetPassword/reset', { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ TCNumber: nationalId, email: email }),
        });

        const result = await response.json();

        if (response.ok) {
            alert("Şifre sıfırlama başarılı. Lütfen mail kutunuzu kontrol ediniz.");
            window.location.href = '/src/main/resources/static/LoginPage/index.html'; 
        } else {
            alert(result.message || "Böyle bir mail veya National ID bulunamadı.");
        }
    } catch (error) {
        alert("Bir hata oluştu. Lütfen tekrar deneyiniz.");
        console.error("Error:", error);
    }
});
