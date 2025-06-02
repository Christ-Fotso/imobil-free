package com.realestate.gestion_immobiliere.controller;

import com.realestate.gestion_immobiliere.model.Bien;
import com.realestate.gestion_immobiliere.model.User;
import com.realestate.gestion_immobiliere.service.BienService;
import com.realestate.gestion_immobiliere.service.UserService;
import com.realestate.gestion_immobiliere.util.FileUploadUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private BienService bienService;
    @Autowired
    private UserService userService;

    // Affichage de la page d'accueil avec tous les biens
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Optional<User> userOpt = userService.getCurrentUser();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("proprietaire".equals(user.getType())) {
                return "redirect:/indexProprietaire";
            } else {
                return "redirect:/indexClient";
            }
        } else {
            return "redirect:/users/login";
        }
    }

    // Affichage du formulaire d'ajout de bien
    @GetMapping("/biens/add")
    public String showAddBienForm(Model model) {
        model.addAttribute("bien", new Bien()); // Envoi d'un nouveau bien vide au formulaire
        return "add-bien"; // retourne le fichier add-bien.html dans /src/main/resources/templates
    }

    // Traitement du formulaire d'ajout de bien
    @PostMapping("/biens/add")
    public String addBien(@ModelAttribute Bien bien, HttpSession session, RedirectAttributes redirectAttributes, @RequestParam("imageFile") MultipartFile file) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to add a property.");
            return "redirect:/users/login";
        }

        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String uploadDir = "upload-dir/images";
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
                bien.setImageUrl("/images/" + fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", "Error while uploading the image.");
                return "redirect:/biens/add";
            }
        }

        bien.setUser(currentUser);
        bienService.save(bien);
        return "redirect:/indexProprietaire";
    }

    // Recherche de biens avec des filtres
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String ville,
                         @RequestParam(required = false) String type,
                         @RequestParam(required = false) Integer minSuperficie,
                         @RequestParam(required = false) Integer maxSuperficie,
                         @RequestParam(required = false) Double minPrix,
                         @RequestParam(required = false) Double maxPrix,
                         Model model) {
        List<Bien> biens = bienService.search(ville, type, minSuperficie, maxSuperficie, minPrix, maxPrix);
        model.addAttribute("biens", biens);
        return "search-results";  // Utilisez la nouvelle vue pour afficher les résultats
    }



    @GetMapping("/indexClient")
    public String indexClient(Model model) {
        List<Bien> biens = bienService.findAll();
        model.addAttribute("biens", biens);
        return "indexClient";
    }

    @GetMapping("/indexProprietaire")
    public String indexProprietaire(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        List<Bien> biens = bienService.findAllByUserId(currentUser.getId());
        model.addAttribute("biens", biens);
        return "indexProprietaire";
    }


}
