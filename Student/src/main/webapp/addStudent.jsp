<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Add Student</title>
</head>
<body>
<h2>Add New Student</h2>
<form method="post" action="addStudent" enctype="multipart/form-data">
    Name: <input type="text" name="name" required><br><br>
    Gender: 
    <select name="gender">
        <option value="Male">Male</option>
        <option value="Female">Female</option>
        <option value="Other">Other</option>
    </select><br><br>
    Course: <input type="text" name="course" required><br><br>
    DOB: <input type="date" name="dob" required><br><br>
    Photo: <input type="file" name="photo" accept="image/*" required><br><br>
    <button type="submit">Add Student</button>
</form>
</body>
</html>
