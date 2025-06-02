package com.realestate.gestion_immobiliere.service;

import com.realestate.gestion_immobiliere.model.User;
import com.realestate.gestion_immobiliere.repository.UserRepository;
import com.realestate.gestion_immobiliere.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable((User) session.getAttribute("currentUser"));
    }

    public void logout() {
        session.invalidate();
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }

        User user = userOpt.get();

        // Vérifier l'ancien mot de passe
        if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        }

        // Hacher et sauvegarder le nouveau mot de passe
        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
        user.setPassword(hashedNewPassword);
        userRepository.save(user);

        // Mettre à jour la session si c'est l'utilisateur connecté
        Optional<User> currentUser = getCurrentUser();
        if (currentUser.isPresent() && currentUser.get().getId().equals(userId)) {
            session.setAttribute("currentUser", user);
        }
    }

    public User updateUserProfile(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.setName(user.getName());
            updatedUser.setEmail(user.getEmail());
            session.setAttribute("currentUser", updatedUser);
            return userRepository.save(updatedUser);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmail(email));

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.verifyPassword(password, user.getPassword())) {
                session.setAttribute("currentUser", user);
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public User register(User user) {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }
}
