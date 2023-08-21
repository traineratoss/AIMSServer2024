<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Account Activation</title>
</head>

<body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2;">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="center" bgcolor="#333333"
                style="background-color: #333333; height: 100px; ">
                <h1 style="color: #ffa941; ">Congratulations! </h1>
            </td>
        </tr>
        <tr style="background-color: #c8c8ca; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
            <td align="left" valign="center" bgcolor="#c8c8ca"
                style="background-color: #c8c8ca; padding:20px; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
                <p style="font-size: 18px; line-height: 1.5; ">
                    Dear <span style="font-weight: bold;">${username}</span>,
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    Welcome to <span>${companyName}</span> App! We are thrilled to have you as a new member of our community. To get started, you will need to activate your account.
                </p>
                <div align="left" style="background-color: rgba(180, 179, 179,0.4); border-left: 4px solid #ffa941; width: 25%; margin-left:20px;">
                    <p style="font-size: 16px; line-height: 1.5; margin: 5px;">
                        <strong  style="font-size: 18px">Your login credentials:</strong><br>
                        <b>Email:</b> ${email}<br>
                        <b>Password:</b> ${password}
                    </p>
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

