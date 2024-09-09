package org.example.abb_interview_task.mapper;

import org.example.abb_interview_task.dto.CardRequestDto;
import org.example.abb_interview_task.dto.CardResponseDto;
import org.example.abb_interview_task.entity.Cards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring"
)
public interface CardMapper {
    Cards requestToCard(CardRequestDto dto);

    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "CId", target = "cId")
    CardResponseDto cardToResponse(Cards cards);
}
