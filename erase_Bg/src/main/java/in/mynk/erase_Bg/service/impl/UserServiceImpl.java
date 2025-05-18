package in.mynk.erase_Bg.service.impl;

import in.mynk.erase_Bg.dto.UserDto;
import in.mynk.erase_Bg.entity.UserEntity;
import in.mynk.erase_Bg.repository.UserRepository;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(UserDto userDto) {
        Optional<UserEntity> optionalUser = userRepository.findByClerkId(userDto.getClerkId());
        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();
            existingUser.setEmail(userDto.getEmail());
            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setPhotoUrl(userDto.getPhotoUrl());
            if (userDto.getCredits() != null) {
                existingUser.setCredits(userDto.getCredits());
            }
            existingUser = userRepository.save(existingUser);
            System.out.println("Saving user with credits: " + userDto.getCredits());
            System.out.println("Saving user with photoUrl: " + userDto.getPhotoUrl());
            return maptoDTO(existingUser);
        }
        UserEntity newUser = maptoEntity(userDto);
        userRepository.save(newUser);
        return maptoDTO(newUser);

    }

    @Override
    public UserDto getUserByClerkId(String clerkId) {
        UserEntity userEntity = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return  maptoDTO(userEntity);
    }

    @Override
    public void deleteUserByClerkId(String clerkId) {
        UserEntity userEntity = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(userEntity);
        System.out.println("Deleting user: " + userEntity.getEmail());
    }

    private UserDto maptoDTO(UserEntity newUser) {
      return   UserDto.builder()
                .clerkId(newUser.getClerkId())
                .credits(newUser.getCredits())
                .email(newUser.getEmail())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .photoUrl(newUser.getPhotoUrl())
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
