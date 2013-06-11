package csdn.Concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public class Activity implements Runnable{
    
    private Condition playCondition;
    private Lock lock;
    
    public Activity(Lock lock, Condition playCondition){
        this.playCondition = playCondition;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (true){
            lock.lock();
            try {
                this.playCondition.await();
                play();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        
    }
    
    private void play(){
        System.out.println("play");
    }
    
}
