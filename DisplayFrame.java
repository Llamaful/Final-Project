import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;

public class DisplayFrame extends JFrame {
  public static final Color BG_COLOR = Color.decode("0x181819");

  public DisplayFrame() {
    setSize(1024, 798);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setBackground(BG_COLOR.darker());
    // getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
    getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
    // setAlwaysOnTop(true);

    add(new Panel());
  }
}