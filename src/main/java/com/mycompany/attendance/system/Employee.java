package com.mycompany.attendance.system;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity 
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String department;
    private String status;
    
    // NEW: Added email field to fix the "cannot find symbol" error
    @Column(name = "employee_email")
    private String email;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "role")
    private String role;
    @Column(name = "joining_date") // This tells Java to look at the SQL column with underscores
    private String joiningDate;    // This is the variable name React will see

    // Default Constructor
    public Employee() {}

    // Updated Constructor
    public Employee(String name, String department, String status, String email, String username, String password, String role) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Getter and Setter for Email (Crucial for the Mailer)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getJoiningDate() {
    return joiningDate;
}

public void setJoiningDate(String joiningDate) {
    this.joiningDate = joiningDate;
}
}