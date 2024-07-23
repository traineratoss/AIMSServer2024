<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Pasword reset</title>
</head>

<body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2; color: #333333">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="center" bgcolor="#333333"
                style="background-color: #333333; height: 100px; ">
                <h1 style="color: #ffa941; ">Password reset request</h1>
            </td>
        </tr>
        <tr style="background-color: #c8c8ca; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
            <td align="left" valign="center" bgcolor="#c8c8ca"
                style="background-color: #c8c8ca; padding:20px; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
                <p style="font-size: 18px; line-height: 1.5; ">
                    Dear <span style="font-weight: bold;">${username}</span>,
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    We hope you are doing well. It appears that you have requested a password reset for your account. We understand how important it is to regain access to your account, and we're here to assist you with the process.
                </p>
                <div align="left" style="background-color: rgba(180, 179, 179,0.4); border-left: 4px solid #ffa941; width: 50%; margin-left:10px;">
                    <strong style="padding: 10px;">Account recovery code: </strong><br><span style="font-size: 24px; padding: 10px;">${password}</span>
                    <ol style="font-size: 16px; line-height: 1.5;">
                        <li>After successfully verifying this code, you will be automatically redirected to the password reset page.</li>
                        <li>On the password reset page, choose a new, strong, and unique password for your account. Please remember to keep your password secure and avoid sharing it with anyone.</li>
                    </ol>
                </div>
                <p style="font-size: 16px; line-height: 1.5;">
                    Best regards,<br>
                    <span style="font-style: italic;"><b>${companyName}</b></span>
                </p>
            </td>
        </tr>
    </table>
</body>
</html>

