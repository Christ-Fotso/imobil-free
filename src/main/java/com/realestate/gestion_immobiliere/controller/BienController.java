package com.realestate.gestion_immobiliere.controller;

import org.springframework.format.annotation.DateTimeFormat;
import com.realestate.gestion_immobiliere.model.Bien;
import com.realestate.gestion_immobiliere.model.Transaction;
import com.realestate.gestion_immobiliere.model.User;
import com.realestate.gestion_immobiliere.service.BienService;
import com.realestate.gestion_immobiliere.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import com.realestate.gestion_immobiliere.util.FileUploadUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import java.util.Optional;

@Controller
@RequestMapping("/biens")
public class BienController {
    @Autowired
    private BienService bienService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public String getAllBiens(Model model) {
        List<Bien> biens = bienService.findAll();
        model.addAttribute("biens", biens);
        return "biens/list";
    }



    @PostMapping
    public String addBien(@ModelAttribute Bien bien) {
        bienService.save(bien);
        return "redirect:/";
    }
    @GetMapping("/edit/{id}")
    public String showEditBienForm(@PathVariable Long id, Model model) {
        Optional<Bien> optionalBien = bienService.findById(id);
        if (optionalBien.isPresent()) {
            model.addAttribute("bien", optionalBien.get());
            return "biens/edit";  // Assurez-vous que le fichier biens/edit.html existe
        } else {
            return "redirect:/";  // Rediriger si le bien n'est pas trouvé
        }
    }


    @PostMapping("/edit")
    public String editBien(@ModelAttribute Bien bien, @RequestParam("imageFile") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String uploadDir = "upload-dir/images";

            try {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
                bien.setImageUrl("/images/" + fileName); // Mise à jour du chemin de l'image si une nouvelle est téléchargée
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", "Erreur lors du téléchargement de l'image.");
                return "redirect:/biens/edit/" + bien.getId();
            }
        }
        bienService.save(bien);
        return "redirect:indexPoprietaire";  // Rediriger vers la page d'accueil après l'édition
    }


    @GetMapping("/delete/{id}")
    public String deleteBien(@PathVariable Long id) {
        bienService.delete(id);
        return "redirect:/indexProprietaire";
    }
    @GetMapping("/rent/{id}")
    public String showRentBienForm(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vous devez être connecté pour accéder à cette page.");
            return "redirect:/users/login";
        }

        Optional<Bien> optionalBien = bienService.findById(id);
        if (!optionalBien.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bien non trouvé.");
            return "redirect:/";
        }

        model.addAttribute("bien", optionalBien.get());
        return "louer";
    }

    @PostMapping("/louer/{id}")
    public String rentBien(
            @PathVariable Long id,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1. Verifica utente
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi essere connesso per affittare un bene.");
            return "redirect:/users/login";
        }

        // 2. Carica il bien
        Bien bien = bienService.findById(id)
                .orElseThrow(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Bene non trovato.");
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });

        // 3. Crea e salva la transazione
        Transaction transaction = new Transaction();
        transaction.setBien(bien);
        transaction.setUser(currentUser);
        transaction.setStartDate(startDate);
        transaction.setEndDate(endDate);
        transactionService.save(transaction);

        return "redirect:/users/profile";
    }

}
