package com.tio.wallet.mapper;

import com.tio.wallet.dto.response.UserLookupResponse;
import com.tio.wallet.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserLookupResponse toLookupResponse(User user) {
        return new UserLookupResponse(
                user.getEmail(),
                user.getName()
        );
    }
}
