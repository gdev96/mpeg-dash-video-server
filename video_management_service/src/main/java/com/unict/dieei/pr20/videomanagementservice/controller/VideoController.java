package com.unict.dieei.pr20.videomanagementservice.controller;

import com.unict.dieei.pr20.videomanagementservice.entity.User;
import com.unict.dieei.pr20.videomanagementservice.entity.Video;
import com.unict.dieei.pr20.videomanagementservice.service.UserService;
import com.unict.dieei.pr20.videomanagementservice.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@RestController
@RequestMapping(path = "/videos")
public class VideoController {

    @Autowired
    VideoService videoService;

    @Autowired
    UserService userService;

    @PostMapping
    public @ResponseBody ResponseEntity<Video> addVideo(Authentication auth, @RequestBody Video video) {
        if(auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<User> optionalUser = userService.findUserByEmail(auth.getName());
        if(!optionalUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();
        video.setUser(user);
        video.setState("pending");
        Video savedVideo = videoService.addVideo(video);
        return new ResponseEntity<>(savedVideo, HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}")
    public @ResponseBody ResponseEntity<Void> uploadVideo(Authentication auth, @PathVariable Integer id,
                                                          @RequestParam("file") MultipartFile file) {
        //CHECKS
        if(auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<User> optionalUser = userService.findUserByEmail(auth.getName());
        if(!optionalUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();
        Optional<Video> optionalVideo = videoService.findVideoById(id);
        if(!optionalVideo.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Video video = optionalVideo.get();
        if(!video.getUser().equals(user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        //SAVE VIDEO FILE TO DISK
        if(file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try(InputStream inputStream = file.getInputStream()) {
            Path videoPath = Files.createDirectories(Paths.get("/var/video/" + id + "/video.mp4"));
            Files.copy(inputStream, videoPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //SEND POST REQUEST TO VIDEO PROCESSING SERVICE
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"videoId\":" + id + "}";
        RequestEntity<String> request = new RequestEntity<>(
                body,
                headers,
                HttpMethod.POST,
                URI.create("http://video_processing_service_1:5000/videos/process")
        );
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        System.out.println(response);

        video.setState("uploaded");
        videoService.addVideo(video);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public @ResponseBody ResponseEntity<Iterable<Video>> getAllVideos() {
        Iterable<Video> videos = videoService.findAllVideos();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public @ResponseBody ResponseEntity<Void> getVideo(@PathVariable Integer id) {
        if(videoService.existsVideoById(id)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/videofiles/" + id + "/video.mpd"));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
