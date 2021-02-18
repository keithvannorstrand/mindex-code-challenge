package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Compensation fetchEmployeeCompensation(String id) {
        Compensation comp = compensationRepository.findByEmployeeId(id);

        if (comp == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        comp.setEmployee(employeeService.read(id));

        return comp;
    }

    @Override
    public Compensation create(Compensation comp) {
        LOG.debug("Creating compensation [{}]", comp);

        comp.setCompensationId(UUID.randomUUID().toString());
        compensationRepository.insert(comp);

        return comp;
    }
}
