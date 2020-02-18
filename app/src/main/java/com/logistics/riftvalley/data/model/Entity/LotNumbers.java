package com.logistics.riftvalley.data.model.Entity;

public class LotNumbers {

    String lotNumbers;
    String grade;

    public LotNumbers(String lotNumbers, String grade) {
        this.lotNumbers = lotNumbers;
        this.grade = grade;
    }

    public String getLotNumbers() {
        return lotNumbers;
    }

    public void setLotNumbers(String lotNumbers) {
        this.lotNumbers = lotNumbers;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
