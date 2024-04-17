package ru.greenhubserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_time")
    private Instant createdTime;

    @JsonIgnore
    @OneToOne(mappedBy = "imageId", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private User user;

    @JsonIgnore
    @OneToOne(mappedBy = "imageId", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Publication publication;
}
