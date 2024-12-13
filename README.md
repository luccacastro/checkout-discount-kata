# Checkout Discount Shell

## Introduction
Checkout Discount Shell is a Java-based command-line application designed to simulate a supermarket checkout system with special pricing rules for various products. It uses modern technologies such as **Java 17**, **Spring Shell**, and **Maven** to provide a simple and interactive solution.

The program allows users to scan items individually or in bulk (via JSON files), calculate totals, and generate receipts. The system supports special pricing rules (e.g., "Buy 3 for 130 pence") and is easy to extend and maintain.

---


## Key Features
1. **Command-Line Interface**:
    - Use commands to scan items, view totals, reset the checkout, and more.

2. **Special Pricing Rules**:
    - Supports deals like "Buy 3 for 130 pence".

3. **Batch Scanning**:
    - Scan items from a JSON file for convenience.

4. **Testing**:
    - Includes tests to ensure everything works correctly.

---

## Technologies Used
- **Java 17**
- **Spring Boot 3.4**
- **Spring Shell**
- **Maven**
- **JUnit 5**

---

## How to Run the Application
### Prerequisites
- **Java 17** installed.
- **Maven** installed.

### Steps to Run
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd cdl-discount-shell
   ```

2. Build the application:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   java -jar target/cdl-discount-shell-0.0.1-SNAPSHOT.jar
   ```

### Commands
Once the application is running, you can use the following commands:

#### 1. Scan Items
Scan an individual item by specifying the SKU and optionally the amount:
```bash
scan --sku A --amount 2
```
- If the `--amount` option is not provided, it defaults to `1`.

- **Response**:
  ```plaintext
  Item scanned: A. Running total: 100 pence.
  ```

Scan items from a JSON file instead of specifying individual SKUs:
```bash
scan --json scandata.json
```
- **Response**:
  ```plaintext
  Items from JSON file scanned successfully. Running total: 275 pence.
  ```

> **Note**: The `--sku` and `--json` options are mutually exclusive. Use either to scan items, but not both in the same command.

#### 2. View Total
Get the total amount:
```bash
checkout-total
```
- **Response**:
  ```plaintext
  Final total: 275 pence.
  ```

Generate a detailed receipt:
```bash
checkout-total --showReceipt true
```
- **Response**:
  A receipt with a breakdown of items and totals.

#### 3. Reset Checkout
Clear all scanned items:
```bash
reset
```
- **Response**:
  ```plaintext
  Checkout has been reset.
  ```

#### 4. View Catalogue
Display available products and their pricing rules:
```bash
catalogue
```
- **Response**:
  ```plaintext
  SKU       Name       Unit Price (pence)   Special Quantity   Special Price (pence)
  A         Apple      50                   3                 130
  B         Banana     30                   2                 45
  C         Carrot     20                   -                 -
  ```

#### 5. Exit Application
Exit the shell:
```bash
exit
```

---

## Testing
Run tests with Maven:
```bash
mvn test
```
### Example Test Commands
- Run all tests:
  ```bash
  mvn test
  ```
- Run a specific test class:
  ```bash
  mvn test -Dtest=CheckoutTest
  ```
- Run a specific test method:
  ```bash
  mvn test -Dtest=CheckoutTest#testScanSingleItem
  ```

---

## Example Usage
### JSON File Example
Create a JSON file in the `src/main/resources` directory, e.g., `scandata.json`:
```json
{
  "A": 3,
  "B": 2,
  "C": 5
}
```
Scan items from the file:
```bash
scan --json scandata.json
```

### Sample Output
```plaintext
shell:>scan --sku A --amount 3
Item scanned: A. Running total: 130 pence.

shell:>checkout-total --showReceipt true
┌──────────────────────────────────────────────────────────┐
│                     CDL SOLUTIONS                       │
├──────────────────────────────────────────────────────────┤
│ Date: Fri, Dec 13, 2024 • 05:26:01                      │
├──────────────────────────────────────────────────────────┤
│ Token: 8B085411-B7E1-48                                 │
├──────────────────────────────────────────────────────────┤
│ Apple x3                                        130 pence│
├──────────────────────────────────────────────────────────┤
│ TOTAL:                                          130 pence│
└──────────────────────────────────────────────────────────┘
```

