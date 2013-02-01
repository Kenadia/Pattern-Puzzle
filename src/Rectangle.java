/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

import java.awt.*;

/**
 *
 * @author kenschiller
 */
public class Rectangle {
    private int x_;
    private int y_;
    private int w_;
    private int h_;
    private Color color_;
    private boolean active_;
    public Rectangle(Color color) {
        color_ = PuzzlePanel.relativelyTransparent(color, 0.15f);
        active_ = false;
    }
    public boolean active() {
        return active_;
    }
    public void setX(int x) {
        x_ = x;
    }
    public void setY(int y) {
        y_ = y;
    }
    public void setW(int w) {
        w_ = w;
    }
    public void setH(int h) {
        h_ = h;
    }
    public void activate() {
        active_ = true;
    }
    public void deactivate() {
        active_ = false;
    }
    public void show(Graphics g) {
        if(active_) {
            g.setColor(color_);
            g.fillRect(x_, y_, w_, h_);
        }
    }
}
