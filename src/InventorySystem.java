import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InventorySystem {
    private static LoginScreen loginScreen;
    private static MainApplication mainApplication;
    private static UserManager userManager;
    private static InventoryManager inventoryManager;
    private static User currentUser;
    private static final String PRODUCTS_FILE = "products.txt";

/**
 * Hlavní metoda aplikace pro správu inventáře.
 * Nastavuje vzhled systému a inicializuje komponenty.
 * @param args argumenty příkazové řádky
 */

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Nepodařilo se nastavit vzhled systému: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            userManager = new UserManager();
            inventoryManager = new InventoryManager();
            setupDemoData();

            loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }

    /**
     * Nastavuje demo data - vytváří testovací uživatele a načítá produkty.
     */
    private static void setupDemoData() {
        userManager.addUser(new User("Leonid", "Titarov", "tvojeMama", "6969", UserRole.ADMIN));
        userManager.addUser(new User("Pavel", "Vaclavek", "GeometyDash", "7980", UserRole.WAREHOUSE_MANAGER));
        userManager.addUser(new User("Damian", "Smekal", "Tasemnice", "SN24680", UserRole.EMPLOYEE));

        // Načtení produktů ze souboru
        loadProductsFromFile();
}

    /**
     * Načítá produkty ze souboru, pokud neexistuje vytvoří defaultní.
     */
    private static void loadProductsFromFile() {
        try {
            // Pokud soubor neexistuje, vytvoříme ho s defaultními daty
            if (!Files.exists(Paths.get(PRODUCTS_FILE))) {
                createDefaultProductsFile();
            }

            // Načtení produktů ze souboru
            try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Přeskočení prázdných řádků
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    // Rozdělení řádku na jednotlivé části (oddělené středníkem)
                    String[] parts = line.split(";");
                    if (parts.length >= 5) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        int quantity = Integer.parseInt(parts[2].trim());
                        ProductCategory category = ProductCategory.valueOf(parts[3].trim());
                        String status = parts[4].trim();

                        // Přidání produktu do inventáře
                        inventoryManager.addProduct(new Product(id, name, quantity, category, status));
                    }
                }
                System.out.println("Produkty byly úspěšně načteny ze souboru.");
            }
        } catch (IOException e) {
            System.err.println("Chyba při načítání produktů ze souboru: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Neočekávaná chyba při načítání produktů: " + e.getMessage());
        }
    }

    /**
     * Vytváří soubor s výchozími produkty pro testování.
     */

    private static void createDefaultProductsFile() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("P001;Monitor Dell 24\";15;ELECTRONICS;Skladem");
            lines.add("P002;Klávesnice Logitech;30;ELECTRONICS;Skladem");
            lines.add("P003;Kancelářský stůl;5;FURNITURE;Skladem");
            lines.add("P004;Židle ergonomická;8;FURNITURE;Objednáno");
            lines.add("P005;Tiskový papír A4;50;OFFICE_SUPPLIES;Skladem");
            lines.add("P006;Tonery HP;12;OFFICE_SUPPLIES;Málo zásob");
            lines.add("P007;USB flash disk 64GB;25;ELECTRONICS;Skladem");
            lines.add("P008;Šanony A4;40;OFFICE_SUPPLIES;Skladem");
            lines.add("P009;Router WiFi;7;ELECTRONICS;Objednáno");
            lines.add("P010;Konferenční stolek;3;FURNITURE;Skladem");

            Files.write(Paths.get(PRODUCTS_FILE), lines);
            System.out.println("Soubor s defaultními produkty byl vytvořen.");
        } catch (IOException e) {
            System.err.println("Chyba při vytváření souboru s produkty: " + e.getMessage());
        }
    }

/**
 * Ověřuje přihlašovací údaje a otevírá hlavní aplikaci.
 * @return true pokud je autentifikace úspěšná
 */

    public static boolean authenticateUser(String firstName, String lastName, String password, String serialNumber) {
        currentUser = userManager.authenticateUser(firstName, lastName, password, serialNumber);
        if (currentUser != null) {
            loginScreen.dispose();
            mainApplication = new MainApplication(currentUser, inventoryManager, userManager);
            mainApplication.setVisible(true);
            return true;
        }
        return false;
    }

    /**
     * Vrací aktuálně přihlášeného uživatele.
     * @return objekt aktuálního uživatele nebo null
     */

    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Odhlašuje uživatele a vrací na přihlašovací obrazovku.
     */
    
    public static void logout() {
        if (mainApplication != null) {
            mainApplication.dispose();
        }
        currentUser = null;
        loginScreen = new LoginScreen();
        loginScreen.setVisible(true);
    }
}