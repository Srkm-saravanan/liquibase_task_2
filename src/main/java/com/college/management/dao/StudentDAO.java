package com.college.management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.college.management.model.Class;
import com.college.management.model.Student;
import com.college.management.model.Subject;
import com.college.management.util.DBUtil;

public class StudentDAO {

    // --- SQL QUERIES ---
    private static final String INSERT_STUDENT_SQL = 
        "INSERT INTO student (roll_number, first_name, last_name, email, class_id) VALUES (?, ?, ?, ?, ?);";
        
    private static final String SELECT_ALL_STUDENTS = 
        "SELECT s.student_id, s.roll_number, s.first_name, s.last_name, s.email, c.class_name, s.class_id " +
        "FROM student s JOIN class c ON s.class_id = c.class_id " +
        "ORDER BY s.student_id;";
        
    private static final String DELETE_STUDENT_SQL = 
        "DELETE FROM student WHERE student_id = ?;";

    private static final String ASSIGN_SUBJECT_SQL = 
        "INSERT INTO student_subject_mapping (student_id, subject_id) VALUES (?, ?);";

    private static final String SELECT_SUBJECTS_BY_STUDENT_ID = 
        "SELECT s.subject_id, s.subject_name " +
        "FROM subject s " +
        "JOIN student_subject_mapping m ON s.subject_id = m.subject_id " +
        "WHERE m.student_id = ?;";

    private static final String SELECT_STUDENT_WITH_CLASS_BY_ID = 
        "SELECT s.*, c.class_name FROM student s " +
        "JOIN class c ON s.class_id = c.class_id " +
        "WHERE s.student_id = ?";

    private static final String UNASSIGN_SUBJECT_SQL = 
        "DELETE FROM student_subject_mapping WHERE student_id = ? AND subject_id = ?";

    private static final String SELECT_ALL_MAPPINGS = 
        "SELECT student_id, subject_id FROM student_subject_mapping";

    // --- BASIC CRUD METHODS ---

    public int insertStudent(Student newStudent) {
        int studentId = -1;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     INSERT_STUDENT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, newStudent.getRollNumber());
            preparedStatement.setString(2, newStudent.getFirstName());
            preparedStatement.setString(3, newStudent.getLastName());
            preparedStatement.setString(4, newStudent.getEmail());
            preparedStatement.setInt(5, newStudent.getClassId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        studentId = rs.getInt(1);
                        newStudent.setStudentId(studentId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentId;
    }

    public List<Student> selectAllStudents() {
        List<Student> students = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("student_id");
                String rollNo = rs.getString("roll_number");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String className = rs.getString("class_name");
                int classId = rs.getInt("class_id");

                Class studentClass = new Class(classId, className);
                Student student = new Student(id, rollNo, firstName, lastName, email, classId);
                student.setStudentClass(studentClass);

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public boolean deleteStudent(int studentId) {
        boolean rowDeleted = false;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_SQL)) {

            statement.setInt(1, studentId);
            rowDeleted = statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }

    // --- SUBJECT MAPPING METHODS ---

    public boolean assignSubjectToStudent(int studentId, int subjectId) {
        boolean rowInserted = false;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(ASSIGN_SUBJECT_SQL)) {

            statement.setInt(1, studentId);
            statement.setInt(2, subjectId);
            rowInserted = statement.executeUpdate() > 0;

        } catch (SQLException e) {
            // ignore
        }
        return rowInserted;
    }

    public List<Subject> getSubjectsByStudentId(int studentId) {
        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SUBJECTS_BY_STUDENT_ID)) {

            statement.setInt(1, studentId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                subjects.add(new Subject(
                        rs.getInt("subject_id"),
                        rs.getString("subject_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public Student getStudentById(int studentId) {
        Student student = null;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_STUDENT_WITH_CLASS_BY_ID)) {

            statement.setInt(1, studentId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                student = new Student(
                        studentId,
                        rs.getString("roll_number"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getInt("class_id"));

                student.setStudentClass(
                        new Class(rs.getInt("class_id"), rs.getString("class_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public boolean unassignSubject(int studentId, int subjectId) {
        boolean rowDeleted = false;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UNASSIGN_SUBJECT_SQL)) {

            statement.setInt(1, studentId);
            statement.setInt(2, subjectId);
            rowDeleted = statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }

    public Map<Integer, Set<Integer>> getAllEnrollments() {
        Map<Integer, Set<Integer>> enrollmentMap = new HashMap<>();

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_MAPPINGS);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                enrollmentMap
                    .computeIfAbsent(rs.getInt("student_id"), k -> new HashSet<>())
                    .add(rs.getInt("subject_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollmentMap;
    }

    // --- 🔍 NEW SEARCH METHOD (POLICE SEARCH MODE) ---
    public List<Student> searchStudents(String query) {
        List<Student> students = new ArrayList<>();

        String sql =
            "SELECT s.student_id, s.roll_number, s.first_name, s.last_name, s.email, c.class_name, s.class_id " +
            "FROM student s JOIN class c ON s.class_id = c.class_id " +
            "WHERE s.student_id = ? OR s.first_name LIKE ? OR s.last_name LIKE ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int idSearch = 0;
            try {
                idSearch = Integer.parseInt(query);
            } catch (NumberFormatException e) {
                // Not an ID, treat as name search
            }

            statement.setInt(1, idSearch);
            statement.setString(2, "%" + query + "%");
            statement.setString(3, "%" + query + "%");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Class studentClass = new Class(
                        rs.getInt("class_id"),
                        rs.getString("class_name"));

                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("roll_number"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getInt("class_id"));

                student.setStudentClass(studentClass);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}
