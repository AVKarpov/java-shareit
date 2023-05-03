package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveNewUserTest() {
        User user = User.builder()
                .name("Test user name")
                .email("test@test.com")
                .build();

        Assertions.assertNull(user.getId());
        userRepository.save(user);
        Assertions.assertNotNull(user.getId());
    }

    @Test
    void findAllByOrderByIdAscTest() {
        User user1 = User.builder()
                .name("Test user1 name")
                .email("test1@test.com")
                .build();
        User user2 = User.builder()
                .name("Test user2 name")
                .email("test2@test.com")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAllByOrderByIdAsc();
        Assertions.assertEquals(2, users.size(), "Количество пользователе не совпадает");
        Assertions.assertEquals(user1.getName(), users.get(0).getName());
        Assertions.assertEquals(user2.getName(), users.get(1).getName());
    }

}
