package csdn.Concurrency.openclose;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TheLock  implements Runnable{

    private Condition openCondition;
    private Condition closeCondition;
    private Lock theLock;
    
    public TheLock(){
        theLock = new ReentrantLock();
        openCondition = theLock.newCondition();
        closeCondition = theLock.newCondition();
    }
    
    @Override
    public void run() {
        new Thread(new Open(theLock, openCondition)).start();
        new Thread(new Close(theLock, closeCondition)).start();
        while(true){
            try {
                if (openTheLock() == true){
                    theLock.lock();
                    openCondition.signalAll();
                    theLock.unlock();
                    
                    Thread.sleep(3000);
                    
                    theLock.lock();
                    closeCondition.signalAll();
                    theLock.unlock();
                }else{
                    System.out.println("Open action failed");
                }
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                theLock.unlock();
            }
        }
    }
    
    private boolean openTheLock(){
        return (System.nanoTime() % 3) == 1;
    }
    
    class Open implements Runnable{
        private Lock lock;
        private Condition openCondition;
        
        Open(Lock lock, Condition openCondition){
            this.lock = lock;
            this.openCondition = openCondition;
        }
        
        @Override
        public void run() {
            while (true){
                lock.lock();
                try {
                    this.openCondition.await();
                    onOpen();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
        
        protected void onOpen(){
            System.out.println("Opened");
        }
        
    }
    
    
    class Close implements Runnable{
        private Lock lock;
        private Condition closeCondition;
        
        Close(Lock lock, Condition closeCondition){
            this.lock = lock;
            this.closeCondition = closeCondition;
        }
        
        @Override
        public void run() {
            while (true){
                lock.lock();
                try {
                    this.closeCondition.await();
                    onClose();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
        
        protected void onClose(){
            System.out.println("Closed");
        }
    }
    
    public static void main(String[] args){
        new Thread(new TheLock()).start();
    }
}
