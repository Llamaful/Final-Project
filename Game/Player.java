package Game;
import java.awt.Color;
import java.awt.Graphics;

public class Player {
  public final int MAX_HEALTH = 100, ACCELERATION = 4000, SPEED = 10000;
  public final int size = 50;
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
    g.fillOval((int)x-size/2, (int)y-size/2, size, size);
  }
}