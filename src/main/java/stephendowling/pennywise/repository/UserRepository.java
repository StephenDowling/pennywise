package stephendowling.pennywise.repository;


import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import stephendowling.pennywise.model.User;

//for talking to the DB
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //custom methods here 

    // find a user by their username 
    Optional<User> findByUsername(String username); 
    
    //for use when registering a new user 
    boolean existsByUsername(String username); //does this user already exist?
    boolean existsByEmail(String email); //is this email already registered?


}

