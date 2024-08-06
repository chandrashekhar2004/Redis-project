package com.redis.config.service;
import com.redis.config.Repository.EmployeeRepository;
import com.redis.config.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    public Employee saveEmployee(Employee employee) {
        if (employee.getId() == 0) {





            employee.setId(generateUniqueId());
        }
        return employeeRepository.save(employee);
    }
    public Employee createUser(Employee user) {
        return employeeRepository.save(user);
    }


    private long generateUniqueId() {
        return System.currentTimeMillis();
    }
    public Employee getEmployeeById(long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public void deleteEmployee(long id) {
        employeeRepository.deleteById(id);
    }
    public Iterable<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
