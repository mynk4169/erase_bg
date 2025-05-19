package in.mynk.erase_Bg.security;

import in.mynk.erase_Bg.entity.User;
import in.mynk.erase_Bg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByClerkId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with clerkId: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getClerkId(),
                "", // No password needed as we're using Clerk for authentication
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 