<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Management Report</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            background: rgba(255, 255, 255, 0.25);
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
            backdrop-filter: blur(4px);
            -webkit-backdrop-filter: blur(4px);
            border-radius: 15px;
            border: 1px solid rgba(255, 255, 255, 0.18);
            padding: 30px;
            width: 100%;
            max-width: 1100px;
            text-align: center;
            overflow-x: auto;
        }

        h2 {
            color: #333;
            margin-bottom: 25px;
            font-weight: 600;
            font-size: 1.8em;
        }

        /* Message Styling */
        .message {
            margin-bottom: 20px;
            padding: 12px 20px;
            border-radius: 8px;
            font-weight: 600;
            text-align: left;
        }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }

        /* Table Container */
        .table-container {
            overflow-x: auto;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            margin-top: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: rgba(255, 255, 255, 0.6);
            min-width: 900px;
        }

        th, td {
            padding: 15px 12px;
            text-align: left;
            border-bottom: 1px solid rgba(0,0,0,0.05);
            color: #444;
            font-size: 0.95em;
        }

        th {
            background-color: #4a90e2;
            color: white;
            text-transform: uppercase;
            font-size: 0.85em;
            letter-spacing: 0.5px;
            font-weight: bold;
        }

        tr:last-child td {
            border-bottom: none;
        }

        tr:hover {
            background-color: rgba(255, 255, 255, 0.4);
            transition: background-color 0.3s ease;
        }

        /* Class Badge */
        .class-badge {
            background-color: #e2e6ea;
            color: #555;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: bold;
            display: inline-block;
        }

        /* No Data Message */
        .no-data {
            color: #555;
            font-style: italic;
            margin: 30px 0;
            font-size: 1.1em;
        }
        .no-data a { color: #007bff; text-decoration: none; font-weight: 600; }
        .no-data a:hover { text-decoration: underline; }

        /* Bottom Buttons */
        .btn-container {
            margin-top: 30px;
            display: flex;
            justify-content: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 25px;
            font-weight: bold;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            font-size: 1em;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }

        .btn i { margin-right: 10px; }

        .btn-register { background-color: #28a745; color: white; }
        .btn-register:hover { background-color: #218838; transform: translateY(-3px); box-shadow: 0 6px 15px rgba(0,0,0,0.15); }

        .btn-menu { background-color: #007bff; color: white; }
        .btn-menu:hover { background-color: #0069d9; transform: translateY(-3px); box-shadow: 0 6px 15px rgba(0,0,0,0.15); }

        /* Action Buttons in Table */
        .action-btn-group {
            display: flex;
            gap: 10px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .action-btn {
            padding: 10px 16px;
            border-radius: 8px;
            text-decoration: none;
            color: white;
            font-size: 0.9em;
            font-weight: 600;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
            min-width: 120px;
            justify-content: center;
        }

        .action-btn i {
            margin-right: 8px;
            font-size: 1.1em;
        }

        .action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
        }

        .item-assign {
            background-color: #9b59b6; /* Purple for Learnings */
        }
        .item-assign:hover {
            background-color: #8e44ad;
        }

        .item-delete {
            background-color: #e74c3c;
        }
        .item-delete:hover {
            background-color: #c0392b;
        }
    </style>

    <script>
        function confirmDelete(id, name) {
            if (confirm("Are you sure you want to delete this student?\n\nName: " + name + "\nThis action cannot be undone.")) {
                window.location.href = "StudentServlet?action=delete&id=" + id;
            }
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>Student Management Report</h2>

        <c:if test="${not empty message}">
            <div class="message ${message.startsWith('Error') ? 'error' : 'success'}">
                ${message}
            </div>
        </c:if>

        <div class="table-container">
            <c:choose>
                <c:when test="${not empty listStudent}">
                    <table>
                        <thead>
                            <tr>
                                <th>Roll No</th>
                                <th>First Name</th>
                                <th>Last Name</th>
                                <th>Email</th>
                                <th>Mobile</th>
                                <th>Address</th>
                                <th>Class</th>
                                <th style="text-align: center;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="student" items="${listStudent}">
                                <tr>
                                    <td><strong>${student.rollNumber}</strong></td>
                                    <td>${student.firstName}</td>
                                    <td>${student.lastName}</td>
                                    <td>${student.email}</td>
                                    <td>${student.mobileNumber}</td>
                                    <td>${student.address}</td>
                                    <td><span class="class-badge">${student.studentClass.className}</span></td>
                                    <td>
                                        <div class="action-btn-group">
                                            <a href="StudentServlet?action=manageLearning&id=${student.studentId}" 
                                               class="action-btn item-assign" 
                                               title="Manage Subjects & Library">
                                                <i class="fas fa-graduation-cap"></i> Learnings
                                            </a>
                                            
                                            <a href="#" 
                                               onclick="confirmDelete(${student.studentId}, '${student.firstName} ${student.lastName}')" 
                                               class="action-btn item-delete" 
                                               title="Delete Student">
                                                <i class="fas fa-trash-alt"></i> Delete
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="no-data">
                        <p>No students found. <a href="StudentServlet?action=new">Register the first one now!</a></p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="btn-container">
            <a href="StudentServlet?action=new" class="btn btn-register">
                <i class="fas fa-user-plus"></i> Register New Student
            </a>
            <a href="index.html" class="btn btn-menu">
                <i class="fas fa-home"></i> Main Menu
            </a>
        </div>
    </div>
</body>
</html>