package com.unict.dieei.pr20.videomanagementservice.controller;

import com.unict.dieei.pr20.videomanagementservice.entity.Video;
import com.unict.dieei.pr20.videomanagementservice.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(path = "/videos")
public class VideoController {

    @Autowired
    VideoService videoService;

    @PostMapping
    public @ResponseBody ResponseEntity<Video> addVideo(@RequestBody Video video) {
        Video savedVideo = videoService.addVideo(video);
        return new ResponseEntity<>(savedVideo, HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}")
    public @ResponseBody ResponseEntity<Void> uploadVideo(@PathVariable Integer id) {
        Optional<Video> optionalVideo = videoService.findVideoById(id);
        if(optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
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
