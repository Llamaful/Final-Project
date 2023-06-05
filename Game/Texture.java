package Game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import javax.imageio.ImageIO;

public class Texture {
  public Image texture;
  public Integer width, height;

  public Texture(String pathname) {
    try {
      texture = ImageIO.read(new File(pathname));
    } catch (Exception e) {
      System.out.println("ERROR: Image not found! Attempted: " + pathname);
    }
  }

  public Texture(String pathname, int width, int height) {
    try {
      texture = ImageIO.read(new File(pathname));
      this.width = width;
      this.height = height;
    } catch (Exception e) {
      System.out.println("ERROR: Image not found! Attempted: " + pathname);
    }
  }

  public void draw(Graphics g, ImageObserver io, int x, int y) {
    g.drawImage(texture, x, y, width == null ? texture.getWidth(io) : width, height == null ? texture.getHeight(io) : height, io);
  }
}