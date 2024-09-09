package org.example.abb_interview_task.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.abb_interview_task.dto.UserDto;
import org.example.abb_interview_task.dto.UserRequestDto;
import org.example.abb_interview_task.entity.User;
import org.example.abb_interview_task.exception.NotFoundException;
import org.example.abb_interview_task.mapper.UserMapper;
import org.example.abb_interview_task.repository.CardsRepository;
import org.example.abb_interview_task.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CardsRepository cardsRepository;

    @Transactional(rollbackOn = Exception.class)
    public String deleteUser(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        userRepository.deleteUser(user.getUId());
        cardsRepository.deleteCardsByUserId(user.getUId());
        return "User deleted successfully";
    }

    @Transactional(rollbackOn = Exception.class)
    public UserDto updateUser(UserDetails userDetails, UserRequestDto userRequestDto) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setFirstName(userRequestDto.getFirstName());
        user.setEmail(userRequestDto.getEmail());
        user.setLastName(userRequestDto.getLastName());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        User savedUser = userRepository.save(user);
        return userMapper.userToDto(savedUser);
    }

    public UserDto getCurrentUser(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.userToDto(user);
    }

}
