import static java.lang.Thread.sleep;

public class ProduceThread implements Runnable {
    ThreadSafeArray arr;

    public ProduceThread(ThreadSafeArray arr) {
        this.arr = arr;
    }

    @Override
    public void run() {
        while(true) {
            try{
                arr.produce();
                sleep(100); // damits nicht so chaotisch ist
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
