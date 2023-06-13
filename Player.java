import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

public class Player implements Sprite {
  public Image image;
  public Weapon weapon;
  public double x, y, velocityX, velocityY, health, MAX_HEALTH;
  final Rectangle bound = new Rectangle(96, 96, 32, 64);
  final double ACCELERATION = 8000, MAXSPEED = 500, FRICTION = 0.00001;

  public Player(Image image, double x, double y, double MAX_HEALTH) {
    this.image = image; this.x = x; this.y = y;
    velocityX = 0; velocityY = 0;
    health = MAX_HEALTH; this.MAX_HEALTH = MAX_HEALTH;
    weapon = Weapon.newPistol(); weapon.bulletColor = Color.ORANGE;
  }

  public void draw(Point mouse, Graphics g, ImageObserver io) {
    g.drawImage(image, (int)x-32, (int)y-16, 64, 64, io);
    if (weapon != null) weapon.draw(this, mouse, (Graphics2D)g.create(), io);
  }

  public void damage(double amount) {
    health -= amount;
    if (health < 0) health = 0;
  }
  
  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public Point getPoint() {
    return new Point((int)x, (int)y);
  }
}
