package org.whencanibe.crudforum.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder= passwordEncoder;
    }

    public User registerUser(String email, String password, String username){
        if(userRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("이미 존재하는 회원입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public User login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("이메일 혹은 비밀번호가 틀립니다."));
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new RuntimeException("이메일 혹은 비밀번호가 틀립니다.");
        }
        return user;
    }

    public void deleteUser(Long userId){
        if(!userRepository.existsById(userId)){
            throw new RuntimeException("해당 회원을 찾을 수 없습니다.");
        }
        userRepository.deleteById(userId);
    }
}
