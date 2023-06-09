import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;

public class Weapon {
  public static Weapon newPistol() { return new Weapon("Pistol", 10, "images/pistol.png"); }

  // -------

  public String name;
  public double damage;
  public Image image;

  public Weapon(String name, double damage, String pathname) {
    this.name = name; this.damage = damage;
    image = Panel.getImage(pathname);
  }

  public Weapon(String name, double damage, Image image) {
    this.name = name; this.damage = damage;
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