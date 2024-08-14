document.getElementById('submitButton').addEventListener('click', function () {
    const msisdn = document.getElementById('phone').value;
    const password = document.getElementById('password').value;

    
    console.log("MSISDN:", msisdn);

    fetch('http://35.242.205.201/v1/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            msisdn: '90'+msisdn,
            password: password,
        }),
    })
    .then(response => response.json())
    .then(data => {
        if (data.statusCodeValue == 200) {
            localStorage.removeItem('msisdn');
            localStorage.setItem('msisdn', '90'+msisdn);
            window.location.href = `/src/main/resources/static/UserInformationPage/UserInformationPage.html`;
        } else {
            console.log(data)
            alert('Login failed: ' + data.message);
        }
    })
    .catch((error) => {
        console.error('Error:', error);
        alert('Bir hata oluştu. Lütfen tekrar deneyiniz.');
    });
});
