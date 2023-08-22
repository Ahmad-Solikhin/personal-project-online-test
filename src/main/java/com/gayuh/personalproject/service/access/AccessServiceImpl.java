package com.gayuh.personalproject.service.access;

import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.entity.Access;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.repository.AccessRepository;
import com.gayuh.personalproject.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessServiceImpl extends ParentService implements AccessService {

    private final AccessRepository accessRepository;

    @Override
    public List<MasterResponse> getAll() {
        return accessRepository.findAll()
                .stream().map(access -> new MasterResponse(access.getId(), access.getName())).toList();
    }

    @Override
    public MasterResponse getById(Long accessId) {
        var access = findById(accessId);
        return new MasterResponse(access.getId(), access.getName());
    }

    @Override
    public MasterResponse create(MasterRequest request) {
        validationService.validate(request);
        Access access = accessRepository.save(Access.builder()
                .name(request.name())
                .build());

        return new MasterResponse(access.getId(), access.getName());
    }

    @Override
    public MasterResponse update(MasterRequest request, Long accessId) {
        validationService.validate(request);
        Access access = findById(accessId);
        access.setName(request.name());
        accessRepository.save(access);

        return new MasterResponse(access.getId(), access.getName());
    }

    @Override
    public void deleteById(Long accessId) {
        findById(accessId);
        accessRepository.deleteById(accessId);
    }

    private Access findById(Long accessId) {
        return accessRepository.findById(accessId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }
}
