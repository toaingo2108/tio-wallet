package com.tio.wallet.service;

import com.tio.wallet.dto.response.UserLookupResponse;
import com.tio.wallet.entity.User;
import com.tio.wallet.exception.NotFoundResourceException;
import com.tio.wallet.mapper.UserMapper;
import com.tio.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserLookupResponse getUserLookup(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundResourceException("Tài khoản không tồn tại"));
        return userMapper.toLookupResponse(user);
    }
}
