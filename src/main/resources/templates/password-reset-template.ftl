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
                <h1 style="color: #ffa941; ">Your new password is here!</h1>
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
                <div align="left" style="background-color: rgba(180, 179, 179,0.4); border-left: 4px solid #ffa941; width: 50%; margin-left:20px;">
                    <ol style="font-size: 16px; line-height: 1.5;">
                        <li>Click
                            <a href="http://127.0.0.1:5173/login" style="color:#d88f35; text-decoration: none; font-weight: bolder;">here </a>
                            to access the login page.
                        </li>
                        <li>Once you are on the login page, use the temporary password provided below to log in to your account:<br>
                            <strong>Temporary Password: </strong><span>${password}</span>
                        </li>
                        <li>After successfully logging in, you will be automatically redirected to the password reset page.</li>
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

