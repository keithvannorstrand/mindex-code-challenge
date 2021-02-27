package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

public interface CompensationService {
    Compensation createCompensation(Compensation comp);
    Compensation readCompensationByEmployeeId(String id);
}
