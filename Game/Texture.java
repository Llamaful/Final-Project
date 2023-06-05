package Game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Texture {
  public Image texture;
  public Integer width, height;

  public Texture(String pathname) {
    try {
      texture = ImageIO.read(new File(pathname));
    } catch (IOException e) {
      try {
        texture = ImageIO.read(getClass().getResource(pathname));
      } catch (IOException e1) {
        
      }
    }
  }

  public Texture(String pathname, int width, int height) {
    try {
      this.width = width;
      this.height = height;
      texture = ImageIO.read(new File(pathname));
    } catch (IOException e) {
      try {
        texture = ImageIO.read(getClass().getResource(pathname));
      } catch (IOException e1) {
        
      }
    }
  }

  public void draw(Graphics g, ImageObserver io, int x, int y) {
    if (texture == null) return;
    g.drawImage(texture, x, y, width == null ? texture.getWidth(io) : width, height == null ? texture.getHeight(io) : height, io);
  }
}