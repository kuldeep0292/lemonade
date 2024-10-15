package com.example.lemonade_stand.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<SalesRecord, Integer> {
	// Retrieve the single sales record (assuming there's only one)
	SalesRecord findFirstByOrderByIdDesc();
}
