package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompensationController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation comp){
        return employeeService.createCompensation(comp);
    }
}
