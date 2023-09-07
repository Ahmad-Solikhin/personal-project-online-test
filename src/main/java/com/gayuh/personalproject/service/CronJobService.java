package com.gayuh.personalproject.service;

import com.gayuh.personalproject.repository.TestHistoryRepository;
import com.gayuh.personalproject.repository.TestRepository;
import com.gayuh.personalproject.service.test.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronJobService {

    private final TestService testService;
    private final TestHistoryRepository testHistoryRepository;
    private final TestRepository testRepository;

    /**
     * Cronjob to check not finished test when the time is already expired
     * Check the started_at and accumulate second from all question in tests
     * If started_at + second is before current time, update the test histories to finished and calculate the score
     */
    @Scheduled(fixedRate = 3600000L)
    private void finishTestJob() {
        List<String> testHistoryIds = testHistoryRepository.findUnfinishedAndTimeIsExpired(LocalDateTime.now());

        log.info("There are {} job in finish test job", testHistoryIds.size());

        if (!testHistoryIds.isEmpty()) {
            AtomicInteger done = new AtomicInteger(0);
            testHistoryIds.forEach(testHistoryId -> {
                String testId = testRepository.findFirstIdByTestHistoryId(testHistoryId);
                testService.finishTest(testId);
                done.getAndIncrement();
            });

            log.info("Finish test job done {}/{}", testHistoryIds.size(), done.get());
        }
    }
}
