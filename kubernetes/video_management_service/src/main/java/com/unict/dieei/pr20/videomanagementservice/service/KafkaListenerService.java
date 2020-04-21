package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.exception.RestException;
import com.unict.dieei.pr20.videomanagementservice.model.log.LogInfo;
import com.unict.dieei.pr20.videomanagementservice.model.videoserver.Video;
import com.unict.dieei.pr20.videomanagementservice.repository.log.LogInfoRepository;
import com.unict.dieei.pr20.videomanagementservice.repository.videoserver.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
@Transactional
public class KafkaListenerService {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    LogInfoRepository logInfoRepository;

    @KafkaListener(topics = "${KAFKA_MAIN_TOPIC}")
    public void listen(String message) {
        System.out.println("Received message: " + message);

        // Timestamp arrival time
        long arrivalTime = System.currentTimeMillis();

        String[] messageParts = message.split("\\|");

        String status = messageParts[0];
        Integer videoId = Integer.parseInt(messageParts[1]);
        String requestId = messageParts[2];

        int statusCode;
        if(status.equals("processed")) {
            statusCode = 200;
            Video video = videoRepository.findById(videoId).get();
            video.setState("Available");
            videoRepository.save(video);
        }
        else if(status.equals("processingFailed")) {
            statusCode = 500;
            Video video = videoRepository.findById(videoId).get();
            video.setState("NotAvailable");
            videoRepository.save(video);

            // Delete pending files
            File dir = new File("/var/video/" + videoId);
            File[] files = dir.listFiles();
            if(files != null)
                for(File file : files)
                    if(!file.delete())
                        throw new RestException("Unable to delete video files", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            return;
        }
        // Timestamp finish time and evaluate response time
        long responseTime = System.currentTimeMillis() - arrivalTime;

        String api = "KAFKA";
        String componentName = System.getenv("HOST_NAME");
        int inputPayloadSize = message.length();
        int outputPayloadSize = 0;

        LogInfo logInfo = new LogInfo(api, inputPayloadSize, outputPayloadSize, responseTime, statusCode, requestId, componentName);
        logInfoRepository.save(logInfo);
    }
}
