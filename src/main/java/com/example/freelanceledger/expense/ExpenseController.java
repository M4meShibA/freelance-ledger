package com.example.freelanceledger.expense;

import com.example.freelanceledger.user.AppUserDetails;
import com.example.freelanceledger.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    private User currentUser(AppUserDetails principal) {
        return principal.getUser();
    }

    @GetMapping
    public String list(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        model.addAttribute("expenses", expenseService.findAllForUser(currentUser(principal)));
        return "expense/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ExpenseForm());
        model.addAttribute("categories", expenseService.findAllCategories());
        return "expense/form";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal AppUserDetails principal,
                          @Valid @ModelAttribute("form") ExpenseForm form,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", expenseService.findAllCategories());
            return "expense/form";
        }
        expenseService.create(currentUser(principal), form.getCategoryId(), form.getDescription(),
                form.getAmount(), form.getSpentOn());
        return "redirect:/expenses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal AppUserDetails principal,
                            @PathVariable Long id, Model model) {
        Expense expense = expenseService.findByIdForUser(id, currentUser(principal));
        ExpenseForm form = new ExpenseForm();
        form.setId(expense.getId());
        form.setCategoryId(expense.getCategory().getId());
        form.setDescription(expense.getDescription());
        form.setAmount(expense.getAmount());
        form.setSpentOn(expense.getSpentOn());
        model.addAttribute("form", form);
        model.addAttribute("categories", expenseService.findAllCategories());
        return "expense/form";
    }

    @PostMapping("/{id}")
    public String update(@AuthenticationPrincipal AppUserDetails principal,
                          @PathVariable Long id,
                          @Valid @ModelAttribute("form") ExpenseForm form,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", expenseService.findAllCategories());
            return "expense/form";
        }
        expenseService.update(id, currentUser(principal), form.getCategoryId(), form.getDescription(),
                form.getAmount(), form.getSpentOn());
        return "redirect:/expenses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        expenseService.delete(id, currentUser(principal));
        return "redirect:/expenses";
    }

    /** 画面フォームとエンティティを分けるための入力用クラス */
    public static class ExpenseForm {
        private Long id;

        @NotNull(message = "勘定科目を選択してください")
        private Long categoryId;

        private String description;

        @NotNull(message = "金額を入力してください")
        @DecimalMin(value = "0.0", message = "金額は0以上で入力してください")
        private BigDecimal amount;

        @NotNull(message = "支出日を入力してください")
        private LocalDate spentOn;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public LocalDate getSpentOn() { return spentOn; }
        public void setSpentOn(LocalDate spentOn) { this.spentOn = spentOn; }
    }
}
