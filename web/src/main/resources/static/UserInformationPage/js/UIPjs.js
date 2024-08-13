document.addEventListener('DOMContentLoaded', () => {
    const msisdn = localStorage.getItem('msisdn');
    console.log(msisdn);

    if (!msisdn) {
        alert('No MSISDN found. Please log in again.');
        window.location.href = 'login.html';
        return;
    }

   
    fetch(`http://35.242.205.201/v1/api/packages/getUserPackageByMsisdn?msisdn=${msisdn}`)
        .then(response => response.json())
        .then(packageData => {
            const totalData = packageData.amountData;
            const totalMinutes = packageData.amountMinutes;
            const totalSms = packageData.amountSms;
            console.log(packageData)

            
            fetch(`http://35.242.205.201/v1/api/balance/remainingBalance?msisdn=${msisdn}`)
                .then(response => response.json())
                .then(balanceData => {
                    const remainingData = balanceData.balanceData;
                    const remainingMinutes = balanceData.balanceMinutes;
                    const remainingSms = balanceData.balanceSms;
                    console.log(balanceData)

                    
                    const dataPercentage = ((remainingData / totalData) * 100).toFixed(1);
                    const minutesPercentage = ((remainingMinutes / totalMinutes) * 100).toFixed(1);
                    const smsPercentage = ((remainingSms / totalSms) * 100).toFixed(1);

                    
                    updateCircle('#circle1', dataPercentage);
                    updateCircle('#circle2', minutesPercentage);
                    updateCircle('#circle3', smsPercentage);
                })
                .catch(error => console.error('Data fetch error:', error));
        })
        .catch(error => console.error('Package details fetch error:', error));
});

function updateCircle(circleId, percentage) {
    const circleElement = document.querySelector(circleId).querySelector('circle');
    const textElement = document.querySelector(circleId).querySelector('text');

    const circumference = 2 * Math.PI * circleElement.r.baseVal.value;
    const offset = circumference - (percentage / 100) * circumference;

    circleElement.style.strokeDasharray = circumference;
    circleElement.style.strokeDashoffset = offset;

    textElement.textContent = `${percentage}%`;
}
