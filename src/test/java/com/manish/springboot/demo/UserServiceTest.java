package com.manish.springboot.demo;


import com.manish.springboot.demo.entity.User;
import com.manish.springboot.demo.repository.UserRepository;
import com.manish.springboot.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User person1;
    private User person2;

    @BeforeEach
    void setUp() {
        person1 = new User(1L, "John Doe",17);
        person2 = new User(2L, "Jane Doe",35);

        when(userRepository.findAll()).thenReturn(Arrays.asList(person1, person2));
        when(userRepository.findByName(person1.getName())).thenReturn(Optional.of(person1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.existsById(1L)).thenReturn(true);
    }

    @Test
    void testGetAllPeople() {
        List<User> people = userService.findAllUsers();
        assertThat(people).hasSize(2);
        assertThat(people).containsExactly(person1, person2);
    }

    @Test
    void testGetUserById() {
        Optional<User> retrievedUser = userService.findUserByName(person1.getName());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get()).isEqualTo(person1);
    }

    @Test
    void testAddUser() {
        User user = new User(1L, "Manish",35);
        User savedUser = userService.saveUser(user);
        assertThat(savedUser).isEqualTo(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"John Doe", "Jane Doe"})
    void testAddUserParameterized(String name) {
        User person = new User(3L, name,20);
        when(userRepository.save(person)).thenReturn(person);

        User savedUser = userService.saveUser(person);

        assertThat(savedUser.getName()).isEqualTo(name);
        verify(userRepository, times(1)).save(person);
    }

    @ParameterizedTest
    @CsvSource({
            "null, User is null",
            "10, User is a minor",
            "30, User is an adult",
            "70, User is a senior"
    })
    void testProcessUser(String ageString, String expectedMessage) {
        Integer age = "null".equals(ageString) ? null : Integer.parseInt(ageString);
        User user = age != null ? new User(1L,"Rohit",age) : null;
        String result = userService.processUser(user);
        assertEquals(expectedMessage, result);
    }
}

