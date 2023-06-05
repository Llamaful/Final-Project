package Game.Weapons;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import Game.Texture;

public class Pistol implements Weapon {
  public Texture texture;

  public Pistol() {
    texture = new Texture("../Images/pistol.png");
  }

  public void draw(Graphics2D g, ImageObserver io, int x, int y, double rotation) {
    AffineTransform transform = new AffineTransform();
    transform.rotate(rotation);
    g.setTransform(transform);
    texture.draw(g, io, x, y);
    g.setTransform(null);
  }
}
