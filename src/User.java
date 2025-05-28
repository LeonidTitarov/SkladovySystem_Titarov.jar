// Třída reprezentující uživatele systému
class User {
    private String firstName;
    private String lastName;
    private String password;
    private String serialNumber;
    private UserRole role;

    /**
     * Konstruktor pro User
     * */
    public User(String firstName, String lastName, String password, String serialNumber, UserRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.serialNumber = serialNumber;
        this.role = role;
    }

    /**
     * Getter Setter
     * */
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Prověřování zda se naše údaje shodují s
     * údají v našem systému, které jsme předem zadali.
     * */
    public boolean authenticate(String firstName, String lastName, String password, String serialNumber) {
        return this.firstName.equals(firstName) &&
                this.lastName.equals(lastName) &&
                this.password.equals(password) &&
                this.serialNumber.equals(serialNumber);
    }

    /**
     * Výpis jména plus role
     * */
    public String toString() {
        return firstName + " " + lastName + " (" + role + ")";
    }
}
