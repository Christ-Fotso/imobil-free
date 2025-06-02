package com.realestate.gestion_immobiliere.controller;

import com.realestate.gestion_immobiliere.model.Transaction;
import com.realestate.gestion_immobiliere.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public String listTransactions(Model model) {
        List<Transaction> transactions = transactionService.findAll();
        model.addAttribute("transactions", transactions);
        return "transactions/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "transactions/create";
    }

    @PostMapping("/create")
    public String createTransaction(@ModelAttribute Transaction transaction, RedirectAttributes redirectAttributes) {
        Transaction savedTransaction = transactionService.save(transaction);
        redirectAttributes.addFlashAttribute("successMessage", "Transaction created successfully with ID " + savedTransaction.getId());
        return "redirect:/transactions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Transaction> transaction = transactionService.findById(id);
        if (transaction.isPresent()) {
            model.addAttribute("transaction", transaction.get());
            return "transactions/edit";
        } else {
            return "redirect:/transactions";
        }
    }

    @PostMapping("/edit")
    public String updateTransaction(@ModelAttribute Transaction transaction, RedirectAttributes redirectAttributes) {
        transactionService.save(transaction);
        redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully");
        return "redirect:/transactions";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transactionService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Transaction deleted successfully");
        return "redirect:/transactions";
    }
}
