import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Správce databáze – zajišťuje veškerou komunikaci s MS SQL Serverem.
 * Nahrazuje ukládání dat do ArrayList a souboru products.txt.
 */
public class DatabaseManager {

    // ── Připojovací údaje ──────────────────────────────────────────────────────
    private static final String HOST     = "localhost";
    private static final int    PORT     = 1433;
    private static final String DATABASE = "InventoryDB";
    private static final String USER     = "sa";
    private static final String PASSWORD = "Playstation2020x";

    private static final String URL =
            "jdbc:sqlserver://localhost:1433"
                    + ";databaseName=InventoryDB"
                    + ";encrypt=true"
                    + ";trustServerCertificate=true";

    // ── Připojení ──────────────────────────────────────────────────────────────

    /**
     * Otevře a vrátí nové JDBC připojení k databázi.
     * Volající je zodpovědný za jeho zavření (try-with-resources).
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UŽIVATELÉ
    // ══════════════════════════════════════════════════════════════════════════

    /** Uloží nového uživatele do tabulky users. */
    public void addUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, password, serial_number, role) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getSerialNumber());
            ps.setString(5, user.getRole().name());   // ukládáme enum jako String
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Chyba při přidávání uživatele: " + e.getMessage());
        }
    }

    /** Odstraní uživatele podle sériového čísla. Vrátí true při úspěchu. */
    public boolean removeUser(User user) {
        String sql = "DELETE FROM users WHERE serial_number = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getSerialNumber());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Chyba při odstraňování uživatele: " + e.getMessage());
            return false;
        }
    }

    /**
     * Ověří přihlašovací údaje.
     * @return odpovídající User, nebo null pokud nenalezen.
     */
    public User authenticateUser(String firstName, String lastName,
                                 String password, String serialNumber) {
        String sql = "SELECT * FROM users "
                + "WHERE first_name = ? AND last_name = ? "
                + "AND password = ? AND serial_number = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, password);
            ps.setString(4, serialNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Chyba při autentizaci: " + e.getMessage());
        }
        return null;
    }

    /** Vrátí seznam všech uživatelů z databáze. */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Chyba při načítání uživatelů: " + e.getMessage());
        }
        return list;
    }

    /** Pomocná metoda – převede řádek ResultSet na objekt User. */
    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("serial_number"),
                UserRole.valueOf(rs.getString("role"))  // převod String → enum
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRODUKTY
    // ══════════════════════════════════════════════════════════════════════════

    /** Uloží nový produkt do tabulky products. */
    public void addProduct(Product product) {
        String sql = "INSERT INTO products (product_id, name, quantity, category, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product.getProductId());
            ps.setString(2, product.getName());
            ps.setInt(3,    product.getQuantity());
            ps.setString(4, product.getCategory().name());
            ps.setString(5, product.getStatus());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Chyba při přidávání produktu: " + e.getMessage());
        }
    }

    /** Odstraní produkt podle ID. Vrátí true při úspěchu. */
    public boolean removeProduct(String productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, productId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Chyba při odstraňování produktu: " + e.getMessage());
            return false;
        }
    }

    /** Aktualizuje existující produkt v databázi. Vrátí true při úspěchu. */
    public boolean updateProduct(String productId, String name, int quantity,
                                 ProductCategory category, String status) {
        String sql = "UPDATE products SET name = ?, quantity = ?, category = ?, status = ? "
                + "WHERE product_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2,    quantity);
            ps.setString(3, category.name());
            ps.setString(4, status);
            ps.setString(5, productId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Chyba při aktualizaci produktu: " + e.getMessage());
            return false;
        }
    }

    /** Najde produkt podle ID. Vrátí null pokud neexistuje. */
    public Product findProductById(String productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapProduct(rs);
            }
        } catch (SQLException e) {
            System.err.println("Chyba při hledání produktu: " + e.getMessage());
        }
        return null;
    }

    /** Vrátí seznam všech produktů. */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapProduct(rs));

        } catch (SQLException e) {
            System.err.println("Chyba při načítání produktů: " + e.getMessage());
        }
        return list;
    }

    /**
     * Fulltextové vyhledávání produktů – hledá v id, názvu, kategorii a statusu.
     * Používá SQL LIKE operátor.
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();
        String like = "%" + keyword.toLowerCase() + "%";
        String sql = "SELECT * FROM products "
                + "WHERE LOWER(product_id) LIKE ? OR LOWER(name) LIKE ? "
                + "OR LOWER(category) LIKE ? OR LOWER(status) LIKE ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Chyba při vyhledávání produktů: " + e.getMessage());
        }
        return list;
    }

    /** Vrátí produkty filtrované podle kategorie. */
    public List<Product> getProductsByCategory(ProductCategory category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, category.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Chyba při filtrování produktů: " + e.getMessage());
        }
        return list;
    }

    /** Pomocná metoda – převede řádek ResultSet na objekt Product. */
    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getString("product_id"),
                rs.getString("name"),
                rs.getInt("quantity"),
                ProductCategory.valueOf(rs.getString("category")),
                rs.getString("status")
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANSAKCE
    // ══════════════════════════════════════════════════════════════════════════

    /** Uloží novou transakci do tabulky transactions. */
    public void addTransaction(InventoryTransaction transaction) {
        String sql = "INSERT INTO transactions (type, product_id, product_name, quantity, username) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, transaction.getType().name());
            ps.setString(2, transaction.getProductId());
            ps.setString(3, transaction.getProductName());
            ps.setInt(4,    transaction.getQuantity());
            ps.setString(5, transaction.getUser());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Chyba při ukládání transakce: " + e.getMessage());
        }
    }

    /** Vrátí seznam všech transakcí seřazených od nejnovější. */
    public List<InventoryTransaction> getAllTransactions() {
        List<InventoryTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapTransaction(rs));

        } catch (SQLException e) {
            System.err.println("Chyba při načítání transakcí: " + e.getMessage());
        }
        return list;
    }

    /** Vyhledávání v transakcích – hledá v id produktu, názvu, uživateli a typu. */
    public List<InventoryTransaction> searchTransactions(String keyword) {
        List<InventoryTransaction> list = new ArrayList<>();
        String like = "%" + keyword.toLowerCase() + "%";
        String sql = "SELECT * FROM transactions "
                + "WHERE LOWER(product_id) LIKE ? OR LOWER(product_name) LIKE ? "
                + "OR LOWER(username) LIKE ? OR LOWER(type) LIKE ? "
                + "ORDER BY timestamp DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Chyba při vyhledávání transakcí: " + e.getMessage());
        }
        return list;
    }

    /** Pomocná metoda – převede řádek ResultSet na objekt InventoryTransaction. */
    private InventoryTransaction mapTransaction(ResultSet rs) throws SQLException {
        InventoryTransaction t = new InventoryTransaction(
                TransactionType.valueOf(rs.getString("type")),
                rs.getString("product_id"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getString("username")
        );
        return t;
    }
}