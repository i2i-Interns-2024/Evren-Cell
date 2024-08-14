document.addEventListener("DOMContentLoaded", function() {
    const packageSelect = document.getElementById("package");

    fetch("http://35.242.205.201/v1/api/packages/getAllPackages")
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            data.forEach(pkg => {
                const option = document.createElement("option");
                option.value = pkg.packageName; 
                option.textContent = `${pkg.packageName} - ${pkg.amountMinutes} min - ${pkg.price} $ - ${pkg.amountData} MB - ${pkg.amountSms} SMS - ${pkg.period} period`;
                packageSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error("There was a problem with the fetch operation:", error);
        });

    document.getElementById('registerForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const name = document.getElementById('name').value;
        const surname = document.getElementById('surname').value;
        const TCNumber = document.getElementById('nationalid').value;
        const msisdn = document.getElementById('phone').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const packageName = document.getElementById('package').value;
        console.log(name,surname,TCNumber,msisdn,email,packageName);



        fetch('http://35.242.205.201/v1/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: name,
                surname: surname,
                msisdn: '90'+msisdn,
                email: email,
                password:password,
                TCNumber: TCNumber,
                packageName: packageName
            
            }),
        })
        .then(response => response.json())
        .then(data => {
            if (data.statusCodeValue == 200) {
                alert('Kayıt BAŞARILI!!!');
                window.location.href = `/src/main/resources/static/LoginPage/index.html`;
            } else {
                alert('KAYIT BAŞARISIZ: ' + data.message);
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Bir hata oluştu. Lütfen tekrar deneyiniz.');
        });
    });
});
