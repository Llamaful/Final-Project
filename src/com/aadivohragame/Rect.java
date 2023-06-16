package com.aadivohragame;
import java.awt.Rectangle;

/**
 * Defines a sublass of {@code Rectangle}, but with
 * an added boolean instance variable.
 * 
 * This variable {@code isWall} defines whether or not
 * bullets should colliding, differentiating between
 * walls or holes.
 */
public class Rect extends Rectangle {
  public boolean isWall;
  public Rect(int x, int y, int width, int height) {
    super(x, y, width, height);
    isWall = true;
  }
  public Rect(int x, int y, int width, int height, boolean isWall) {
    super(x, y, width, height);
    this.isWall = isWall;
  }
}
