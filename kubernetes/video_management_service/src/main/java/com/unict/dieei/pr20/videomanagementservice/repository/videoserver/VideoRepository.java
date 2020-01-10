package com.unict.dieei.pr20.videomanagementservice.repository.videoserver;

import com.unict.dieei.pr20.videomanagementservice.model.videoserver.Video;
import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<Video, Integer> {
}
