public class Transaction {
    int fromAccountID;
    int toAccountID;
    int amount;
    int tryNr;

    public Transaction(int fromAccountID, int toAccountID, int amount) {
        this.fromAccountID = fromAccountID;
        this.toAccountID = toAccountID;
        this.amount = amount;
        this.tryNr = 1;
    }
}
