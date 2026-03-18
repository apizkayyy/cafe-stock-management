package com.cafe.stockmanagement.repository;

import com.cafe.stockmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring reads the method name and writes the SQL for you!
    // "find User where email = ?"
    Optional<User> findByEmail(String email);

    // "find User where googleId = ?"
    Optional<User> findByGoogleId(String googleId);

    // "does a User exist where email = ?"
    Boolean existsByEmail(String email);
}

/* 
🧠 **Method Name Magic** — Spring JPA reads your method name and generates the SQL automatically:
```
findByEmail        →  SELECT * FROM users WHERE email = ?
existsByEmail      →  SELECT COUNT(*) FROM users WHERE email = ?
findByGoogleId     →  SELECT * FROM users WHERE google_id = ?
*/