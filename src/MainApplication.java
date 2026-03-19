import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class MainApplication extends JFrame {
    private User currentUser;
    private InventoryManager inventoryManager;
    private UserManager userManager;

    private JTabbedPane tabbedPane;
    private ProductsPanel productsPanel;
    private TransactionsPanel transactionsPanel;
    private UserManagementPanel userManagementPanel;
    private JLabel statusLabel;


    /**
     * @MainApplication
     * konstruktor
     * */
    public MainApplication(User currentUser, InventoryManager inventoryManager, UserManager userManager) {
        this.currentUser = currentUser;
        this.inventoryManager = inventoryManager;
        this.userManager = userManager;
        setupUI();
    }


    /**
     * Inicializuje a konfiguruje uživatelské rozhraní hlavního okna aplikace.
     * Vytváří layout s nástrojovou lištou, záložkovým panelem a stavovým řádkem.
     * Pro uživatele administrávy přídává záložku pro správu se zaměstnanci.
     * */

    // základní vlastnosti hlavního okna
    private void setupUI() {
        setTitle("Skladový systém - " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // hlavní panel borderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // vytvoření lišty v horním panelu
        JToolBar toolBar = createToolBar();
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // Vytvoření záložkového panelu a základních záložek
        tabbedPane = new JTabbedPane();
        productsPanel = new ProductsPanel(inventoryManager, currentUser);
        transactionsPanel = new TransactionsPanel(inventoryManager);

        // Přidání záložek Produkty plus transkce
        tabbedPane.addTab("Produkty", new ImageIcon(), productsPanel, "Správa produktů ve skladu");
        tabbedPane.addTab("Transakce", new ImageIcon(), transactionsPanel, "Historie transakcí");

        // přidání záložky pouze pro uživatele  administrály
        if (currentUser.getRole() == UserRole.ADMIN) {
            userManagementPanel = new UserManagementPanel(userManager);
            tabbedPane.addTab("Uživatelé", new ImageIcon(), userManagementPanel, "Správa uživatelů");
        }

        // přidání záložkového papíru do středu
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // vytvoření stavového panelu do spodní části
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Přihlášen jako: " + currentUser.getFullName() + " | Role: " + currentUser.getRole());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // přidání lebelu do pravé části panelu
        JLabel versionLabel = new JLabel("Verze 1.0");
        versionLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(versionLabel, BorderLayout.EAST);

        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        // přidání listener než uživatel defenitivně bude chtít ukončit svůj program
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        MainApplication.this,
                        "Opravdu chcete ukončit aplikaci?",
                        "Potvrzení ukončení",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }


    /**
     * Vytvoří nástrojovou lištu s tlačítky Obnovit, Odhlásit se a Ukončit.
     * @return nakonfigurovaná nástrojová lišta
     */

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton refreshButton = new JButton("Obnovit");
        refreshButton.addActionListener(e -> refreshData());

        JButton logoutButton = new JButton("Odhlásit se");
        logoutButton.addActionListener(e -> InventorySystem.logout());

        JButton exitButton = new JButton("Ukončit");
        exitButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Opravdu chcete ukončit aplikaci?",
                    "Potvrzení ukončení",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        toolBar.add(refreshButton);
        toolBar.addSeparator();
        toolBar.add(logoutButton);
        toolBar.add(exitButton);

        return toolBar;
    }

    /**
     * Obnoví data ve všech panelech aplikace podle oprávnění uživatele.
     */

    // refhreshuje data no a pokud jste navic jeste admin tak vam to refhresne i uzivatele
    private void refreshData() {
        productsPanel.refreshData();
        transactionsPanel.refreshData();
        if (currentUser.getRole() == UserRole.ADMIN && userManagementPanel != null) {
            userManagementPanel.refreshData();
        }
    }
}
