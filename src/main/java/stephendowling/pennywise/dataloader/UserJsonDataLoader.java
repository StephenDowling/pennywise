package stephendowling.pennywise.dataloader;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import stephendowling.pennywise.model.User;
import stephendowling.pennywise.repository.UserRepository;

//used to load Users into the system on start up, will be ignored if there's already data in the DB

@Component
public class UserJsonDataLoader implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(UserJsonDataLoader.class);
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public UserJsonDataLoader(ObjectMapper objectMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder; // Initialize PasswordEncoder
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            try (InputStream inputStream = getClass().getResourceAsStream("/data/users.json")) {
                Users allUsers = objectMapper.readValue(inputStream, Users.class);
                log.info("Reading {} users from JSON data and saving them to the database.", allUsers.Users().size());

                // Iterate through users and encode their passwords
                for (User user : allUsers.Users()) {
                    if (user.getPassword() != null) {
                        // Encode the password before saving
                        String encodedPassword = passwordEncoder.encode(user.getPassword());
                        user.setPassword(encodedPassword); // Set the encoded password
                    }
                }

                // Save all users with encoded passwords
                userRepository.saveAll(allUsers.Users());
                log.info("Successfully saved {} users to the database.", allUsers.Users().size());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        } else {
            log.info("Not loading users from JSON data because the collection already contains data.");
        }
    }
}
