package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.exception.RestException;
import com.unict.dieei.pr20.videomanagementservice.model.log.LogInfo;
import com.unict.dieei.pr20.videomanagementservice.model.videoserver.Video;
import com.unict.dieei.pr20.videomanagementservice.repository.log.LogInfoRepository;
import com.unict.dieei.pr20.videomanagementservice.repository.videoserver.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Transactional
public class KafkaListenerService {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    LogInfoRepository logInfoRepository;

    @KafkaListener(topics="${KAFKA_MAIN_TOPIC}")
    public void listen(String message) {
        System.out.println("Received message: " + message);

        // Timestamp arrival time
        long arrivalTime = System.currentTimeMillis();

        String[] messageParts = message.split("\\|");

        String status = messageParts[0];
        Integer videoId = Integer.parseInt(messageParts[1]);
        Long requestId = Long.parseLong(messageParts[2]);

        if(status.equals("processed")) {
            Video video = videoRepository.findById(videoId).get();
            video.setState("Available");
            videoRepository.save(video);
        }
        else if(status.equals("processingFailed")) {
            Video video = videoRepository.findById(videoId).get();
            video.setState("NotAvailable");
            videoRepository.save(video);
            try {
                Files.delete(Paths.get("/var/video/" + videoId));
            } catch (IOException e) {
                throw new RestException("Unable to delete video files", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        // Timestamp finish time and evaluate response time
        long responseTime = System.currentTimeMillis() - arrivalTime;

        // Update response time in logs
        String componentName = System.getenv("HOST_NAME");
        LogInfo logInfo = logInfoRepository.findByRequestIdAndComponentName(requestId, componentName).get();
        Long oldResponseTime = logInfo.getResponseTime();
        logInfo.setResponseTime(oldResponseTime + responseTime);
        logInfoRepository.save(logInfo);
    }
}
