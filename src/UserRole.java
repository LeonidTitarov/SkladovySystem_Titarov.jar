

/**
 * Enum pro uživatelské role
 * */
enum UserRole {
    ADMIN("Administrátor"),
    WAREHOUSE_MANAGER("Vedoucí skladu"),
    EMPLOYEE("Zaměstnanec");

    private String display;

    UserRole(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
