public class ConsumeThread extends Thread {
    ThreadSafeArray arr;

    public ConsumeThread(ThreadSafeArray arr) {
        this.arr = arr;
    }

    @Override
    public void run() {
        while(true) {
            try{
                arr.consume();
                sleep(100);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
