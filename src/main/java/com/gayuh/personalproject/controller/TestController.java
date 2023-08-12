package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.TestAddRequest;
import com.gayuh.personalproject.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/tests")
@RequiredArgsConstructor
public class TestController {

    private final ValidationService validationService;

    @PostMapping
    public ResponseEntity<TestAddRequest> testAdd(
            @RequestBody TestAddRequest request
    ){
        validationService.validate(request);

        return ResponseEntity.ok(request);
    }

    @PostMapping("2")
    public ResponseEntity<TestAddRequest> testAdd2(
            @RequestBody @Valid TestAddRequest request
    ){
        return ResponseEntity.ok(request);
    }
}
