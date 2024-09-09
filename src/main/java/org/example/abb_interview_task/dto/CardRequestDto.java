package org.example.abb_interview_task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.LuhnCheck;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CardRequestDto implements Serializable {
    @NotNull
    @NotEmpty
    @NotBlank
    @LuhnCheck
    @Length(min = 11, max = 11)
    private String cardNumber;

}
