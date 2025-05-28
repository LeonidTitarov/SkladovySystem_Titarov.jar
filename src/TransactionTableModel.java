import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.List;

/// Model dat pro tabulku transakcí
class TransactionTableModel extends AbstractTableModel {
    private String[] columnNames = {"ID", "Typ", "ID Produktu", "Název produktu", "Množství", "Uživatel", "Datum a čas"};
    private java.util.List<InventoryTransaction> transactions;

    /**
     *Konstruktor
     * přijímá seznam produktů a uloží si ho jako zdroj
     * */
    public TransactionTableModel(List<InventoryTransaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Aktualizuje data v tabulce novým seznamem transakcí a
     * automaticky informuje tabulku o změně s pomocí
     * @fireTableDataChanged();
     * */
    public void updateData(List<InventoryTransaction> transactions) {
        this.transactions = transactions;
        fireTableDataChanged();
    }

    /**
     * Vrací počet řádků
     * */
    @Override
    public int getRowCount() {
        return transactions.size();
    }

    /**
     * Vrací počet sloupců
     * */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    /**
     * Vrací počet slupců podle indexu
     * */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    /**
     * vrací hodnotu pro konkretní bunku v tabulce podle
     * řádku a sloupce
     * */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InventoryTransaction transaction = transactions.get(rowIndex);
        switch (columnIndex) {
            case 0: return transaction.getTransactionId();
            case 1: return transaction.getType();
            case 2: return transaction.getProductId();
            case 3: return transaction.getProductName();
            case 4: return transaction.getQuantity();
            case 5: return transaction.getUser();
            case 6: return formatDate(transaction.getTimestamp());
            default: return null;
        }
    }
    /**
     * Vytváří datum a čas díky knihovně SimpleDateFormat,
     * pořadí si určujeme sami
     * */
    private String formatDate(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(date);
    }
}
