<!DOCTYPE html>
<html  xmlns:th="http://www.thymeleaf.org">
<head>
  <title>Password Reset Instructions</title>
</head>
<body>
<p>
  Dear ${username},
</p>
<p>
  Welcome to ${companyName}!
</p>
<p>
  We hope you are doing well. It appears that you have requested a password reset for your account. We understand how important it is to regain access to your account, and we're here to assist you with the process.
</p>
<p>
  To proceed with the password reset, please follow the instructions below:
</p>
<ol>
  <li>Click on the following link to access the login page: <a>http://127.0.0.1:5173/login</a></li>
  <li>Once you are on the login page, use the temporary password provided below to log in to your account:<br>
    <pre>Temporary Password: <span>${password}</span></pre>
  </li>
  <li>After successfully logging in, you will be automatically redirected to the password reset page.</li>
  <li>On the password reset page, choose a new, strong, and unique password for your account. Please remember to keep your password secure and avoid sharing it with anyone.</li>
</ol>
<p>
  Best regards,<br>
  The <span>${companyName}</span> Team
</p>
</body>
</html>
