package ru.greenhubserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Achievement achievement;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Publication publication;
}
