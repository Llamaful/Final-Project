import java.awt.Color;
import java.awt.Graphics;

public class Player {
  public static final int MAX_HEALTH = 100, SPEED = 200;
  public final int width = 30, height = 30;
  public double x, y, health;

  public Player() {
    x = 0; y = 0;
    health = MAX_HEALTH;
  }

  public void draw(Graphics g) {
    g.setColor(Color.RED);
    g.fillOval((int)x, (int)y, 50, 50);
  }
}