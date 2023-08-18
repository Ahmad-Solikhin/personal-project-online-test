package com.gayuh.personalproject.service.topic;

import com.gayuh.personalproject.dto.TopicRequest;
import com.gayuh.personalproject.dto.TopicResponse;
import com.gayuh.personalproject.entity.Topic;
import com.gayuh.personalproject.repository.TopicRepository;
import com.gayuh.personalproject.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final ValidationService validationService;

    @Override
    public List<TopicResponse> getAllTopic() {
        return topicRepository.findAll()
                .stream().map(topic -> new TopicResponse(topic.getId(), topic.getName())).toList();
    }

    @Override
    public TopicResponse findById(Long topicId) {
        var topic = privateFindById(topicId);
        return new TopicResponse(topic.getId(), topic.getName());
    }

    @Override
    public TopicResponse create(TopicRequest request) {
        validationService.validate(request);
        Topic topic = topicRepository.save(Topic.builder()
                .name(request.name())
                .build());

        return new TopicResponse(topic.getId(), topic.getName());
    }

    @Override
    public TopicResponse update(TopicRequest request, Long topicId) {
        validationService.validate(request);
        Topic topic = privateFindById(topicId);
        topic.setName(request.name());
        topic = topicRepository.save(topic);

        return new TopicResponse(topic.getId(), topic.getName());
    }

    @Override
    public void deleteById(Long topicId) {
        Topic topic = privateFindById(topicId);
        topicRepository.delete(topic);
    }

    private Topic privateFindById(Long topicId) {
        return topicRepository.findById(topicId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data not found")
        );
    }
}
