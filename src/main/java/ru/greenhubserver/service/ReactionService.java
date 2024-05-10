package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.greenhubserver.entity.Publication;
import ru.greenhubserver.entity.Reaction;
import ru.greenhubserver.entity.ReactionType;
import ru.greenhubserver.entity.User;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.repository.ReactionRepository;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final UserService userService;
    private final PublicationService publicationService;

    public void saveReaction(Long publicationId, String reactionTypeStr, Principal principal) {
        ReactionType reactionType = ReactionType.valueOf(reactionTypeStr);
        User user = userService.findByUsername(principal.getName());
        Publication publication = publicationService.findPublicationById(publicationId);
        Reaction reaction = findByPublicationIdAndUserId(publication, user).orElse(null);
        if (reaction != null) {
            reaction.setReactionType(reactionType);

            if (reactionType == ReactionType.LIKE) {
                publication.setRating(publication.getRating() + 2);
            } else {
                publication.setRating(publication.getRating() - 2);
            }
        } else {
            reaction = new Reaction();
            reaction.setPublication(publication);
            reaction.setUser(user);
            reaction.setReactionType(reactionType);

            if (reactionType == ReactionType.LIKE) {
                publication.setRating(publication.getRating() + 1);
            } else {
                publication.setRating(publication.getRating() - 1);
            }
        }

        publicationService.savePublication(publication);
        reactionRepository.save(reaction);
    }

    public Optional<Reaction> findByPublicationIdAndUserId(Publication publication, User user) {
        return reactionRepository.findByPublicationAndUser(publication, user);
    }

    public void deleteReaction(Long publicationId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Publication publication = publicationService.findPublicationById(publicationId);
        Reaction reaction = findByPublicationIdAndUserId(publication, user).orElseThrow(() -> new NotFoundException("Reaction not found"));
        if (reaction.getReactionType() == ReactionType.LIKE) {
            publication.setRating(publication.getRating() - 1);
        } else {
            publication.setRating(publication.getRating() + 1);
        }
        publicationService.savePublication(publication);
        reactionRepository.delete(reaction);
    }
}
