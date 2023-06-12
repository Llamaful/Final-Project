import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// import java.awt.Graphics;
// import javax.swing.JLabel; 
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Panel extends JPanel {
  public static final Color BG_COLOR = Color.decode("0x181819");

  final int UPDATE_MS = 50;

  final Random random = new Random();

  // player: 64px by 64px
  Player player = new Player(getImage("images/player.png"), 128, 128);
  
  ArrayList<Bullet> bullets = new ArrayList<Bullet>();

  ArrayList<Enemy> enemies = new ArrayList<Enemy>();

  class Bullet {
    public int x, y;
    public Color color;
    public double velocityX, velocityY, damage;
    public boolean sourcePlayer; // true = from player | false = from enemy
    public Rectangle bounds = new Rectangle();
    public Bullet(Color color, int x, int y, double velocityX, double velocityY, double damage, boolean sourcePlayer) {
      this.color = color;
      this.x = x; this.y = y; this.velocityX = velocityX; this.velocityY = velocityY;
      this.damage = damage; this.sourcePlayer = sourcePlayer;
      bounds = new Rectangle(x, y, 8, 8);
    }
    public void move(double dt) {
      x += velocityX * dt;
      y += velocityY * dt;
      updateBounds();
    }
    public void updateBounds() {
      bounds.x = x;
      bounds.y = y;
    }
    public boolean shouldBeRemoved() {
      return screens[currentScreen].walls.isColliding(bounds) || x < -4 || x > 1028 || y < -4 || y > 772;
    }
  }
  
  Point mouse;
  byte dir_up = 0, dir_down = 0, dir_left = 0, dir_right = 0;

  // dimentions: 1024 by 768
  private Screen[] screens = new Screen[] {
    new Screen(getImage("images/background1.jpg"), new Walls(new Rectangle(0, 0, 1024, 64), new Rectangle(0, 64, 64, 640), new Rectangle(0, 704, 320, 64), new Rectangle(448, 704, 576, 64), new Rectangle(960, 192, 64, 512)), -1, -1, -1, 1),
    new Screen(getImage("images/background2.jpg"), new Walls(new Rectangle[0]), 1, 0, 1, 1)
  };
  private int currentScreen = 0;

  class Screen {
    public Image image;
    public Walls walls;
    public int exitTop, exitLeft, exitBottom, exitRight;
    public Screen(Image image, Walls walls, int exitTop, int exitLeft, int exitBottom, int exitRight) {
      this.image = image; this.walls = walls;
      this.exitTop = exitTop; this.exitLeft = exitLeft; this.exitBottom = exitBottom; this.exitRight = exitRight;
    }
  }
   
  class Walls {
    Rectangle[] walls;
    public Walls(Rectangle... walls) {
      this.walls = walls;
    }
    public boolean isPlayerColliding() {
      for (Rectangle r : walls) {
        if (r.intersects(player.bound)) return true;
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
    // setPreferredSize();
    setMaximumSize(new Dimension(1024, 768));
    setSize(new Dimension(1024, 768));

    Mouse mouse = new Mouse();
    addMouseListener(mouse);
    addMouseMotionListener(mouse);

    addKeyListener(new KeyPress());

    Timer timer = new Timer(UPDATE_MS, new TimerListener());
    timer.setRepeats(true);
    timer.start();

    // NOTE: remove later
    for (int i = 0; i < 10; i++) {
      enemies.add(new Enemy("images/enemy.png", "images/enemy_hit.png", 1064, 140, 64, 64, 20, 0.6)); 
    }

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
    public void mousePressed(MouseEvent e) {
      double dirX = mouse.x - player.x;
      double dirY = mouse.y - player.y;
      final double magnitude = invSqrt(dirX * dirX + dirY * dirY);
      dirX *= magnitude; dirY *= magnitude;

      Bullet b = new Bullet(player.weapon.bulletColor, (int) player.x, (int) player.y, player.weapon.speed * dirX, player.weapon.speed * dirY, player.weapon.damage, true);
      bullets.add(b);
      
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
  }

  /** Don't worry about it */
  public static double invSqrt(double x) {
    final double xhalf = 0.5d * x;
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
      updateEnemies();

      repaint();
    }

    private void updateBullet() {
      for (int i = 0; i < bullets.size(); i++) {
        bullets.get(i).move(UPDATE_MS / 1000.0);
        if (bullets.get(i).shouldBeRemoved()) {
          bullets.remove(i--);
        }
      }
    }

    private void updatePlayer() {
      player.velocityX += (dir_right - dir_left) * player.ACCELERATION * UPDATE_MS / 1000.0;
      player.velocityY += (dir_down - dir_up) * player.ACCELERATION * UPDATE_MS / 1000.0;
      if (magnitude(player.velocityX, player.velocityY) > player.MAXSPEED) {
        double angle = Math.atan2((dir_down - dir_up), (dir_right - dir_left));
        player.velocityX = Math.cos(angle) * player.MAXSPEED;
        player.velocityY = Math.sin(angle) * player.MAXSPEED;
      }

      movePlayer(UPDATE_MS / 1000.0);

      updatePlayerBounds();

      checkScreenExit();
    }

    private void updateEnemies() {
      for (int e = 0; e < enemies.size(); e++) {
        final Enemy en = enemies.get(e);
        en.moveToTarget(UPDATE_MS / 1000.0);

        // change target
        if (random.nextDouble() < 0.5 * (UPDATE_MS / 1000.0)) {
          en.target = new Point(random.nextInt(64, 960), random.nextInt(64, 704));
        }

        // fire bullet
        if (random.nextDouble() < en.firePercentage * (UPDATE_MS / 1000.0)) {
          double dirX = player.x - en.x;
          double dirY = player.y - en.y;
          final double magnitude = invSqrt(dirX * dirX + dirY * dirY);
          dirX *= magnitude; dirY *= magnitude;
          bullets.add(new Bullet(en.weapon.bulletColor, (int)en.x, (int)en.y, en.weapon.speed * dirX, en.weapon.speed * dirY, en.weapon.damage, false));
        }

        // bullet collisions
        for (int i = 0; i < bullets.size(); i++) {
          if (!bullets.get(i).sourcePlayer) continue;
          if (bullets.get(i).bounds.intersects(enemies.get(e).bounds)) {
            enemies.get(e).damage(bullets.get(i).damage);
            enemies.get(e).hit = true;
            if (enemies.get(e).health <= 0) enemies.remove(e--);
            bullets.remove(i--);
            break;
          }
        }
      }
    }
  }

  private void checkScreenExit() {
    if (player.y < -32) { /* Exit Top */
      if (switchScreen(screens[currentScreen].exitTop))
      player.y = 736;
    } else if (player.y > 800) { /* Exit Bottom */
      if (switchScreen(screens[currentScreen].exitBottom))
      player.y = 32;
    } else if (player.x < -16) { /* Exit Left */
      if (switchScreen(screens[currentScreen].exitLeft))
      player.x = 1008;
    } else if (player.x > 1040) { /* Exit Right */
      if (switchScreen(screens[currentScreen].exitRight))
      player.x = 16;
    }
  }

  private boolean switchScreen(int screen) {
    if (screen < 0 || screen >= screens.length) return false;
    currentScreen = screen;
    bullets.clear();
    return true;
  }

  private void updatePlayerBounds() {
    // update bounding box
    player.bound.x = (int)player.x - 16;
    player.bound.y = (int)player.y - 16;
  }

  private void movePlayer(double dt) {
    movePlayerStepX(player.velocityX * dt);
    movePlayerStepY(player.velocityY * dt);
    player.velocityX *= Math.pow(player.FRICTION, dt);
    player.velocityY *= Math.pow(player.FRICTION, dt);
  }

  private void movePlayerStepX(double dx) {
    double dir = dx < 0 ? -1 : 1;
    for (int i = 0; i <= Math.abs(dx)-1; i++) {
      player.x += dir;
      updatePlayerBounds();
      if (screens[currentScreen].walls.isPlayerColliding()) {
        player.x -= dir;
        return;
      }
    }
    // sorry :(
    dir = dx % 1;
    player.x += dir;
    updatePlayerBounds();
    if (screens[currentScreen].walls.isPlayerColliding()) {
      player.x -= dir;
      return;
    }
  }

  private void movePlayerStepY(double dy) {
    double dir = dy < 0 ? -1 : 1;
    for (int i = 0; i <= Math.abs(dy)-1; i++) {
      player.y += dir;
      updatePlayerBounds();
      if (screens[currentScreen].walls.isPlayerColliding()) {
        player.y -= dir;
        return;
      }
    }
    // sorry :(
    dir = dy % 1;
    player.y += dir;
    updatePlayerBounds();
    if (screens[currentScreen].walls.isPlayerColliding()) {
      player.y -= dir;
      return;
    }
  }

  private double magnitude(double x, double y) {
    return Math.sqrt(x * x + y * y);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Draw background
    g.drawImage(screens[currentScreen].image, 0, 0, 1024, 768, this);

    ArrayList<Sprite> sprites = new ArrayList<Sprite>(enemies);
    sprites.add(player);
    sprites.sort((a, b) -> (int)(a.getY() - b.getY()));
    for (Sprite s : sprites) {
      s.draw(s.getClass() == Player.class ? mouse : player.getPoint(), g, this);
    }

    // Draw bullets
    for (Bullet b : bullets) {
      g.setColor(b.color);
      g.fillOval(b.x-4, b.y-4, 8, 8);
    }
  }

  static public Image getImage(String pathname) {
    try {
      return ImageIO.read(new File(pathname));
    } catch (IOException e1) {
      try {
        return ImageIO.read(Panel.class.getResource(pathname));
      } catch (IOException e2) {
        e1.printStackTrace();
        e2.printStackTrace();
      }
    }
    return null;
  }
}