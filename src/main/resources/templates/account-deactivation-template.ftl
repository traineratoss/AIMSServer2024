<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <title>Account Deactivated</title>
</head>
<body>
<p>
  Dear <span>${username}</span>,
</p>
<p>
  We hope this message finds you well. We are writing to inform you that your account with ${companyName} has been deactivated, effective as of ${date}.
</p>
<p>
  The deactivation of your account is a result of your departure from ${companyName}. As per our internal policies, user accounts are deactivated when an employee leaves the company to ensure the security and integrity of our platform.
</p>
<p>
  With the deactivation of your account, you will no longer have access to your account and its associated features on our platform. Any data or content associated with your account will be securely retained for a specific period in accordance with our data retention policies.
</p>
<p>
  Best regards,<br>
  <span>${companyName}</span>
</p>
</body>
</html>