package com.example.freelanceledger.income;

import com.example.freelanceledger.common.exception.ResourceNotFoundException;
import com.example.freelanceledger.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Transactional(readOnly = true)
    public List<Income> findAllForUser(User user) {
        return incomeRepository.findByUserOrderByReceivedOnDesc(user);
    }

    @Transactional(readOnly = true)
    public Income findByIdForUser(Long id, User user) {
        return incomeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("収入データが見つかりません: id=" + id));
    }

    @Transactional
    public Income create(User user, String clientName, String description, BigDecimal amount, LocalDate receivedOn) {
        Income income = new Income(user, clientName, description, amount, receivedOn);
        return incomeRepository.save(income);
    }

    @Transactional
    public Income update(Long id, User user, String clientName, String description, BigDecimal amount, LocalDate receivedOn) {
        Income income = findByIdForUser(id, user);
        income.update(clientName, description, amount, receivedOn);
        return income; // JPAのダーティチェックで更新される
    }

    @Transactional
    public void delete(Long id, User user) {
        Income income = findByIdForUser(id, user);
        incomeRepository.delete(income);
    }
}
