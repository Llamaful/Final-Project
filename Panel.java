
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
  final double playerACCELERATION = 8000, playerMAXSPEED = 500, FRICTION = 0.00001;
  
  Image weaponImage = getImage("images/pistol.png");
  
  Point mouse;
  byte dir_up = 0, dir_down = 0, dir_left = 0, dir_right = 0;
  
  public Panel() {
    setBackground(BG_COLOR);
    setPreferredSize(new Dimension(1024, 768));

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
      if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) dir_up = 1;
      else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) dir_down = 1;
      if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) dir_left = 1;
      else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) dir_right = 1;
    }
    public void keyReleased(KeyEvent e) {
      int key = e.getKeyCode();
      if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) dir_up = 0;
      else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) dir_down = 0;
      if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) dir_left = 0;
      else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) dir_right = 0;
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
      playerVelocityX += (dir_right - dir_left) * playerACCELERATION * UPDATE_MS / 1000.0;
      playerVelocityY += (dir_down - dir_up) * playerACCELERATION * UPDATE_MS / 1000.0;
      if (magnitude(playerVelocityX, playerVelocityY) > playerMAXSPEED) {
        double angle = Math.atan2((dir_down - dir_up), (dir_right - dir_left));
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

  private Image bg = getImage("images/llama_with_hay.jpeg");

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Draw background
    g.drawImage(getImage("images/background1.jpg"), 0, 0, 1024, 768, this);

    g.drawImage(bg, 50, 50, 64, 32, this);

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