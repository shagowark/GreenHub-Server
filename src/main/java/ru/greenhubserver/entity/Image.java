package ru.greenhubserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
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

    @JsonIgnore
    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private User user;

    @JsonIgnore
    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Achievement achievement;

    @JsonIgnore
    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Publication publication;
}
