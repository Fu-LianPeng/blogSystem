package com.nowcoder.community.Service;

import com.nowcoder.community.Util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Service-2022-08-24 21:28
 */
@Service
public class FollowService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    public void follow(int entityType,int entityId,int userId){
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);

                Double score = operations.opsForZSet().score(followerKey, userId);
                operations.multi();
                if(score==null){
                    operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                    operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                }else{
                    operations.opsForZSet().remove(followeeKey,entityId);
                    operations.opsForZSet().remove(followerKey,userId);
                }
                return operations.exec();
            }
        });
    }

    public long getFollowerNum(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    public long getFolloweeNum(int entityType, int userId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    public boolean getFollowStatus(int entityType,int entityId,int userId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }

    /**
     *
     * @return 用户的粉丝列表
     */
    public List<Map<String,Object>> getFollowers(int entityType,int entityId,int offset,int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        Set userIds = redisTemplate.opsForZSet().range(followerKey, offset, offset+limit-1);

        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (Object userId : userIds) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("user",userService.getUser((Integer) userId));
            map.put("followTime",new Date(redisTemplate.opsForZSet().score(followerKey,userId).longValue()));
            list.add(map);
        }
        return list;
    }

    /**
     *
     * @param entityType
     * @param userId
     * @return 用户的关注列表
     */
    public List<Map<String,Object>> getFollowees(int entityType,int userId,int offset,int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        Set followeeIds = redisTemplate.opsForZSet().range(followeeKey, offset, offset+limit-1);
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (Object followeeId : followeeIds) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("user",userService.getUser((Integer) followeeId));
            map.put("followTime",new Date(redisTemplate.opsForZSet().score(followeeKey,followeeId).longValue()));
            list.add(map);
        }
        return list;
    }


}
