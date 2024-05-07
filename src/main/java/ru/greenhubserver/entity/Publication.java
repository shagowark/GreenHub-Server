package ru.greenhubserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "publication")
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "rating")
    private Long rating;

    @Column(name = "comments_count")
    private Long commentsCount;

    @Column(name = "created_time", nullable = false)
    @CreationTimestamp
    private Instant createdTime;

    @Enumerated(EnumType.STRING)
    private State state;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "image_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Image image;

    @ManyToMany
    @JoinTable(
            name = "publication_tag",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Tag> tags;

    @OneToMany(mappedBy = "publication")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Reaction> reactions;

    @OneToMany(mappedBy = "publication")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;



}


