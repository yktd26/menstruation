/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csdn.wacher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

public class TextFileAppendWatcher implements FileWatcher {

    private long lastLength;
    private File watchedFile;
    private List<FileEventListener> listeners;

    public TextFileAppendWatcher(String filepath) throws IOException {

        this.watchedFile = new File(filepath);
        if ((!watchedFile.exists()) || (!watchedFile.isFile())) {
            throw new FileNotFoundException(watchedFile.getCanonicalPath());
        }

        this.listeners = new ArrayList<FileEventListener>();
        //From begining
        //this.lastLength = 0;

        //From current size
        this.lastLength = Files.size(Paths.get(this.watchedFile.getCanonicalPath()));
    }

    @Override
    public void run() {
        WatchService watcher = null;
        try {
            Path file = Paths.get(watchedFile.getParent());
            watcher = file.getFileSystem().newWatchService();
            file.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException ex) {
            return;
        }
        while (true) {
            try {
                WatchKey watchKey = watcher.take();
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent event : events) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW){
                        continue;
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY
                            && ((Path) event.context())
                                        .endsWith(watchedFile.getName())) {
                        
                        long newSize = Files.size(Paths.get(this.watchedFile.getCanonicalPath()));
                        //Workaround why we can have a 0 size file
                        if (newSize == 0){
                            continue;
                        }
                        FileEvent e = new FileEvent(
                                changed(
                                    newSize
                                ));

                        for (FileEventListener l : listeners) {
                            l.performed(e);
                        }

                    }
                }
                //Reset
                if (!watchKey.reset()){
                    break;
                }
            } catch (InterruptedException | IOException e) {
                break;
            }
        }
    }

    @Override
    public void addListener(FileEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void watch() {
        new Thread(this).start();
    }

    protected String changed(long newLength) {
        try {
            RandomAccessFile f = new RandomAccessFile(this.watchedFile, "r");
            
            if (this.lastLength == newLength){
                return "";
            }
            
            //Seek to last position
            if (this.lastLength > 0) {
                f.seek(this.lastLength);
            }
            //Remove
            if (newLength < this.lastLength) {
                this.lastLength = newLength;
                return "";
            }

            byte[] readed = new byte[new Long(newLength - this.lastLength).intValue()];
            f.readFully(readed);
            this.lastLength = newLength;
            return new String(readed);
        } catch (IOException ex) {
            this.lastLength = newLength;
            return "";
        }
    }

    public static void main(String[] args) {
        /*if (args.length == 0){
         System.exit(1);
         }*/
        FileWatcher watcher = null;
        try {
            watcher = new TextFileAppendWatcher("C:\\Users\\vdmdev2\\Desktop\\test.txt"/*args[0]*/);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
        //registre implementation to list of listeners
        watcher.addListener(new FileEventListener() {
            @Override
            public void performed(FileEvent event) {
                System.out.print(event.changedText);
            }
        });

        //Start watch the file
        watcher.watch();
    }
}
