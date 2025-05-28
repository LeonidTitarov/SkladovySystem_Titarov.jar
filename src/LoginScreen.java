import javax.swing.*;
import java.awt.*;

// Třída pro přihlašovací obrazovku


class LoginScreen extends JFrame {
    private JTextField firstNameField; // jméno
    private JTextField lastNameField; // přijmení
    private JPasswordField passwordField; // heslo
    private JTextField serialNumberField; // id daného uživatele
    private JButton loginButton; // vstupní tlačítko
    private JButton exitButton; // výstupní tlačítko

/**
 * @LoginScreen
 * je přihlašovací okno, kde uživatel musí zadat své údaje
 * aby se dostal do systému Je zde okno pro jméno,přijmení,heslo,seriove cislo
 * */

    public LoginScreen() {
        setTitle("Skladový systém - Přihlášení");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("SKLADOVÝ SYSTÉM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 120));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("Přihlašte se pro přístup", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 20));

        formPanel.add(new JLabel("Jméno:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Příjmení:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Heslo:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Sériové číslo:"));
        serialNumberField = new JTextField();
        formPanel.add(serialNumberField);

        JPanel emptyPanel = new JPanel();
        formPanel.add(emptyPanel);

        loginButton = new JButton("Přihlásit se");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> attemptLogin());
        formPanel.add(loginButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitButton = new JButton("Ukončit");
        exitButton.addActionListener(e -> System.exit(0));
        bottomPanel.add(exitButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        getRootPane().setDefaultButton(loginButton);
    }


    /**
     * ověření přihlasovacích údajů pokud nebudou vyplněna nebo se shodovat s těmi co
     * mám v systému vypíše to chybnou zprávu okna
     * */

    private void attemptLogin() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String serialNumber = serialNumberField.getText().trim();

        // kontroluje aktulní přihlašovací místa.
        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || serialNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Všechna pole musí být vyplněna!", "Chyba přihlášení", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // kontroluje údaje v systému, které jsou předem napsané.
        if (!InventorySystem.authenticateUser(firstName, lastName, password, serialNumber)) {
            JOptionPane.showMessageDialog(this, "Neplatné přihlašovací údaje!", "Chyba přihlášení", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}