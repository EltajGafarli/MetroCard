package org.example.abb_interview_task.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CardResponseDto {
    private long cId;
    private String cardNumber;
    private BigDecimal balance;
    private boolean isActive;

}
