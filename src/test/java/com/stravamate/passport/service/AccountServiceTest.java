package com.stravamate.passport.service;

import com.stravamate.passport.exception.ResourceNotFoundException;
import com.stravamate.passport.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void deleteAccountDeletesUserRecord() {
        AccountService accountService = new AccountService(userRepository);
        when(userRepository.deleteById(1L)).thenReturn(1);

        accountService.deleteAccount(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteAccountRejectsMissingUser() {
        AccountService accountService = new AccountService(userRepository);
        when(userRepository.deleteById(1L)).thenReturn(0);

        assertThatThrownBy(() -> accountService.deleteAccount(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
