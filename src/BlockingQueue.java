import Prog1Tools.IOTools;

import java.sql.Time;
import java.util.Random;
import java.util.Set;

public class BlockingQueue {
    static final int MAXNUMBEROFTRANSACTIONSINQUEUE = 100;
    static final int NUMBEROFACCOUNTS = 10000;
    static final int MAXTRYNUMBER = 3;
    int index; // Einfüge-Index
    Account[] accounts;
    Transaction[] transactions;

    public BlockingQueue() {
        this.accounts = new Account[NUMBEROFACCOUNTS];
        for (int i = 0; i < NUMBEROFACCOUNTS; i++) {
            accounts[i] = new Account();
        }
        this.transactions = new Transaction[MAXNUMBEROFTRANSACTIONSINQUEUE];
        this.index = 0;
    }

    public void produce(Transaction transaction) throws InterruptedException {
        synchronized (this) {
            while(!(this.index < MAXNUMBEROFTRANSACTIONSINQUEUE)) { // warum nicht außerhalb?
                wait();
            }
            transactions[index++] = transaction;
            notifyAll();
        }
    }

    public void consume() throws InterruptedException, TimeException {
        synchronized (this) {
            while(!(this.index > 0)) {
                long timeBarrier = System.currentTimeMillis() + 5000;
                wait(5000);
                if(System.currentTimeMillis() > timeBarrier) {
                    throw new TimeException(Thread.currentThread().getName() + " terminated after 5s waiting time");
                }
            }
            // Buchen wenn möglich
            if((this.accounts[this.transactions[0].fromAccountID - 1].balance - this.transactions[0].amount) >= 0) {
                this.accounts[this.transactions[0].fromAccountID - 1].balance -= this.transactions[0].amount;
                this.accounts[this.transactions[0].toAccountID - 1].balance += this.transactions[0].amount;
                moveTransactionsForward();
                this.index--;
            } else if(this.transactions[0].tryNr >= MAXTRYNUMBER) { // letzter Versuch gescheitert
                /*
                System.out.println("A transaction was dropped after third try! \n" +
                        "trynr: " + this.transactions[0].tryNr + " accID: "
                        + this.transactions[0].fromAccountID + " that has "
                        + this.accounts[this.transactions[0].fromAccountID - 1].balance);
                 */
                moveTransactionsForward();
                this.index--;
            } else { // hinten anstellen
                Transaction tmp = this.transactions[0];
                tmp.tryNr++;
                moveTransactionsForward();
                if(this.index > 0) {
                    this.transactions[index-1] = tmp;
                } else {
                    this.transactions[0] = tmp;
                }
            }
            notifyAll();
        }
    }

    private void moveTransactionsForwardNull(int k) {
        for (int i = k; i < this.transactions.length; i++) {
            this.transactions[i] = null;
        }
    }

    private void moveTransactionsForward() {
        for (int i = 1; i < this.transactions.length; i++) {
            if(this.transactions[i-1] == this.transactions[i]) {
                moveTransactionsForwardNull(i);
                return;
            }
            this.transactions[i-1] = this.transactions[i];
        }
        this.transactions[transactions.length - 1] = null;
    }

    void printTransactions() {
        System.out.println("--- TRANSACTIONS --- ");
        for (int i = 0; i < transactions.length; i++) {
            if(transactions[i] != null) {
                System.out.print(" " + transactions[i].fromAccountID + " | " + transactions[i].toAccountID + " | " + transactions[i].amount);
            } else {
                System.out.print(" null");
            }
        }
        System.out.println();
    }

    void printAccounts() {
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i] != null) {
                System.out.print(accounts[i].balance + " ");
            }
        }
        System.out.println();
    }

    void checkSum() {
        int must = NUMBEROFACCOUNTS * Account.STARTBALANCE;
        int is = 0;
        synchronized (this) {
            for (int i = 0; i < this.accounts.length; i++) {
                is += accounts[i].balance;
            }
        }
        System.out.println("Balance check: " + is + "/" + must);
    }

    String timeEvaluation(long timeStart, int F_Producer, int Z_Consumer) {
        long timeNeeded = System.currentTimeMillis() - timeStart - 5000;
        long transactionsMade = ProducerBranch.TRANSACTIONSPERPRODUCER * F_Producer;
        return timeNeeded + " ms needed. That were " + transactionsMade/timeNeeded
                + " transactions/ms (Z=" + Z_Consumer + ",F=" + F_Producer + ")";
    }

    public static void main(String[] args) throws Exception {
        do {
            int Z_Consumer = IOTools.readInt("Z (Consumer) = ");
            int F_Producer = IOTools.readInt("F (Producer) = ");
            int threadsBefore = Thread.activeCount();
            long timeStart = System.currentTimeMillis();
            long tmpTime = timeStart;
            BlockingQueue bq = new BlockingQueue();

            for (int i = 0; i < F_Producer; i++) {
                new Thread(new ProducerBranch(bq)).start();
            }

            for (int i = 0; i < Z_Consumer; i++) {
                new Thread(new ConsumerCentral(bq)).start();
            }

            while(Thread.activeCount() > threadsBefore) {
                Thread.sleep(20);
                if(System.currentTimeMillis() > tmpTime + 1000) {
                    tmpTime = System.currentTimeMillis();
                    //bq.printAccounts();
                }
            }
            System.out.println(bq.timeEvaluation(timeStart, F_Producer, Z_Consumer));
            bq.checkSum();
        } while (Character.toLowerCase(IOTools.readChar("[q]uit or [r]epeat ")) != 'q');
    }
}
