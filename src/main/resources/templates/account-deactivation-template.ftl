<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Account Deactivated</title>
</head>

<body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2; color: #333333">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="center" bgcolor="#333333"
                style="background-color: #333333; height: 100px; ">
                <h1 style="color: #ffa941; ">We are sorry to see you leaving...</h1>
            </td>
        </tr>
        <tr style="background-color: #c8c8ca; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
            <td align="left" valign="center" bgcolor="#c8c8ca"
                style="background-color: #c8c8ca; padding:20px; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
                <p style="font-size: 18px; line-height: 1.5; ">
                    Dear <span style="font-weight: bold;">${username}</span>,
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    We hope this message finds you well. We are writing to inform you that your account with <b>${companyName}</b> has been deactivated, effective as of ${date}. <br>
                    The deactivation of your account is a result of your departure from ${companyName}. As per our internal policies, user accounts are deactivated when an employee leaves the company to ensure the security and integrity of our platform. <br>
                    With the deactivation of your account, you will no longer have access to your account and its associated features on our platform. Any data or content associated with your account will be securely retained for a specific period in accordance with our data retention policies.
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

