import javax.swing.*;
import java.util.List;

public class InventorySystemTests {
    private static LoginScreen loginScreen;
    private static MainApplication mainApplication;
    private static UserManager userManager;
    private static InventoryManager inventoryManager;
    private static DatabaseManager databaseManager;   // ← nové
    private static User currentUser;

    /**
     * Hlavní metoda – inicializuje DB a spouští GUI.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Nepodařilo se nastavit vzhled systému: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {

            // 1) Vytvoříme správce databáze a otestujeme připojení
            databaseManager = new DatabaseManager();
            try {
                databaseManager.getConnection().close();
                System.out.println("Připojení k databázi úspěšné.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Nepodařilo se připojit k databázi!\n" + e.getMessage(),
                        "Chyba DB",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            // 2) Předáme DatabaseManager správcům
            userManager      = new UserManager(databaseManager);
            inventoryManager = new InventoryManager(databaseManager);

            // 3) Demo data – vloží se jen pokud DB je prázdná
            setupDemoData();

            loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }

    /**
     * Vloží demo uživatele a produkty, pouze pokud tabulky jsou prázdné.
     * Tím se vyhneme duplicitám při každém spuštění.
     */
    private static void setupDemoData() {

        // Uživatelé – přidáme jen pokud žádní nejsou
        List<User> existingUsers = userManager.getAllUsers();
        if (existingUsers.isEmpty()) {
            userManager.addUser(new User("Leonid", "Titarov",  "1234",    "6969",    UserRole.ADMIN));
            userManager.addUser(new User("Pavel",  "Vaclavek", "GeometyDash",  "7980",    UserRole.WAREHOUSE_MANAGER));
            userManager.addUser(new User("Damian", "Smekal",   "Tasemnice",    "SN24680", UserRole.EMPLOYEE));
            System.out.println("Demo uživatelé byli vytvořeni.");
        }

        // Produkty – přidáme jen pokud žádné nejsou
        List<Product> existingProducts = inventoryManager.getAllProducts();
        if (existingProducts.isEmpty()) {
            inventoryManager.addProduct(new Product("P001", "Monitor Dell 24\"",     15, ProductCategory.ELECTRONICS,    "Skladem"));
            inventoryManager.addProduct(new Product("P002", "Klávesnice Logitech",   30, ProductCategory.ELECTRONICS,    "Skladem"));
            inventoryManager.addProduct(new Product("P003", "Kancelářský stůl",       5, ProductCategory.FURNITURE,       "Skladem"));
            inventoryManager.addProduct(new Product("P004", "Židle ergonomická",       8, ProductCategory.FURNITURE,       "Objednáno"));
            inventoryManager.addProduct(new Product("P005", "Tiskový papír A4",       50, ProductCategory.OFFICE_SUPPLIES, "Skladem"));
            inventoryManager.addProduct(new Product("P006", "Tonery HP",              12, ProductCategory.OFFICE_SUPPLIES, "Málo zásob"));
            inventoryManager.addProduct(new Product("P007", "USB flash disk 64GB",   25, ProductCategory.ELECTRONICS,    "Skladem"));
            inventoryManager.addProduct(new Product("P008", "Šanony A4",             40, ProductCategory.OFFICE_SUPPLIES, "Skladem"));
            inventoryManager.addProduct(new Product("P009", "Router WiFi",             7, ProductCategory.ELECTRONICS,    "Objednáno"));
            inventoryManager.addProduct(new Product("P010", "Konferenční stolek",      3, ProductCategory.FURNITURE,       "Skladem"));
            System.out.println("Demo produkty byly vytvořeny.");
        }
    }

    /**
     * Ověří přihlašovací údaje přes DB a otevře hlavní aplikaci.
     */
    public static boolean authenticateUser(String firstName, String lastName,
                                           String password, String serialNumber) {
        currentUser = userManager.authenticateUser(firstName, lastName, password, serialNumber);
        if (currentUser != null) {
            loginScreen.dispose();
            mainApplication = new MainApplication(currentUser, inventoryManager, userManager);
            mainApplication.setVisible(true);
            return true;
        }
        return false;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        if (mainApplication != null) {
            mainApplication.dispose();
        }
        currentUser = null;
        loginScreen = new LoginScreen();
        loginScreen.setVisible(true);
    }
}