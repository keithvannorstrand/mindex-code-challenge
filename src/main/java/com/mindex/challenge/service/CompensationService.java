package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

public interface CompensationService {
    Compensation fetchEmployeeCompensation(String id);
    Compensation create(Compensation comp);
}
