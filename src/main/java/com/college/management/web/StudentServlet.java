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

import com.college.management.dao.BookDAO;
import com.college.management.dao.ClassDAO;
import com.college.management.dao.StudentDAO;
import com.college.management.dao.SubjectDAO;
import com.college.management.model.Book;
import com.college.management.model.Class;
import com.college.management.model.Student;
import com.college.management.model.Subject;

@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StudentDAO studentDAO;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private BookDAO bookDAO;

    public void init() {
        studentDAO = new StudentDAO();
        classDAO = new ClassDAO();
        subjectDAO = new SubjectDAO();
        bookDAO = new BookDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String formType = request.getParameter("formType");
        
        if ("assignSubject".equals(formType)) {
            handleAssignment(request, response);
        } else if ("issueBook".equals(formType)) {
            handleBookIssue(request, response);
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
            case "new": showNewForm(request, response); break;
            case "delete": deleteStudent(request); listStudents(request, response); break;
            case "report": showMatrixReport(request, response); break;
            
            // --- UNIFIED LEARNING DASHBOARD ---
            case "manageLearning": 
                int id = Integer.parseInt(request.getParameter("id"));
                manageLearning(request, response, id, "subject"); // Default to subject tab
                break;

            // --- DELETE ACTIONS ---
            case "unassign": unassignSubject(request, response); break;
            case "returnBook": returnBook(request, response); break;
            
            default: listStudents(request, response); break;
        }
    }

    // --- DASHBOARD LOADER ---
    private void manageLearning(HttpServletRequest request, HttpServletResponse response, int studentId, String activeTab) throws ServletException, IOException {
        try {
            Student student = studentDAO.getStudentById(studentId);
            
            // 1. Fetch Subject Data
            List<Subject> allSubjects = subjectDAO.selectAllSubjects();
            List<Subject> existingSubjects = studentDAO.getSubjectsByStudentId(studentId);
            
            // 2. Fetch Book Data
            List<Book> allBooks = bookDAO.selectAllBooks();
            List<Book> issuedBooks = bookDAO.getBooksByStudentId(studentId);
            
            // 3. Set Attributes
            request.setAttribute("student", student);
            request.setAttribute("studentId", studentId);
            request.setAttribute("activeTab", activeTab); // Tell JSP which tab to open
            
            request.setAttribute("allSubjects", allSubjects);
            request.setAttribute("existingSubjects", existingSubjects);
            
            request.setAttribute("allBooks", allBooks);
            request.setAttribute("issuedBooks", issuedBooks);
            
            // 4. Send to New Dashboard
            request.getRequestDispatcher("student-learning.jsp").forward(request, response);
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            response.sendRedirect("StudentServlet"); 
        }
    }

    // --- ACTION HANDLERS ---

    private void handleAssignment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int subjectId = Integer.parseInt(request.getParameter("subjectId"));
        boolean success = studentDAO.assignSubjectToStudent(studentId, subjectId);
        
        if (success) request.setAttribute("subjectMessage", "Subject assigned successfully!");
        else request.setAttribute("subjectMessage", "Error: Subject already assigned.");
        
        // Reload Dashboard (Keep Subject Tab Open)
        manageLearning(request, response, studentId, "subject");
    }

    private void handleBookIssue(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        boolean success = bookDAO.assignBookToStudent(studentId, bookId);
        
        if (success) request.setAttribute("bookMessage", "Book issued successfully! 📖");
        else request.setAttribute("bookMessage", "Error: Book already issued.");
        
        // Reload Dashboard (Keep Book Tab Open)
        manageLearning(request, response, studentId, "book");
    }
    
    private void unassignSubject(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int subjectId = Integer.parseInt(request.getParameter("subjectId"));
        studentDAO.unassignSubject(studentId, subjectId);
        
        request.setAttribute("subjectMessage", "Subject removed successfully.");
        
        manageLearning(request, response, studentId, "subject"); 
    }

    private void returnBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        bookDAO.unassignBook(studentId, bookId);
        
        request.setAttribute("bookMessage", "Book returned successfully.");
        
        manageLearning(request, response, studentId, "book");
    }

    // --- UPDATED REPORT METHOD (with Book Matrix support) ---
    private void showMatrixReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchQuery = request.getParameter("search");
        List<Student> studentList;
        
        // 1. Fetch Students (with optional search)
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            studentList = studentDAO.searchStudents(searchQuery.trim());
        } else {
            studentList = studentDAO.selectAllStudents();
        }
        
        // 2. Fetch Subject Data (For Subject Matrix)
        List<Subject> allSubjects = subjectDAO.selectAllSubjects();
        Map<Integer, Set<Integer>> enrollmentMap = studentDAO.getAllEnrollments();
        
        // 3. NEW: Fetch Book Data (For Book Issued Matrix)
        Map<Integer, String> bookMap = bookDAO.getAllIssuedBooksReport();
        
        // 4. Send Everything to JSP
        request.setAttribute("allStudents", studentList);
        request.setAttribute("allSubjects", allSubjects);
        request.setAttribute("enrollmentMap", enrollmentMap);
        request.setAttribute("bookMap", bookMap); // New attribute for book data
        
        request.getRequestDispatcher("student-report.jsp").forward(request, response);
    }

    // --- EXISTING HELPER METHODS (UNCHANGED) ---
    private void insertNewStudent(HttpServletRequest request) {
        String rollNumber = request.getParameter("rollNumber");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        int classId = Integer.parseInt(request.getParameter("classId"));
        String mobileNumber = request.getParameter("mobileNumber");
        String address = request.getParameter("address");

        Student newStudent = new Student(rollNumber, firstName, lastName, email, classId, mobileNumber, address);
        int newId = studentDAO.insertStudent(newStudent);
        if (newId != -1) request.setAttribute("message", "Success! Student added: " + firstName);
        else request.setAttribute("message", "Error! Could not add student. (Check for duplicate Roll Number).");
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Class> classList = classDAO.selectAllClasses();
        request.setAttribute("classList", classList);
        request.getRequestDispatcher("student-form.jsp").forward(request, response);
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Student> listStudent = studentDAO.selectAllStudents();
        request.setAttribute("listStudent", listStudent);
        request.getRequestDispatcher("student-list.jsp").forward(request, response);
    }

    private void deleteStudent(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            studentDAO.deleteStudent(id);
            request.setAttribute("message", "Student deleted successfully.");
        } catch (Exception e) { e.printStackTrace(); }
    }
}