import java.util.List;

/**
 * Správce uživatelů – nyní deleguje veškeré ukládání na DatabaseManager.
 */
class UserManager {

    private final DatabaseManager db;

    public UserManager(DatabaseManager db) {
        this.db = db;
    }

    public void addUser(User user) {
        db.addUser(user);
    }

    public boolean removeUser(User user) {
        return db.removeUser(user);
    }

    public User authenticateUser(String firstName, String lastName,
                                 String password, String serialNumber) {
        return db.authenticateUser(firstName, lastName, password, serialNumber);
    }

    public List<User> getAllUsers() {
        return db.getAllUsers();
    }
}