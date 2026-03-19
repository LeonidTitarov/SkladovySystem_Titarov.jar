import java.util.List;

/**
 * Správce inventáře – nyní deleguje veškeré ukládání na DatabaseManager.
 * Logika (receiveProducts, issueProducts, atd.) zůstává stejná,
 * jen místo ArrayList používáme SQL databázi.
 */
class InventoryManager {

    private final DatabaseManager db;

    public InventoryManager(DatabaseManager db) {
        this.db = db;
    }

    // ── Produkty ───────────────────────────────────────────────────────────────

    public void addProduct(Product product) {
        db.addProduct(product);
    }

    public boolean removeProduct(String productId) {
        return db.removeProduct(productId);
    }

    public boolean updateProduct(String productId, String name, int quantity,
                                 ProductCategory category, String status) {
        return db.updateProduct(productId, name, quantity, category, status);
    }

    public Product findProductById(String productId) {
        return db.findProductById(productId);
    }

    public List<Product> getAllProducts() {
        return db.getAllProducts();
    }

    public List<Product> searchProducts(String keyword) {
        return db.searchProducts(keyword);
    }

    public List<Product> getProductsByCategory(ProductCategory category) {
        return db.getProductsByCategory(category);
    }

    // ── Příjem / Výdej ─────────────────────────────────────────────────────────

    /**
     * Navýší množství produktu a uloží transakci RECEIVE do DB.
     */
    public boolean receiveProducts(String productId, int quantity, User user) {
        Product product = findProductById(productId);
        if (product != null) {
            product.increaseQuantity(quantity);
            // Aktualizujeme množství a status v DB
            db.updateProduct(productId, product.getName(),
                    product.getQuantity(), product.getCategory(), product.getStatus());
            // Zapíšeme transakci
            db.addTransaction(new InventoryTransaction(
                    TransactionType.RECEIVE,
                    productId,
                    product.getName(),
                    quantity,
                    user.getFullName()
            ));
            return true;
        }
        return false;
    }

    /**
     * Sníží množství produktu a uloží transakci ISSUE do DB.
     */
    public boolean issueProducts(String productId, int quantity, User user) {
        Product product = findProductById(productId);
        if (product != null && product.decreaseQuantity(quantity)) {
            db.updateProduct(productId, product.getName(),
                    product.getQuantity(), product.getCategory(), product.getStatus());
            db.addTransaction(new InventoryTransaction(
                    TransactionType.ISSUE,
                    productId,
                    product.getName(),
                    quantity,
                    user.getFullName()
            ));
            return true;
        }
        return false;
    }

    // ── Transakce ──────────────────────────────────────────────────────────────

    public void addTransaction(InventoryTransaction transaction) {
        db.addTransaction(transaction);
    }

    public List<InventoryTransaction> getAllTransactions() {
        return db.getAllTransactions();
    }

    public List<InventoryTransaction> searchTransactions(String keyword) {
        return db.searchTransactions(keyword);
    }
}