package com.mycompany.attendance.system.controller;

import com.mycompany.attendance.system.Agenda;
import com.mycompany.attendance.system.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/agenda")
@CrossOrigin(origins = "http://localhost:5173") // Connects to your React app
public class AgendaController {

    @Autowired
    private AgendaRepository agendaRepository;

    @GetMapping
    public List<Agenda> getAllTasks() {
        return agendaRepository.findAll();
    }

    @PostMapping
    public Agenda addTask(@RequestBody Agenda task) {
        return agendaRepository.save(task);
    }

   @DeleteMapping("/{id}")
public ResponseEntity<?> deleteTask(@PathVariable Long id) {
    try {
        agendaRepository.deleteById(id); // This actually hits MySQL
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.status(500).build();
    }
}
}