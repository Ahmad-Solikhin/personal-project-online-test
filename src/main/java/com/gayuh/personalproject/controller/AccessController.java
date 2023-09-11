package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.access.AccessService;
import com.gayuh.personalproject.util.CustomResponse;
import com.gayuh.personalproject.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/accesses")
@RequiredArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @GetMapping
    public ResponseEntity<Object> getAllAccess() {
        List<MasterResponse> responses = accessService.getAll();

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + responses.size(),
                HttpStatus.OK,
                responses
        );
    }

    @GetMapping(value = "{accessId}")
    public ResponseEntity<Object> getAccessById(
            @PathVariable(name = "accessId") Long accessId
    ) {
        MasterResponse response = accessService.getById(accessId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @PostMapping
    public ResponseEntity<Object> createAccess(
            @RequestBody MasterRequest request,
            UserObject userObject
    ) {
        FilterUtil.filterAdmin(userObject);

        MasterResponse response = accessService.create(request);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED,
                response
        );
    }

    @PutMapping(value = "{accessId}")
    public ResponseEntity<Object> updateAccess(
            @PathVariable(name = "accessId") Long accessId,
            @RequestBody MasterRequest request,
            UserObject userObject
    ) {
        FilterUtil.filterAdmin(userObject);

        MasterResponse response = accessService.update(request, accessId);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @DeleteMapping(value = "{accessId}")
    public ResponseEntity<Object> deleteAccess(
            @PathVariable(name = "accessId") Long accessId,
            UserObject userObject
    ) {
        FilterUtil.filterAdmin(userObject);

        accessService.deleteById(accessId);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }
}
