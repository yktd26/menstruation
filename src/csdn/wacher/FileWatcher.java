/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csdn.wacher;

/**
 *
 * @author vdmdev2
 */
public interface FileWatcher extends Runnable{
    public void addListener(FileEventListener listener);
    public void watch();
}
