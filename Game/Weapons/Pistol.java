package Game.Weapons;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import Game.Texture;

public class Pistol implements Weapon {
  public Texture texture;

  public Pistol() {
    texture = new Texture("Images/pistol.png", 32, 32);
  }

  public void draw(Graphics2D g, ImageObserver io, int x, int y, double rotation) {
    g.translate(x, y);
    g.rotate(rotation);
    if (rotation > Math.PI / 2 || rotation < -Math.PI / 2) {
      g.scale(1, -1);
    }
    texture.draw(g, io, 0, 0);
    g.dispose();
  }
}
