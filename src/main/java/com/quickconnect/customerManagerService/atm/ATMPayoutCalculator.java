package com.quickconnect.customerManagerService.atm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ATMPayoutCalculator {

    // Available denominations
    private static final List<Integer> DENOMINATIONS = Arrays.asList(100, 50, 10); // descending order
    private static final List<Integer> TARGET_AMOUNTS = Arrays.asList(30, 50, 60, 80, 140, 230, 370, 610, 980);

    public static void main(String[] args) {
        for (int amount : TARGET_AMOUNTS) {
            System.out.println("Payout combinations for " + amount + " EUR:");
            List<List<Integer>> combinations = new ArrayList<>();
            findCombinations(amount, 0, new ArrayList<>(), combinations);
            for (List<Integer> combo : combinations) {
                System.out.printf("  %d x 100 EUR, %d x 50 EUR, %d x 10 EUR%n",
                        combo.get(0), combo.get(1), combo.get(2));
            }
            if (combinations.isEmpty()) {
                System.out.println("  No valid combinations.");
            }
            System.out.println();
        }
    }

    private static void findCombinations(int amount, int index, List<Integer> current, List<List<Integer>> result) {
        if (index == DENOMINATIONS.size()) {
            if (amount == 0) {
                result.add(new ArrayList<>(current));
            }
            return;
        }

        int denom = DENOMINATIONS.get(index);
        for (int count = 0; count <= amount / denom; count++) {
            current.add(count);
            findCombinations(amount - count * denom, index + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
