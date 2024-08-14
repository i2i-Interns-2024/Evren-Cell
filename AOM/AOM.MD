# AOM (Account Order Management) Service

> AOM is a module that is responsible for managing the account orders.
> It is a microservice that is part of the larger system that is responsible for managing the orders of the accounts.
> It is a RESTful service that is built using Spring Boot and Java.
---

## Features
- Create a customer account
- Create balance for a customer account
- Fetch customer account details
- Fetch customer account balance
- Reset customer password
---
## Interactions
- With UI Applications: 
    - AOM accepts requests from the 5 different UI applications including the web, android, ios, desktop and sms applications.
    - These requests include creating a customer account, creating a balance for a customer account, fetching customer account details, 
    - fetching customer account balance and resetting customer password.
- With VoltDB:
    - AOM interacts with the VoltDB to receive the customer account details, usage details, and remaining balance details.
- With Oracle DB:
    - AOM interacts with the Oracle DB to create a customer account, create a balance for a customer account,
   login a customer account, fetch customer account details and reset customer password.
- With Hazelcast:
    - AOM interacts with the Hazelcast to send the customer MSISDN number to be processes by TGF during the account creation process.
- With Notification Service:
    - AOM interacts with the Notification Service to send the new customer password when customer forgets the password.
---
## Technologies
- Java
- Spring Boot
- OpenAPI
- Docker
- Maven
- Oracle DB
- VoltDB

---
## High Level Design
![AOM Service Diagram](docs/aom_diagram.png)

---
## Endpoints
<table style="width:100%">
  <tr>
    <th>Method</th>
    <th>Endpoint</th>
    <th>Description</th>
    <th>Request Details</th>
  </tr>

  <tr>
      <td>POST</td>
      <td>/v1/api/forgetPassword/reset</td>
      <td>Reset the password of a customer account.</td>
      <td><a href="#reset">Request Details</a></td>
  </tr>

  <tr>
      <td>POST</td>
      <td>/v1/api/auth/register</td>
      <td>Create a customer account.</td>
        <td><a href="#register">Request Details</a></td>
  </tr>

  <tr>
      <td>POST</td>
      <td>/v1/api/auth/login</td>
      <td>Login a customer account.</td>
        <td><a href="#login">Request Details</a></td>
  </tr>

  <tr>
      <td>GET</td>
      <td>/v1/api/packages/getUserPackageByMsisdn</td>
      <td>Fetch customer account details by customer's msisdn.</td>
      <td><a href="#getCustomerByMsisdn">Request Details</a></td>
  </tr>

  <tr>
      <td>GET</td>
      <td>/v1/api/packages/getPackageDetails</td>
      <td>Fetch package details by package name.</td>
        <td><a href="#getPackageDetails">Request Details</a></td>
  </tr>

  <tr>
      <td>GET</td>
      <td>/v1/api/packages/getAllPackages</td>
      <td>Fetch all packages.</td>
        <td><a href="#getAllPackages">Request Details</a></td>
  </tr>

  <tr>
      <td>GET</td>
      <td>/v1/api/customer/getCustomerByMsisdn</td>
      <td>Fetch customer account details by customer's msisdn.</td>
        <td><a href="#getCustomerByMsisdn">Request Details</a></td>
  </tr>

  <tr>
      <td>GET</td>
      <td>/v1/api/customer/getAllCustomers</td>
      <td>Fetch all customer accounts.</td>
        <td><a href="#getAllCustomers">Request Details</a></td>
  </tr>

  <tr>
      <td>GET</td>
      <td>/v1/api/balance/remainingBalance</td>
      <td>Fetch remaining balance of a customer account.</td>
        <td><a href="#remainingBalance">Request Details</a></td>
  </tr>

</table>

---

## Valid Request Examples

#### <a id="reset"> Reset Password For User
```
  v1/api/forgetPassword/reset
{
    "email": "string",
    "TCNumber": "stringstrin"
}
```

#### <a id="register"> Register For User
```
  v1/api/auth/register
{
    "name": "string",
    "surname": "string",
    "msisdn": "stringstring",
    "email": "string",
    "password": "string",
    "TCNumber": "stringstrin",
    "packageName": "string"
}
```

#### <a id="login"> Login For User
```
  v1/api/auth/login
{
    "msisdn": "stringstring",
    "password": "string"
}
```

#### <a id="getCustomerByMsisdn"> Get Customer By Msisdn
```
  v1/api/packages/getUserPackageByMsisdn?msisdn={string}
```

#### <a id="getPackageDetails"> Get Package Details
```
  v1/api/packages/getPackageDetails?packageName={string}
```

#### <a id="getAllPackages"> Get All Packages
```
  v1/api/packages/getAllPackages
```

#### <a id="getCustomerByMsisdn"> Get Customer By Msisdn
```
  v1/api/customer/getCustomerByMsisdn?msisdn={string}
```

#### <a id="getAllCustomers"> Get All Customers
```
  v1/api/customer/getAllCustomers
```

#### <a id="remainingBalance"> Get Remaining Balance
```
  v1/api/balance/remainingBalance?msisdn={string}
```
---

