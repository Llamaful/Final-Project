package Game.Weapons;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public interface Weapon {
  public void draw(Graphics2D g, ImageObserver io, int x, int y, double rotation);
}
