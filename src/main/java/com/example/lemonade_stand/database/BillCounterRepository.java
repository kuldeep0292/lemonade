package com.example.lemonade_stand.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillCounterRepository extends JpaRepository<BillCounterRecord, Integer> {
	// Custom query to find a bill by denomination
	BillCounterRecord findByBillDenomination(Integer denomination);

	// Custom query to find the latest sales record (by ID, descending)
	BillCounterRecord findFirstByOrderByIdDesc();
}
