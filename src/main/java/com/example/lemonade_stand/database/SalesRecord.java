package com.example.lemonade_stand.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SalesRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the record
    private int totalLemonadesSold; // Total number of lemonades sold

    // Default constructor
    public SalesRecord() {
        this.totalLemonadesSold = 0; // Initialize with zero sold
    }

    public Long getId() {
        return id;
    }

    public int getTotalLemonadesSold() {
        return totalLemonadesSold;
    }

    public void setTotalLemonadesSold(int totalLemonadesSold) {
        this.totalLemonadesSold = totalLemonadesSold;
    }

    // Method to increment the total lemonades sold
    public void incrementLemonadesSold(int amount) {
        this.totalLemonadesSold += amount;
    }
}
