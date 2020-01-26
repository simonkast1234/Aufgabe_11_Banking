import static java.lang.Thread.sleep;

public class ProducerBranch implements Runnable {
    BlockingQueue bq;
    final static int MAXTRANSACTIONAMOUNT = 10000;
    final static int TRANSACTIONSPERPRODUCER = 1000000;

    public ProducerBranch(BlockingQueue bq) {
        this.bq = bq;
    }

    @Override
    public void run() {
        for (int i = 0; i < TRANSACTIONSPERPRODUCER; i++) {
            try {
                int fromAccountID = (int)(Math.random() * BlockingQueue.NUMBEROFACCOUNTS + 1.0);
                int toAccountID = (int)(Math.random() * BlockingQueue.NUMBEROFACCOUNTS + 1.0);
                if(toAccountID == fromAccountID) continue;
                int amount = (int)(Math.random() * MAXTRANSACTIONAMOUNT + 1.0);
                bq.produce(new Transaction(fromAccountID, toAccountID, amount));
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
