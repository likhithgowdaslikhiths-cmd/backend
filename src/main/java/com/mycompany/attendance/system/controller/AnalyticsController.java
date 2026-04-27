package com.mycompany.attendance.system.controller;

import com.mycompany.attendance.system.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    // ✅ DAILY SUMMARY (THIS IS WHAT FRONTEND NEEDS)
   // ✅ UPDATED SUMMARY LOGIC
@GetMapping("/summary")
public Map<String, Object> getTodaySummary() {
    Map<String, Object> data = new HashMap<>();
    try {
        List<AttendanceRecord> allRecords = attendanceRepo.findAll();
        LocalDate today = LocalDate.now();

        // 1. Filter for today's records once
        List<AttendanceRecord> todayRecords = allRecords.stream()
                .filter(a -> a.getTimestamp() != null &&
                             a.getTimestamp().toLocalDate().equals(today))
                .toList();

        long totalEmployees = employeeRepo.count();

        // 2. Count Total Present (Anyone who clocked in, regardless of LATE/PRESENT status)
        long presentToday = todayRecords.stream()
                .map(AttendanceRecord::getEmployeeId)
                .count();

        // 3. Count Late specifically (USING equalsIgnoreCase)
        long lateToday = todayRecords.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("LATE"))
                .map(AttendanceRecord::getEmployeeId)
                .distinct()
                .count();

        data.put("totalEmployees", totalEmployees);
        data.put("presentToday", presentToday);
        data.put("lateToday", lateToday);

    } catch (Exception e) {
        e.printStackTrace();
        data.put("totalEmployees", 0);
        data.put("presentToday", 0);
        data.put("lateToday", 0);
    }
    return data;

}
    // ✅ WEEKLY STATS (KEEP THIS)
   @GetMapping("/analytics")
public Map<String, Object> getAnalytics() {

    Map<String, Object> data = new HashMap<>();

    try {
        List<AttendanceRecord> records = attendanceRepo.findAll();
        LocalDate today = LocalDate.now();

        List<AttendanceRecord> todayRecords = records.stream()
                .filter(r -> r.getTimestamp() != null &&
                             r.getTimestamp().toLocalDate().equals(today))
                .toList();

        long presentToday = todayRecords.stream()
        .map(AttendanceRecord::getEmployeeId)
        .distinct()
        .count();

long lateToday = todayRecords.stream()
        .filter(r -> r.getStatus() != null && r.getStatus().equalsIgnoreCase("LATE"))
        .map(AttendanceRecord::getEmployeeId)
        .distinct()
        .count();

        

        long totalEmployees = employeeRepo.count();

        double percentage = totalEmployees == 0 ? 0 :
                ((presentToday + lateToday) * 100.0) / totalEmployees;

        data.put("totalEmployees", totalEmployees);
        data.put("presentToday", presentToday);
        data.put("lateToday", lateToday);
        data.put("attendancePercentage", percentage);

    } catch (Exception e) {
        e.printStackTrace();

        // RETURN SAFE DATA instead of crashing
        data.put("totalEmployees", 0);
        data.put("presentToday", 0);
        data.put("lateToday", 0);
        data.put("attendancePercentage", 0);
    }

    return data;
}
@GetMapping("/weekly")
public List<Map<String, Object>> getWeeklyStats() {

    List<Map<String, Object>> result = new ArrayList<>();

    try {
        List<AttendanceRecord> allRecords = attendanceRepo.findAll();
        long totalEmployees = employeeRepo.count();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            long present = allRecords.stream()
                    .filter(a -> a.getTimestamp() != null &&
                                 a.getTimestamp().toLocalDate().equals(date))
                    .map(AttendanceRecord::getEmployeeId)
                    .distinct()
                    .count();

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("name", date.getDayOfWeek().toString().substring(0, 3)); // MON
            dayData.put("raw", present);
            dayData.put("percentage", totalEmployees == 0 ? 0 :
                    Math.round((present * 100.0) / totalEmployees));

            result.add(dayData);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return result;
}
}