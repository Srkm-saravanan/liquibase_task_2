<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student List</title>
    <style>
        /* Glassmorphism Theme */
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            margin: 0; padding: 0; 
            background: linear-gradient(135deg, #a8dadc 0%, #457b9d 100%); 
            display: flex; justify-content: center; align-items: center; min-height: 100vh;
        }
        .container { 
            width: 95%; max-width: 1000px; padding: 30px; border-radius: 20px; 
            background: rgba(255, 255, 255, 0.2); 
            backdrop-filter: blur(10px); -webkit-backdrop-filter: blur(10px); 
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37); 
            border: 1px solid rgba(255, 255, 255, 0.3); 
            color: #1d3557; text-align: center;
        }
        h2 { color: #1d3557; margin-bottom: 20px; font-weight: 600; }
        .message { 
            padding: 15px; margin-bottom: 20px; border-radius: 8px; font-weight: 600; 
            background-color: #d8f3da; color: #1e7e34; border: 1px solid #c3e6cb; text-align: left;
        }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        th, td { border: 1px solid rgba(29, 53, 87, 0.1); padding: 12px; text-align: left; }
        th { background-color: #457b9d; color: white; text-transform: uppercase; font-size: 0.9em; }
        tr:nth-child(even) { background-color: rgba(255, 255, 255, 0.3); }
        tr:hover { background-color: rgba(255, 255, 255, 0.5); }
        
        .delete-btn {
            background-color: #e76f51; color: white; border: none; padding: 6px 10px;
            border-radius: 5px; cursor: pointer; font-weight: 600; font-size: 0.9em;
        }
        .delete-btn:hover { background-color: #cc4e30; }
        
        .add-link { 
            display: inline-block; margin-top: 20px; padding: 12px 15px; text-decoration: none; 
            border-radius: 10px; font-size: 15px; font-weight: 600; 
            background-color: #52b788; color: white; margin-right: 10px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        .main-menu-btn { background-color: #00bfff; }
    </style>
    
    <script>
        function confirmDelete(id, name) {
            if (confirm("Are you sure you want to delete student: " + name + "?")) {
                window.location.href = 'StudentServlet?action=delete&id=' + id;
            }
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>Student Management Report</h2>
        
        <c:if test="${not empty message}"><p class="message">${message}</p></c:if>

        <c:if test="${empty listStudent}">
            <p>No students found. Add one now!</p>
        </c:if>
        
        <c:if test="${not empty listStudent}">
            <table>
                <thead>
                    <tr>
                        <th>S.No</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Assigned Class</th> 
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="student" items="${listStudent}" varStatus="loop">
                        <tr>
                            <td>${loop.index + 1}</td>
                            <td>${student.firstName} ${student.lastName}</td>
                            <td>${student.email}</td>
                            <td><b>${student.studentClass.className}</b></td>
                            <td>
                                <a href="StudentServlet?action=assign&id=${student.studentId}" 
                                   style="background-color: #ff9f1c; color: white; padding: 6px 10px; text-decoration: none; border-radius: 5px; font-size: 0.9em; margin-right: 5px;">
                                   Assign Subject
                                </a>
                                
                                <button class="delete-btn" onclick="confirmDelete(${student.studentId}, '${student.firstName}')">Delete</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <a href="StudentServlet?action=new" class="add-link">← Register New Student</a>
        <a href="index.html" class="add-link main-menu-btn">Main Menu</a>
    </div>
</body>
</html>