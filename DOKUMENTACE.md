# Skladový systém – Dokumentace

## Co to vlastně je

Skladový systém je desktopová aplikace v Javě kterou jsem dělal jako školní projekt. Slouží k tomu aby firma nebo sklad mohl sledovat co má na skladě, kdo tam pracuje a co se s produkty děje – tedy příjmy a výdeje. Celé to běží přes grafické okno takže uživatel nemusí nic psát do terminálu, prostě kliká.

Data se ukládají do SQL databáze která běží v Dockeru, takže když aplikaci zavřeš a znovu spustíš, vše tam stále je. To byl jeden z hlavních cílů – aby to nebyla jen "hračka" co po zavření ztratí všechna data.

---

## Jak to funguje – průchod aplikací

Když spustíš aplikaci, první věc která se stane je že se pokusí připojit k databázi. Pokud se to nepovede, rovnou ti vyhodí chybové okno a skončí – nemá cenu pouštět zbytek bez databáze.

Pak se zobrazí přihlašovací obrazovka kde musíš zadat jméno, příjmení, heslo a sériové číslo. Tyhle čtyři věci se musí shodovat s tím co je v databázi, nestačí jen heslo. Po přihlášení se otevře hlavní okno a podle toho jakou roli máš vidíš buď dvě nebo tři záložky.

---

## Co dělají jednotlivé třídy

### `InventorySystem.java`
Tohle je vstupní bod celé aplikace, obsahuje metodu `main`. Stará se o inicializaci – spouští databázové připojení, vytváří správce uživatelů a inventáře, a při prvním spuštění naplní databázi demo daty. Také zde žijí statické metody `authenticateUser()` a `logout()` které se volají z různých míst aplikace.

### `DatabaseManager.java`
Tahle třída je srdce celého propojení s databází. Obsahuje veškerou SQL logiku – přidávání, mazání, aktualizaci i vyhledávání pro produkty, uživatele i transakce. Používá `PreparedStatement` aby se předešlo SQL injection útokům. Ostatní třídy ji jen volají a nemusí vůbec vědět jak SQL funguje.

### `InventoryManager.java`
Správce inventáře – stará se o produkty a transakce. Metody jako `receiveProducts()` a `issueProducts()` v sobě kombinují dvě věci najednou: upraví množství produktu a zároveň zapíší transakci do databáze. Původně tato třída pracovala s ArrayListem v paměti, teď vše deleguje na `DatabaseManager`.

### `UserManager.java`
Jednodušší správce který se stará o uživatele. Má metody pro přidání, odebrání a ověření uživatele. Autentizace funguje tak že se pošlou čtyři parametry do databáze a buď se najde shoda nebo ne.

### `Product.java`
Model produktu – drží data jako id, název, množství, kategorii a status. Zajímavá věc je metoda `updateStatus()` která se automaticky volá při každé změně množství a sama rozhodne jestli je produkt "Skladem", "Málo zásob" nebo "Vyprodáno". Tohle se děje bez toho aby to musel někdo ručně nastavovat.

### `User.java`
Model uživatele. Obsahuje metodu `authenticate()` která porovnává zadané údaje s uloženými – jméno, příjmení, heslo i sériové číslo musí sedět.

### `InventoryTransaction.java`
Reprezentuje jeden záznam v historii – co se stalo, s jakým produktem, kolik kusů a kdo to udělal. Timestamp se nastavuje automaticky při vytvoření objektu.

### Enum třídy
`ProductCategory`, `TransactionType` a `UserRole` jsou enumy které omezují možné hodnoty. Například kategorie může být jen ELECTRONICS, FURNITURE, OFFICE\_SUPPLIES a podobně. Každý enum má také český popis pro zobrazení v GUI přes metodu `toString()`.

### Panel třídy
`ProductsPanel`, `TransactionsPanel` a `UserManagmentPanel` jsou grafické panely které vidíš jako záložky v hlavním okně. Každý panel si sám řídí svoje tabulky, vyhledávání a tlačítka. Panel pro uživatele je viditelný pouze pro admina.

### Table model třídy
`ProductTableModel`, `TransactionTableModel` a `UserTableModel` jsou mosty mezi daty a tabulkami v GUI. Dědí z `AbstractTableModel` a říkají Swing tabulce kolik má sloupců, jak se jmenují a co zobrazit v každé buňce.

### `LoginScreen.java`
Přihlašovací okno. Validuje že jsou vyplněna všechna pole a pak zavolá `InventorySystem.authenticateUser()`. Pokud se přihlášení nepovede, ukáže chybovou hlášku a smaže heslo.

### `MainApplication.java`
Hlavní okno aplikace po přihlášení. Vytváří záložkový panel, toolbar s tlačítky Obnovit / Odhlásit / Ukončit a stavový řádek dole kde vidíš kdo je přihlášen. Před zavřením se ptá jestli to opravdu chceš.

---

## Co mi dalo zabrat

**Největší problém byl Docker a připojení k databázi.** Dlouho jsem nevěděl proč se aplikace nemůže připojit a přitom důvod byl jednoduchý – Docker Desktop nebyl spuštěný. Pak byl kontejner vytvořený ale zastavený, pak SQL Server potřeboval půl minuty na nastartování. Každý z těchto kroků vypadá jednoduše ale když nevíš co hledáš, strávíš nad tím hodně času.

**JDBC driver** byl další věc. Java sama o sobě neumí komunikovat s SQL Serverem, potřebuješ k tomu přidat extra `.jar` soubor. Chybová hláška `No suitable driver found` na začátku vůbec nevypadala jako "chybí ti soubor" – spíš jako by byl problém v kódu.

**Refaktoring `InventoryManager` a `UserManager`** byl zajímavý. Původně obě třídy držely data v ArrayListu v paměti. Přepsat je tak aby místo ArrayListu volaly databázi nebylo technicky složité, ale musel jsem přemýšlet o tom jak změna jedné věci ovlivní zbytek kódu – například `receiveProducts()` musí po příjmu zboží aktualizovat jak množství v tabulce `products` tak přidat řádek do tabulky `transactions`.

---

## Co jsem se naučil

Pochopil jsem jak vlastně funguje propojení aplikace s databází přes JDBC – že nestačí jen napsat SQL, musíš mít driver, URL připojení, ošetřit výjimky a správně zavírat spojení. Používání `try-with-resources` pro automatické zavírání `Connection` a `PreparedStatement` mi přišlo jako elegantní řešení.

Naučil jsem se pracovat s Dockerem na základní úrovni – spouštět kontejnery, koukat na logy, ověřovat jestli něco běží. Předtím jsem Docker znal jen jako pojem.

Také jsem lépe pochopil rozdíl mezi tím mít data v paměti versus v databázi. Data v paměti jsou rychlá ale dočasná. Databáze je pomalejší ale data přežijí cokoliv – restart aplikace, restart počítače, výpadek proudu.

---

## Jak projekt spustit

Před spuštěním aplikace musíš mít běžící Docker s SQL Serverem:

```bash
# Spusť Docker Desktop, pak:
docker start sql

# Počkej 30 sekund a ověř:
docker logs sql --tail 3
# Hledáš: Recovery is complete.
```

Pak spusť aplikaci normálně z IntelliJ. Při prvním spuštění se automaticky vytvoří demo uživatelé a produkty.

Přihlašovací údaje pro testování:

| Jméno | Heslo | Sériové číslo | Role |
|---|---|---|---|
| Leonid Titarov | tvojeMama | 6969 | Admin |
| Pavel Vaclavek | GeometyDash | 7980 | Vedoucí skladu |
| Damian Smekal | Tasemnice | SN24680 | Zaměstnanec |
