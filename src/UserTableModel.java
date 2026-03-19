    import javax.swing.table.AbstractTableModel;
    import java.util.List;

    // Model dat pro tabulku uživatelů
    class UserTableModel extends AbstractTableModel {

    //    Představ si, že AbstractTableModel je prázdný formulář od úřadu.
    //    Ty (UserTableModel) ho vyplníš svými daty (jména, čísla...), aby byl užitečný.

        private String[] columnNames = {"Jméno", "Příjmení", "Sériové číslo", "Role"};
        private List<User> users;

        /**
         * Konstuktor incialituje model tabulky pro seznam uživatelů
         * */
        public UserTableModel(List<User> users) {
            this.users = users;
        }

        /**
         * Aktualizuje data v tabulce s novým seznamem uživatelů a
         * automaticky informuje tabulku o změně s pomocí
         * @fireTableDataChanged();
         * */
        public void updateData(List<User> users) {
            this.users = users;
            fireTableDataChanged();
        }
        /**
         * Vrací počet řádku
         * */
        @Override
        public int getRowCount() {
            return users.size();
        }
        /**
         * Vrací počet sloupců
         * */
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        /**
         * Vrací název sloupce podle indexu
         * */
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        /**
         * vrací hodnotu pro konkretní bunku v tabulce podle řádku a sloupce
         *  rowIndex index řádku-(uživatele)
         *  columIndex index sloupce
         * */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User user = users.get(rowIndex);
            switch (columnIndex) {
                case 0: return user.getFirstName();
                case 1: return user.getLastName();
                case 2: return user.getSerialNumber();
                case 3: return user.getRole();
                default: return null;
            }
        }

        /**
         * Nachází uživatele podle indexu v řádku
         * */
        public User getUserAt(int rowIndex) {
            return users.get(rowIndex);
        }
    }
