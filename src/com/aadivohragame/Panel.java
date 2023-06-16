package com.aadivohragame;
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

  final boolean SPEEDRUN_DEBUG = true;

  final int UPDATE_MS = 50;
  double TIMER = 0, screenTIMER = 0;

  static final Random random = new Random();

  // player: 64px by 64px
  Player player = new Player(getImage("images/player.png"), 128, 128, 150);
  
  ArrayList<Bullet> bullets = new ArrayList<Bullet>();

  ArrayList<Enemy> enemies = new ArrayList<Enemy>();
  private int clearCount = 0;

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
      return screens[currentScreen].walls.isCollidingWall(bounds) || x < -4 || x > 1028 || y < -4 || y > 772;
    }
  }
  
  Point mouse;
  byte dir_up = 0, dir_down = 0, dir_left = 0, dir_right = 0;

  // dimentions: 1024 by 768
  private Screen[] screens = new Screen[] {
    new Screen(getImage("images/background1.jpg"), new Walls(new Rect(0, 0, 1024, 64), new Rect(0, 64, 64, 640), new Rect(0, 704, 320, 64), new Rect(448, 704, 576, 64), new Rect(960, 192, 64, 512)), -1, -1, 2, 1),
    new Screen(getImage("images/background2.jpg"), new Walls(new Rect(0, 0, 1024, 64), new Rect(960, 64, 64, 640), new Rect(0, 192, 64, 512), new Rect(0, 704, 320, 64), new Rect(448, 704, 576, 64), new Rect(160, 512, 64, 64), new Rect(592, 192, 262, 382)), -1, 0, 3, -1),
    new Screen(getImage("images/background3.jpg"), new Walls(new Rect(0, 704, 1024, 64), new Rect(0, 64, 64, 640), new Rect(0, 0, 320, 64), new Rect(448, 0, 576, 64), new Rect(960, 192, 64, 512)), 0, -1, -1, 3),
    new Screen(getImage("images/background4.jpg"), new Walls(new Rect(0, 0, 320, 64), new Rect(448, 0, 576, 64), new Rect(0, 704, 1024, 64), new Rect(960, 64, 64, 640), new Rect(0, 192, 64, 512), new Rect(224, 64, 96, 256, false), new Rect(448, 64, 96, 640, false)), 1, 2, -1, -1)
  };
  private int currentScreen = 0;

  class Screen {
    private static int screenCount = 0;

    public Image image;
    public Walls walls;
    public ArrayList<Enemy> enemies;
    public int exitTop, exitLeft, exitBottom, exitRight, screenNumber;
    public Screen(Image image, Walls walls, int exitTop, int exitLeft, int exitBottom, int exitRight) {
      this.image = image; this.walls = walls;
      this.exitTop = exitTop; this.exitLeft = exitLeft; this.exitBottom = exitBottom; this.exitRight = exitRight;
      screenNumber = screenCount++;

      enemies = new ArrayList<Enemy>();
      addEnemies();
    }
    private void addEnemies() {
      if (walls == null) return;
      if (screenNumber == 3) {
        int debug_rifleCount = 0;
        for (int i = 0; i < 12; i++) {
          final int x = i % 2 == 0 ? 820 : 720, y = 108 + i * 48;
          final double r = random.nextDouble();
          enemies.add(r < 0.2 ? createRifleEnemy(x, y) : createDefaultEnemy(x, y));
          if (r < 0.2) debug_rifleCount++;
        }
        if (SPEEDRUN_DEBUG) System.out.println("Screen #3 spawned " + debug_rifleCount + " rifle enemies (" + String.format("%+.2f", (((double)debug_rifleCount / 12 - 0.2) * 100)) + "%)");
        return;
      }
      if (screenNumber == 2) {
        // aadi boss
        Enemy boss = new Enemy("images/boss.png", "images/boss_hit.png", 500, 575, 256, 256, 1200, 5);
        boss.weapon = Weapon.newRifle();
        boss.weapon.speed /= 2;
        enemies.add(boss);
        return;
      }
      for (int i = 0; i < 10; i++) {
        Point p = getRandomPointWithin();
        enemies.add(createDefaultEnemy(p.x, p.y));
      }
      if (screenNumber == 1) {
        enemies.add(createRifleEnemy(530, 640));
        enemies.add(createRifleEnemy(725, 135));
      } else if (SPEEDRUN_DEBUG) {
        System.out.println("\nLoading map...");
      }
    }
    private Point getRandomPointWithin() {
      Point p = new Point(random.nextInt(128*3, 928), random.nextInt(128, 672));
      Rectangle r = new Rectangle(p.x-32, p.y-32, 64, 64);
      while (walls.isColliding(r)) {
        p = new Point(random.nextInt(128*3, 928), random.nextInt(128, 672));
        r.x = p.x-32; r.y = p.y-32;
      }
      return p;
    }
    private Enemy createDefaultEnemy(int x, int y) {
      return new Enemy("images/enemy.png", "images/enemy_hit.png", x, y, 64, 64, 20, 0.75);
    }
    private Enemy createRifleEnemy(int x, int y) {
      Enemy bigGuy = new Enemy("images/enemy2.png", "images/enemy2_hit.png", x, y, 64, 64, 80, 0.7);
      bigGuy.weapon = Weapon.newRifle();
      bigGuy.weapon.speed /= 2;
      return bigGuy;
    }
  }
   
  class Walls {
    Rect[] walls;
    public Walls(Rect... walls) {
      this.walls = walls;
    }
    public boolean isPlayerColliding() {
      for (Rect r : walls) {
        if (r.intersects(player.bound)) return true;
      }
      return false;
    }
    public boolean isColliding(Rectangle r) {
      for (Rect o : walls) {
        if (o.intersects(r)) return true;
      }
      return false;
    }
    public boolean isCollidingWall(Rectangle r) {
      for (Rect o : walls) {
        if (!o.isWall) continue;
        if (o.intersects(r)) return true;
      }
      return false;
    }
    public boolean isColliding(int x, int y) {
      for (Rect r : walls) {
        if (r.contains(x, y)) return true;
      }
      return false;
    }
  }

  Timer timer;
  boolean timerHasStarted = false;
  boolean runTimer = true;
  
  JLabel scoreLabel;
  int score = 0;
  
  public Panel() {
    setBackground(BG_COLOR);
    // setPreferredSize();
    setMaximumSize(new Dimension(1024, 768));
    setSize(new Dimension(1024, 768));

    scoreLabel = new JLabel("Score: 0");
    scoreLabel.setForeground(Color.WHITE);
    scoreLabel.setFont(getFont().deriveFont(20f));
    add(scoreLabel);

    Mouse mouse = new Mouse();
    addMouseListener(mouse);
    addMouseMotionListener(mouse);

    addKeyListener(new KeyPress());

    timer = new Timer(UPDATE_MS, new TimerListener());
    timer.setRepeats(true);
    // timer.start();

    enemies = screens[currentScreen].enemies;

    // initialize variables
    // start timer
  }

  private void addPoints(int num) {
    score += num;
    scoreLabel.setText("Score: " + score);
  }

  private class KeyPress implements KeyListener {
    public KeyPress() {
      setFocusable(true);
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
      if (!timerHasStarted) {
        timer.start();
        timerHasStarted = true;
        if (SPEEDRUN_DEBUG) {
          System.out.println("\n> Beginning speedrun at " + java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(java.time.LocalDateTime.now()));
        }
      }

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
      if (!timerHasStarted) repaint();
    }
    public void mouseMoved(MouseEvent e) {
      mouse = e.getPoint();
      if (!timerHasStarted) repaint();
    }
    public void mousePressed(MouseEvent e) {
      if (!timerHasStarted) return;

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
      if (runTimer) {
        TIMER += UPDATE_MS / 1000.0;
        screenTIMER += UPDATE_MS / 1000.0;
      }

      updatePlayer();
      if (player.health <= 0) return;
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

      for (int i = 0; i < bullets.size(); i++) {
        if (bullets.get(i).sourcePlayer) continue;
        if (bullets.get(i).bounds.intersects(player.bound)) {
          player.damage(bullets.get(i).damage);
          bullets.remove(i--);
          if (player.health <= 0) {
            timer.stop();
            repaint();
            return;
          }
        }
      }

      checkScreenExit();
    }

    private void updateEnemies() {
      for (int e = 0; e < enemies.size(); e++) {
        final Enemy en = enemies.get(e);
        en.moveToTarget(screens[currentScreen].walls, UPDATE_MS / 1000.0);

        // change target
        if (random.nextDouble() < 0.5 * (UPDATE_MS / 1000.0)) {
          en.target = new Point(random.nextInt(currentScreen == 3 ? 575 : 64, 960), random.nextInt(currentScreen == 2 ? 350 : 64, 704));
        }

        // fire bullet
        if (random.nextDouble() < en.firePercentage * (UPDATE_MS / 1000.0)) {
          double dirX = player.x - en.x;
          double dirY = player.y - en.y;
          final double magnitude = invSqrt(dirX * dirX + dirY * dirY);
          dirX *= magnitude; dirY *= magnitude;
          bullets.add(new Bullet(en.weapon.bulletColor, (int)en.x, (int)en.y, en.weapon.speed * dirX, en.weapon.speed * dirY, en.weapon.damage, false));
          if (currentScreen == 2) { // don't worry bout it
            dirX *= random.nextDouble(0.9, 1.1);
            dirY *= random.nextDouble(0.9, 1.1);
            bullets.add(new Bullet(en.weapon.bulletColor, (int)en.x, (int)en.y, en.weapon.speed * dirY, en.weapon.speed * dirX, en.weapon.damage, false));
            dirX *= random.nextDouble(0.9, 1.1);
            dirY *= random.nextDouble(0.9, 1.1);
            bullets.add(new Bullet(en.weapon.bulletColor, (int)en.x, (int)en.y, en.weapon.speed * -dirY, en.weapon.speed * -dirX, en.weapon.damage, false));
          }
        }

        // bullet collisions
        for (int i = 0; i < bullets.size(); i++) {
          if (!bullets.get(i).sourcePlayer) continue;
          if (bullets.get(i).bounds.intersects(enemies.get(e).bounds)) {
            enemies.get(e).damage(bullets.get(i).damage);
            enemies.get(e).hit = true;
            bullets.remove(i--);
            if (enemies.get(e).health <= 0) {
              addPoints(enemies.get(e).points);
              enemies.remove(e--);
              checkEnemies();
              break;
            }
          }
        }
      }
    }
  }

  private void checkEnemies() {
    if (enemies.size() == 0) clearCount++;
    if (clearCount == screens.length) {
      runTimer = false;
      if (SPEEDRUN_DEBUG) System.out.println("\nCleared with a time of " + getTimer(TIMER) + "\n");
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
    fixPlayer();
  }

  private boolean switchScreen(int screen) {
    if (screen < 0 || screen >= screens.length) return false;
    if (SPEEDRUN_DEBUG) System.out.println("> Exited screen #" + String.format("%02d", currentScreen+1) + " with a time of " + getTimer(screenTIMER));
    screenTIMER = 0;
    screens[currentScreen].enemies = enemies;
    currentScreen = screen;
    enemies = screens[currentScreen].enemies;
    bullets.clear();
    return true;
  }

  private void fixPlayer() {
    final Walls w = screens[currentScreen].walls;
    if (!w.isPlayerColliding()) return;
    // sorry for janky code a little bit
    if (!w.isColliding((int)player.x, (int)player.y-32)) { /* UP! */
      while (w.isPlayerColliding()) {
        player.y--;
        updatePlayerBounds();
      }
    } else if (!w.isColliding((int)player.x, (int)player.y+32)) {
      while (w.isPlayerColliding()) {
        player.y++;
        updatePlayerBounds();
      }
    } else if (!w.isColliding((int)player.x-16, (int)player.y)) {
      while (w.isPlayerColliding()) {
        player.x--;
        updatePlayerBounds();
      }
    } else if (!w.isColliding((int)player.x+16, (int)player.y)) {
      while (w.isPlayerColliding()) {
        player.x++;
        updatePlayerBounds();
      }
    }
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
      g.setColor(new Color(0, 0, 0, 64));
      g.fillOval(b.x-2, b.y-2, 12, 12);
      g.setColor(b.color);
      g.fillOval(b.x-4, b.y-4, 8, 8);
    }

    drawHealthBar(g);

    g.setColor(Color.WHITE);
    g.setFont(getFont().deriveFont(32f));
    g.drawString(getTimer(TIMER), 10, 752);

    // DEBUG! View bounding boxes of walls:
    // for (Rect r : screens[currentScreen].walls.walls) {
    //   g.setColor(r.isWall ? Color.YELLOW : Color.RED);
    //   g.drawRect(r.x, r.y, r.width, r.height);
    // }

    if (player.health <= 0) {
      g.setColor(new Color(0, 0, 0, 128));
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(Color.WHITE);
      // Font big = getFont().deriveFont(Font.BOLD, 128f);
      try {
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Mokoto Glitch Mark.ttf")));
      } catch (IOException|FontFormatException e) {
        // e.printStackTrace();

        // second attempt
        try {
          GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("com/aadivohragame/Mokoto Glitch Mark.ttf")));
        } catch (IOException|FontFormatException e2) {
          System.out.println("\nError: Font \"Mokoto Glitch Mark.ttf\" could not be loaded!\n");
        }
      }
      if (SPEEDRUN_DEBUG) System.out.println("\nDied with a time of " + getTimer(TIMER) + "\n");
      Font big = new Font("mokoto glitch mark I", Font.BOLD, 128);
      g.setFont(big);
      final String loseText = "You died!";
      final int textWidth = g.getFontMetrics().stringWidth(loseText);
      g.drawString(loseText, getWidth()/2 - textWidth/2, getHeight()/2);
    }
  }

  private String getTimer(double t) {
    final int minutes = (int)t / 60;
    final int seconds = (int)(t % 60);
    final int ms = (int)((t % 1) * 100);
    return String.format("%02d:%02d:%02d", minutes, seconds, ms);
  }

  private void drawHealthBar(Graphics g) {
    g.setColor(Color.BLACK);
    g.fillRect(708, 720, 300, 32);
    g.setColor(Color.RED);
    final int width = (int)(292 * (player.health / player.MAX_HEALTH));
    g.fillRect(712, 724, width, 24);
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