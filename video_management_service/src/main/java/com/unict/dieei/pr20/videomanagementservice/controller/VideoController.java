package com.unict.dieei.pr20.videomanagementservice.controller;

import com.unict.dieei.pr20.videomanagementservice.entity.Video;
import com.unict.dieei.pr20.videomanagementservice.service.CallStatsService;
import com.unict.dieei.pr20.videomanagementservice.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Enumeration;

@RestController
@RequestMapping(path = "/videos")
public class VideoController {

    @Autowired
    VideoService videoService;

    @Autowired
    CallStatsService callStatsService;

    @PostMapping
    public @ResponseBody ResponseEntity<Video> addVideo(Authentication auth,
                                                        @RequestBody Video video,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        callStatsService.addCallStats(request, response);
        Video videoInfo = videoService.addVideo(auth, video);
        return new ResponseEntity<>(videoInfo, HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}")
    public @ResponseBody ResponseEntity<Video> uploadVideo(Authentication auth, @PathVariable Integer id,
                                                          @RequestParam("file") MultipartFile file) {
        Video uploadedVideo = videoService.uploadVideo(auth, id, file);
        return new ResponseEntity<>(uploadedVideo, HttpStatus.CREATED);
    }

    @GetMapping
    public @ResponseBody ResponseEntity<Iterable<Video>> getAllVideos() {
        Iterable<Video> videos = videoService.getAllVideos();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public @ResponseBody ResponseEntity<Void> getVideo(@PathVariable Integer id) {
        videoService.checkVideoExistence(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/videofiles/" + id + "/video.mpd"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
