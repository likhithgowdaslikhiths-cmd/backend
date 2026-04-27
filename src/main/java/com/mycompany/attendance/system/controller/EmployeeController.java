package com.mycompany.attendance.system.controller;

import com.mycompany.attendance.system.Employee;
import com.mycompany.attendance.system.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private JavaMailSender mailSender; // This fixes the 'mailSender' error

    @GetMapping
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }
    @DeleteMapping("/{id}")
public void deleteEmployee(@PathVariable Long id) {
    repository.deleteById(id);
}

    @PostMapping
    public Employee addEmployee(@RequestBody Employee employee) {
        employee.setJoiningDate(java.time.LocalDate.now().toString());
        // 1. Generate the details
        String today = java.time.LocalDate.now().toString(); 
        
        Employee processed = generateAutomatedDetails(employee);
        
        // 2. Save to DB
        Employee saved = repository.save(processed);
        
        // 3. Send Email
        sendWelcomeEmail(saved);
        
        return saved;
    }

    private Employee generateAutomatedDetails(Employee emp) {
    // 1. Clean the name and split it
    String fullName = emp.getName().trim().toLowerCase();
    String[] nameParts = fullName.split("\\s+");
    String firstName = nameParts[0];

    // 2. Generate Username: christa_f (if last name exists) or just christa
    String generatedUsername = (nameParts.length > 1) 
        ? firstName + "_" + nameParts[nameParts.length - 1].charAt(0) 
        : firstName;

    // 3. Generate Password: Christa@123!
    java.util.Random rand = new java.util.Random();
    int randomNum = 100 + rand.nextInt(900); // 3-digit random number
    String capFirstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
    String generatedPassword = capFirstName + "@" + randomNum + "!";

    // 4. Set the fields back to the employee object
    emp.setUsername(generatedUsername);
    emp.setPassword(generatedPassword);
    emp.setRole("EMPLOYEE");
    emp.setStatus("ACTIVE");

    return emp;
}

    private void sendWelcomeEmail(Employee emp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@gmail.com"); 
            message.setTo(emp.getEmail()); // It will use the email from the React form
            message.setSubject("Welcome to Worexa Technologies!");
           String emailBody = "Dear " + emp.getName() + ",\n\n" +
                           "Welcome to the team! Your employee portal account has been created.\n\n" +
                           "Your Login Credentials:\n" +
                           "--------------------------\n" +
                           "Username: " + emp.getUsername() + "\n" +
                           "Password: " + emp.getPassword() + "\n" +
                           "--------------------------\n\n" +
                           "Please log in at your earliest convenience to complete your profile.\n\n" +
                           "Best Regards,\n" +
                           "HR Management Team";
            message.setText(emailBody);
            mailSender.send(message);
            System.out.println("Email sent!");
        } catch (Exception e) {
            System.err.println("Mail Error: " + e.getMessage());
        }
    }
}