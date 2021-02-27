package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure readReportingStructure(String id) {
        Employee employee = employeeRepository.findByEmployeeId(id);
        LOG.info("ReportingStructure for [{}]", employee);

        HashSet<String> employeeIds = new HashSet();
        employeeIds.add(employee.getEmployeeId());
        int directReports = 0;
        if (employee.getDirectReports() != null && employee.getDirectReports().size() > 0) {
            directReports = createReportsSet(employeeIds, employee.getDirectReports()).size() - 1;
        }

        return new ReportingStructure(employee, directReports);
    }

    private HashSet<String> createReportsSet(HashSet<String> employeeIds, List<Employee> directReports) {
        for (int i=0; i<directReports.size(); i++) {
            Employee employee = read(directReports.get(i).getEmployeeId());
            LOG.info("createReportsSet: [{}]", employee);
            directReports.set(i, employee);
            employeeIds.add(employee.getEmployeeId());
            if (employee.getDirectReports() != null && employee.getDirectReports().size() > 0) {
                createReportsSet(employeeIds,employee.getDirectReports());
            }
        }
        LOG.info("employeeIds.size [{}]", employeeIds.size());
        return employeeIds;
    }

    @Override
    public Compensation readEmployeeCompensation(String id) {
        Compensation comp = compensationRepository.findByEmployeeId(id);

        if (comp == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        comp.setEmployee(read(id));

        return comp;
    }

}
