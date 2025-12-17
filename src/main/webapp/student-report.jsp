<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Master Enrollment Matrix</title>
    <style>
        body { 
            font-family: 'Segoe UI', sans-serif; 
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); 
            padding: 20px;
        }
        h2 { text-align: center; color: #333; }
        
        .table-container {
            overflow-x: auto; /* Scroll if too many subjects */
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            border-radius: 10px;
            background: white;
            margin-top: 20px;
        }

        table { width: 100%; border-collapse: collapse; min-width: 800px; }
        
        th, td { 
            padding: 12px 15px; text-align: center; border-bottom: 1px solid #ddd; 
        }
        
        th { 
            background-color: #009879; color: white; 
            white-space: nowrap; /* Keep headers on one line */
        }
        
        /* Freeze the first column (Student Name) */
        td:first-child, th:first-child {
            position: sticky; left: 0; background-color: #f1f1f1; z-index: 1; font-weight: bold;
        }
        th:first-child { z-index: 2; background-color: #007f65; color: white; }

        tr:nth-of-type(even) { background-color: #f3f3f3; }
        tr:hover { background-color: #f1f1f1; }

        /* Status Badges */
        .enrolled { 
            background-color: #d4edda; color: #155724; 
            padding: 5px 10px; border-radius: 15px; font-size: 0.85em; font-weight: bold;
        }
        .not-enrolled { 
            color: #ccc; font-style: italic; font-size: 0.9em;
        }
        
        .main-menu-btn {
            display: inline-block; margin-bottom: 20px; 
            background: #333; color: white; padding: 10px 20px; 
            text-decoration: none; border-radius: 5px;
        }

        /* Search Bar Styling */
        .search-container {
            text-align: center; 
            margin-bottom: 20px;
        }
        .search-input {
            padding: 10px; width: 300px; border-radius: 5px; border: 1px solid #ccc;
        }
        .search-btn {
            padding: 10px 20px; background-color: #007bff; color: white; 
            border: none; border-radius: 5px; cursor: pointer;
        }
        .search-btn:hover { background-color: #0056b3; }
        .clear-link { margin-left: 10px; text-decoration: none; color: #555; }
    </style>
</head>
<body>

    <a href="index.html" class="main-menu-btn">← Main Menu</a>
    <h2>Student Subject Enrollment Matrix</h2>

    <div class="search-container">
        <form action="StudentServlet" method="get">
            <input type="hidden" name="action" value="report">
            <input type="text" name="search" placeholder="Search by ID or Name..." class="search-input">
            <input type="submit" value="Search 🔍" class="search-btn">
            <a href="StudentServlet?action=report" class="clear-link">Clear</a>
        </form>
    </div>

    <div class="table-container">
        <table>
            <thead>
                <tr>
                    <th>Student Name</th>
                    <th>Class</th>
                    <c:forEach var="sub" items="${allSubjects}">
                        <th>${sub.subjectName}</th>
                    </c:forEach>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="student" items="${allStudents}">
                    <tr>
                        <td style="text-align: left;">${student.firstName} ${student.lastName}</td>
                        <td>${student.studentClass.className}</td>
                        
                        <c:forEach var="sub" items="${allSubjects}">
                            <td>
                                <c:choose>
                                    <c:when test="${not empty enrollmentMap[student.studentId] and enrollmentMap[student.studentId].contains(sub.subjectId)}">
                                        <span class="enrolled">Enrolled</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="not-enrolled">- Null -</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </c:forEach>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</body>
</html>