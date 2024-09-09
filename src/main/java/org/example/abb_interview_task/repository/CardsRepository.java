package org.example.abb_interview_task.repository;

import jakarta.persistence.LockModeType;
import org.example.abb_interview_task.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardsRepository extends JpaRepository<Cards, Long> {

    Optional<Cards> findCardsByCardNumber(String cardNumber);

    @Query(value = "SELECT c from Cards c where c.user.uId = :userId and c.isActive=true")
    List<Cards> findCardsByUserId(@Param("userId") long userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT c from Cards c where c.cardNumber = :cardNumber")
    Optional<Cards> findCardByNumberWithLock(@Param("cardNumber") String cardNumber);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            value = "SELECT c from Cards c where c.user.uId = :userId and c.cId = :cardId and c.isActive=true"
    )
    Optional<Cards> getCardsByUserIdAndCardId(@Param("userId") long userId, @Param("cardId") long cardId);


    @Query(
            value = "SELECT c from Cards c where c.user.uId = :userId and c.cardNumber = :cardNumber and c.isActive=true"
    )
    Optional<Cards> getCardsByUserIdAndCardNumber(@Param(value = "userId") long userId, @Param("cardNumber") String cardNumber);

    @Modifying
    @Query(
            value = "update Cards c set c.isActive=false where c.user.uId = :userId and c.cId = :cardId"
    )
    void deleteByUserAndCardId(@Param("userId") long userId, @Param("cardId") long cardId);


    @Query(
            value = "update Cards c set c.isActive=false where c.user.uId= :uId"
    )
    @Modifying
    void deleteCardsByUserId(@Param("uId") long uId);

}
