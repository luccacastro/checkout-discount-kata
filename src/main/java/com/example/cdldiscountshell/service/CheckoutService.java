package com.example.cdldiscountshell.service;

import com.example.cdldiscountshell.model.PricingRule;
import com.example.cdldiscountshell.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class CheckoutService {
    private static final int RECEIPT_WIDTH = 60;

    private List<PricingRule> pricingRules;
    private Map<String, Integer> items;

    @Autowired
    public CheckoutService() {
        this.pricingRules = new ArrayList<>();
        this.items = new HashMap<>();

        Product productA = new Product("A", "Apple", 50);
        Product productB = new Product("B", "Banana", 30);
        Product productC = new Product("C", "Carrot", 20);
        Product productD = new Product("D", "Date", 15);

        pricingRules.add(new PricingRule(productA, 3, 130));
        pricingRules.add(new PricingRule(productB, 2, 45));
        pricingRules.add(new PricingRule(productC, 0, 0));
        pricingRules.add(new PricingRule(productD, 0, 0));
    }

    public void scan(String item, int quantity) {
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public int calculateCheckoutTotal() {
        return items.entrySet().stream()
                .mapToInt(entry -> calculateItemTotal(entry.getKey(), entry.getValue()))
                .sum();
    }

    private int calculateItemTotal(String item, int quantity) {
        PricingRule rule = findPricingRuleBySku(item);
        if (rule == null) {
            return 0;
        }

        int unitPrice = rule.getProduct().getUnitPrice();
        int specialQuantity = rule.getSpecialQuantity();
        int specialPrice = rule.getSpecialPrice();

        if (specialQuantity > 0 && quantity >= specialQuantity) {
            int specialBundles = quantity / specialQuantity;
            int remainder = quantity % specialQuantity;
            return specialBundles * specialPrice + remainder * unitPrice;
        }
        return quantity * unitPrice;
    }

    private PricingRule findPricingRuleBySku(String sku) {
        return pricingRules.stream()
                .filter(rule -> rule.getProduct().getSku().equalsIgnoreCase(sku))
                .findFirst()
                .orElse(null);
    }

    public void reset() {
        items.clear();
    }

    public List<PricingRule> getPricingRules() {
        return pricingRules;
    }


    private String generateLine(char leftChar, char fillChar, char rightChar) {
        return leftChar + String.valueOf(fillChar).repeat(RECEIPT_WIDTH - 2) + rightChar + "\n";
    }

    private String centerText(String text) {
        int padding = (RECEIPT_WIDTH - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(RECEIPT_WIDTH - text.length() - padding);
    }

    private String formatRow(String leftText, String rightText) {
        int availableSpace = RECEIPT_WIDTH - leftText.length() - rightText.length() - 4; // Accounting for │ │
        return "│ " + leftText + " ".repeat(Math.max(0, availableSpace)) + rightText + " │\n";
    }

    private void appendHeader(StringBuilder receipt, String headerText) {
        receipt.append(generateLine('┌', '─', '┐'));
        receipt.append(formatRow(centerText(headerText), ""));
        receipt.append(generateLine('├', '─', '┤'));
    }

    private void appendFooter(StringBuilder receipt) {
        receipt.append(generateLine('└', '─', '┘'));
    }

    private void appendDateAndToken(StringBuilder receipt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy • HH:mm:ss");
        String dateTime = LocalDateTime.now().format(formatter);
        String token = UUID.randomUUID().toString().substring(0, 16).toUpperCase();

        receipt.append(formatRow("Date:", dateTime));
        receipt.append(generateLine('├', '─', '┤'));
        receipt.append(formatRow("Token:", token));
        receipt.append(generateLine('├', '─', '┤'));
    }

    private void appendItems(StringBuilder receipt) {
        items.forEach((item, quantity) -> {
            PricingRule rule = findPricingRuleBySku(item);
            if (rule != null) {
                int total = calculateItemTotal(item, quantity);
                int originalTotal = rule.getProduct().getUnitPrice() * quantity;

                receipt.append(formatRow(item + " x" + quantity, total + " pence"));
                if (originalTotal != total) {
                    receipt.append(formatRow("", "(was " + originalTotal + " pence)"));
                }
            }
        });
    }

    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        appendHeader(receipt, "CDL SOLUTIONS");

        appendDateAndToken(receipt);

        appendItems(receipt);

        receipt.append(generateLine('├', '─', '┤'));
        receipt.append(formatRow("TOTAL:", calculateCheckoutTotal() + " pence"));
        appendFooter(receipt);

        return receipt.toString();
    }
}