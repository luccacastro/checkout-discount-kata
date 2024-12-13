package com.example.cdldiscountshell;

import com.example.cdldiscountshell.shell.CheckoutShell;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.standard.ShellComponent;


import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ShellComponent
class CheckoutShellTest {

    @Autowired
    private CheckoutShell checkoutShell;

    @Test
    void testScanCommand() {
        String response = checkoutShell.scan("A", 1, null);
        assertTrue(response.contains("Running total: 50 pence"));
    }

    @Test
    void testScanFromJsonCommand() {
        String response = checkoutShell.scan(null, 1, "target/classes/scandata.json");
        assertTrue(response.contains("455"));
    }

    @Test
    void testCheckoutTotalCommand() {
        checkoutShell.scan("A", 3, null);
        String response = checkoutShell.checkoutTotal(false);
        assertTrue(response.contains("Final total: 130 pence"));
    }

    @Test
    void testCheckoutTotalWithReceipt() {
        checkoutShell.scan("A", 3, null);
        String response = checkoutShell.checkoutTotal(true);
        assertTrue(response.contains("CDL SOLUTIONS"));
    }

    @Test
    void testResetCommand() {
        checkoutShell.scan("A", 3, null);
        checkoutShell.reset();
        String response = checkoutShell.checkoutTotal(false);
        assertTrue(response.contains("No items have been added"));
    }
}

