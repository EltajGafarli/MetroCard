package org.example.abb_interview_task.controller;

import org.example.abb_interview_task.dto.UserDto;
import org.example.abb_interview_task.dto.UserRequestDto;
import org.example.abb_interview_task.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.is;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCurrentUser_ShouldReturnUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        when(userService.getCurrentUser(any())).thenReturn(userDto);

        mockMvc.perform(get("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("John");
        userRequestDto.setLastName("Doe");
        userRequestDto.setEmail("john.doe@example.com");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setFirstName("John");
        updatedUserDto.setLastName("Doe");
        updatedUserDto.setEmail("john.doe@example.com");

        when(userService.updateUser(any(), any(UserRequestDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage() throws Exception {
        when(userService.deleteUser(any())).thenReturn("User deleted successfully");

        mockMvc.perform(delete("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }
}
