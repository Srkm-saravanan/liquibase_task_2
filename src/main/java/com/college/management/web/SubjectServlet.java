package com.college.management.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.college.management.dao.SubjectDAO;
import com.college.management.model.Subject;

@WebServlet("/SubjectServlet")
public class SubjectServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SubjectDAO subjectDAO;

    public void init() {
        subjectDAO = new SubjectDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // POST is used for inserting data
        insertNewSubject(request);
        // After insertion, redirect to GET method to show the updated list
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check for the 'action' parameter to see if a delete request was sent
        String action = request.getParameter("action");
        if (action != null && action.equals("delete")) {
            deleteSubject(request);
        }
        
        // Always retrieve and show the list of subjects
        List<Subject> subjectList = subjectDAO.selectAllSubjects();
        request.setAttribute("subjectList", subjectList);
        
        request.getRequestDispatcher("subject-list.jsp").forward(request, response);
    }

    private void insertNewSubject(HttpServletRequest request) {
        String subjectName = request.getParameter("subjectName");
        if (subjectName != null && !subjectName.trim().isEmpty()) {
            Subject newSubject = new Subject(subjectName.trim());
            int newId = subjectDAO.insertSubject(newSubject);
            
            if (newId != -1) {
                request.setAttribute("message", "Success! Subject '" + subjectName + "' added with ID: " + newId);
            } else {
                request.setAttribute("message", "Error! Subject could not be added.");
            }
        }
    }

    private void deleteSubject(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean isSuccess = subjectDAO.deleteSubject(id);
            
            if (isSuccess) {
                request.setAttribute("message", "Success! Subject with ID " + id + " was deleted.");
            } else {
                request.setAttribute("message", "Error! Subject with ID " + id + " could not be found or deleted.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Error! Invalid subject ID provided for deletion.");
        }
    }
}