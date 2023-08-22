package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.role.RoleService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<Object> getAllRole() {

        List<MasterResponse> responses = roleService.getAll();

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + responses.size(),
                HttpStatus.OK,
                responses
        );
    }

    @GetMapping(value = "{roleId}")
    public ResponseEntity<Object> getRoleById(
            @PathVariable(name = "roleId") Long roleId
    ) {

        MasterResponse response = roleService.getById(roleId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @PostMapping
    public ResponseEntity<Object> createRole(
            @RequestBody MasterRequest request
    ) {

        MasterResponse response = roleService.create(request);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED,
                response
        );

    }

    @PutMapping(value = "{roleId}")
    public ResponseEntity<Object> updateRole(
            @PathVariable(name = "roleId") Long roleId,
            @RequestBody MasterRequest request
    ) {

        MasterResponse response = roleService.update(request, roleId);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @DeleteMapping(value = "{roleId}")
    public ResponseEntity<Object> deleteRole(
            @PathVariable(name = "roleId") Long roleId
    ) {

        roleService.deleteById(roleId);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.NO_CONTENT
        );
    }
}
