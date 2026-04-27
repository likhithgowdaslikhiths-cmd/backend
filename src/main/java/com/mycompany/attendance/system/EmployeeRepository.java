package com.mycompany.attendance.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Leave this empty! Spring fills it in at runtime.
}