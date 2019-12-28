package com.unict.dieei.pr20.videomanagementservice.controller;

import com.unict.dieei.pr20.videomanagementservice.entity.User;
import com.unict.dieei.pr20.videomanagementservice.entity.Video;
import com.unict.dieei.pr20.videomanagementservice.service.UserService;
import com.unict.dieei.pr20.videomanagementservice.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
        Video savedVideo = videoService.addVideo(video);
        return new ResponseEntity<>(savedVideo, HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}")
    public @ResponseBody ResponseEntity<Void> uploadVideo(Authentication auth, @PathVariable Integer id) {
        if(auth == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<User> optionalUser = userService.findUserByEmail(auth.getName());
        if(!optionalUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();
        Optional<Video> optionalVideo = videoService.findVideoById(id);
        if(optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            if(!video.getUser().equals(user)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            video.setState("uploaded");
            videoService.addVideo(video);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
