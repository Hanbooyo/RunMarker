package com.stravamate.passport.service;

import com.stravamate.passport.exception.ResourceNotFoundException;
import com.stravamate.passport.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final UserRepository userRepository;

    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void deleteAccount(Long userId) {
        int deletedRows = userRepository.deleteById(userId);
        if (deletedRows == 0) {
            throw new ResourceNotFoundException("User account was not found.");
        }
    }
}
