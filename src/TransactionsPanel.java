import javax.swing.*;
import java.awt.*;

/// Panel pro zobrazení transakcí
class TransactionsPanel extends JPanel {
    private InventoryManager inventoryManager;
    private JTable transactionsTable;
    private TransactionTableModel tableModel;
    private JTextField searchField;
/**
 * Vyrváří celý panel pro zobrazení transakcí
 * */
    public TransactionsPanel(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;

        setLayout(new BorderLayout());

        JPanel controlsPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Hledat:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchTransactions());
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Hledat");
        searchButton.addActionListener(e -> searchTransactions());
        searchPanel.add(searchButton);

        controlsPanel.add(searchPanel, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Obnovit");
        refreshButton.addActionListener(e -> refreshData());
        actionsPanel.add(refreshButton);

        controlsPanel.add(actionsPanel, BorderLayout.EAST);

        add(controlsPanel, BorderLayout.NORTH);

        tableModel = new TransactionTableModel(inventoryManager.getAllTransactions());
        transactionsTable = new JTable(tableModel);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * obnoví data v tabulce
     * ziská aktulní data a knim přidá po refreshování pokud se něco stalo další transakci
     * */
    public void refreshData() {
        tableModel.updateData(inventoryManager.getAllTransactions());
    }

    /**
     * Hledá tranksakce z vyhledávacího pole
     * pokud je prázdné zobrazí všechny transakce
     * aktualizuje tabulku s pomocí
     * @tableModel.updateData()
     * */
    private void searchTransactions() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            tableModel.updateData(inventoryManager.getAllTransactions());
        } else {
            tableModel.updateData(inventoryManager.searchTransactions(keyword));
        }
    }
}

