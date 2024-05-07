package ru.greenhubserver.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.greenhubserver.dto.controller.AchievementDto;
import ru.greenhubserver.dto.controller.UserBigDto;
import ru.greenhubserver.dto.controller.UserChangesDto;
import ru.greenhubserver.dto.controller.UserSmallDto;
import ru.greenhubserver.dto.security.RegistrationUserDto;
import ru.greenhubserver.entity.*;
import ru.greenhubserver.exceptions.BadRequestException;
import ru.greenhubserver.exceptions.NoRightsException;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.repository.UserRepository;

import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;
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

    public Optional<User> findByUserName(String username) { // TODO переделать на выкидывание ошибки
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUserName(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Username '%s' not found", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }

    @Transactional
    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setEmail(registrationUserDto.getEmail());
        user.setRoles(Set.of(roleService.getUserRole()));
        user.setState(State.VISIBLE);
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

    public void banUser(Long id) {
        User user = findById(id);
        user.setState(State.BANNED);
        userRepository.save(user);
    }

    public void editUser(Long id, UserChangesDto dto, Principal principal) {
        User user = findByUserName(principal.getName()).orElseThrow(() -> new NotFoundException("User not found"));
        if (!user.getId().equals(id)){
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
        if (dto.getEmail() != null){
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

}
