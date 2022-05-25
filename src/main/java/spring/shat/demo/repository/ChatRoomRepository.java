package spring.shat.demo.repository;

import io.netty.util.internal.SuppressJava6Requirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import spring.shat.demo.dto.ChatRoom;
import spring.shat.demo.redis.RedisSubscriber;

import javax.annotation.PostConstruct;
import java.nio.channels.Channel;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {

    // 채팅방(topic)에 발행되는 메세지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;

    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;

    // 채팅방 정보가 초기화 되지 않도록 생성 시 Redis Hash에 저장하도록 처리
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;

    // 채팅방의 대화 메세지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic 정보를 Map에 넣어 roonId로 찾을 수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    // Version #1 - private Map<String, ChatRoom> chatRoomMap;

    @PostConstruct
    private void init(){
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRoom(){
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoom findRoomById(String id){
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }


    /**
     *  채팅방 생성
     */
    public ChatRoom createChatRoom(String name){
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }


    /**
     *  채팅방 입장
     */
    public void enterChatRoom(String roomId){
        ChannelTopic topic = topics.get(roomId);
        if(topic == null){
            topic = new ChannelTopic(roomId);
            redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    public ChannelTopic getTopic(String roomId){
        return topics.get(roomId);
    }
}
