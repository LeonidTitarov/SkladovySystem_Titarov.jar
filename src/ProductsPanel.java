import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Panel pro správu produktů
class ProductsPanel extends JPanel {
    private InventoryManager inventoryManager;
    private User currentUser;

    private JTable productsTable;
    private JTextField searchField;
    private JComboBox<ProductCategory> categoryFilter;
    private ProductTableModel tableModel;

    /**
     * Konstruktor pro vytvoření panelu správy produktů.
     * Inicializuje kompletní uživatelské rozhraní včetně vyhledávacích filtrů,
     * ovládacích tlačítek a tabulky produktů.
     * @param inventoryManager správce inventáře pro práci s produkty
     * @param currentUser aktuálně přihlášený uživatel
     *
     *                    Tuhle celou metodu mi udelal ChatGTP
     */
    public ProductsPanel(InventoryManager inventoryManager, User currentUser) {
        this.inventoryManager = inventoryManager;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());

        JPanel controlsPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Hledat:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> filterProducts());
        filterPanel.add(searchField);

        JButton searchButton = new JButton("Hledat");
        searchButton.addActionListener(e -> filterProducts());
        filterPanel.add(searchButton);

        filterPanel.add(new JLabel("Kategorie:"));
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem(null);
        for (ProductCategory category : ProductCategory.values()) {
            categoryFilter.addItem(category);
        }
        categoryFilter.addActionListener(e -> filterProducts());
        filterPanel.add(categoryFilter);

        controlsPanel.add(filterPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Přidat produkt");
        addButton.addActionListener(e -> addProduct());
        actionsPanel.add(addButton);

        JButton editButton = new JButton("Upravit");
        editButton.addActionListener(e -> editProduct());
        actionsPanel.add(editButton);

        JButton deleteButton = new JButton("Odstranit");
        deleteButton.addActionListener(e -> deleteProduct());
        actionsPanel.add(deleteButton);

        JButton receiveButton = new JButton("Příjem");
        receiveButton.addActionListener(e -> receiveProducts());
        actionsPanel.add(receiveButton);

        JButton issueButton = new JButton("Výdej");
        issueButton.addActionListener(e -> issueProducts());
        actionsPanel.add(issueButton);

        controlsPanel.add(actionsPanel, BorderLayout.EAST);

        add(controlsPanel, BorderLayout.NORTH);

        tableModel = new ProductTableModel(inventoryManager.getAllProducts());
        productsTable = new JTable(tableModel);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Obnovuje data v tabulce produktů.
     * Získá aktuální data z inventoryManager a aktualizuje
     * tableModel pro zobrazení nejnovějších informací.
     */
    public void refreshData() {
        tableModel.updateData(inventoryManager.getAllProducts());
    }

    /**
     * Filtruje produkty podle zadaných kritérií.
     * Kombinuje vyhledávání podle textu a filtrování podle kategorie.
     * Aktualizuje zobrazení tabulky s filtrovanými výsledky.
     */
    private void filterProducts() {
        List<Product> filteredProducts;

        String searchText = searchField.getText().trim();
        ProductCategory category = (ProductCategory) categoryFilter.getSelectedItem();

        if (category != null) {
            filteredProducts = inventoryManager.getProductsByCategory(category);
            if (!searchText.isEmpty()) {
                List<Product> searchResults = new ArrayList<>();
                for (Product product : filteredProducts) {
                    if (product.getProductId().toLowerCase().contains(searchText.toLowerCase()) ||
                            product.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            product.getStatus().toLowerCase().contains(searchText.toLowerCase())) {
                        searchResults.add(product);
                    }
                }
                filteredProducts = searchResults;
            }
        } else if (!searchText.isEmpty()) {
            filteredProducts = inventoryManager.searchProducts(searchText);
        } else {
            filteredProducts = inventoryManager.getAllProducts();
        }

        tableModel.updateData(filteredProducts);
    }

    /**
     * Zobrazuje dialog pro přidání nového produktu.
     * Kontroluje oprávnění uživatele, validuje vstupní data
     * a přidává produkt do inventáře včetně transakce.
     */
    private void addProduct() {
        if (currentUser.getRole() == UserRole.EMPLOYEE) {
            JOptionPane.showMessageDialog(this,
                    "Nemáte oprávnění přidávat nové produkty.",
                    "Přístup odepřen",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }



        ///    Tuhle celou UI krom logiky mi udelal ChatGTP


        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Přidat nový produkt", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ID produktu:"));
        JTextField idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Název:"));
        JTextField nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Množství:"));
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        formPanel.add(quantitySpinner);

        formPanel.add(new JLabel("Kategorie:"));
        JComboBox<ProductCategory> categoryCombo = new JComboBox<>(ProductCategory.values());
        formPanel.add(categoryCombo);

        formPanel.add(new JLabel("Status:"));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Skladem", "Málo zásob", "Vyprodáno", "Objednáno"});
        formPanel.add(statusCombo);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Uložit");
        saveButton.addActionListener(e -> {
            String productId = idField.getText().trim();
            String name = nameField.getText().trim();
            int quantity = (Integer) quantitySpinner.getValue();
            ProductCategory category = (ProductCategory) categoryCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();

            if (productId.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "ID produktu a název jsou povinné položky.",
                        "Chybějící údaje",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (inventoryManager.findProductById(productId) != null) {
                JOptionPane.showMessageDialog(dialog,
                        "Produkt s tímto ID již existuje.",
                        "Duplicitní ID",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product newProduct = new Product(productId, name, quantity, category, status);
            inventoryManager.addProduct(newProduct);
            inventoryManager.addTransaction(new InventoryTransaction(
                    TransactionType.RECEIVE,
                    productId,
                    name,
                    quantity,
                    currentUser.getFullName()
            ));
            refreshData();
            dialog.dispose();
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Zrušit");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Zobrazuje dialog pro úpravu existujícího produktu.
     * Kontroluje výběr produktu a oprávnění uživatele.
     * Umožňuje úpravu všech parametrů kromě ID produktu.
     */
    private void editProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vyberte produkt k úpravě.",
                    "Žádný produkt není vybrán",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentUser.getRole() == UserRole.EMPLOYEE) {
            JOptionPane.showMessageDialog(this,
                    "Nemáte oprávnění upravovat produkty.",
                    "Přístup odepřen",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
                       ///   Tuhle celou UI mi udelal ChatGTP


        int modelRow = productsTable.convertRowIndexToModel(selectedRow);
        Product selectedProduct = tableModel.getProductAt(modelRow);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Upravit produkt", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ID produktu:"));
        JTextField idField = new JTextField(selectedProduct.getProductId());
        idField.setEditable(false);
        formPanel.add(idField);

        formPanel.add(new JLabel("Název:"));
        JTextField nameField = new JTextField(selectedProduct.getName());
        formPanel.add(nameField);

        formPanel.add(new JLabel("Množství:"));
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(
                selectedProduct.getQuantity(), 0, 1000, 1));
        formPanel.add(quantitySpinner);

        formPanel.add(new JLabel("Kategorie:"));
        JComboBox<ProductCategory> categoryCombo = new JComboBox<>(ProductCategory.values());
        categoryCombo.setSelectedItem(selectedProduct.getCategory());
        formPanel.add(categoryCombo);

        formPanel.add(new JLabel("Status:"));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Skladem", "Málo zásob", "Vyprodáno", "Objednáno"});
        statusCombo.setSelectedItem(selectedProduct.getStatus());
        formPanel.add(statusCombo);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Uložit");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            int quantity = (Integer) quantitySpinner.getValue();
            ProductCategory category = (ProductCategory) categoryCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Název je povinná položka.",
                        "Chybějící údaje",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            inventoryManager.updateProduct(
                    selectedProduct.getProductId(),
                    name,
                    quantity,
                    category,
                    status
            );

            refreshData();
            dialog.dispose();
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Zrušit");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Odstraňuje vybraný produkt z inventáře.
     * Kontroluje výběr produktu a oprávnění (pouze admin).
     * Zobrazuje potvrzovací dialog před smazáním.
     */
    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vyberte produkt k odstranění.",
                    "Žádný produkt není vybrán",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentUser.getRole() != UserRole.ADMIN) {
            JOptionPane.showMessageDialog(this,
                    "Pouze administrátor může odstraňovat produkty.",
                    "Přístup odepřen",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = productsTable.convertRowIndexToModel(selectedRow);
        Product selectedProduct = tableModel.getProductAt(modelRow);

        int option = JOptionPane.showConfirmDialog(this,
                "Opravdu chcete odstranit produkt \"" + selectedProduct.getName() + "\"?",
                "Potvrzení odstranění",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            inventoryManager.removeProduct(selectedProduct.getProductId());
            refreshData();
        }
    }

    /**
     * Zpracovává příjem produktů na sklad.
     * Kontroluje výběr produktu, validuje zadané množství
     * a aktualizuje inventář včetně vytvoření transakce.
     */
    private void receiveProducts() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vyberte produkt k příjmu na sklad.",
                    "Žádný produkt není vybrán",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = productsTable.convertRowIndexToModel(selectedRow);
        Product selectedProduct = tableModel.getProductAt(modelRow);

        String input = JOptionPane.showInputDialog(this,
                "Zadejte množství k příjmu na sklad:",
                "Příjem produktů",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.isEmpty()) {
            return;
        }

        try {
            int quantity = Integer.parseInt(input);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Množství musí být větší než nula.",
                        "Neplatné množství",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (inventoryManager.receiveProducts(selectedProduct.getProductId(), quantity, currentUser)) {
                refreshData();
                JOptionPane.showMessageDialog(this,
                        "Úspěšně přijato " + quantity + " kusů produktu \"" + selectedProduct.getName() + "\".",
                        "Příjem dokončen",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Nepodařilo se přijmout produkt na sklad.",
                        "Chyba příjmu",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Zadejte platné číslo.",
                    "Neplatný vstup",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Zpracovává výdej produktů ze skladu.
     * Kontroluje dostupnost zásob, validuje množství
     * a aktualizuje inventář včetně vytvoření transakce.
     */
    private void issueProducts() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vyberte produkt k výdeji ze skladu.",
                    "Žádný produkt není vybrán",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = productsTable.convertRowIndexToModel(selectedRow);
        Product selectedProduct = tableModel.getProductAt(modelRow);

        if (selectedProduct.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Produkt \"" + selectedProduct.getName() + "\" není skladem.",
                    "Nelze vydat",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Zadejte množství k výdeji ze skladu (max. " + selectedProduct.getQuantity() + "):",
                "Výdej produktů",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.isEmpty()) {
            return;
        }

        try {
            int quantity = Integer.parseInt(input);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Množství musí být větší než nula.",
                        "Neplatné množství",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantity > selectedProduct.getQuantity()) {
                JOptionPane.showMessageDialog(this,
                        "Nelze vydat více kusů, než je skladem.",
                        "Nedostatek zásob",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (inventoryManager.issueProducts(selectedProduct.getProductId(), quantity, currentUser)) {
                refreshData();
                JOptionPane.showMessageDialog(this,
                        "Úspěšně vydáno " + quantity + " kusů produktu \"" + selectedProduct.getName() + "\".",
                        "Výdej dokončen",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Nepodařilo se vydat produkt ze skladu.",
                        "Chyba výdeje",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Zadejte platné číslo.",
                    "Neplatný vstup",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
