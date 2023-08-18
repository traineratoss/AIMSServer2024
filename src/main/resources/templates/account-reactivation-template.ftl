<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Account Reactivated</title>
</head>
<body>
<p>
    Dear <span>${username}</span>,
</p>
<p>
    We hope this message finds you well. We are delighted to inform you that your account with <span>${companyName}</span> has been reactivated, effective immediately as of <span>${date}</span>.
</p>
<p>
    Your account was previously deactivated due to your departure from <span>${companyName}</span>. However, we are thrilled to welcome you back as you have returned to our company. As per our internal policies, user accounts are deactivated when an employee leaves the company to ensure the security and integrity of our platform.
</p>
<p>
    With the reactivation of your account, you will once again have full access to all the features and services offered on our platform. Your data and previous interactions have been securely restored, ensuring a seamless experience as you resume using our platform.
</p>
<p>
    <strong>Login Credentials:</strong><br>
    Email: <span>${email}</span><br>
    Password: <span>${password}</span>
</p>
<p>
    Should you encounter any issues or have any questions regarding your reactivated account, please do not hesitate to reach out to our support team. We are here to assist you with anything you may need.
</p>
<p>
    Welcome back!<br>
    Best regards,<br>
    <span>${companyName}</span>
</p>
</body>
</html>
