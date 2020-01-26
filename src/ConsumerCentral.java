import static java.lang.Thread.sleep;

public class ConsumerCentral implements Runnable {
    BlockingQueue bq;

    public ConsumerCentral(BlockingQueue bq) {
        this.bq = bq;
    }

    @Override
    public void run() {
        while(true) {
            try {
                bq.consume();
            } catch(TimeException e) {
                System.out.println(e.getMessage());
                break;
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
