package com.manish.springboot.demo;

import com.manish.springboot.demo.controller.UserController;
import com.manish.springboot.demo.entity.User;
import com.manish.springboot.demo.exception.UserNotFoundException;
import com.manish.springboot.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", 30);
    }

    @Test
    void testSaveUser() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":5,\"name\":\"John Doe\",\"age\":30}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/users/1"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService).saveUser(argThat(u -> u.getId() == null));
    }

    @Test
    void testSaveUserInvalid() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"age\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserByName() throws Exception {
        when(userService.findUserByName(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetUserByNameNotFound() throws Exception {
        when(userService.findUserByName(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/Nobody"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(new User(1L, "Jane Doe", 31));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Jane Doe\",\"age\":31}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.age").value(31));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(put("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Jane Doe\",\"age\":31}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        doThrow(new UserNotFoundException(99L)).when(userService).deleteUser(99L);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}
