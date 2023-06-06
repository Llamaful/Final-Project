
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
// import java.awt.Graphics;
// import javax.swing.JLabel; 
import java.io.File;
import java.io.IOException;

public class Panel extends JPanel {
  public static final Color BG_COLOR = Color.decode("0x181819");

  final int UPDATE_MS = 50;

  Image playerImage = getImage("images/player.png");
  double playerX = 50, playerY = 50, playerVelocityX = 0, playerVelocityY = 0;
  final double playerACCELERATION = 5000, playerMAXSPEED = 20000, FRICTION = 0.0001;
  
  Image weaponImage = getImage("images/pistol.png");
  
  Point mouse;
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
    public void mouseDragged(MouseEvent e) {
      mouse = e.getPoint();
    }
    public void mouseMoved(MouseEvent e) {
      mouse = e.getPoint();
    }
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
      playerVelocityX += dir_x * playerACCELERATION * UPDATE_MS / 1000.0;
      playerVelocityY += dir_y * playerACCELERATION * UPDATE_MS / 1000.0;
      if (magnitude(playerVelocityX, playerVelocityY) > playerMAXSPEED) {
        double angle = Math.atan2(dir_y, dir_x);
        playerVelocityX = Math.cos(angle) * playerMAXSPEED;
        playerVelocityY = Math.sin(angle) * playerMAXSPEED;
      }

      movePlayer(UPDATE_MS / 1000.0);

      repaint();
    }
  }

  private void movePlayer(double dt) {
    playerX += playerVelocityX * dt;
    playerY += playerVelocityY * dt;
    playerVelocityX *= Math.pow(FRICTION, dt);
    playerVelocityY *= Math.pow(FRICTION, dt);
  }

  private double magnitude(double x, double y) {
    return Math.sqrt(x * x + y * y);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.drawImage(getImage("images/llama_with_hay.jpeg"), 50, 50, 64, 32, this);

    g.drawImage(playerImage, (int)playerX-32, (int)playerY-16, 64, 64, this);

    if (mouse == null) return;
    drawWeapon((Graphics2D)g);
  }

  private void drawWeapon(Graphics2D g) {
    final double angle = Math.atan2(mouse.getY() - playerY, mouse.getX() - playerX);
    g.translate((int)(playerX + Math.cos(angle) * 32), (int)(playerY + Math.sin(angle) * 32));
    g.rotate(angle);
    if (angle > Math.PI/2 || angle < -Math.PI/2) {
      g.scale(1, -1);
    }
    g.drawImage(weaponImage, 0, 0, 32, 32, this);
  }

  public Image getImage(String pathname) {
    try {
      return ImageIO.read(new File(pathname));
    } catch (IOException e1) {
      try {
        return ImageIO.read(getClass().getResource(pathname));
      } catch (IOException e2) {
        e1.printStackTrace();
        e2.printStackTrace();
      }
    }
    return null;
  }
}