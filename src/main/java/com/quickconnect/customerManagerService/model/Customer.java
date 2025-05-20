package com.quickconnect.customerManagerService.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.ToString;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a customer in the system.
 * Includes validation rules to ensure data integrity:
 * - First and last names must not be blank
 * - Age must be 19 or older
 * - ID must be present and unique
 */
@Setter
@Getter
@NoArgsConstructor               // ✅ Required for Jackson
@AllArgsConstructor              // ✅ Used for tests and manual creation
@ToString
public class Customer {
    // Getters and setters
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Min(19)
    @Max(90)
    private int age;

    @NotNull
    private Integer id;

}
