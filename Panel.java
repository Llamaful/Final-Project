import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
// import java.awt.Graphics;
// import javax.swing.JLabel; 
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Panel extends JPanel {
  public static final Color BG_COLOR = Color.decode("0x181819");

  final int UPDATE_MS = 50;

  // player: 64px by 64px
  Image playerImage = getImage("images/player.png");
  double playerX = 128, playerY = 128, playerVelocityX = 0, playerVelocityY = 0;
  final Rectangle playerBound = new Rectangle(96, 96, 32, 64);
  final double playerACCELERATION = 8000, playerMAXSPEED = 500, FRICTION = 0.00001;
  
  Image weaponImage = getImage("images/pistol.png");
  ArrayList<Bullet> bullets = new ArrayList<Bullet>();
  final double bulletSpeed = 750;

  class Bullet {
    public int x, y;
    public double velocityX, velocityY;
    public Rectangle bounds = new Rectangle();
    public Bullet(int x, int y, double velocityX, double velocityY) {
      this.x = x; this.y = y; this.velocityX = velocityX; this.velocityY = velocityY;
      bounds = new Rectangle(x, y, 4, 4);
    }
    public void move(double dt) {
      x += velocityX * dt;
      y += velocityY * dt;
    }
  }
  
  Point mouse;
  byte dir_up = 0, dir_down = 0, dir_left = 0, dir_right = 0;

  private Image bgImage = getImage("images/background1.jpg");
  private Walls walls = new Walls(new Rectangle(0, 0, 1024, 64));

  class Walls {
    Rectangle[] walls;
    public Walls(Rectangle... walls) {
      this.walls = walls;
    }
    public boolean isPlayerColliding() {
      for (Rectangle r : walls) {
        if (r.intersects(playerBound)) return true;
      }
      return false;
    }

    public boolean isColliding(Rectangle r) {
      for (Rectangle o : walls) {
        if (o.intersects(r)) return true;
      }
      return false;
    }
  }
  
  public Panel() {
    setBackground(BG_COLOR);
    setPreferredSize(new Dimension(1024, 768));

    Mouse mouse = new Mouse();
    addMouseListener(mouse);
    addMouseMotionListener(mouse);

    addKeyListener(new KeyPress());

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
      System.out.println("Clicked!"); // emily
      // timer.start()
      
      // create bullet (give x, y, and velocities)
      // add to array list
      // playerXDirection = (playerVelocityX) / Math.abs(playerVelocityX);
      double dirX = mouse.x - playerX;
      double dirY = mouse.y - playerY;
      final double magnitude = invSqrt(dirX * dirX + dirY * dirY);
      dirX *= magnitude; dirY *= magnitude;

      Bullet b = new Bullet((int) playerX, (int) playerY, bulletSpeed * dirX, bulletSpeed * dirY);
      bullets.add(b);
      
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
  }

  /** Don't worry about it */
  public static double invSqrt(double x) {
    double xhalf = 0.5d * x;
    long i = Double.doubleToLongBits(x);
    i = 0x5fe6ec85e7de30daL - (i >> 1);
    x = Double.longBitsToDouble(i);
    x *= (1.5d - xhalf * x * x);
    return x;
}

  public class TimerListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      updatePlayer();
      updateBullet();

      repaint();
    }

    private void updateBullet() {
      // emily add code
      // for each Bullet b, call b.move(UPDATE_MS / 1000.0)
      // check for wall collisions or out of bounds: delete
      for (int i = 0; i < bullets.size(); i++) {
        bullets.get(i).move(UPDATE_MS / 1000.0);
        if (walls.isColliding(bullets.get(i).bounds)) {
          bullets.remove(i);
          i--;
        }
      }
    }

    private void updatePlayer() {
      playerVelocityX += (dir_right - dir_left) * playerACCELERATION * UPDATE_MS / 1000.0;
      playerVelocityY += (dir_down - dir_up) * playerACCELERATION * UPDATE_MS / 1000.0;
      if (magnitude(playerVelocityX, playerVelocityY) > playerMAXSPEED) {
        double angle = Math.atan2((dir_down - dir_up), (dir_right - dir_left));
        playerVelocityX = Math.cos(angle) * playerMAXSPEED;
        playerVelocityY = Math.sin(angle) * playerMAXSPEED;
      }

      movePlayer(UPDATE_MS / 1000.0);

      updatePlayerBounds();
    }
  }

  private void updatePlayerBounds() {
    // update bounding box
    playerBound.x = (int)playerX - 16;
    playerBound.y = (int)playerY - 16;
  }

  private void movePlayer(double dt) {
    movePlayerStepX(playerVelocityX * dt);
    movePlayerStepY(playerVelocityY * dt);
    playerVelocityX *= Math.pow(FRICTION, dt);
    playerVelocityY *= Math.pow(FRICTION, dt);
  }

  private void movePlayerStepX(double dx) {
    double dir = dx < 0 ? -1 : 1;
    for (int i = 0; i <= Math.abs(dx)-1; i++) {
      playerX += dir;
      updatePlayerBounds();
      if (walls.isPlayerColliding()) {
        playerX -= dir;
        return;
      }
    }
    // sorry :(
    dir = dx % 1;
    playerX += dir;
    updatePlayerBounds();
    if (walls.isPlayerColliding()) {
      playerX -= dir;
      return;
    }
  }

  private void movePlayerStepY(double dy) {
    double dir = dy < 0 ? -1 : 1;
    for (int i = 0; i <= Math.abs(dy)-1; i++) {
      playerY += dir;
      updatePlayerBounds();
      if (walls.isPlayerColliding()) {
        playerY -= dir;
        return;
      }
    }
    // sorry :(
    dir = dy % 1;
    playerY += dir;
    updatePlayerBounds();
    if (walls.isPlayerColliding()) {
      playerY -= dir;
      return;
    }
  }

  private double magnitude(double x, double y) {
    return Math.sqrt(x * x + y * y);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Draw background
    g.drawImage(bgImage, 0, 0, 1024, 768, this);

    // Draw player
    g.drawImage(playerImage, (int)playerX-32, (int)playerY-16, 64, 64, this);

    g.setColor(Color.RED);
    g.drawRect(playerBound.x, playerBound.y, playerBound.width, playerBound.height);

    // Draw bullets
    g.setColor(Color.RED);
    for (Bullet b : bullets) {
      g.fillOval(b.x-2, b.y-2, 4, 4);
    }

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