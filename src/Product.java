// Třída reprezentující produkt
class Product {
    private String productId;
    private String name;
    private int quantity;
    private ProductCategory category;
    private String status;


    /**
     *Konstruktor
     * */

    public Product(String productId, String name, int quantity, ProductCategory category, String status) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.status = status;
    }

    /**
     *Getter Setter
     * */

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateStatus();
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Zkoumá počet daného produktu a podle toho
     * dává odpověd
     * */

    private void updateStatus() {
        if (quantity <= 0) {
            status = "Vyprodáno";
        } else if (quantity < 5) {
            status = "Málo zásob";
        } else {
            status = "Skladem";
        }
    }

    /**
     * Navyšuje počet daného produktu a obnovuje údaje
     * */

    public void increaseQuantity(int amount) {
        if (amount > 0) {
            quantity += amount;
            updateStatus();
        }
    }


    /**
     * Odebírá počet daného produktu a obnovuje údaje
     * */
    public boolean decreaseQuantity(int amount) {
        if (amount > 0 && amount <= quantity) {
            quantity -= amount;
            updateStatus();
            return true;
        }
        return false;
    }


    /**
     * Nám vraci pole objekt,který reprezentuje data transakce
     * */
    public Object[] toTableRow() {
        return new Object[]{productId, name, quantity, category, status};
    }

    @Override
    public String toString() {
        return name + " (ID: " + productId + ")";
    }
}

