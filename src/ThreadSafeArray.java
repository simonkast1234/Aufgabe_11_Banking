/**
 * producerConsumer Problem: Mehrere Threads greifen auf dieselben Ressourcen zu
 * Achtung: sleep() oder wait() wirft immer eine interruptedException wenn notified
 *          -> also handlen (try-catch oder hochwerfen)
 * wait(), sleep() etc in diesem Fall immer im synchronised block, damit klar ist, welcher Thread schlafen muss.
 * notify() wird ein random thread geweckt
 */

import java.util.Random;

public class ThreadSafeArray {
    int[] arr;
    int index;
    boolean rising = true;
    public ThreadSafeArray(int size) {
        this.arr = new int[size];
        this.index = 0;
    }

    public void produce() throws InterruptedException {
        int r = new Random().nextInt();
        synchronized (this) { // könnte auch (this)/(arr) nehmen, dann wäre das ganze objekt gesperrt
            while(!rising) { // Schleife, falls einfügen aufgrund ArrayOutOfBounds nicht passiert
                wait(); // hier warten, bis notified . WENN DAS HIER, DANN IMMER INTERRUPTED EXCEPTION WERFEN
            }
            if(index == arr.length - 1) this.rising = false;
            System.out.println(Thread.currentThread().getName() + " produced " + r);
            arr[index++] = r;
            notifyAll(); // alle aufwecken, damit consumer weiß, dass es weitergehen kann
        }
    }

    public void consume() throws InterruptedException {
        synchronized (this) {
            while(rising) {
                wait();
            }
            if(index == 1) this.rising = true;
            int toR = this.arr[0];
            for (int i = 1; i < this.arr.length; i++) {
                this.arr[i-1] = this.arr[i];
            }
            this.index--;
            System.out.println(Thread.currentThread().getName() + " consumed " + toR);
            notifyAll();
        }
    }

    public static void main(String[] args) {
        ThreadSafeArray arr = new ThreadSafeArray(20);
        new Thread(new ProduceThread(arr)).start(); // runnable
        new ConsumeThread(arr).start(); // thread
        new Thread(new ProduceThread(arr)).start(); // runnable
        new ConsumeThread(arr).start(); // thread
    }
}
