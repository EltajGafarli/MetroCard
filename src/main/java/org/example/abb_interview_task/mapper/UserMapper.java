package org.example.abb_interview_task.mapper;

import lombok.RequiredArgsConstructor;
import org.example.abb_interview_task.dto.RegisterRequest;
import org.example.abb_interview_task.dto.UserDto;
import org.example.abb_interview_task.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        componentModel = "spring"
)
@RequiredArgsConstructor
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(source = "UId", target = "id")
    public abstract UserDto userToDto(User user);

    @Mapping(target = "password", expression = "java( passwordEncoder.encode(request.getPassword()) )")
    @Mapping(target = "isEnabled", constant = "true")
    public abstract User requestToUser(RegisterRequest request);
}
