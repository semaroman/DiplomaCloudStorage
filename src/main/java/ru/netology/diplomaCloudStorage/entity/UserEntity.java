package ru.netology.diplomaCloudStorage.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users", schema = "public")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Getter
    @Column(nullable = false, unique = true)
    private String login;

    @Getter
    @Column(nullable = false)
    private String password;
}
