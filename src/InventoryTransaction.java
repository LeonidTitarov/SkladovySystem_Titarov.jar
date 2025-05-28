import java.text.SimpleDateFormat;
import java.util.Date;

// Třída reprezentující transakce v inventáři
class InventoryTransaction {
    private static int nextId = 1;

    private int transactionId;
    private TransactionType type;
    private String productId;
    private String productName;
    private int quantity;
    private String user;
    private Date timestamp;

/**
 * Konstruktor pro InventoryTransaction
 * */
    public InventoryTransaction(TransactionType type, String productId, String productName, int quantity, String user) {
        this.transactionId = nextId++;
        this.type = type;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.user = user;
        this.timestamp = new java.util.Date();
    }

    /**
     * Getter Setter
     * */
    public int getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUser() {
        return user;
    }

    public java.util.Date getTimestamp() {
        return timestamp;
    }

/**
 * @toTableRow
 * nam vraci pole objekt,který reprezentuje data transakce
 * */

    public Object[] toTableRow() {
        return new Object[]{transactionId, type, productId, productName, quantity, user, formatDate(timestamp)
        };
    }
/**
 * @formatDate
 * nám vrací datum a čas díky SimpeDateFormat knihovně
 * a pak si to sami můžeme naformátujeme
 * */

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(date);
    }
}

