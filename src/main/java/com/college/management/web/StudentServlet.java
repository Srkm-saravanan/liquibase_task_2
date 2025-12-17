package com.college.management.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.college.management.dao.ClassDAO;
import com.college.management.dao.StudentDAO;
import com.college.management.dao.SubjectDAO;
import com.college.management.model.Class;
import com.college.management.model.Student;
import com.college.management.model.Subject;

@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StudentDAO studentDAO;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;

    public void init() {
        studentDAO = new StudentDAO();
        classDAO = new ClassDAO();
        subjectDAO = new SubjectDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String formType = request.getParameter("formType");

        if ("assignSubject".equals(formType)) {
            handleAssignment(request, response);
        } else {
            insertNewStudent(request);
            doGet(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) { action = "list"; }

        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "delete":
                deleteStudent(request);
                listStudents(request, response);
                break;
            case "assign":
                prepareAssignForm(request, response);
                break;
            case "unassign":
                unassignSubject(request, response);
                break;
            case "report": // Matrix Report Case
                showMatrixReport(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }

    // --- UPDATED METHOD: Prepares the Matrix Data with SEARCH Support ---
    private void showMatrixReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String searchQuery = request.getParameter("search");
        List<Student> studentList;

        // 1. Get Rows (Students) - Filtered or All
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // If user searched for something, use the search method
            studentList = studentDAO.searchStudents(searchQuery.trim());
        } else {
            // Otherwise, show everyone
            studentList = studentDAO.selectAllStudents();
        }
        
        // 2. Get Columns (Subjects)
        List<Subject> allSubjects = subjectDAO.selectAllSubjects();
        
        // 3. Get Data (Who is enrolled in what?)
        // Returns Map<StudentId, Set<SubjectId>>
        Map<Integer, Set<Integer>> enrollmentMap = studentDAO.getAllEnrollments();
        
        request.setAttribute("allStudents", studentList);
        request.setAttribute("allSubjects", allSubjects);
        request.setAttribute("enrollmentMap", enrollmentMap);
        
        request.getRequestDispatcher("student-report.jsp").forward(request, response);
    }

    private void prepareAssignForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int studentId = Integer.parseInt(request.getParameter("id"));
            Student student = studentDAO.getStudentById(studentId);
            List<Subject> allSubjects = subjectDAO.selectAllSubjects();
            List<Subject> existingSubjects = studentDAO.getSubjectsByStudentId(studentId);
            
            request.setAttribute("student", student);
            request.setAttribute("studentId", studentId);
            request.setAttribute("allSubjects", allSubjects);
            request.setAttribute("existingSubjects", existingSubjects);
            request.getRequestDispatcher("student-assign.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("StudentServlet");
        }
    }

    private void unassignSubject(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int subjectId = Integer.parseInt(request.getParameter("subjectId"));
        studentDAO.unassignSubject(studentId, subjectId);
        request.setAttribute("message", "Subject removed successfully.");
        request.setAttribute("id", studentId); 
        prepareAssignForm(request, response); 
    }

    private void handleAssignment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int subjectId = Integer.parseInt(request.getParameter("subjectId"));
        boolean success = studentDAO.assignSubjectToStudent(studentId, subjectId);
        
        if (success) {
            request.setAttribute("message", "Subject assigned successfully!");
        } else {
            request.setAttribute("message", "Error: Subject already assigned or invalid.");
        }
        request.setAttribute("id", studentId); 
        
        // Manually reload lists for the view
        List<Subject> allSubjects = subjectDAO.selectAllSubjects();
        List<Subject> existingSubjects = studentDAO.getSubjectsByStudentId(studentId);
        Student student = studentDAO.getStudentById(studentId); 
        
        request.setAttribute("student", student); 
        request.setAttribute("studentId", studentId);
        request.setAttribute("allSubjects", allSubjects);
        request.setAttribute("existingSubjects", existingSubjects);
        request.getRequestDispatcher("student-assign.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Class> classList = classDAO.selectAllClasses();
        request.setAttribute("classList", classList);
        request.getRequestDispatcher("student-form.jsp").forward(request, response);
    }

    private void insertNewStudent(HttpServletRequest request) {
        String rollNumber = request.getParameter("rollNumber");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        int classId = Integer.parseInt(request.getParameter("classId"));

        Student newStudent = new Student(rollNumber, firstName, lastName, email, classId);
        int newId = studentDAO.insertStudent(newStudent);
        
        if (newId != -1) {
            request.setAttribute("message", "Success! Student added: " + firstName);
        } else {
            request.setAttribute("message", "Error! Could not add student. (Check for duplicate Roll Number).");
        }
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Student> listStudent = studentDAO.selectAllStudents();
        request.setAttribute("listStudent", listStudent);
        request.getRequestDispatcher("student-list.jsp").forward(request, response);
    }

    private void deleteStudent(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            studentDAO.deleteStudent(id);
            request.setAttribute("message", "Student deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}