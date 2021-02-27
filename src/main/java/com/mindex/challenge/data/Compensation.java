package com.mindex.challenge.data;

import java.util.Date;

public class Compensation {
    private String compensationId;
    private String employeeId;
    private Employee employee;
    private double salary;
    private Date effectiveDate;

    public Compensation () { }

    public void setCompensationId(String compensationId) { this.compensationId = compensationId; }

    public String getCompensationId() { return this.compensationId; }

    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeId() { return this.employeeId; }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }
}
