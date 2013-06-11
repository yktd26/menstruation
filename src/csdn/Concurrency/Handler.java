/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csdn.Concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Handler implements Runnable{
    
    private Condition playCondition;
    private Lock lock;
    private boolean var = true;
    public Handler(Lock lock, Condition playCondition){
        this.playCondition = playCondition;
        this.lock = lock;
    }
    
    private boolean change(boolean toChange){
        return !toChange;
    }

    @Override
    public void run() {
        while (true){
            lock.lock();
            try {
                var = change(var);
                if (var == true){
                    playCondition.signalAll();
                    lock.unlock();
                    Thread.sleep(5000);
                }else{
                    lock.unlock();
                    Thread.sleep(10000);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                lock.unlock();
            }
        }
    }
    
    
    public static void main(String... args){
        final Lock lock = new ReentrantLock();
        final Condition playCondition = lock.newCondition();
        new Thread(new Activity(lock, playCondition)).start();
        new Thread(new Handler(lock, playCondition)).start();
    }
    
}
