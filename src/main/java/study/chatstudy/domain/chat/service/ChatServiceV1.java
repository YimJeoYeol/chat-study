package study.chatstudy.domain.chat.service;
import java.sql.Timestamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import study.chatstudy.domain.chat.model.Message;
import study.chatstudy.domain.chat.model.response.ChatListResponse;
import study.chatstudy.domain.repository.ChatRepository;
import study.chatstudy.domain.repository.entity.Chat;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ChatServiceV1 {

    private final ChatRepository chatRepository;

    public ChatListResponse chatList(String from, String to) {
        List<Chat> chats = chatRepository.findTop10BySenderOrReceiverOrderByTIDDesc(from, to);

        // Entity -> DTO
        List<Message> res = chats.stream()
                .map(chat -> new Message(chat.getReceiver(), chat.getSender(), chat.getMessage()))
                .collect(Collectors.toList());

        return new ChatListResponse(res);
    }

    @Transactional(transactionManager = "createChatTransacationMansger")
    public void saveChatMessage(Message msg) {
        Chat chat = Chat.builder().
                sender(msg.getFrom()).
                receiver(msg.getTo()).
                message(msg.getMessage()).
                created_at(new Timestamp(System.currentTimeMillis())).
                build();

        chatRepository.save(chat);
    }
}