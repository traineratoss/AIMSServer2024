<!--

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Login Request Notification</title>
</head>

<body  style="font-family: Arial, sans-serif;">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="center" bgcolor="#333333"
                style="background-color: #333333; height: 100px;">
                <h1 style="color: #c8c8ca; ">Welcome to <span
                        style="color: #ffa941;">${companyName}</span>!</h1>
            </td>
        </tr>
        <tr style="background-color: #c8c8ca;">
            <td align="left" valign="center" bgcolor="#c8c8ca"
                style="background-color: #c8c8ca; padding:20px">
                <p style="font-size: 18px; line-height: 1.5; ">
                    Dear <span style="font-weight: bold;">${username}</span>,
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    I hope this message finds you well. I am writing to inform you that we have recorded a login
                    request in our account system.
                </p>
                <div align="left" style="background-color: rgba(180, 179, 179,0.4); border-left: 4px solid #ffa941; width: 25%; margin-left:20px;">
                    <p style="font-size: 16px; line-height: 1.5; margin: 5px;">
                        <strong  style="font-size: 18px">Request details:</strong><br>
                        <b>User:</b> ${username}<br>
                        <b>Email:</b> ${email}<br>
                        <b>Date and time:</b> ${date}
                    </p>
                </div>
                <p style="font-size: 16px; line-height: 1.5; margin: 20px 0;">
                    If you did not initiate this request or have any concerns, please contact our support team.
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    Best regards,<br>
                    <span style="font-style: italic;"><b>${companyName}</b></span>
                </p>
            </td>
        </tr>
    </table>

</body>

</html> -->

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Login Request Notification</title>
</head>

<body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2;">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="center" bgcolor="#333333"
                style="background-color: #333333; height: 100px; ">
                <h1 style="color: #c8c8ca; ">Welcome to <span
                        style="color: #ffa941;">${companyName}</span>!</h1>
            </td>
        </tr>
        <tr style="background-color: #c8c8ca; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
            <td align="left" valign="center" bgcolor="#c8c8ca"
                style="background-color: #c8c8ca; padding:20px; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
                <p style="font-size: 18px; line-height: 1.5; ">
                    Dear <span style="font-weight: bold;">${username}</span>,
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    I hope this message finds you well. I am writing to inform you that we have recorded a login
                    request in our account system.
                </p>
                <div align="left" style="background-color: rgba(180, 179, 179,0.4); border-left: 4px solid #ffa941; width: 25%; margin-left:20px;">
                    <p style="font-size: 16px; line-height: 1.5; margin: 5px;">
                        <strong  style="font-size: 18px">Request details:</strong><br>
                        <b>User:</b> ${username}<br>
                        <b>Email:</b> ${email}<br>
                        <b>Date and time:</b> ${date}
                    </p>
                </div>
                <p style="font-size: 16px; line-height: 1.5; margin: 20px 0;">
                    If you did not initiate this request or have any concerns, please contact our support team.
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

