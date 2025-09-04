<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Added</title>
</head>
<body>
    <h2>✅ Student Added Successfully!</h2>

    <p><b>Name:</b> ${name}</p>
    <p><b>Gender:</b> ${gender}</p>
    <p><b>Course:</b> ${course}</p>
    <p><b>DOB:</b> ${dob}</p>

    <p><b>Photo:</b><br>
        <img src="data:image/png;base64,${photoBase64}" width="150"/>
    </p>

    <p><b>QR Code:</b><br>
        <img src="data:image/png;base64,${qrBase64}" width="150"/>
    </p>
</body>
</html>
