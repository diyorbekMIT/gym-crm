package com.gym.crm.model;

import java.time.LocalDate;
import java.util.Objects;

public class Trainee extends User {

    private Long userId;
    private LocalDate dateOfBirth;
    private String address;

    public Trainee() {
    }

    public Trainee(
            Long userId,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean active,
            LocalDate dateOfBirth,
            String address
    ) {
        super(firstName, lastName, username, password, active);
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        Trainee trainee = (Trainee) o;

        return Objects.equals(userId, trainee.userId)
                && Objects.equals(dateOfBirth, trainee.dateOfBirth)
                && Objects.equals(address, trainee.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, dateOfBirth, address);
    }

    @Override
    public String toString() {
        return "Trainee{"
                + "userId=" + userId
                + ", firstName='" + getFirstName() + '\''
                + ", lastName='" + getLastName() + '\''
                + ", username='" + getUsername() + '\''
                + ", active=" + isActive()
                + ", dateOfBirth=" + dateOfBirth
                + ", address='" + address + '\''
                + '}';
    }
}