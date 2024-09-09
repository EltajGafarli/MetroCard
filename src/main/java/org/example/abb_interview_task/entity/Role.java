package org.example.abb_interview_task.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Role extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private RoleEnum roleEnum;

    @ManyToOne(
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,
                    CascadeType.MERGE,
                    CascadeType.DETACH
            }
    )
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @Override
    public int hashCode() {
        return Objects
                .hash(this.id, this.roleEnum, this.getCreatedAt(), this.getUpdatedAt());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Role role)) {
            return false;
        }

        return Objects.deepEquals(this.id, role.id)
                && Objects.deepEquals(this.roleEnum, role.roleEnum)
                && Objects.deepEquals(this.getCreatedAt(), role.getCreatedAt())
                && Objects.deepEquals(this.getUpdatedAt(), role.getUpdatedAt());

    }

}