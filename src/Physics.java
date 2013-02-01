/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

/**
 *
 * @author kenschiller
 */
public class Physics {
    private double x_;
    private double y_;
    private double xv_;
    private double yv_;
    private double xa_;
    private double ya_;
    public Physics(double x, double y, double xv, double yv, double xa, double ya) {
        x_ = x;
        y_ = y;
        xv_ = xv;
        yv_ = yv;
        xa_ = xa;
        ya_ = ya;
    }
    public int getX() {
        return (int) x_;
    }
    public int getY() {
        return (int) y_;
    }
    public double getXV() {
        return xv_;
    }
    public double getYV() {
        return yv_;
    }
    public double getXA() {
        return xa_;
    }
    public double getYA() {
        return ya_;
    }
    public void calculate() {
        xv_ += xa_;
        yv_ += ya_;
        x_ += xv_;
        y_ += yv_;
    }
}
