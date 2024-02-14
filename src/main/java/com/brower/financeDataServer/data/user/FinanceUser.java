package com.brower.financeDataServer.data.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FinanceUser {
    @Id
    private String id;
    private String email;
    private String password;
    private List<String> roles;
    private boolean resetPassword;
    private boolean verified;

    public FinanceUser() {
        this.roles = new ArrayList<>();
    }
}
