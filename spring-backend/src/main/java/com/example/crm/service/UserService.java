package com.example.crm.service;
import com.example.crm.model.User;
import com.example.crm.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
public class UserService {
    private final UserRepository repo; private final BCryptPasswordEncoder encoder;
    public UserService(UserRepository repo, BCryptPasswordEncoder encoder){this.repo=repo;this.encoder=encoder;}
    public User register(User u){ u.setPassword(encoder.encode(u.getPassword())); return repo.save(u); }
    public User findByEmail(String email){ return repo.findByEmail(email).orElse(null); }
    public boolean checkPassword(User user, String raw){ return encoder.matches(raw,user.getPassword()); }
}
