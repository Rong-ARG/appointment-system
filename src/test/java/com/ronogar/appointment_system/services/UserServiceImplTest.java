package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.exceptions.DuplicateResourceException;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import com.ronogar.appointment_system.services.auth.CurrentUserService;
import com.ronogar.appointment_system.services.user.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getAllUsers() {

        Account account = new Account();
        account.setEmail("iwannajob@gmail.com");


        User user = new User();
        user.setFirstName("Ariel");
        user.setAccount(account);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Ariel", result.getFirst().getFirstName());
    }

    @Test
    public void getAllUsers_returnsEmptyList_whenNoUsersExist() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(0, result.size());
    }

    @Test
    public void getUserById_returnsUser_WhenIsOwnProfile() {

        Account account = new Account();
        account.setEmail("Iwannajob@gmail.com");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Juan");
        user.setAccount(account);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(currentUserService.getAuthenticatedUser()).thenReturn(user);

        Account authAccount = new Account();
        authAccount.setId(2L);
        when(currentUserService.getAuthenticatedAccount()).thenReturn(authAccount);


        UserResponseDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());
        assertEquals("Iwannajob@gmail.com", result.getEmail());

    }

    @Test
    void getUserById_throwsAccessDenied_whenNotOwnProfile() {
        User user = new User();
        user.setId(1L);

        User userdata = new User();
        userdata.setId(2L);

        Account account = new Account();
        account.setRoles(Set.of(Role.USER));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserService.getAuthenticatedUser()).thenReturn(userdata);
        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);

        assertThrows(AccessDeniedException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void getUserById_returnsUser_WhenIsAdminViewOtherProfile() {
        Account targetAccount = new Account();
        targetAccount.setEmail("Marcos@gmail.com");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Juan");
        user.setAccount(targetAccount);

        User userdata = new User();
        userdata.setId(2L);

        Account account = new Account();
        account.setRoles(Set.of(Role.ADMIN));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserService.getAuthenticatedUser()).thenReturn(userdata);
        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);

        UserResponseDTO result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());

    }

    @Test
    public void getUserByEmail_returnsUser_WhenIsSearchUser() {
        Account account = new Account();
        account.setEmail("Iwannajob@gmail.com");

        User user = new User();
        user.setAccount(account);

        when(userRepository.findByAccountEmail("Iwannajob@gmail.com")).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserByEmail("Iwannajob@gmail.com");

        assertNotNull(result);
        assertEquals("Iwannajob@gmail.com", result.getEmail());
    }

    @Test
    public void getUserByEmail_throwsResourceNotFound_whenEmailNotFound() {

        when(userRepository.findByAccountEmail("Iwannajob@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("Iwannajob@gmail.com"));
    }

    @Test
    public void getUserByLastname_returnsUser_WhenIsSearchUser() {

        Account account = new Account();
        account.setEmail("Iwannajob@gmail.com");

        User user = new User();
        user.setLastName("llanos");
        user.setAccount(account);

        when(userRepository.findByLastName("llanos")).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.getUserByLastName("llanos");

        assertNotNull(result);
        assertEquals("llanos", result.getFirst().getLastName());


    }

    @Test
    public void getUserByLastname_throwsResourceNotFound_whenLastNameNotFound() {
        when(userRepository.findByLastName("llanos")).thenReturn(List.of());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByLastName("llanos"));

    }

    @Test
    public void CreateUser_returnsUser_WhenIsCreateUser() {

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("Iwannajob@gmail.com");
        userRequestDTO.setPassword("1234");
        userRequestDTO.setFirstName("Juan");

        when(accountRepository.findByEmail("Iwannajob@gmail.com")).thenReturn(Optional.empty());

        when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("PasswordEncrypted");

        when(accountRepository.save(any(Account.class))).then(i -> i.getArgument(0));

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponseDTO result = userService.createUser(userRequestDTO);

        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());

    }

    @Test
    public void CreateUser_throwsDuplicateUser_whenEmailAlreadyExists() {

        User existingUser = new User();

        Account account = new Account();
        account.setEmail("Iwannajob@gmail.com");
        account.setUser(existingUser);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("Iwannajob@gmail.com");

        when(accountRepository.findByEmail("Iwannajob@gmail.com")).thenReturn(Optional.of(account));

        assertThrows(DuplicateResourceException.class,
                () -> userService.createUser(userRequestDTO));

    }

    @Test
    public void updateUser_returnsUser_WhenIsUpdateUser() {

        Account account = new Account();
        account.setEmail("Iwannajob@gmail.com");

        User user = new User();
        user.setId(1L);
        user.setAccount(account);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("Juan");
        userRequestDTO.setLastName("llanos");
        userRequestDTO.setPassword("1234");
        userRequestDTO.setEmail("newEmail@gmail.com");
        userRequestDTO.setPhone("125123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserService.getAuthenticatedUser()).thenReturn(user);
        when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("PasswordEncrypted");

        userService.updateUser(1L, userRequestDTO);

        assertEquals("Juan", user.getFirstName());
        assertEquals("llanos", user.getLastName());
        assertEquals("newEmail@gmail.com", account.getEmail());
        verify(accountRepository).save(account);
        verify(userRepository).save(user);

    }

    @Test
    public void updateUser_throwsAccessDeniedException_WhenNotIsUser() {

       User authUser = new User();
       authUser.setId(1L);

        Account account = new Account();
        User user = new User();
        user.setId(2L);
        user.setAccount(account);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserService.getAuthenticatedUser()).thenReturn(authUser);

        assertThrows(AccessDeniedException.class,
                () -> userService.updateUser(1L, new UserRequestDTO()));

    }


}
