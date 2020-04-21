package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.exception.RestException;
import com.unict.dieei.pr20.videomanagementservice.model.videoserver.User;
import com.unict.dieei.pr20.videomanagementservice.model.videoserver.Video;
import com.unict.dieei.pr20.videomanagementservice.repository.videoserver.UserRepository;
import com.unict.dieei.pr20.videomanagementservice.repository.videoserver.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@Transactional
public class VideoService {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${KAFKA_MAIN_TOPIC}")
    private String mainTopic;

    public Video addVideo(Authentication auth, Video video) {
        Optional<User> optionalUser = userRepository.findByEmail(auth.getName());
        User user = optionalUser.get();
        video.setUser(user);
        video.setState("WaitingUpload");
        return videoRepository.save(video);
    }

    public Video uploadVideo(Authentication auth, Integer id, String requestId, MultipartFile file) {
        Optional<User> optionalUser = userRepository.findByEmail(auth.getName());
        User user = optionalUser.get();
        Optional<Video> optionalVideo = videoRepository.findById(id);
        if(!optionalVideo.isPresent()) {
            throw new RestException("Video infos not found", HttpStatus.BAD_REQUEST);
        }
        Video video = optionalVideo.get();
        if(!video.getUser().equals(user)) {
            throw new RestException("Requested resource belongs to another user", HttpStatus.FORBIDDEN);
        }
        if(!video.getState().equals("WaitingUpload")) {
            throw new RestException("Video already uploaded", HttpStatus.BAD_REQUEST);
        }

        // Save video file to disk
        if(file.isEmpty()) {
            throw new RestException("Video file is empty", HttpStatus.BAD_REQUEST);
        }
        try(InputStream inputStream = file.getInputStream()) {
            Path videoPath = Files.createDirectories(Paths.get("/var/video/" + id + "/video.mp4"));
            Files.copy(inputStream, videoPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RestException("Video file already exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Send message to Kafka
        kafkaTemplate.send(mainTopic, "process|" + video.getId() + "|" + requestId);

        video.setState("Uploaded");

        return videoRepository.save(video);
    }

    public Iterable<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public void isVideoAvailable(Integer id) {
        Optional<Video> optionalVideo = videoRepository.findById(id);
        if(!optionalVideo.isPresent()) {
            throw new RestException("Requested video not found", HttpStatus.NOT_FOUND);
        }
        Video video = optionalVideo.get();
        if(!video.getState().equals("Available")) {
            throw new RestException("Video not available", HttpStatus.NOT_FOUND);
        }
    }
}
