package ru.netology.diplomaCloudStorage.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "files", schema = "public")
public class FileEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String name;

    @Getter
    @Column(nullable = false)
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    public FileEntity(String name, byte[] content, UserEntity user) {
        this.name = name;
        this.content = content;
        this.user = user;
    }
}
