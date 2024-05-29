package ru.greenhubserver.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.greenhubserver.dto.controller.*;
import ru.greenhubserver.dto.security.RegistrationUserDto;
import ru.greenhubserver.entity.*;
import ru.greenhubserver.exceptions.BadRequestException;
import ru.greenhubserver.exceptions.NoRightsException;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.exceptions.UserBannedException;
import ru.greenhubserver.repository.UserRepository;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ImageCloudService imageCloudService;
    private final ImageService imageService;
    private final AchievementService achievementService;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user.getState() == State.BANNED) throw new UserBannedException("User is banned");
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList())
                .disabled(user.getState() == State.BANNED)
                .build();
    }

    @Transactional
    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setEmail(registrationUserDto.getEmail());
        user.setRoles(Set.of(roleService.getUserRole()));
        user.setState(State.VISIBLE);
        user.setImage(imageService.findById(1L)); // default image id
        return userRepository.save(user);
    }

    public UserBigDto getUser(Long id) {
        User user = findById(id);
        return UserBigDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .image(imageCloudService.getImage(user.getImage().getName()))
                .subscriptionsCount((long) user.getSubscriptions().size())
                .subscribersCount((long) user.getSubscribers().size())
                .build();
    }

    public UserBigDto getUser(String username, Principal principal) {
        User target = findByUsername(username);
        User user = findByUsername(principal.getName());
        return UserBigDto.builder()
                .id(target.getId())
                .email(target.getEmail())
                .username(target.getUsername())
                .image(imageCloudService.getImage(target.getImage().getName()))
                .subscriptionsCount((long) target.getSubscriptions().size())
                .subscribersCount((long) target.getSubscribers().size())
                .state(target.getState())
                .isSubscribed(user.getSubscriptions().contains(target))
                .roles(target.getRoles().stream().map(x ->
                        RoleDto.builder()
                                .name(x.getName())
                                .build()
                ).collect(Collectors.toSet()))
                .build();
    }

    public Set<UserSmallDto> getUserSubscriptions(Long id) {
        User user = findById(id);
        Set<UserSmallDto> res = new HashSet<>();
        for (User subscription : user.getSubscriptions()) {
            if (subscription.getState() == State.BANNED) continue;
            res.add(UserSmallDto.builder()
                    .userId(subscription.getId())
                    .username(subscription.getUsername())
                    .userImage(imageCloudService.getImage(subscription.getImage().getName()))
                    .build());
        }
        return res;
    }

    public Set<UserSmallDto> getUserSubscribers(Long id) {
        User user = findById(id);
        Set<UserSmallDto> res = new HashSet<>();
        for (User subscription : user.getSubscribers()) {
            if (subscription.getState() == State.BANNED) continue;
            res.add(UserSmallDto.builder()
                    .userId(subscription.getId())
                    .username(subscription.getUsername())
                    .userImage(imageCloudService.getImage(subscription.getImage().getName()))
                    .build());
        }
        return res;
    }

    public void banUser(Long id, Principal principal) {
        User target = findById(id);
        User user = findByUsername(principal.getName());
        if (user.equals(target)) throw new BadRequestException("Cannot ban yourself");
        target.setState(State.BANNED);
        target.getPublications().forEach(x -> x.setState(State.BANNED));
        userRepository.save(target);
    }

    public void unbanUser(Long id, Principal principal) {
        User target = findById(id);
        User user = findByUsername(principal.getName());
        if (user.equals(target)) throw new BadRequestException("Cannot unban yourself");
        target.setState(State.VISIBLE);
        target.getPublications().forEach(x -> x.setState(State.VISIBLE));
        userRepository.save(target);
    }

    public void editUser(Long id, UserChangesDto dto, Principal principal) {
        User user = findByUsername(principal.getName());
        if (!user.getId().equals(id)) {
            throw new NoRightsException("Cannot change other's profile");
        }
        if (dto.getImage() != null) {
            Image image = new Image();
            image = imageService.save(image);
            image.setName(imageCloudService.generateFileName(image.getId(), dto.getImage()));
            imageService.save(image);
            imageCloudService.saveImage(dto.getImage(), image.getName());

            user.setImage(image);
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        userRepository.save(user);
    }

    public Set<AchievementDto> getUserAchievements(Long id) {
        User user = findById(id);
        return user.getAchievements().stream().map(x ->
                        AchievementDto.builder()
                                .id(x.getId())
                                .name(x.getName())
                                .image(imageCloudService.getImage(x.getImage().getName()))
                                .build())
                .collect(Collectors.toSet());
    }

    public void editUserAchievements(Long id, Set<String> achievements) {
        User user = findById(id);
        user.setAchievements(
                achievements.stream().map(achievementService::findByName).collect(Collectors.toSet())
        );
        userRepository.save(user);
    }

    public void subscribeToUser(Long id, Principal principal) {
        User target = findById(id);
        User user = findByUsername(principal.getName());

        if (user.equals(target)) throw new BadRequestException("Cannot subscribe to yourself");
        if (user.getSubscriptions().contains(target)) throw new BadRequestException("Cannot subscribe twice");

        target.getSubscribers().add(user);
        user.getSubscriptions().add(target);
        userRepository.save(user);
        userRepository.save(target);
    }

    public void unsubscribeToUser(Long id, Principal principal) {
        User target = findById(id);
        User user = findByUsername(principal.getName());

        if (user.equals(target)) throw new BadRequestException("Cannot unsubscribe from yourself");
        if (!user.getSubscriptions().contains(target)) throw new BadRequestException("Cannot unsubscribe twice");

        target.getSubscribers().remove(user);
        user.getSubscriptions().remove(target);
        userRepository.save(user);
        userRepository.save(target);
    }

    public void upgradeUser(Long id, Principal principal) {
        User target = findById(id);
        User user = findByUsername(principal.getName());

        if (user.equals(target)) throw new BadRequestException("Cannot upgrade yourself");
        if (target.getRoles().contains(roleService.getModeratorRole()))
            throw new BadRequestException("User is already a moderator");

        target.getRoles().add(roleService.getModeratorRole());
        userRepository.save(target);
    }

    public void downgradeUser(Long id, Principal principal) {
        User target = findById(id);
        User user = findByUsername(principal.getName());

        if (user.equals(target)) throw new BadRequestException("Cannot downgrade yourself");
        if (!target.getRoles().contains(roleService.getModeratorRole()))
            throw new BadRequestException("User is not a moderator");

        target.getRoles().remove(roleService.getModeratorRole());
        userRepository.save(target);
    }

    public void checkIfUserBanned(Principal principal) {
        User user = findByUsername(principal.getName());
        if (user.getState() == State.BANNED) {
            throw new NoRightsException("User is banned");
        }
    }
}
