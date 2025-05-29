import javax.swing.table.AbstractTableModel;
import java.util.List;

// Model dat pro tabulku produktů
class ProductTableModel extends AbstractTableModel {
    private String[] columnNames = {"ID", "Název", "Množství", "Kategorie", "Status"};
    private List<Product> products;

/**
 *Konstruktor
 * přijímá seznam produktů a uloží si ho jako zdroj
 * */

    public ProductTableModel(List<Product> products) {
        this.products = products;
    }

    /**
     * Aktualizuje data v tabulce novým seznamem produktů a
     * automaticky informuje tabulku o změně s pomocí
     * @fireTableDataChanged();
     * */

    public void updateData(List<Product> products) {
        this.products = products;
        fireTableDataChanged();
    }

    /**
     * Vrací počet řádku
     * */

    @Override
    public int getRowCount() {
        return products.size();
    }

    /**
     *vrací počet sloupců
     * */

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    /**
     * vrací název sloupce podle indexu
     * */

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * vrací hodnotu pro konkretní bunku v tabulce podle
     * řádku a sloupce
     * */

  ///  Tuhle celou metodu mi udelal ChatGTP
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = products.get(rowIndex);
        switch (columnIndex) {
            case 0: return product.getProductId();
            case 1: return product.getName();
            case 2: return product.getQuantity();
            case 3: return product.getCategory();
            case 4: return product.getStatus();
            default: return null;
        }
    }

    /**
     * vrací konkrétní produkt objekt podle indexu řádku
     * */

    public Product getProductAt(int rowIndex) {
        return products.get(rowIndex);
    }
}
