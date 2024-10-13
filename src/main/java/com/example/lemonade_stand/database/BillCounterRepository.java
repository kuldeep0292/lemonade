package com.example.lemonade_stand.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillCounterRepository extends JpaRepository<BillCounter, Integer> {
    // Custom query to find a bill by denomination
    BillCounter findByBillDenomination(Integer denomination);
}
