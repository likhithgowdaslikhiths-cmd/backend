package com.mycompany.attendance.system.controller;

import com.mycompany.attendance.system.AttendanceRecord;
import com.mycompany.attendance.system.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // ✅ MARK ATTENDANCE
    @PostMapping("/mark")
public AttendanceRecord markAttendance(@RequestBody AttendanceRecord record) {

    LocalDate today = LocalDate.now();

    // ✅ 1. Prevent duplicate attendance
    boolean alreadyMarked = attendanceRepository.findAll().stream()
        .anyMatch(r -> r.getEmployeeId().equals(record.getEmployeeId()) &&
                       r.getTimestamp().toLocalDate().equals(today));

    if (alreadyMarked) {
        throw new RuntimeException("Attendance already marked for today");
    }

    // ✅ 2. Get current time
    java.time.LocalTime now = java.time.LocalTime.now();

    // ✅ 3. Decide status
    if (now.isAfter(java.time.LocalTime.of(10, 0))) {
        record.setStatus("LATE");
    } else {
        record.setStatus("PRESENT");
    }

    // ✅ 4. Save
    return attendanceRepository.save(record);
}

    // ✅ GET ALL (USED BY ADMIN PAGE)
    @GetMapping("/all")
    public List<AttendanceRecord> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public void deleteAttendance(@PathVariable Long id) {
        attendanceRepository.deleteById(id);
    }

    // ✅ EXPORT EXCEL
   @GetMapping("/export")
public void exportToExcel(HttpServletResponse response, @RequestParam(required = false) String month) throws IOException {

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    
    // Customize filename based on selection
    String fileName = (month != null && !month.isEmpty()) ? "attendance_" + month + ".xlsx" : "attendance_full.xlsx";
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

    // 1. Get all records first
    List<AttendanceRecord> records = attendanceRepository.findAll();

    // 2. Apply filtering logic if a month is provided (Format expected: "YYYY-MM")
    if (month != null && !month.isEmpty()) {
        records = records.stream()
            .filter(r -> r.getTimestamp() != null && r.getTimestamp().toString().startsWith(month))
            .collect(Collectors.toList());
    }

    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Attendance");

        Row header = sheet.createRow(0);
        String[] cols = {"ID", "Employee ID", "Status", "Timestamp"};

        // Create Header Style
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(style);
        }

        int rowNum = 1;
        for (AttendanceRecord r : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getId());
            row.createCell(1).setCellValue(r.getEmployeeId());
            row.createCell(2).setCellValue(r.getStatus());
            row.createCell(3).setCellValue(r.getTimestamp().toString());
        }

        workbook.write(response.getOutputStream());
    }
}

    // ✅ ANALYTICS (YOUR NEW FEATURE)
   @GetMapping("/analytics")
public Map<String, Object> getAnalytics() {

    List<AttendanceRecord> records = attendanceRepository.findAll();
    LocalDate today = LocalDate.now();

    long presentToday = records.stream()
            .filter(r -> r.getTimestamp().toLocalDate().equals(today)
                    && "PRESENT".equals(r.getStatus()))
            .count();

    long lateToday = records.stream()
            .filter(r -> r.getTimestamp().toLocalDate().equals(today)
                    && "LATE".equals(r.getStatus()))
            .count();

    long totalEmployees = records.stream()
            .map(AttendanceRecord::getEmployeeId)
            .distinct()
            .count();

    double percentage = totalEmployees == 0 ? 0 :
            ((presentToday + lateToday) * 100.0) / totalEmployees;

    Map<String, Object> data = new HashMap<>();
    data.put("totalEmployees", totalEmployees);
    data.put("presentToday", presentToday);
    data.put("lateToday", lateToday); // ✅ NEW
    data.put("attendancePercentage", percentage);

    return data;
}
}