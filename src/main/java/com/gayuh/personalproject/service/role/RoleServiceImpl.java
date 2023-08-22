package com.gayuh.personalproject.service.role;

import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.entity.Role;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.repository.RoleRepository;
import com.gayuh.personalproject.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ParentService implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<MasterResponse> getAll() {
        return roleRepository.findAll()
                .stream().map(role -> new MasterResponse(role.getId(), role.getName())).toList();
    }

    @Override
    public MasterResponse getById(Long roleId) {
        var role = findById(roleId);
        return new MasterResponse(role.getId(), role.getName());
    }

    @Override
    public MasterResponse create(MasterRequest request) {
        validationService.validate(request);
        Role role = roleRepository.save(Role.builder()
                .name(request.name())
                .build());

        return new MasterResponse(role.getId(), role.getName());
    }

    @Override
    public MasterResponse update(MasterRequest request, Long roleId) {
        validationService.validate(request);
        Role role = findById(roleId);
        role.setName(request.name());
        roleRepository.save(role);

        return new MasterResponse(role.getId(), role.getName());
    }

    @Override
    public void deleteById(Long roleId) {
        findById(roleId);
        roleRepository.deleteById(roleId);
    }

    private Role findById(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }
}
