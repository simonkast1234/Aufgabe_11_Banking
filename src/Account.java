public class Account {

    int ID;
    public static int nextID = 1;
    final static int STARTBALANCE = 100000;
    int balance;

    public Account() {
        this.ID = nextID++;
        this.balance = STARTBALANCE;
    }
}
