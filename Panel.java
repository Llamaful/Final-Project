import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
// import java.awt.Graphics;
// import javax.swing.JLabel; 

public class Panel extends JPanel {
  public static final Color BG_COLOR = Color.decode("0x181819");

  // add variables here
  
  public Panel() {
    setBackground(BG_COLOR);
    setPreferredSize(new Dimension(1000, 700));

    Mouse mouse = new Mouse();
    addMouseListener(mouse);
    addMouseMotionListener(mouse);

    addKeyListener(new KeyPress());

    // initialize variables
    // start timer
  }

  private class KeyPress implements KeyListener {
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
  }

  private class Mouse implements MouseListener, MouseMotionListener {
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Texture test = new Texture("llama_with_hay.jpeg", 100, 100);
    test.drawImage(g, this, 50, 50);
  }
}