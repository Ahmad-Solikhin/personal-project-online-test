package com.gayuh.personalproject.service.topic;

import com.gayuh.personalproject.dto.TopicRequest;
import com.gayuh.personalproject.dto.TopicResponse;

import java.util.List;

public interface TopicService {
    List<TopicResponse> getAllTopic();

    TopicResponse findById(Long topicId);

    TopicResponse create(TopicRequest request);

    TopicResponse update(TopicRequest request, Long topicId);

    void deleteById(Long topicId);
}
