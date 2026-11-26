package com.gym.workload.model;

import java.util.HashMap;
import java.util.Map;

public class TrainerWorkloadEntry {

    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;

    // Map<year, Map<month(1-12), totalMinutes>>
    private final Map<Integer, Map<Integer, Integer>> yearlySummary = new HashMap<>();

    public TrainerWorkloadEntry(
            String username,
            String firstName,
            String lastName,
            boolean isActive
    ) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    // Called when ActionType = ADD
    public void addMinutes(int year, int month, int minutes) {
        yearlySummary
                .computeIfAbsent(year, y -> new HashMap<>())
                .merge(month, minutes, Integer::sum);
    }

    // Called when ActionType = DELETE
    public void subtractMinutes(int year, int month, int minutes) {
        Map<Integer, Integer> monthMap = yearlySummary.get(year);
        if (monthMap == null) return;

        monthMap.merge(month, -minutes, Integer::sum);

        // Clean up: remove month if total hits 0 or below
        monthMap.entrySet().removeIf(e -> e.getValue() <= 0);

        // Clean up: remove year if no months left
        if (monthMap.isEmpty()) {
            yearlySummary.remove(year);
        }
    }

    // --- Getters & Setters ---

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Map<Integer, Map<Integer, Integer>> getYearlySummary() {
        return yearlySummary;
    }
}