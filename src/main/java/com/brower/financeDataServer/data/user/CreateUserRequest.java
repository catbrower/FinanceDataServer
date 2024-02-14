package com.brower.financeDataServer.data.user;

import lombok.*;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateUserRequest {
    private String email;
    private String password;
}
