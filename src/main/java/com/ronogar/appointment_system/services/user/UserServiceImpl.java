package com.ronogar.appointment_system.services.user;

import com.ronogar.appointment_system.dtos.user.UserPatchDTO;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.exceptions.DuplicateResourceException;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    private User toEntity(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhone(userRequestDTO.getPhone());
        return user;
    }

    private UserResponseDTO toDto(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setEmail(user.getAccount().getEmail());
        userResponseDTO.setPhone(user.getPhone());
        return userResponseDTO;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        return userRepository.findByAccountEmail(email)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    @Override
    public List<UserResponseDTO> getUserByLastName(String lastname) {
        List<User> users = userRepository.findByLastName(lastname);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User with last name " + lastname + " not found");
        }
        return users.stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        Account account = accountRepository.findByEmail(userRequestDTO.getEmail())
                .map(this::attachUserRole)
                .orElseGet(() -> createAccount(userRequestDTO));

        User user = toEntity(userRequestDTO);
        user.setAccount(account);
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    private Account attachUserRole(Account account) {
        if (account.getUser() != null) {
            throw new DuplicateResourceException(
                    "A user profile already exists with email " + account.getEmail()
            );
        }
        account.getRoles().add(Role.USER);
        return accountRepository.save(account);
    }

    private Account createAccount(UserRequestDTO userRequestDTO) {
        Account account = new Account();
        account.setEmail(userRequestDTO.getEmail());
        account.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        account.setRoles(new HashSet<>(Set.of(Role.USER)));
        return accountRepository.save(account);
    }

    @Override
    public void updateUser(Long id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhone(userRequestDTO.getPhone());

        Account account = user.getAccount();
        account.setEmail(userRequestDTO.getEmail());
        account.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        accountRepository.save(account);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        Account account =  user.getAccount();
        userRepository.deleteById(id);
        if(account.getProfessional() == null) {
            accountRepository.delete(account);
        } else {
            account.getRoles().remove(Role.USER);
            accountRepository.save(account);
        }
    }

    @Override
    public void patchUser(Long id, UserPatchDTO userPatchDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        if (userPatchDTO.getFirstName() != null) {
            user.setFirstName(userPatchDTO.getFirstName());
        }
        if (userPatchDTO.getLastName() != null) {
            user.setLastName(userPatchDTO.getLastName());
        }
        if (userPatchDTO.getEmail() != null) {
            Account account = user.getAccount();
            account.setEmail(userPatchDTO.getEmail());
            accountRepository.save(account);
        }
        if (userPatchDTO.getPhone() != null) {
            user.setPhone(userPatchDTO.getPhone());
        }
        userRepository.save(user);
    }
}
