package com.example.cdldiscountshell.shell;

import com.example.cdldiscountshell.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ShellComponent
public class CheckoutShell {

    private final CheckoutService checkoutService;

    @Autowired
    public CheckoutShell(CheckoutService checkout) {
        this.checkoutService = checkout;
    }

    @ShellMethod(key="scan" ,value="Scan an item by entering its SKU (e.g., A, B, C, D).")
    public String scan(
            @ShellOption(defaultValue = ShellOption.NULL) String sku,
            @ShellOption(defaultValue = "1") int amount,
            @ShellOption(defaultValue = ShellOption.NULL) String json) {

        try {
            if (json != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Integer> items = mapper.readValue(new File(json), new TypeReference<Map<String, Integer>>() {});

                items.forEach((itemSku, itemQuantity) -> checkoutService.scan(itemSku.toUpperCase(), itemQuantity));
                return "Items scanned from JSON file: " + json + ". Running total: " + checkoutService.calculateCheckoutTotal() + " pence.";
            }

            if (sku != null) {
                checkoutService.scan(sku.toUpperCase(), amount);
                return "Item scanned: " + sku + ". Running total: " + checkoutService.calculateCheckoutTotal() + " pence.";
            }

            return "You must provide either an SKU with optional amount (default to 1) or a JSON file path.";
        } catch (Exception e) {
            return "Error scanning items: " + e.getMessage();
        }
    }

    @ShellMethod(key="total",value="Calculate the total price of scanned items.")
    public String total() {
        return "Final total: " + checkoutService.calculateCheckoutTotal() + " pence.";
    }

    @ShellMethod(key="reset", value="Reset the checkout system, clearing all scanned items.")
    public String reset() {
        checkoutService.reset();
        return "Checkout has been reset.";
    }

    @ShellMethod(key="checkout-total", value="Generate and display a detailed receipt.")
    public String checkoutTotal(@ShellOption(defaultValue = "false") boolean showReceipt) {
        if (checkoutService.calculateCheckoutTotal() == 0) {
            return "No items have been added. Please scan an item first.";
        }
        return showReceipt ? checkoutService.generateReceipt() : "Final total: " + checkoutService.calculateCheckoutTotal() + " pence.";
    }

    @ShellMethod(key="exit", value="Exit the application.")
    public String exit() {
        System.exit(0);
        return "Exiting the application. Goodbye!";
    }


    @ShellMethod(key="catalogue", value="Display a colored table of all available products and their pricing.")
    public String catalogue() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Item", "Unit Price (pence)", "Special Quantity", "Special Price (pence)"}); // Headers
        checkoutService.getPricingRules().forEach(rule -> {
            rows.add(new String[]{
                    rule.getProduct().getSku(),
                    String.valueOf(rule.getProduct().getUnitPrice()),
                    String.valueOf(rule.getSpecialQuantity()),
                    String.valueOf(rule.getSpecialPrice())
            });
        });

        TableModel model = new ArrayTableModel(
                rows.toArray(new String[0][0])
        );

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_heavy);

        tableBuilder.on(CellMatchers.table())
                .addAligner(SimpleHorizontalAligner.center);

        return tableBuilder.build().render(80);
    }


}
