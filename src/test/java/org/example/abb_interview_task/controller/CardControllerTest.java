package org.example.abb_interview_task.controller;
import org.example.abb_interview_task.dto.CardRequestDto;
import org.example.abb_interview_task.dto.CardResponseDto;
import org.example.abb_interview_task.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCards_ShouldReturnCreatedStatus() throws Exception {
        CardRequestDto requestDto = CardRequestDto.builder().build();
        when(cardService.addCard(any(), any(CardRequestDto.class))).thenReturn("Card added successfully");

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Card added successfully"));
    }

    @Test
    void getCards_ShouldReturnCardList() throws Exception {
        CardResponseDto cardResponseDto = CardResponseDto.builder().build();
        when(cardService.getCards(any())).thenReturn(Collections.singletonList(cardResponseDto));

        mockMvc.perform(get("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getCardsById_ShouldReturnCard() throws Exception {
        CardResponseDto cardResponseDto = CardResponseDto.builder().build();
        when(cardService.getCardById(any(), anyLong())).thenReturn(cardResponseDto);

        mockMvc.perform(get("/api/cards/{cardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardId").exists());
    }

    @Test
    void deleteCardById_ShouldReturnSuccessMessage() throws Exception {
        when(cardService.deleteCard(any(), anyLong())).thenReturn("Card deleted successfully");

        mockMvc.perform(delete("/api/cards/{cardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Card deleted successfully"));
    }

    @Test
    void increaseBalance_ShouldReturnSuccessMessage() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(50);
        when(cardService.increaseBalance(any(), any(), any(BigDecimal.class)))
                .thenReturn("Card balance increased successfully");

        mockMvc.perform(post("/api/cards/balance/{cardNumber}", "1234567890123456")
                        .param("amount", amount.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Card balance increased successfully"));
    }

    @Test
    void useCard_ShouldReturnSuccessMessage() throws Exception {
        when(cardService.useCard(any())).thenReturn("Successful");

        mockMvc.perform(get("/api/cards/use/{cardNumber}", "1234567890123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Successful"));
    }
}
