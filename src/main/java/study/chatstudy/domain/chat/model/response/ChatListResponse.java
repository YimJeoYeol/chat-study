package study.chatstudy.domain.chat.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import study.chatstudy.domain.chat.model.Message;

import java.util.*;


@Schema(description = "Chatting List")
public record ChatListResponse(
        @Schema(description = "return Messagae : []")
        List<Message> result
) {}