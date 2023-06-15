import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

public class Enemy implements Sprite {
  public Image image;
  public Weapon weapon;
  public double x, y, health, MAX_HEALTH, speed = 200, firePercentage;
  public int width, height, points;
  public Rectangle bounds;
  public Point target;

  public Image hitImage;
  public boolean hit = false;

  public Enemy(String pathname, String hitPathname, double x, double y, int width, int height, double MAX_HEALTH, double firePercentage) {
    this.image = Panel.getImage(pathname); this.x = x; this.y = y; this.width = width; this.height = height;
    this.MAX_HEALTH = MAX_HEALTH; health = MAX_HEALTH; this.firePercentage = firePercentage;
    bounds = new Rectangle((int)x - width/2, (int)y - height/2, width, height);
    target = new Point((int)x, (int)y);
    weapon = Weapon.newPistol(); weapon.speed /= 2;
    points = (int)(MAX_HEALTH/10 + 10 + Panel.random.nextInt(5));
    hit = false;
    hitImage = Panel.getImage(hitPathname);
  }

  public void damage(double amount) {
    health -= amount;
    if (health < 0) health = 0;
  }

  public boolean atTarget() {
    if (target == null) return true;
    return closeEnough(x, target.getX()) && closeEnough(y, target.getY());
  }

  private boolean closeEnough(double a, double b) {
    return Math.abs(b - a) < 0.0001;
  }

  public void moveToTarget(Panel.Walls walls, double dt) {
    if (target == null) return;

    stepEnemyX(walls, clamp(target.x - x, 1) * speed * dt);
    stepEnemyY(walls, clamp(target.y - y, 1) * speed * dt);

    updateBounds();
  }

  private void stepEnemyX(Panel.Walls walls, double dx) {
    double dir = dx < 0 ? -1 : 1;
    for (int i = 0; i <= Math.abs(dx)-1; i++) {
      x += dir;
      updateBounds();
      if (walls.isColliding(bounds)) {
        x -= dir;
        return;
      }
    }
    // sorry :(
    dir = dx % 1;
    x += dir;
    updateBounds();
    if (walls.isColliding(bounds)) {
      x -= dir;
      updateBounds();
      return;
    }
  }

  private void stepEnemyY(Panel.Walls walls, double dy) {
    double dir = dy < 0 ? -1 : 1;
    for (int i = 0; i <= Math.abs(dy)-1; i++) {
      y += dir;
      updateBounds();
      if (walls.isColliding(bounds)) {
        y -= dir;
        return;
      }
    }
    // sorry :(
    dir = dy % 1;
    y += dir;
    updateBounds();
    if (walls.isColliding(bounds)) {
      y -= dir;
      updateBounds();
      return;
    }
  }

  private double clamp(double value, double max) {
    return value > max ? max : (value < -max ? -max : value);
  }

  public void updateBounds() {
    bounds.x = (int)x - width/2;
    bounds.y = (int)y - height/2;
  }
  
  public void draw(Point pointTo, Graphics g, ImageObserver io) {
    g.drawImage(hit ? hitImage : image, (int)(x-width/2), (int)(y-width/2), width, height, io);
    if (hit) hit = false;
    if (weapon != null) weapon.draw(this, pointTo, (Graphics2D)g.create(), io);
  }

  public void drawHealthBar(Graphics g) {
    final int barWidth = getHealthBarSize();
    
    g.setColor(Color.BLACK);
    g.fillRect((int)x - barWidth/2 - 2, (int)y - height/2 - 22, barWidth + 4, 12);
    g.setColor(Color.GREEN);
    g.fillRect((int)x - barWidth/2, (int)y - height/2 - 20, barWidth, 8);
  }

  public int getHealthBarSize() {
    if (MAX_HEALTH > 300) return 300;
    if (MAX_HEALTH < 100) return 100;
    return (int)MAX_HEALTH;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
}
