import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;

public class Weapon {
  public static Weapon newPistol() { return new Weapon("Pistol", Color.RED, 10, 750, "images/pistol.png"); }
  public static Weapon newRifle() { return new Weapon("Rifle", Color.RED.darker(), 25, 1025, "images/rifle.png"); }

  // -------

  public String name;
  public double damage, speed;
  public Color bulletColor;
  public Image image;

  public Weapon(String name, Color bulletColor, double damage, double speed, String pathname) {
    this.name = name; this.bulletColor = bulletColor; this.damage = damage; this.speed = speed;
    image = Panel.getImage(pathname);
  }

  public Weapon(String name, double damage, double speed, Image image) {
    this.name = name; this.damage = damage; this.speed = speed;
    this.image = image;
  }

  public void draw(Sprite sprite, Point mouse, Graphics2D g, ImageObserver io) {
    if (mouse == null) return;
    final double angle = Math.atan2(mouse.getY() - sprite.getY(), mouse.getX() - sprite.getX());
    g.translate((int)(sprite.getX() + Math.cos(angle) * 32), (int)(sprite.getY() + Math.sin(angle) * 32));
    g.rotate(angle);
    if (angle > Math.PI/2 || angle < -Math.PI/2) {
      g.scale(1, -1);
    }
    g.drawImage(image, 0, 0, 32, 32, io);
    g.dispose();
  }
}