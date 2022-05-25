package spring.shat.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.shat.demo.dto.ChatMessage;
import spring.shat.demo.dto.ChatRoom;
import spring.shat.demo.redis.RedisPublisher;
import spring.shat.demo.repository.ChatRoomRepository;
import spring.shat.demo.service.ChatService;
import spring.shat.demo.service.JwtTokenProvider;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {

    // private final SimpMessageSendingOperations messagingTemplate;
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token){

        String nickName = jwtTokenProvider.getUserNameFromJwt(token);

        if(ChatMessage.MessageType.ENTER.equals(message.getType())){
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(nickName + "님이 입장하셨습니다.");
        }
        // messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        // WebSocket에 발향된 메세지를 redis로 발행한다 (publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }

}
