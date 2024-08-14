document.addEventListener('DOMContentLoaded', () => {
    const msisdn = localStorage.getItem('msisdn');
    console.log(msisdn);

    if (!msisdn) {
        alert('No MSISDN found. Please log in again.');
        window.location.href = 'login.html';
        return;
    }

   
    fetch(`http://35.242.205.201/v1/api/packages/getUserPackageByMsisdn?msisdn=${'90'+msisdn}`)
        .then(response => response.json())
        .then(packageData => {
            const totalData = packageData.amountData;
            const totalMinutes = packageData.amountMinutes;
            const totalSms = packageData.amountSms;
            const packageName =packageData.packageName;
            console.log(packageData)

            document.getElementById('packagename').textContent=packageName;

            
            fetch(`http://35.242.205.201/v1/api/balance/remainingBalance?msisdn=${'90'+msisdn}`)
                .then(response => response.json())
                .then(balanceData => {
                    const remainingData = balanceData.balanceData;
                    const remainingMinutes = balanceData.balanceMinutes;
                    const remainingSms = balanceData.balanceSms;
                    const remainingDays=balanceData.edate;
                    console.log(balanceData)

                    const dateObject=new Date(remainingDays);

                    const day=dateObject.getDate();
                    const month = dateObject.getMonth()+1;
                    const year =dateObject.getFullYear();
                    const formattedDate = `${day}/${month}/${year}`;

                    
                    const dataPercentage = ((remainingData / totalData) * 100).toFixed(1);
                    const minutesPercentage = ((remainingMinutes / totalMinutes) * 100).toFixed(1);
                    const smsPercentage = ((remainingSms / totalSms) * 100).toFixed(1);

                    

                    document.getElementById('data-info').textContent=remainingData+"/"+totalData;
                    document.getElementById('minute-info').textContent=remainingMinutes+"/"+totalMinutes;
                    document.getElementById('sms-info').textContent=remainingSms+"/"+totalSms;
                    document.getElementById('day-info1').textContent=formattedDate;
                    document.getElementById('day-info2').textContent=formattedDate;
                    document.getElementById('day-info3').textContent=formattedDate;

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
