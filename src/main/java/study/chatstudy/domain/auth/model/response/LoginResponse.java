package study.chatstudy.domain.auth.model.response
        ;

import io.swagger.v3.oas.annotations.media.Schema;
import study.chatstudy.common.exception.ErrorCode;

@Schema(description = "유저 생성 response")
public record LoginResponse(
        @Schema(description = "error code")
        ErrorCode description,

        @Schema(description = "jwt token")
        String token
) {}