package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.entity.Video;
import com.unict.dieei.pr20.videomanagementservice.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class VideoService {

    @Autowired
    VideoRepository videoRepository;

    public Video addVideo(Video video) {
        return videoRepository.save(video);
    }

    public Optional<Video> findVideoById(Integer id) {
        return videoRepository.findById(id);
    }

    public Iterable<Video> findAllVideos() {
        return videoRepository.findAll();
    }

    public Boolean existsVideoById(Integer id) {
        return videoRepository.existsById(id);
    }
}
