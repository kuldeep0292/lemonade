package com.example.lemonade_stand.database;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BillCounter {

    @Id
    private Integer billDenomination; // The bill value (5, 10, 20)
    private Integer count; // Number of bills

    // Constructors, Getters, Setters
    public BillCounter() {}

    public BillCounter(Integer billDenomination, Integer count) {
        this.billDenomination = billDenomination;
        this.count = count;
    }

    public Integer getBillDenomination() {
        return billDenomination;
    }

    public void setBillDenomination(Integer billDenomination) {
        this.billDenomination = billDenomination;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
