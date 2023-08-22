package com.gayuh.personalproject.service;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class ParentService {
    protected ValidationService validationService;

    @Autowired
    protected void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }
}
