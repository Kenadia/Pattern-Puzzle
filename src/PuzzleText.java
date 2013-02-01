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
public class PuzzleText {
    private String string_;
    private double x_;
    private double y_;
    private int size_;
    private Color color_;
    private double alpha_;
    private double speed_;
    private double decay_;
    public PuzzleText(String string, int x, int y, int size, Color color, double speed) {
        string_ = string;
        x_ = x;
        y_ = y;
        size_ = size;
        color_ = color;
        alpha_ = 1.0;
        speed_ = speed;
        decay_ = 1;
    }
    public String getString() {
        return string_;
    }
    public float getAlpha() {
        return (float) alpha_;
    }
    public void setString(String string) {
        string_ = string;
    }
    public void setDecay(double decay) {
        decay_ = decay;
    }
    public void go() {
        if(decay_ > 0) {
            alpha_ -= 0.005 * Math.pow(1.01, alpha_) * speed_ * decay_;
            if(alpha_ < 0.0) {
                alpha_ = 0.0;
            }
            if(decay_ >= 1) {
                y_ -= 1.5 * alpha_ * speed_;
            }
        }
    }
    public void show(Graphics g) {
        g.setColor(PuzzlePanel.relativelyTransparent(color_, (float) alpha_));
        g.setFont(new Font("Garamond", Font.PLAIN, size_));
        g.drawString(string_, (int) x_, (int) y_);
    }
}
