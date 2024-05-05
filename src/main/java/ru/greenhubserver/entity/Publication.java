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
@ToString(exclude = {"tags", "reactions"})
@EqualsAndHashCode(exclude = {"tags", "reactions"})
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

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(name = "created_time", nullable = false)
    @CreationTimestamp
    private Instant createdTime;

    @ManyToMany
    @JoinTable(
            name = "publication_tag",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @JsonIgnore
    @OneToMany(mappedBy = "publication")
    private Set<Reaction> reactions;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private State state;


}


