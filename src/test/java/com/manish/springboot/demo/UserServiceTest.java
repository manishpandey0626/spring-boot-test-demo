package com.manish.springboot.demo;

import com.manish.springboot.demo.entity.User;
import com.manish.springboot.demo.exception.UserNotFoundException;
import com.manish.springboot.demo.repository.UserRepository;
import com.manish.springboot.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User person1;
    private User person2;

    @BeforeEach
    void setUp() {
        person1 = new User(1L, "John Doe", 17);
        person2 = new User(2L, "Jane Doe", 35);
    }

    @Test
    void testGetAllPeople() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(person1, person2));
        List<User> people = userService.findAllUsers();
        assertThat(people).containsExactly(person1, person2);
    }

    @Test
    void testFindUserByName() {
        when(userRepository.findFirstByName(person1.getName())).thenReturn(Optional.of(person1));
        Optional<User> retrievedUser = userService.findUserByName(person1.getName());
        assertThat(retrievedUser).contains(person1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"John Doe", "Jane Doe"})
    void testAddUser(String name) {
        User person = new User(3L, name, 20);
        when(userRepository.save(person)).thenReturn(person);

        User savedUser = userService.saveUser(person);

        assertThat(savedUser.getName()).isEqualTo(name);
        verify(userRepository).save(person);
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(person1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(1L, new User(null, "Johnny", 40));

        assertThat(updated.getName()).isEqualTo("Johnny");
        assertThat(updated.getAge()).isEqualTo(40);
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, person2))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never()).deleteById(any());
    }

    @ParameterizedTest
    @CsvSource({
            "null, User is null",
            "10, User is a minor",
            "30, User is an adult",
            "70, User is a senior"
    })
    void testProcessUser(String ageString, String expectedMessage) {
        User user = "null".equals(ageString) ? null : new User(1L, "Rohit", Integer.parseInt(ageString));
        assertEquals(expectedMessage, userService.processUser(user));
    }
}
