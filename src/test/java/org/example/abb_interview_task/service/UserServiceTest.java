package org.example.abb_interview_task.service;

import org.example.abb_interview_task.dto.UserDto;
import org.example.abb_interview_task.dto.UserRequestDto;
import org.example.abb_interview_task.entity.User;
import org.example.abb_interview_task.exception.NotFoundException;
import org.example.abb_interview_task.mapper.UserMapper;
import org.example.abb_interview_task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() {
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String result = userService.deleteUser(userDetails);

        assertEquals("User deleted successfully", result);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowNotFoundException() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userDetails));
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        UserDetails userDetails = mock(UserDetails.class);
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("UpdatedFirstName");
        userRequestDto.setLastName("UpdatedLastName");
        userRequestDto.setEmail("updated@example.com");
        userRequestDto.setPhoneNumber("1234567890");

        User user = new User();
        User updatedUser = new User();
        UserDto userDto = new UserDto();

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.userToDto(updatedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(userDetails, userRequestDto);

        assertNotNull(result);
        verify(userRepository).save(user);
        verify(userMapper).userToDto(updatedUser);
    }

    @Test
    void updateUser_ShouldThrowNotFoundException() {
        UserDetails userDetails = mock(UserDetails.class);
        UserRequestDto userRequestDto = new UserRequestDto();

        when(userDetails.getUsername()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userDetails, userRequestDto));
    }

    @Test
    void getCurrentUser_ShouldReturnUserDto() {
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        UserDto userDto = new UserDto();

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.userToDto(user)).thenReturn(userDto);

        UserDto result = userService.getCurrentUser(userDetails);

        assertNotNull(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).userToDto(user);
    }

    @Test
    void getCurrentUser_ShouldThrowNotFoundException() {
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getCurrentUser(userDetails));
    }
}
