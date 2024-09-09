package org.example.abb_interview_task.service;

import org.junit.jupiter.api.Test;
import org.example.abb_interview_task.dto.CardRequestDto;
import org.example.abb_interview_task.dto.CardResponseDto;
import org.example.abb_interview_task.entity.Cards;
import org.example.abb_interview_task.entity.User;
import org.example.abb_interview_task.mapper.CardMapper;
import org.example.abb_interview_task.repository.CardsRepository;
import org.example.abb_interview_task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardsRepository cardsRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCard_ShouldAddCardSuccessfully() {
        UserDetails userDetails = mock(UserDetails.class);
        CardRequestDto cardRequestDto = CardRequestDto.builder().build();
        cardRequestDto.setCardNumber("1234567890123456");

        User user = new User();
        Cards card = new Cards();
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardsRepository.findCardsByCardNumber("1234567890123456")).thenReturn(Optional.empty());
        when(cardMapper.requestToCard(cardRequestDto)).thenReturn(card);

        String result = cardService.addCard(userDetails, cardRequestDto);

        assertEquals("Cards added successfully", result);
        verify(cardsRepository).save(card);
    }

    @Test
    void addCard_ShouldThrowExceptionIfCardAlreadyActive() {
        UserDetails userDetails = mock(UserDetails.class);
        CardRequestDto cardRequestDto =  CardRequestDto.builder().build();
        cardRequestDto.setCardNumber("1234567890123456");

        User user = new User();
        Cards activeCard = new Cards();
        activeCard.setActive(true);

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardsRepository.findCardsByCardNumber("1234567890123456")).thenReturn(Optional.of(activeCard));

        assertThrows(RuntimeException.class, () -> cardService.addCard(userDetails, cardRequestDto));
    }

    @Test
    void getCards_ShouldReturnCardList() {
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        Cards card = new Cards();
        CardResponseDto cardResponseDto = CardResponseDto.builder().build();

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardsRepository.findCardsByUserId(user.getUId())).thenReturn(Collections.singletonList(card));
        when(cardMapper.cardToResponse(card)).thenReturn(cardResponseDto);

        List<CardResponseDto> result = cardService.getCards(userDetails);

        assertFalse(result.isEmpty());
        verify(cardsRepository).findCardsByUserId(user.getUId());
    }

    @Test
    void getCardById_ShouldReturnCard() {
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        Cards card = new Cards();
        CardResponseDto cardResponseDto = CardResponseDto.builder().build();

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardsRepository.getCardsByUserIdAndCardId(user.getUId(), 1L)).thenReturn(Optional.of(card));
        when(cardMapper.cardToResponse(card)).thenReturn(cardResponseDto);

        CardResponseDto result = cardService.getCardById(userDetails, 1L);

        assertNotNull(result);
        verify(cardsRepository).getCardsByUserIdAndCardId(user.getUId(), 1L);
    }

    @Test
    void deleteCard_ShouldDeleteCardSuccessfully() {
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setUId(1L);

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String result = cardService.deleteCard(userDetails, 1L);

        assertEquals("Card deleted successfully", result);
        verify(cardsRepository).deleteByUserAndCardId(1L, 1L);
    }

    @Test
    void increaseBalance_ShouldIncreaseCardBalanceSuccessfully() {
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        Cards card = new Cards();
        BigDecimal amount = BigDecimal.valueOf(50);

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(cardsRepository.getCardsByUserIdAndCardNumber(user.getUId(), "1234567890123456")).thenReturn(Optional.of(card));

        String result = cardService.increaseBalance(userDetails, "1234567890123456", amount);

        assertEquals("Card balance increased successfully", result);
        verify(cardsRepository).save(card);
    }

    @Test
    void useCard_ShouldDecreaseBalanceSuccessfully() {
        Cards card = new Cards();
        when(cardsRepository.findCardsByCardNumber("1234567890123456")).thenReturn(Optional.of(card));

        String result = cardService.useCard("1234567890123456");

        assertEquals("Successful", result);
        verify(cardsRepository).save(card);
    }
}