<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Add Employees to EmployeeDB </title></head>
<body>
<form action="jdbcdatasource" name="AddValuesToDB">
    <input type="hidden" name="getValues" value="false"/>

    <table>
        <tr><td><p>Employee Name </p></td><td><input type="text" name="emp_name"/></td></tr>
        <tr><td><p>Age </p></td><td><input type="text" name="emp_age"/></td></tr>
    </table>
    <input type="submit" value="Add"/>
</form>

</body>
</html>