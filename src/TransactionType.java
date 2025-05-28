

/**
 * Enum pro transakce
 * */
enum TransactionType {
    RECEIVE("Příjem"),
    ISSUE("Výdej");

    private String display;

    TransactionType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
