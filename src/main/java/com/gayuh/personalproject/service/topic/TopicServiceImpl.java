package com.gayuh.personalproject.service.topic;

import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.entity.Topic;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.repository.TopicRepository;
import com.gayuh.personalproject.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl extends ParentService implements TopicService {
    private final TopicRepository topicRepository;

    @Override
    public List<MasterResponse> getAll() {
        return topicRepository.findAll()
                .stream().map(topic -> new MasterResponse(topic.getId(), topic.getName())).toList();
    }

    @Override
    public MasterResponse getById(Long topicId) {
        var topic = findById(topicId);
        return new MasterResponse(topic.getId(), topic.getName());
    }

    @Override
    public MasterResponse create(MasterRequest request) {
        validationService.validate(request);
        Topic topic = topicRepository.save(Topic.builder()
                .name(request.name())
                .build());

        return new MasterResponse(topic.getId(), topic.getName());
    }

    @Override
    public MasterResponse update(MasterRequest request, Long topicId) {
        validationService.validate(request);
        Topic topic = findById(topicId);
        topic.setName(request.name());
        topicRepository.save(topic);

        return new MasterResponse(topic.getId(), topic.getName());
    }

    @Override
    public void deleteById(Long topicId) {
        findById(topicId);
        topicRepository.deleteById(topicId);
    }

    private Topic findById(Long topicId) {
        return topicRepository.findById(topicId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }
}
