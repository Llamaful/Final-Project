import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
// import java.awt.Graphics;
// import javax.swing.JLabel; 

public class Panel extends JPanel {
  public static final Color BG_COLOR = Color.decode("0x181819");

  final int UPDATE_MS = 50;

  Player player = new Player();

  double dir_x = 0, dir_y = 0;
  
  public Panel() {
    setBackground(BG_COLOR);
    setPreferredSize(new Dimension(1000, 700));

    Mouse mouse = new Mouse();
    addMouseListener(mouse);
    addMouseMotionListener(mouse);

    addKeyListener(new KeyPress());
    System.out.println("HELLO");

    Timer timer = new Timer(UPDATE_MS, new TimerListener());
    timer.setRepeats(true);
    timer.start();

    // initialize variables
    // start timer
  }

  private class KeyPress implements KeyListener {
    public KeyPress() {
      setFocusable(true);
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();
      if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) dir_y = -1;
      else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) dir_y = 1;
      if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) dir_x = -1;
      else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) dir_x = 1;
    }
    public void keyReleased(KeyEvent e) {
      int key = e.getKeyCode();
      if (key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) dir_y = 0;
      if (key == KeyEvent.VK_A || key == KeyEvent.VK_D || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) dir_x = 0;
    }
  }

  private class Mouse implements MouseListener, MouseMotionListener {
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {
      System.out.println("Clicked!");
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
  }

  public class TimerListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      player.dx += dir_x * player.ACCELERATION;
      player.dy += dir_y * player.ACCELERATION;
      if (magnitude(player.dx, player.dy) > player.SPEED) {
        double angle = Math.atan2(dir_y, dir_x);
        player.dx = Math.cos(angle) * player.SPEED;
        player.dy = Math.sin(angle) * player.SPEED;
      }

      player.move(UPDATE_MS / 1000.0);

      repaint();
    }
  }

  private double magnitude(double x, double y) {
    return Math.sqrt(x * x + y * y);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Texture test = new Texture("llama_with_hay.jpeg", 100, 100);
    test.draw(g, this, 50, 50);

    player.draw(g);
  }
}