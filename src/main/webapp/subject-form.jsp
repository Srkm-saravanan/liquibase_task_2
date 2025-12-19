<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Subject</title>
    <style>
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            margin: 0; background: linear-gradient(135deg, #a8dadc 0%, #457b9d 100%); 
            display: flex; justify-content: center; align-items: center; min-height: 100vh;
        }
        .glass-card { 
            width: 350px; padding: 40px; border-radius: 20px; 
            background: rgba(255, 255, 255, 0.25); 
            backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); 
            box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37); 
            border: 1px solid rgba(255, 255, 255, 0.3); 
            text-align: center;
        }
        h2 { color: #1d3557; margin-bottom: 25px; }
        input[type="text"] {
            width: 90%; padding: 12px; margin-bottom: 20px; border-radius: 8px;
            border: 1px solid #ccc; font-size: 1rem;
        }
        button {
            width: 100%; padding: 12px; background-color: #2a9d8f; color: white;
            border: none; border-radius: 8px; font-size: 1rem; cursor: pointer; font-weight: bold;
            transition: 0.3s;
        }
        button:hover { background-color: #21867a; transform: translateY(-2px); }
        .back-link { display: block; margin-top: 20px; color: #1d3557; text-decoration: none; }
    </style>
</head>
<body>
    <div class="glass-card">
        <h2>Add New Subject</h2>
        <form action="SubjectServlet" method="post">
            <input type="text" name="subjectName" required placeholder="Ex: Data Structures">
            <button type="submit">Save Subject</button>
        </form>
        <a href="index.html" class="back-link">← Back to Menu</a>
    </div>
</body>
</html>