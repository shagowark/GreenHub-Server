package ru.greenhubserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

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

    @OneToMany(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> user;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Achievement achievement;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Publication publication;
}
