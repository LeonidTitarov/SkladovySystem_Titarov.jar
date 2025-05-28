import javax.swing.*;
import java.awt.*;

// Panel pro správu uživatelů (jen pro admin)
class UserManagementPanel extends JPanel {
    private UserManager userManager;
    private JTable usersTable;
    private UserTableModel tableModel;

    /**
     * Konstruktor pro vytvoření panelu správy uživatelů.
     * Inicializuje uživatelské rozhraní s ovládacími tlačítky
     * a tabulkou pro zobrazení všech uživatelů v systému.
     * @param userManager správce uživatelů pro práci s uživatelskými účty
     */
    public UserManagementPanel(UserManager userManager) {
        this.userManager = userManager;

        setLayout(new BorderLayout());

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Přidat uživatele");
        addButton.addActionListener(e -> addUser());
        controlsPanel.add(addButton);

        JButton editButton = new JButton("Upravit");
        editButton.addActionListener(e -> editUser());
        controlsPanel.add(editButton);

        JButton deleteButton = new JButton("Odstranit");
        deleteButton.addActionListener(e -> deleteUser());
        controlsPanel.add(deleteButton);

        add(controlsPanel, BorderLayout.NORTH);

        tableModel = new UserTableModel(userManager.getAllUsers());
        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Obnovuje data v tabulce uživatelů.
     * Získá aktuální seznam všech uživatelů z userManager
     * a aktualizuje tableModel pro zobrazení nejnovějších dat.
     */
    public void refreshData() {
        tableModel.updateData(userManager.getAllUsers());
    }

    /**
     * Zobrazuje dialog pro přidání nového uživatele.
     * Validuje zadané údaje a kontroluje, zda jsou
     * všechna povinná pole vyplněna před uložením.
     */
    private void addUser() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Přidat nového uživatele", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Jméno:"));
        JTextField firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Příjmení:"));
        JTextField lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Heslo:"));
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Sériové číslo:"));
        JTextField serialNumberField = new JTextField();
        formPanel.add(serialNumberField);

        formPanel.add(new JLabel("Role:"));
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
        formPanel.add(roleCombo);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Uložit");
        saveButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String serialNumber = serialNumberField.getText().trim();
            UserRole role = (UserRole) roleCombo.getSelectedItem();

            if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || serialNumber.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Všechna pole musí být vyplněna.",
                        "Chybějící údaje",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            userManager.addUser(new User(firstName, lastName, password, serialNumber, role));
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
     * Zobrazuje dialog pro úpravu existujícího uživatele.
     * Kontroluje výběr uživatele a předvyplní formulář
     * současnými hodnotami, heslo lze ponechat prázdné.
     */
    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vyberte uživatele k úpravě.",
                    "Žádný uživatel není vybrán",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User selectedUser = tableModel.getUserAt(selectedRow);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Upravit uživatele", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Jméno:"));
        JTextField firstNameField = new JTextField(selectedUser.getFirstName());
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Příjmení:"));
        JTextField lastNameField = new JTextField(selectedUser.getLastName());
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Heslo (nechte prázdné pro ponechání):"));
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Sériové číslo:"));
        JTextField serialNumberField = new JTextField(selectedUser.getSerialNumber());
        formPanel.add(serialNumberField);

        formPanel.add(new JLabel("Role:"));
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setSelectedItem(selectedUser.getRole());
        formPanel.add(roleCombo);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Uložit");
        saveButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String serialNumber = serialNumberField.getText().trim();
            UserRole role = (UserRole) roleCombo.getSelectedItem();

            if (firstName.isEmpty() || lastName.isEmpty() || serialNumber.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Jméno, příjmení a sériové číslo jsou povinné položky.",
                        "Chybějící údaje",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            userManager.removeUser(selectedUser);
            userManager.addUser(new User(
                    firstName,
                    lastName,
                    password.isEmpty() ? selectedUser.getPassword() : password,
                    serialNumber,
                    role
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
     * Odstraňuje vybraného uživatele ze systému.
     * Kontroluje výběr uživatele a brání odstranění
     * posledního administrátora v systému.
     */
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vyberte uživatele k odstranění.",
                    "Žádný uživatel není vybrán",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User selectedUser = tableModel.getUserAt(selectedRow);

        if (selectedUser.getRole() == UserRole.ADMIN && getUserAdminCount() <= 1) {
            JOptionPane.showMessageDialog(this,
                    "Nelze odstranit posledního administrátora v systému.",
                    "Operace zamítnuta",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Opravdu chcete odstranit uživatele \"" + selectedUser.getFullName() + "\"?",
                "Potvrzení odstranění",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            userManager.removeUser(selectedUser);
            refreshData();
        }
    }

    /**
     * Prochází všechny uživatele a vrací počet těch
     * s rolí ADMIN pro kontrolu bezpečnosti systému.
     * @return počet administrátorů v systému
     */
    private int getUserAdminCount() {
        int count = 0;
        for (User user : userManager.getAllUsers()) {
            if (user.getRole() == UserRole.ADMIN) {
                count++;
            }
        }
        return count;
    }
}