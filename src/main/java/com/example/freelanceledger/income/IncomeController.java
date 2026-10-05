package com.example.freelanceledger.income;

import com.example.freelanceledger.user.AppUserDetails;
import com.example.freelanceledger.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    private User currentUser(AppUserDetails principal) {
        return principal.getUser();
    }

    @GetMapping
    public String list(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        model.addAttribute("incomes", incomeService.findAllForUser(currentUser(principal)));
        return "income/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new IncomeForm());
        return "income/form";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal AppUserDetails principal,
                          @Valid @ModelAttribute("form") IncomeForm form,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "income/form";
        }
        incomeService.create(currentUser(principal), form.getClientName(), form.getDescription(),
                form.getAmount(), form.getReceivedOn());
        return "redirect:/incomes";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal AppUserDetails principal,
                            @PathVariable Long id, Model model) {
        Income income = incomeService.findByIdForUser(id, currentUser(principal));
        IncomeForm form = new IncomeForm();
        form.setId(income.getId());
        form.setClientName(income.getClientName());
        form.setDescription(income.getDescription());
        form.setAmount(income.getAmount());
        form.setReceivedOn(income.getReceivedOn());
        model.addAttribute("form", form);
        return "income/form";
    }

    @PostMapping("/{id}")
    public String update(@AuthenticationPrincipal AppUserDetails principal,
                          @PathVariable Long id,
                          @Valid @ModelAttribute("form") IncomeForm form,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "income/form";
        }
        incomeService.update(id, currentUser(principal), form.getClientName(), form.getDescription(),
                form.getAmount(), form.getReceivedOn());
        return "redirect:/incomes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        incomeService.delete(id, currentUser(principal));
        return "redirect:/incomes";
    }

    /** 画面フォームとエンティティを分けるための入力用クラス */
    public static class IncomeForm {
        private Long id;

        @NotBlank(message = "取引先名を入力してください")
        private String clientName;

        private String description;

        @NotNull(message = "金額を入力してください")
        @DecimalMin(value = "0.0", message = "金額は0以上で入力してください")
        private BigDecimal amount;

        @NotNull(message = "入金日を入力してください")
        private LocalDate receivedOn;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public LocalDate getReceivedOn() { return receivedOn; }
        public void setReceivedOn(LocalDate receivedOn) { this.receivedOn = receivedOn; }
    }
}
