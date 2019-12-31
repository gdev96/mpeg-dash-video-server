package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.entity.User;
import com.unict.dieei.pr20.videomanagementservice.entity.Video;
import com.unict.dieei.pr20.videomanagementservice.exception.*;
import com.unict.dieei.pr20.videomanagementservice.repository.UserRepository;
import com.unict.dieei.pr20.videomanagementservice.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
public class VideoService {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    UserRepository userRepository;

    public Video addVideo(Authentication auth, Video video) {
        Optional<User> optionalUser = userRepository.findByEmail(auth.getName());
        User user = optionalUser.get();
        video.setUser(user);
        video.setState("Pending");
        return videoRepository.save(video);
    }

    public Object[] uploadVideo(Authentication auth, Integer id, String xRequestId, MultipartFile file) {
        Optional<User> optionalUser = userRepository.findByEmail(auth.getName());
        User user = optionalUser.get();
        Optional<Video> optionalVideo = videoRepository.findById(id);
        if(!optionalVideo.isPresent()) {
            throw new VideoInfoNotFoundException();
        }
        Video video = optionalVideo.get();
        if(!video.getUser().equals(user)) {
            throw new UserMismatchException();
        }

        //SAVE VIDEO FILE TO DISK
        if(file.isEmpty()) {
            throw new EmptyFileException();
        }
        try(InputStream inputStream = file.getInputStream()) {
            Path videoPath = Files.createDirectories(Paths.get("/var/video/" + id + "/video.mp4"));
            Files.copy(inputStream, videoPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileNotSavedException();
        }

        //SEND POST REQUEST TO VIDEO PROCESSING SERVICE
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-REQUEST-ID", xRequestId);
        String body = "{\"videoId\":" + id + "}";
        URI url = URI.create("http://video_processing_service_1:5000/videos/process");
        RequestEntity<String> request = new RequestEntity<>(body, headers, HttpMethod.POST, url);
        long sendTime = System.currentTimeMillis();

        //SEND REQUEST
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        long communicationDelay = System.currentTimeMillis() - sendTime;
        System.out.println(response);

        video.setState("Uploaded");
        Video savedVideo = videoRepository.save(video);

        return new Object[]{savedVideo, communicationDelay};
    }

    public Iterable<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public void checkVideoExistence(Integer id) {
        if(!videoRepository.existsById(id)) {
            throw new VideoNotFoundException();
        }
    }
}
