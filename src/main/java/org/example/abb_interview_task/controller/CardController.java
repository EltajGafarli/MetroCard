package org.example.abb_interview_task.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.abb_interview_task.dto.CardRequestDto;
import org.example.abb_interview_task.dto.CardResponseDto;
import org.example.abb_interview_task.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/cards")
@Slf4j
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<String> addCards(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CardRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        cardService.addCard(userDetails, requestDto)
                );
    }


    @GetMapping
    public ResponseEntity<List<CardResponseDto>> getCards(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .ok(cardService.getCards(userDetails));
    }

    @GetMapping(path = "/{cardId}")
    public ResponseEntity<CardResponseDto> getCardsById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable long cardId) {
        return ResponseEntity
                .ok(
                        this.cardService.getCardById(userDetails, cardId)
                );
    }


    @DeleteMapping(path = "/{cardId}")
    public ResponseEntity<String> deleteCardById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable long cardId) {
        return ResponseEntity
                .ok(
                        this.cardService.deleteCard(userDetails, cardId)
                );
    }


    @PostMapping(path = "/balance/{cardNumber}")
    public ResponseEntity<String> increaseBalance(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String cardNumber, @RequestParam BigDecimal amount) {
        return ResponseEntity
                .ok(this.cardService.increaseBalance(userDetails, cardNumber, amount));
    }

    @PutMapping(path = "/use/{cardNumber}")
    public ResponseEntity<String> useCard(@PathVariable String cardNumber) {
        return ResponseEntity
                .ok(
                        this.cardService.useCard(cardNumber)
                );
    }

}
