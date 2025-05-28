import java.util.ArrayList;
import java.util.List;

// Správce uživatelů
class UserManager {
    private List<User> users;

    /**
     * Konstruktor pro spávů zaměstatnanců
     * */
    public UserManager() {
        users = new ArrayList<>();
    }

    /**
     * Přidání uživatele do seznamu uživatelů
     * */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Odebrání uživatele z seznamu uživatelů
     * */

    public boolean removeUser(User user) {
        return users.remove(user);
    }

    /**
     * Ověření uživatelé v systému
     * */

    public User authenticateUser(String firstName, String lastName, String password, String serialNumber) {
        for (User user : users) {
            if (user.authenticate(firstName, lastName, password, serialNumber)) {
                return user;
            }
        }
        return null;
    }

/**
 * Vrací nový seznam se všemi uživateli
 * */

    public List<User> getAllUsers() {
        return new java.util.ArrayList<>(users);
    }}

//    public User findUserByFullName(String fullName) {
//        for (User user : users) {
//            if (user.getFullName().equals(fullName)) {
//                return user;
//            }
//        }
//        return null;
//    }
//}
