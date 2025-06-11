package com.synassignment01.service;

import com.synassignment01.dto.RegisterRequest;
import com.synassignment01.dto.RegisterResponse;
import com.synassignment01.exceptions.PasswordMismatchException;
import com.synassignment01.exceptions.UserAlreadyExistsException;
import com.synassignment01.model.UserInfo;
import com.synassignment01.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found {}", username);
            throw new UsernameNotFoundException(username);
        }
        log.info("Successfully loaded the user {}", username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        if(userRepository.findByUsername(registerRequest.getUsername()) != null) {
            log.error("Username {} already in use", registerRequest.getUsername());
            throw new UserAlreadyExistsException(registerRequest.getUsername());
        }
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            log.error("Passwords do not match");
            throw new PasswordMismatchException();
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(registerRequest.getUsername());
        userInfo.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userInfo.setEmail(registerRequest.getEmail());
        userInfo.setAge(registerRequest.getAge());

        userRepository.save(userInfo);
        log.info("Successfully registered user {}", registerRequest.getUsername());
        return new RegisterResponse("Successfully Registered!");
    }

}
