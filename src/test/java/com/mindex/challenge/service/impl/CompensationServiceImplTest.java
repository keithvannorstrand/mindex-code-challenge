package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String employeeUrl;
    private String employeeCompUrl;
    private String employeeCompIdUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeCompUrl = "http://localhost:" + port + "/employee/compensation";
        employeeCompIdUrl = "http://localhost:" + port + "/employee/compensation/{id}";
    }

    @Test
    public void testCreateRead() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        // Create a compensation for our new employee
        Compensation testComp = new Compensation();
        testComp.setEmployeeId(createdEmployee.getEmployeeId());
        testComp.setEffectiveDate("04-APR-20");
        testComp.setSalary(300000);

        Compensation createdComp = restTemplate.postForEntity(employeeCompUrl, testComp, Compensation.class).getBody();

        assertNotNull(createdComp.getCompensationId());
        assertCompensationEquivalence(testComp, createdComp);

        // Read check

        Compensation readComp = restTemplate.getForEntity(employeeCompIdUrl, Compensation.class, createdComp.getEmployeeId()).getBody();
        assertNotNull(readComp);
        assertEmployeeEquivalence(createdComp.getEmployee(), readComp.getEmployee());
        assertCompensationEquivalence(createdComp, readComp);

    }

    // this should just be in a common test utility file
    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getSalary(), actual.getSalary(), 0.01);
    }
}
