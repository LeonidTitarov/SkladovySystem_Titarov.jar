import java.util.ArrayList;
import java.util.List;

// Správce inventáře
class InventoryManager {
    private List<Product> products;
    private List<InventoryTransaction> transactions;

    // Konstruktor
    public InventoryManager() {
        products = new ArrayList<>();
        transactions = new ArrayList<>();
    }

/**
@AddProduct Tahle metoda dělá přídávání produktů
*/
    public void addProduct(Product product) {
        products.add(product);
    }

    /**
    * @removeProduct
     * nám vlastně zajištujě odstranění produktu
    * v závislosti zda se jeho id shoduje s id getProductId()
    * */
    public boolean removeProduct(String productId) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId().equals(productId)) {
                products.remove(i);
                return true;
            }
        }
        return false;
    }

/**
* @updateProduct dělá přenastavení daného produktu.
*  Pozměnujě jeho aktualní hodnoty, pokud není prázdný  */

    public boolean updateProduct(String productId, String name, int quantity, ProductCategory category, String status) {
        Product product = findProductById(productId);
        if (product != null) {
            product.setName(name);
            product.setQuantity(quantity);
            product.setCategory(category);
            product.setStatus(status);
            return true;
        }
        return false;
    }

    /**
     * @findProductById
     * nám hledá náš produktu
     * v závislosti zda se jeho id shoduje s id getProductId()
     * pokud ne vrátí to nulu
     * */

    public Product findProductById(String productId) {
        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }
/**
 * @getAllProducts
 * nám vrací kopii našeho daného seznamu.
 * */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * @searchProducts
     * Náš veškerý text, který zadáme do vyhledavače převádí na malá písmena.
     * Nasledně hledá požadavek podle např id,jmeno, kategorie  nebo status
     * záleží co napíšeme.
     * */
    public List<Product> searchProducts(String keyword) {
        keyword = keyword.toLowerCase();
        List<Product> results = new ArrayList<>();

        for (Product product : products) {
            if (product.getProductId().toLowerCase().contains(keyword) ||
                    product.getName().toLowerCase().contains(keyword) ||
                    product.getCategory().toString().toLowerCase().contains(keyword) ||
                    product.getStatus().toLowerCase().contains(keyword)) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * @getProductsByCategory
     * Vrací seznam všech produktů, které patří do dané kategorie.
     * */

    public List<Product> getProductsByCategory(ProductCategory category) {
           List<Product> results = new ArrayList<>();

        for (Product product : products) {
            if (product.getCategory() == category) {
                results.add(product);
            }
        }

        return results;
    }

    /**
     * @receiveProducts
     * děla, že zvyšuje množství produktů na skladě a zaznamenává to do Trasaction
     * hledá produkt podle id koukne se zda existuje a pak pokud ano zaznamena to
     * do Transaction.
     * */
    public boolean receiveProducts(String productId, int quantity, User user) {
        Product product = findProductById(productId);
        if (product != null) {
            product.increaseQuantity(quantity);
            addTransaction(new InventoryTransaction(
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
     * @issueProducts
     * kontroluje zda produkt existuje a pouší se snížit počet produktů
     * o zadané množství pokud to půjde vrátí true pokud ne false a zaznamená
     * to do Transaction
     * */
    public boolean issueProducts(String productId, int quantity, User user) {
        Product product = findProductById(productId);
        if (product != null && product.decreaseQuantity(quantity)) {
            addTransaction(new InventoryTransaction(
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

    /**
     * @addTransaction
     * přidává transace do seznamu transactions
     * */
    public void addTransaction(InventoryTransaction transaction) {
        transactions.add(transaction);
    }

    /**
     * @getAllTrasactions
     * nám vrátí kopii všech transackcí jako novej list aby nevlivnilo kode
     * */
    public List<InventoryTransaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * @searchTransactions
     * Náš veškerý text, který zadáme do vyhledavače převádí na malá písmena.
     * Nasledně hledá požadavek(transaction) podle např id,jmeno, kategorie nebo status
     * vrací výsledek result jako novej seznam
     * */
    public List<InventoryTransaction> searchTransactions(String keyword) {
        keyword = keyword.toLowerCase();
        List<InventoryTransaction> results = new ArrayList<>();

        for (InventoryTransaction transaction : transactions) {
            if (transaction.getProductId().toLowerCase().contains(keyword) ||
                    transaction.getProductName().toLowerCase().contains(keyword) ||
                    transaction.getUser().toLowerCase().contains(keyword) ||
                    transaction.getType().toString().toLowerCase().contains(keyword)) {
                results.add(transaction);
            }
        }

        return results;
    }
}
