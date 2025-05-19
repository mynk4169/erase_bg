package in.mynk.erase_Bg.service.impl;

import in.mynk.erase_Bg.entity.User;
import in.mynk.erase_Bg.repository.UserRepository;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByClerkId(String clerkId) {
        return userRepository.findByClerkId(clerkId);
    }

    @Override
    public User createUser(String clerkId, String email, String firstName, String lastName) {
        if (userRepository.existsByClerkId(clerkId)) {
            throw new IllegalArgumentException("User with clerkId " + clerkId + " already exists");
        }

        User newUser = User.builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .credits(0)
                .plan("Free")
                .isActive(true)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(String clerkId, String email, String firstName, String lastName) {
        User existingUser = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with clerkId: " + clerkId));

        existingUser.setEmail(email);
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);

        return userRepository.save(existingUser);
    }

    @Override
    public User updateCredits(String clerkId, int credits) {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with clerkId: " + clerkId));

        user.setCredits(credits);
        return userRepository.save(user);
    }

    @Override
    public User updatePlan(String clerkId, String plan) {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with clerkId: " + clerkId));

        user.setPlan(plan);
        return userRepository.save(user);
    }

    @Override
    public void deactivateUser(String clerkId) {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with clerkId: " + clerkId));

        user.setActive(false);
        userRepository.save(user);
    }
}
