package csdn.demo;
 
 
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.List;
 
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
 
import com.sun.awt.AWTUtilities;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
 
public class FlowerRain extends JFrame {
 
    private static final long serialVersionUID = -8037287523655159012L;
 
    private int num = 99;// 花朵数量
    private int speed = 3;// 下降速度
    private boolean flag = true;
    private List<JLabel> labelList = new ArrayList<JLabel>(num);
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private ImageIcon icon = new ImageIcon(ImageIO.read(new FileInputStream(("C:/Users/vdmdev2/Desktop/flower.png"))));// 花朵图片
 
    public FlowerRain() throws Exception {
 
        getContentPane().setLayout(null);
        setTitle("漫天花雨");
        setSize(screenSize);
        setResizable(false);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
        setIconImage(icon.getImage());
        AWTUtilities.setWindowOpaque(this, false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // 防止最小化
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if (getState() == 1) {
                    setState(0);
                }
            }
        });
 
        // Ctrl + E 关闭窗口
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_E) {
                    dispatchEvent(new WindowEvent(FlowerRain.this, WindowEvent.WINDOW_CLOSING));
                }
            }
        });
 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                flag = false;// 将线程循环标志置为false
            }
        });
 
        for (int i = 0; i < num; i++) {
            JLabel jlbl = new JLabel(icon);
            jlbl.setSize(20, 20);
            jlbl.setLocation(random(screenSize.width), random(screenSize.height));
 
            labelList.add(jlbl);
            add(jlbl);
        }
 
    }
 
    public void move() {
        new Thread() {
            public void run() {
                while (flag) {
                    try {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                for (int i = 0; i < labelList.size(); i++) {
                                    JLabel jlbl = labelList.get(i);
                                    Point location = jlbl.getLocation();
 
                                    jlbl.setLocation(location.x + (i % 5 - 2), location.y + speed);
 
                                    location = jlbl.getLocation();
                                    if (location.y >= screenSize.height || location.x <= 0 || location.x >= screenSize.width) {
                                        jlbl.setLocation(random(screenSize.width), 0);
                                    }
                                }
                            }
                        });
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
 
    }
 
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    setVisible(true);
                    move();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
 
    public int random(int max) {
        return (int) (Math.random() * max);
    }
 
    public int random(int min, int max) {
        return random(max - min) + min;
    }
 
    public static void main(String[] args) throws Exception {
        new FlowerRain().start();
    }
}