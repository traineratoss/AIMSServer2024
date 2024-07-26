<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Idea Text Change</title>
</head>

<body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f2f2; color: #333333">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" valign="center" bgcolor="#333333"
                style="background-color: #333333; height: 100px; ">
                <h1 style="color: #ffa941; ">Idea text change notification</h1>
            </td>
        </tr>
        <tr style="background-color: #c8c8ca; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
            <td align="left" valign="center" bgcolor="#c8c8ca"
                style="background-color: #c8c8ca; padding:20px; box-shadow: 0px -10px 10px rgba(0, 0, 0, 0.1);">
                <p style="font-size: 18px; line-height: 1.5; ">
                    Dear <span style="font-weight: bold;">${username}</span>,
                </p>
                <p style="font-size: 16px; line-height: 1.5;">
                    We hope you are doing well. It appears that there has been a change in the text of an idea you are subscribed to. Here is the new idea:
                </p>
                <div align="left" style="background-color: rgba(180, 179, 179,0.4); border-left: 4px solid #ffa941; width: 25%; margin-left:20px;">
                                    <div style="background-color: rgba(180, 179, 179,0.4); padding: 10px; border-radius: 4px;">
                                        <p style="font-size: 16px; line-height: 1.5; margin: 0;">
                                            <b>Title: </b> ${newTitle}
                                        </p>
                                    </div>
                                    <div style="background-color: #e0f7fa; padding: 10px; border-radius: 4px;">
                                        <p style="font-size: 16px; line-height: 1.5; margin: 0;">
                                            <b>Text: </b> ${newText}
                                        </p>
                                    </div>
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

