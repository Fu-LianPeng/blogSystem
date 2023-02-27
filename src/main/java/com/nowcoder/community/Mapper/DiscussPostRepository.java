package com.nowcoder.community.Mapper;

import com.nowcoder.community.Bean.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Mapper-2022-09-08 16:21
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {


}
