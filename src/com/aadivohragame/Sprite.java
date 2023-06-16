package com.aadivohragame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.ImageObserver;

public interface Sprite {
  public void draw(Point pointTo, Graphics g, ImageObserver io);
  public double getX();
  public double getY();
}
