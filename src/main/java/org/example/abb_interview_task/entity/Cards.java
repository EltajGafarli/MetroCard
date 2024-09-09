package org.example.abb_interview_task.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@SQLDelete(sql = "UPDATE cards SET is_active=false where c_id=?")
@Builder
public class Cards extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cId;

    @Column(length = 11, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    private boolean isActive = Boolean.TRUE;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id", referencedColumnName = "uId")
    private User user;


    @PrePersist
    public void preInsert() {
        this.isActive = true;
        this.balance = BigDecimal.ZERO;
    }

    public void addBalance(BigDecimal amount) {
        if (this.balance.add(amount).compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Maksimum balans 100 manat ola bilər.");
        }
        this.balance = this.balance.add(amount);
    }

    public void increaseBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void decreaseBalance() {
        this.balance = this.balance.subtract(BigDecimal.valueOf(0.5f));
    }
}
