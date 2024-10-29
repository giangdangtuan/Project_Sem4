package web.thaiticketmajor.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import web.thaiticketmajor.Models.User;

public interface UserRepository extends JpaRepository<User, Integer>
{
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}