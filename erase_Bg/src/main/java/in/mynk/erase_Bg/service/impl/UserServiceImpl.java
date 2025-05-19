package in.mynk.erase_Bg.service.impl;

import in.mynk.erase_Bg.dto.UserDto;
import in.mynk.erase_Bg.entity.UserEntity;
import in.mynk.erase_Bg.repository.UserRepository;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        log.info("Saving user with clerkId: {}", userDto.getClerkId());
        
        Optional<UserEntity> optionalUser = userRepository.findByClerkId(userDto.getClerkId());
        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();
            log.info("Updating existing user: {}", existingUser.getEmail());
            
            existingUser.setEmail(userDto.getEmail());
            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setPhotoUrl(userDto.getPhotoUrl());
            
            if (userDto.getCredits() != null) {
                log.info("Updating credits for user {}: {} -> {}", 
                    existingUser.getEmail(), existingUser.getCredits(), userDto.getCredits());
                existingUser.setCredits(userDto.getCredits());
            }
            
            existingUser = userRepository.save(existingUser);
            log.info("Successfully updated user: {}", existingUser.getEmail());
            return maptoDTO(existingUser);
        }
        
        log.info("Creating new user with email: {}", userDto.getEmail());
        UserEntity newUser = maptoEntity(userDto);
        newUser = userRepository.save(newUser);
        log.info("Successfully created new user: {}", newUser.getEmail());
        return maptoDTO(newUser);
    }

    @Override
    public UserDto getUserByClerkId(String clerkId) {
        log.info("Fetching user with clerkId: {}", clerkId);
        UserEntity userEntity = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> {
                    log.error("User not found with clerkId: {}", clerkId);
                    return new UsernameNotFoundException("User not found");
                });
        log.info("Found user: {}", userEntity.getEmail());
        return maptoDTO(userEntity);
    }

    @Override
    @Transactional
    public void deleteUserByClerkId(String clerkId) {
        log.info("Deleting user with clerkId: {}", clerkId);
        UserEntity userEntity = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> {
                    log.error("User not found with clerkId: {}", clerkId);
                    return new UsernameNotFoundException("User not found");
                });
        userRepository.delete(userEntity);
        log.info("Successfully deleted user: {}", userEntity.getEmail());
    }

    private UserDto maptoDTO(UserEntity user) {
        return UserDto.builder()
                .clerkId(user.getClerkId())
                .credits(user.getCredits())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .photoUrl(user.getPhotoUrl())
                .build();
    }

    private UserEntity maptoEntity(UserDto userDto) {
        return UserEntity.builder()
                .clerkId(userDto.getClerkId())
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .photoUrl(userDto.getPhotoUrl())
                .credits(userDto.getCredits() != null ? userDto.getCredits() : 50)
                .build();
    }
}
