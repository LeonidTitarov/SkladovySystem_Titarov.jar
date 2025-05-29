import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class InventorySystemTests {
    private InventoryManager inventoryManager;
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        inventoryManager = new InventoryManager();
        userManager = new UserManager();
    }

    @Test
    void testAddAndFindProduct() {
        // Vytvoříme mock produkt
        Product product = createMockProduct("P001", "Laptop", 5);

        // Test přidání produktu
        inventoryManager.addProduct(product);

        // Test nalezení produktu
        Product found = inventoryManager.findProductById("P001");
        assertNotNull(found);
        assertEquals("P001", found.getProductId());
        assertEquals("Laptop", found.getName());
    }

    @Test
    void testRemoveProduct() {
        Product product = createMockProduct("P002", "Mouse", 10);
        inventoryManager.addProduct(product);

        // Test úspěšného odstranění
        boolean removed = inventoryManager.removeProduct("P002");
        assertTrue(removed);

        // Ověření, že produkt už neexistuje
        Product notFound = inventoryManager.findProductById("P002");
        assertNull(notFound);

        // Test odstranění neexistujícího produktu
        boolean notRemoved = inventoryManager.removeProduct("NONEXISTENT");
        assertFalse(notRemoved);
    }

    @Test
    void testSearchProducts() {
        Product laptop = createMockProduct("P001", "Gaming Laptop", 3);
        Product mouse = createMockProduct("P002", "Wireless Mouse", 15);

        inventoryManager.addProduct(laptop);
        inventoryManager.addProduct(mouse);

        // Test vyhledávání podle názvu
        List<Product> results = inventoryManager.searchProducts("laptop");
        assertEquals(1, results.size());
        assertEquals("Gaming Laptop", results.get(0).getName());

        // Test vyhledávání podle ID
        List<Product> idResults = inventoryManager.searchProducts("p002");
        assertEquals(1, idResults.size());
        assertEquals("P002", idResults.get(0).getProductId());
    }

    @Test
    void testUserAuthentication() {
        User user = createMockUser("Jan", "Novák", "heslo123", "SN001");
        userManager.addUser(user);

        // Test úspěšné autentizace
        User authenticated = userManager.authenticateUser("Jan", "Novák", "heslo123", "SN001");
        assertNotNull(authenticated);
        assertEquals("Jan Novák", authenticated.getFullName());

        // Test neúspěšné autentizace - špatné heslo
        User wrongPassword = userManager.authenticateUser("Jan", "Novák", "spatneHeslo", "SN001");
        assertNull(wrongPassword);

        // Test neúspěšné autentizace - neexistující uživatel
        User notExists = userManager.authenticateUser("Petr", "Svoboda", "heslo", "SN999");
        assertNull(notExists);

        // Test přidání a odebrání uživatele
        User newUser = createMockUser("Marie", "Svobodová", "password456", "SN002");
        userManager.addUser(newUser);
        assertEquals(2, userManager.getAllUsers().size());

        boolean removed = userManager.removeUser(newUser);
        assertTrue(removed);
        assertEquals(1, userManager.getAllUsers().size());
    }

    @Test
    void testProductOperations() {
        Product laptop = createMockProduct("P001", "Gaming Laptop", 5);
        Product mouse = createMockProduct("P002", "Wireless Mouse", 15);

        inventoryManager.addProduct(laptop);
        inventoryManager.addProduct(mouse);

        // Test updateProduct
        boolean updated = inventoryManager.updateProduct("P001", "Updated Laptop", 8, ProductCategory.ELECTRONICS, "UPDATED");
        assertTrue(updated);

        Product updatedProduct = inventoryManager.findProductById("P001");
        assertEquals("Updated Laptop", updatedProduct.getName());
        assertEquals(8, updatedProduct.getQuantity());
        assertEquals("UPDATED", updatedProduct.getStatus());

        // Test getProductsByCategory
        List<Product> electronics = inventoryManager.getProductsByCategory(ProductCategory.ELECTRONICS);
        assertEquals(2, electronics.size());

        // Test update neexistujícího produktu
        boolean notUpdated = inventoryManager.updateProduct("NONEXISTENT", "Test", 1, ProductCategory.ELECTRONICS, "ACTIVE");
        assertFalse(notUpdated);
    }

    // Helper metody pro vytvoření mock objektů
    private Product createMockProduct(String id, String name, int quantity) {
        return new Product(id, name, quantity, ProductCategory.ELECTRONICS, "ACTIVE");
    }

    private User createMockUser(String firstName, String lastName, String password, String serialNumber) {
        return new User(firstName, lastName, password, serialNumber, UserRole.EMPLOYEE);
    }
}
