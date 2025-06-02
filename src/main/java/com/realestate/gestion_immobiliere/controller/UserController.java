    package com.realestate.gestion_immobiliere.controller;

    import com.realestate.gestion_immobiliere.model.Transaction;
    import com.realestate.gestion_immobiliere.model.User;
    import com.realestate.gestion_immobiliere.repository.TransactionRepository;
    import com.realestate.gestion_immobiliere.service.TransactionService;
    import com.realestate.gestion_immobiliere.service.UserService;
    import com.realestate.gestion_immobiliere.util.EmailSender;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;
    import jakarta.servlet.http.HttpSession;

    import java.time.LocalDate;
    import java.time.temporal.ChronoUnit;
    import java.util.List;
    import java.util.Optional;

    @Controller
    @RequestMapping("/users")
    public class UserController {

        @Autowired
        private UserService userService;

        @Autowired
        private TransactionService transactionService;
        @Autowired
        private TransactionRepository transactionRepository;

        private int verificationCode; // Code de vérification temporaire
        private Optional<User> loggedInUser;
        private User user;

        @GetMapping("/login")
        public String showLoginForm(Model model) {
            model.addAttribute("user", new User());
            return "users/login";
        }

        // Connexion de l'utilisateur et redirection vers la page 2FA
        @PostMapping("/login")
        public String login(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
            loggedInUser = userService.login(user.getEmail(), user.getPassword());
            if (loggedInUser.isPresent()) {
                // Générer et envoyer le code 2FA
                verificationCode = EmailSender.sendEmail(user.getEmail()); // Envoi du code par email
                this.user=user;
                return "users/2FA"; // Page pour entrer le code 2FA
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password");
                return "redirect:/users/login"; // Redirection en cas d'échec de connexion
            }
        }

        // Vérification du code 2FA
        @PostMapping("/2FA")
        public String verify2FA(@RequestParam String code,
                                RedirectAttributes redirectAttributes, HttpSession session) {

            if (Integer.parseInt(code) == verificationCode) {
                session.setAttribute("user", loggedInUser.get()); // Enregistrer l'utilisateur dans la session
                return "redirect:/" + (loggedInUser.get().getType().equals("client") ? "indexClient" : "indexProprietaire");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Code de vérification incorrect.");
                return "redirect:/users/2FA"; // Rediriger vers la page 2FA en cas de code incorrect
            }
        }

        @GetMapping("/2FA")
        public String show2FAForm() {
            // Afficher le formulaire de saisie du code de vérification 2FA
            return "users/2FA";
        }

        @GetMapping("/re2FA")
        public String re2FA() {
            // Afficher le formulaire de saisie du code de vérification 2FA
            verificationCode = EmailSender.sendEmail(user.getEmail()); // Envoi du code par email
            return "users/2FA";
        }

        @GetMapping("/register")
        public String showRegistrationForm(Model model) {
            model.addAttribute("user", new User());
            return "users/register";
        }

        @PostMapping("/register")
        public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
            User savedUser = userService.register(user);
            if (savedUser != null && savedUser.getId() != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Registration successful!");
                return "redirect:/users/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Registration failed! Please check your details and try again.");
                return "redirect:/users/register";
            }
        }

        @GetMapping("/profile")
        public String showProfile(HttpSession session, Model model) {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "redirect:/users/login";
            }

            model.addAttribute("user", currentUser);

            List<Transaction> transactions;
            if (currentUser.getType().equals("propriétaire")) {
                    transactions = transactionRepository.findByBienUserId(currentUser.getId());
            } else {
                transactions = transactionService.findByUser(currentUser);
            }
            model.addAttribute("transactions", transactions);

            // Calcul des totaux pour le récapitulatif financier
            if (currentUser.getType().equals("client")) {
                double totalMonthlyExpense = calculateMonthlyExpense(transactions);
                double totalExpense = calculateTotalExpense(transactions);
                model.addAttribute("totalMonthlyExpense", totalMonthlyExpense);
                model.addAttribute("totalExpense", totalExpense);
            } else if (currentUser.getType().equals("propriétaire")) {
                double totalMonthlyIncome = calculateMonthlyIncome(transactions);
                double annualIncome = calculateAnnualIncome(transactions);
                double totalIncome = calculateTotalIncome(transactions);
                model.addAttribute("totalMonthlyIncome", totalMonthlyIncome);
                model.addAttribute("annualIncome", annualIncome);
                model.addAttribute("totalIncome", totalIncome);
            }

            return currentUser.getType().equals("propriétaire") ? "profileProprietaire" : "profileClient";
        }

        private double calculateMonthlyExpense(List<Transaction> transactions) {
            LocalDate now = LocalDate.now();
            return transactions.stream()
                    .filter(t -> t.getStartDate().getMonthValue() == now.getMonthValue() &&
                            t.getStartDate().getYear() == now.getYear())
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }

        private double calculateTotalExpense(List<Transaction> transactions) {
            return transactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }

        private double calculateMonthlyIncome(List<Transaction> transactions) {
            LocalDate now = LocalDate.now();
            return transactions.stream()
                    .filter(t -> t.getEndDate().getMonthValue() == now.getMonthValue() &&
                            t.getEndDate().getYear() == now.getYear())
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }

        private double calculateAnnualIncome(List<Transaction> transactions) {
            LocalDate now = LocalDate.now();
            return transactions.stream()
                    .filter(t -> t.getEndDate().getYear() == now.getYear())
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }

        private double calculateTotalIncome(List<Transaction> transactions) {
            return transactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }

        @GetMapping("/logout")
        public String logout(HttpSession session) {
            session.invalidate();
            return "redirect:/users/login";
        }

    }
