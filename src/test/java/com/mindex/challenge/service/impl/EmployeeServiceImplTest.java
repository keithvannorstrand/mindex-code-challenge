package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String employeeCompUrl;
    private String employeeCompIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reportingStructure";
        employeeCompUrl = "http://localhost:" + port + "/compensation";
        employeeCompIdUrl = "http://localhost:" + port + "/employee/{id}/compensation";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    // tests a Reporting Structure with 1 long branch and leaves on each tier
    @Test
    public void testFetchReportingStructureLongBranch() {
        Employee employeeRoot = new Employee("Dalinar", "Kholin", "High King", "Bond Smith");
        Employee employeeTier1Num1 = new Employee("Kaladin", "Stormblessed", "High Marshall", "Windrunner");
        Employee employeeTier1Num2 = new Employee("Shallan", "Devar", "High Lady", "Lightweaver");
        Employee employeeTier1Num3 = new Employee("Jasnah", "Kholin", "Queen", "Elsecaller");
        Employee employeeTier2Num1 = new Employee("Teft", "Bridge Four", "Sergeant", "Windrunner");
        Employee employeeTier2Num2 = new Employee("Adolin", "Kholin", "High Lord", "None");
        Employee employeeTier2Num3 = new Employee("Veil", "N/A", "Thief", "Illusion");
        Employee employeeTier3Num1 = new Employee("Szeth", "Son-Neturo", "Assassin", "Skybreaker");
        Employee employeeTier4Num1 = new Employee("Sylphrena", "Spren", "Ancient Daughter", "Honor");
        Employee employeeTier4Num2 = new Employee("Storm", "Father", "Storm", "Splinter");

        int totalReports = 0;
        // tier 4
        employeeTier4Num1 = restTemplate.postForEntity(employeeUrl, employeeTier4Num1, Employee.class).getBody();
        assertNotNull(employeeTier4Num1.getEmployeeId());
        employeeTier4Num2 = restTemplate.postForEntity(employeeUrl, employeeTier4Num2, Employee.class).getBody();
        assertNotNull(employeeTier4Num2.getEmployeeId());
        List<Employee> tier4 = new ArrayList();
        tier4.add(employeeTier4Num1);
        tier4.add(employeeTier4Num2);

        // tier 3
        employeeTier3Num1.setDirectReports(tier4);
        employeeTier3Num1 = restTemplate.postForEntity(employeeUrl, employeeTier3Num1, Employee.class).getBody();
        assertNotNull(employeeTier3Num1.getEmployeeId());
        assertNotNull(employeeTier3Num1.getDirectReports());
        assertEquals(tier4.size(), employeeTier3Num1.getDirectReports().size());
        ReportingStructure reportingStructure3 = restTemplate
                .getForEntity(
                        reportingStructureUrl,
                        ReportingStructure.class,
                        employeeTier3Num1.getEmployeeId())
                .getBody();
        assertEmployeeEquivalence(employeeTier3Num1, reportingStructure3.getEmployee());
        assertEquals(tier4.size(), reportingStructure3.getNumberOfReports());
        List<Employee> tier3 = new ArrayList();
        tier3.add(employeeTier3Num1);

        //tier 2
        employeeTier2Num1.setDirectReports(tier3);
        employeeTier2Num1 = restTemplate.postForEntity(employeeUrl, employeeTier2Num1, Employee.class).getBody();
        assertNotNull(employeeTier2Num1.getEmployeeId());
        assertNotNull(employeeTier2Num1.getDirectReports());
        assertEquals(tier3.size(), employeeTier2Num1.getDirectReports().size());
        ReportingStructure reportingStructure2 = restTemplate
                .getForEntity(
                        reportingStructureUrl,
                        ReportingStructure.class,
                        employeeTier2Num1.getEmployeeId())
                .getBody();
        assertEmployeeEquivalence(employeeTier2Num1, reportingStructure2.getEmployee());
        assertEquals(tier4.size() + tier3.size(), reportingStructure2.getNumberOfReports());
        employeeTier2Num2 = restTemplate.postForEntity(employeeUrl, employeeTier2Num2, Employee.class).getBody();
        assertNotNull(employeeTier2Num2.getEmployeeId());
        employeeTier2Num3 = restTemplate.postForEntity(employeeUrl, employeeTier2Num3, Employee.class).getBody();
        assertNotNull(employeeTier2Num3.getEmployeeId());
        List<Employee> tier2 = new ArrayList();
        tier2.add(employeeTier2Num1);
        tier2.add(employeeTier2Num2);
        tier2.add(employeeTier2Num3);

        // tier 1
        employeeTier1Num1.setDirectReports(tier2);
        employeeTier1Num1 = restTemplate.postForEntity(employeeUrl, employeeTier1Num1, Employee.class).getBody();
        assertNotNull(employeeTier1Num1.getEmployeeId());
        assertNotNull(employeeTier1Num1.getDirectReports());
        assertEquals(tier2.size(), employeeTier1Num1.getDirectReports().size());
        ReportingStructure reportingStructure1 = restTemplate
                .getForEntity(
                        reportingStructureUrl,
                        ReportingStructure.class,
                        employeeTier1Num1.getEmployeeId())
                .getBody();
        assertEmployeeEquivalence(employeeTier1Num1, reportingStructure1.getEmployee());
        assertEquals(tier4.size() + tier3.size() + tier2.size(), reportingStructure1.getNumberOfReports());
        employeeTier1Num2 = restTemplate.postForEntity(employeeUrl, employeeTier1Num2, Employee.class).getBody();
        assertNotNull(employeeTier1Num2.getEmployeeId());
        employeeTier1Num3 = restTemplate.postForEntity(employeeUrl, employeeTier1Num3, Employee.class).getBody();
        assertNotNull(employeeTier1Num3.getEmployeeId());
        List<Employee> tier1 = new ArrayList();
        tier1.add(employeeTier1Num1);
        tier1.add(employeeTier1Num2);
        tier1.add(employeeTier1Num3);

        // root
        employeeRoot.setDirectReports(tier1);
        employeeRoot = restTemplate.postForEntity(employeeUrl, employeeRoot, Employee.class).getBody();
        assertNotNull(employeeRoot.getEmployeeId());
        assertNotNull(employeeRoot.getDirectReports());
        assertEquals(tier1.size(), employeeRoot.getDirectReports().size());

        ReportingStructure reportingStructure = restTemplate
                .getForEntity(
                        reportingStructureUrl,
                        ReportingStructure.class,
                        employeeRoot.getEmployeeId())
                .getBody();

        assertEmployeeEquivalence(employeeRoot, reportingStructure.getEmployee());
        assertEquals(tier4.size() + tier3.size() + tier2.size() + tier1.size(), reportingStructure.getNumberOfReports());
    }

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
