package com.redis.config.controller;

import com.redis.config.entity.Employee;
import com.redis.config.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/bulk")
    public ResponseEntity<ResponsePayload> createEmployees(@RequestBody Employee employee) {
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePayload("Invalid employee data.", 0, null));
        }

        List<Employee> createdEmployees = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            Employee newEmployee = new Employee();
            newEmployee.setName(employee.getName());
            newEmployee.setDepartment(employee.getDepartment());
            createdEmployees.add(employeeService.saveEmployee(newEmployee));
        }

        ResponsePayload responsePayload = new ResponsePayload("Successfully created employees.", createdEmployees.size(), createdEmployees);
        return ResponseEntity.status(HttpStatus.CREATED).body(responsePayload);
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable long id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployees(
            @RequestHeader(value = "Id", required = false) Optional<Long> id,
            @RequestHeader(value = "Name", required = false) Optional<String> name,
            @RequestHeader(value = "Department", required = false) Optional<String> department) {

        Iterable<Employee> employees = employeeService.getAllEmployees();

        List<Employee> filteredEmployees = StreamSupport.stream(employees.spliterator(), false)
                .filter(employee -> id.map(e -> e.equals(employee.getId())).orElse(true))
                .filter(employee -> name.map(e -> e != null && e.equalsIgnoreCase(employee.getName())).orElse(true))
                .filter(employee -> department.map(e -> e != null && e.equalsIgnoreCase(employee.getDepartment())).orElse(true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredEmployees);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable long id, @RequestBody Employee employee) {
        if (employeeService.getEmployeeById(id) != null) {
            employee.setId(id);
            return employeeService.saveEmployee(employee);
        } else {
            throw new RuntimeException("Employee not found with id: " + id);
        }
    }

    @DeleteMapping("/{id}")




    public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee with ID " + id + " has been successfully deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found with ID: " + id);
        }
    }



    private static class ResponsePayload {
        private String message;
        private int count;
        private List<Employee> employees;

        public ResponsePayload(String message, int count, List<Employee> employees) {
            this.message = message;
            this.count = count;
            this.employees = employees;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public List<Employee> getEmployees() { return employees; }
        public void setEmployees(List<Employee> employees) { this.employees = employees; }
    }


}
