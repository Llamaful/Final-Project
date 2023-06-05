import java.awt.Color;
import java.awt.Graphics;

public class Player {
  public final int MAX_HEALTH = 100, ACCELERATION = 300, SPEED = 1000;
  public final int width = 30, height = 30;
  public double x, y, dx, dy, health;

  public Player() {
    x = 0; y = 0; dx = 0; dy = 0;
    health = MAX_HEALTH;
  }

  public void move(double dt) {
    x += dx * dt;
    y += dy * dt;
    applyFriction(0.001, dt);
  }

  public void applyFriction(double friction, double dt) {
    dx *= Math.pow(friction, dt);
    dy *= Math.pow(friction, dt);
  }

  public void draw(Graphics g) {
    g.setColor(Color.RED);
    g.fillOval((int)x, (int)y, 50, 50);
  }
}