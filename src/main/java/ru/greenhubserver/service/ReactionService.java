package ru.greenhubserver.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import ru.greenhubserver.entity.Publication;
import ru.greenhubserver.entity.Reaction;
import ru.greenhubserver.entity.ReactionType;
import ru.greenhubserver.entity.User;
import ru.greenhubserver.exceptions.BadRequestException;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.repository.ReactionRepository;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final UserService userService;
    private final PublicationService publicationService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveReaction(Long publicationId, String reactionTypeStr, Principal principal) {
        synchronized (this) {
            ReactionType reactionType = ReactionType.valueOf(reactionTypeStr);
            User user = userService.findByUsername(principal.getName());
            Publication publication = publicationService.findPublicationById(publicationId);
            if (findByPublicationIdAndUserId(publication, user) == null) {
                Reaction reaction = new Reaction();
                reaction.setPublication(publication);
                reaction.setUser(user);
                reaction.setReactionType(reactionType);

                if (reactionType == ReactionType.LIKE) {
                    publication.setRating(publication.getRating() + 1);
                } else {
                    publication.setRating(publication.getRating() - 1);
                }
                publicationService.savePublication(publication);
                reactionRepository.save(reaction);
            } else {
                throw new BadRequestException("Reaction already exists");
            }
        }
    }

    public Reaction findByPublicationIdAndUserId(Publication publication, User user) {
        return reactionRepository.findByPublicationAndUser(publication, user).orElse(null);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteReaction(Long publicationId, Principal principal) {
        synchronized (this) {
            User user = userService.findByUsername(principal.getName());
            Publication publication = publicationService.findPublicationById(publicationId);
            Reaction reaction = findByPublicationIdAndUserId(publication, user);
            if (reaction != null) {
                if (reaction.getReactionType() == ReactionType.LIKE) {
                    publication.setRating(publication.getRating() - 1);
                } else {
                    publication.setRating(publication.getRating() + 1);
                }
                reactionRepository.delete(reaction);
                publicationService.savePublication(publication);
            } else {
                throw new NotFoundException("Reaction not found");
            }
        }
    }
}
