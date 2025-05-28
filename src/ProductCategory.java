// Enum pro kategorie produktů

/**
 * Názvy kategorii zboží
 * */
enum ProductCategory {
    ELECTRONICS("Elektronika"),
    OFFICE_SUPPLIES("Kancelářské potřeby"),
    FURNITURE("Nábytek"),
    IT_HARDWARE("IT Hardware"),
    FOOD("Potraviny"),
    OTHER("Ostatní");

    private String display;

    ProductCategory(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
