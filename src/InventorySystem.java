import javax.swing.*;
import java.util.List;

public class InventorySystem {
    private static LoginScreen loginScreen;
    private static MainApplication mainApplication;
    private static UserManager userManager;
    private static InventoryManager inventoryManager;
    private static DatabaseManager databaseManager;
    private static User currentUser;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Nepodařilo se nastavit vzhled: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {

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

            userManager      = new UserManager(databaseManager);
            inventoryManager = new InventoryManager(databaseManager);

            setupDemoData();

            loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }

    private static void setupDemoData() {
        if (userManager.getAllUsers().isEmpty()) {
            userManager.addUser(new User("Leonid", "Titarov",  "tvojeMama",   "6969",    UserRole.ADMIN));
            userManager.addUser(new User("Pavel",  "Vaclavek", "GeometyDash", "7980",    UserRole.WAREHOUSE_MANAGER));
            userManager.addUser(new User("Damian", "Smekal",   "Tasemnice",   "SN24680", UserRole.EMPLOYEE));
        }

        if (inventoryManager.getAllProducts().isEmpty()) {
            inventoryManager.addProduct(new Product("P001", "Monitor Dell 24\"",   15, ProductCategory.ELECTRONICS,    "Skladem"));
            inventoryManager.addProduct(new Product("P002", "Klávesnice Logitech", 30, ProductCategory.ELECTRONICS,    "Skladem"));
            inventoryManager.addProduct(new Product("P003", "Kancelářský stůl",     5, ProductCategory.FURNITURE,       "Skladem"));
            inventoryManager.addProduct(new Product("P004", "Židle ergonomická",     8, ProductCategory.FURNITURE,       "Objednáno"));
            inventoryManager.addProduct(new Product("P005", "Tiskový papír A4",     50, ProductCategory.OFFICE_SUPPLIES, "Skladem"));
            inventoryManager.addProduct(new Product("P006", "Tonery HP",            12, ProductCategory.OFFICE_SUPPLIES, "Málo zásob"));
            inventoryManager.addProduct(new Product("P007", "USB flash disk 64GB", 25, ProductCategory.ELECTRONICS,    "Skladem"));
            inventoryManager.addProduct(new Product("P008", "Šanony A4",           40, ProductCategory.OFFICE_SUPPLIES, "Skladem"));
            inventoryManager.addProduct(new Product("P009", "Router WiFi",           7, ProductCategory.ELECTRONICS,    "Objednáno"));
            inventoryManager.addProduct(new Product("P010", "Konferenční stolek",    3, ProductCategory.FURNITURE,       "Skladem"));
        }
    }

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