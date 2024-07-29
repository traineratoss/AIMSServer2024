<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Idea Rating Update Notification</title>
</head>

<body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2; color: #333333">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="center" bgcolor="#333333"
                style="background-color: #333333; height: 100px;">
                <h1 style="color: #ffa941;">Welcome back, ${username}!</h1>
            </td>
        </tr>
        <tr style="background-color: #c8c8ca; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
            <td align="left" valign="center" bgcolor="#c8c8ca"
                style="background-color: #c8c8ca; padding:20px; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
                <p style="font-size: 18px; line-height: 1.5;">
                    Dear <span style="font-weight: bold;">${username}</span>,
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    We hope this message finds you well. We are pleased to inform you that the rating for the idea titled <br>"<b>${ideaTitle}</b>"<br> has been updated as of <span>${date}</span>.
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    The new rating is now: <span style="font-weight: bold; color: #ffa941;">${newRating} stars</span>.
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    Your engagement and feedback are highly valued, and we encourage you to continue sharing your thoughts and ideas with our community.
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
