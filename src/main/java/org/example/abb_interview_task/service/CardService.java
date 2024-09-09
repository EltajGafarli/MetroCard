package org.example.abb_interview_task.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.abb_interview_task.dto.CardRequestDto;
import org.example.abb_interview_task.dto.CardResponseDto;
import org.example.abb_interview_task.entity.Cards;
import org.example.abb_interview_task.entity.User;
import org.example.abb_interview_task.exception.NotFoundException;
import org.example.abb_interview_task.mapper.CardMapper;
import org.example.abb_interview_task.repository.CardsRepository;
import org.example.abb_interview_task.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final UserRepository userRepository;
    private final CardsRepository cardsRepository;
    private final CardMapper cardMapper;

    @Transactional
    public String addCard(UserDetails userDetails, CardRequestDto requestDto) {
        Optional<Cards> cardsByCardNumber = cardsRepository.findCardsByCardNumber(requestDto.getCardNumber());
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
        );


        if (cardsByCardNumber.isPresent()) {
            Cards cards = cardsByCardNumber.get();
            if (!cards.isActive()) {
                cards.setActive(true);
                cards.setUser(user);
                cardsRepository.save(cards);
                return "Card added successfully";
            } else {
                throw new RuntimeException("Card is active");
            }
        }


        Cards cards = this.cardMapper.requestToCard(requestDto);

        user.addCard(cards);

        this.cardsRepository.save(cards);

        return "Cards added successfully";


    }

    @org.springframework.transaction.annotation.Transactional
    public List<CardResponseDto> getCards(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
        );


        return cardsRepository.findCardsByUserId(user.getUId())
                .stream()
                .map(this.cardMapper::cardToResponse)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional
    public CardResponseDto getCardById(UserDetails userDetails, long cardId) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        Cards cards = cardsRepository.getCardsByUserIdAndCardId(user.getUId(), cardId)
                .orElseThrow(
                        () -> new NotFoundException("Card not found")
                );

        return this.cardMapper.cardToResponse(cards);

    }


    @Transactional
    public String deleteCard(UserDetails userDetails, long cardId) {
        long userId = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
        ).getUId();
        this.cardsRepository.deleteByUserAndCardId(userId, cardId);

        return "Card deleted successfully";
    }


    @Transactional
    public String increaseBalance(UserDetails userDetails, String cardNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Cannot increase card balance by more than 100");
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        Optional<Cards> cards = cardsRepository.getCardsByUserIdAndCardNumber(user.getUId(), cardNumber);

        if (cards.isPresent()) {
            Cards card = cards.get();
            card.increaseBalance(amount);
            cardsRepository.save(card);
            return "Card balance increased successfully";
        }


        Optional<Cards> cardsByCardNumber = cardsRepository.findCardByNumberWithLock(cardNumber);
        if (cardsByCardNumber.isPresent()) {
            Cards card = cardsByCardNumber.get();
            if (card.getUser() == null || !card.isActive()) {
                user.addCard(card);
                card.setUser(user);
                card.increaseBalance(amount);
                cardsRepository.save(card);
                return "Card balance increased successfully";
            } else {
                throw new RuntimeException("Card is active");
            }
        } else {
            throw new NotFoundException("Card not found");
        }

    }

    @Transactional
    public String useCard(String cardNumber) {
        Cards cards = cardsRepository.findCardByNumberWithLock(cardNumber)
                .orElseThrow(
                        () -> new NotFoundException("Card not found")
                );

        cards.decreaseBalance();
        cardsRepository.save(cards);

        return "Successful";
    }


}
