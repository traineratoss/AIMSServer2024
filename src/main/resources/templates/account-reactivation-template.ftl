<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Account Reactivated</title>
</head>

<body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2; color: #333333">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="center" bgcolor="#333333"
                style="background-color: #333333; height: 100px; ">
                <h1 style="color: #ffa941; ">Welcome back, ${username}!</h1>
            </td>
        </tr>
        <tr style="background-color: #c8c8ca; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
            <td align="left" valign="center" bgcolor="#c8c8ca"
                style="background-color: #c8c8ca; padding:20px; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
                <p style="font-size: 18px; line-height: 1.5; ">
                    Dear <span style="font-weight: bold;">${username}</span>,
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                   We hope this message finds you well. We are delighted to inform you that your account with
                    <span>${companyName}</span>
                    has been reactivated, effective immediately as of
                    <span>${date}</span>.
                    <br>
                    Your account was previously deactivated due to you company. As per our internal policies, user accounts are deactivated when an employee leaves the company to ensure the security and integrity of our platform.
                    <br>
                    With the reactivation of your account, you will once again have full access tr departure from <b>${companyName}</b>.
                                                                                                                     <br>
                                                                                                                     However, we are thrilled to welcome you back as you have returned to ouro all the features and services offered on our platform. <br> Your data and previous interactions have been securely restored, ensuring a seamless experience as you resume using our platform.
                </p>
                <div align="left" style="background-color: rgba(180, 179, 179,0.4); border-left: 4px solid #ffa941; width: 50%; margin-left:20px;">
                     <p style="font-size: 16px; line-height: 1.5; margin: 5px;">
                        <strong  style="font-size: 18px">Login Credentials:</strong><br>
                        <b>Username:</b> ${username}<br>
                        <b>Email:</b> ${email}<br>
                        <b>Date and time:</b> ${date}
                    </p>
                </div>
                <p style="font-size: 16px; line-height: 1.5;">
                    If you encounter any issues or have any questions regarding your reactivated account, please do not hesitate to reach out to our support team. We are here to assist you with anything you may need.
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    Best regards,<br>
                    <span style="font-style: italic;"><b>${companyName}</b></span>
                </p>
            </td>
        </tr>
    </table>
</body>
</html>

