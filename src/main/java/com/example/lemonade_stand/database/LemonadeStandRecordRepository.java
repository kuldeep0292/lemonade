package com.example.lemonade_stand.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LemonadeStandRecordRepository extends JpaRepository<LemonadeStandRecord, Integer> {
	// Custom query to find a bill by denomination
	LemonadeStandRecord findByBillDenomination(Integer denomination);

	// Custom query to find the latest sales record (by ID, descending)
	LemonadeStandRecord findFirstByOrderByIdDesc();
}
