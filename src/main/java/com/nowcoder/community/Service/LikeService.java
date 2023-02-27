package com.nowcoder.community.Service;

import com.nowcoder.community.Util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Service-2022-08-22 23:25
 */
@Service
public class LikeService {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public void like(int userId,int entityType,int entityId,int userIdOwnEntity){

        redisTemplate.execute(new SessionCallback<Object>(){
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(userIdOwnEntity);
                Boolean isLike = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                operations.multi();//开启事务
                if(isLike){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }
    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(likeKey,userId)?1:0;
    }

    public Long getLikeNum(int entityType,int entityId){
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(likeKey);
    }

    public int getUserLikeNum(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer num = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return num==null?0:num;
    }

}
