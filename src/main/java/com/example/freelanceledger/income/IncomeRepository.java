package com.example.freelanceledger.income;

import com.example.freelanceledger.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserOrderByReceivedOnDesc(User user);

    Optional<Income> findByIdAndUser(Long id, User user);
}
