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
public class PuzzlePiece {
    private int colorInt_;
    private Color color_;
    private boolean state_;
    private boolean removed_;
    private float alpha_;
    private float glow_;
    private boolean inTransit_;
    //private boolean beingMoved_;
    private Physics physics_;
    private int size_;
    private Coordinates destination_;
    private int overlapCounter_;
    public static final int MAX_COLORS = 6;
    public PuzzlePiece(int colorInt, boolean state, Coordinates c, int size) {
        colorInt_ = colorInt;
        color_ = getColor(colorInt);
        state_ = state;
        removed_ = false;
        alpha_ = 1.0f;
        glow_ = 0.0f;
        inTransit_ = false;
        //beingMoved_ = false;
        physics_ = new Physics(c.x, c.y, 0, 0, 0, 0);
        size_ = size;
        destination_ = null;
        overlapCounter_ = 0;
    }
    public static PuzzlePiece randomPiece(Coordinates c, int size, int colors, double [] special) {
        if(colors > MAX_COLORS) {
            colors = MAX_COLORS;
        }
        boolean state = Math.random() * 2 < 1;
        int colorRandom = (int) (Math.random() * colors);
        if(Math.random() < special[0]) {
            return new PuzzlePieceO(colorRandom, state, c, size);
        }
        else {
            return new PuzzlePiece(colorRandom, state, c, size);
        }
    }
    private static Color getColor(int colorInt) {
        switch(colorInt) {
            case 0:
                return Color.red;
            case 1:
                return Color.green;
            case 2:
                return Color.blue;
            case 3:
                return Color.yellow;
            case 4:
                return Color.cyan;
            case 5:
                return Color.magenta;
            default:
                return null;
        }
    }
    public int getColorInt() {
        return colorInt_;
    }
    public boolean getState() {
        return state_;
    }
    public float getAlpha() {
        return alpha_;
    }
    public boolean inTransit() {
        return inTransit_;
    }
    /*public boolean beingMoved() {
        return beingMoved_;
    }*/
    public int getX() {
        return physics_.getX();
    }
    public int getY() {
        return physics_.getY();
    }
    public Coordinates getCoordinates() {
        return new Coordinates(physics_.getX(), physics_.getY());
    }
    public Coordinates getDestination() {
        return destination_;
    }
    public int getOverlapCounter() {
        return overlapCounter_;
    }
    public int getBonus() {
        return 0;
    }
    public void toggle() {
        state_ = !state_;
    }
    public void setRemoved() {
        removed_ = true;
        color_ = Color.gray;
    }
    /*public void resetHighlight() {
        highlight_ = false;
    }*/
    public void setAlpha(float alpha) {
        alpha_ = alpha;
    }
    public void addGlow(float percent) {
        float change = percent * (1.0f - glow_);
        glow_ += change;
    }
    public void setMovement(double xv, double yv, double xa, double ya) {
        physics_ = new Physics(physics_.getX(), physics_.getY(), xv, yv, xa, ya);
        //inTransit_ = true;
        //beingMoved_ = true;
    }
    public void setAcceleration(double xa, double ya) {
        physics_ = new Physics(physics_.getX(), physics_.getY(), physics_.getXV(), physics_.getYV(), xa, ya);
        //inTransit_ = true;
    }
    public void setPhysics(int x, int y, double xv, double yv, double xa, double ya) {
        physics_ = new Physics(x, y, xv, yv, xa, ya);
        /*if(!highlight_) {
            inTransit_ = true;
        }*/
    }
    public void setDestination(Coordinates c) {
        destination_ = c;
        inTransit_ = true;
    }
    public void incrementOverlapCounter() {
        overlapCounter_++;
    }
    public void resetOverlapCounter() {
        overlapCounter_ = 0;
    }
    public static boolean betweenBounds(int a, int bound1, int bound2) {
        if(a == bound1) {
            return true;
        }
        else if(a > bound1) {
            return a <= bound2;
        }
        else {
            return a >= bound2;
        }
    }
    public void go() {
        if(removed_) {
            physics_.calculate();
            alpha_ -= 0.010f;
            if(alpha_ < 0) alpha_ = 0;
        }
        else if(inTransit_) {
            int originalX = physics_.getX();
            int originalY = physics_.getY();
            physics_.calculate();
            int newX = physics_.getX();
            int newY = physics_.getY();
            if(destination_ != null && betweenBounds(destination_.x, originalX, newX) && betweenBounds(destination_.y, originalY, newY)) {
                physics_ = new Physics(destination_.x, destination_.y, 0, 0, 0, 0);
                inTransit_ = false;
                //beingMoved_ = false;
            }
        }
        if(glow_ > 0.0f) {
            glow_ -= 0.005;
        }
        if(glow_ < 0.0f) {
            glow_ = 0.0f;
        }
    }
    public void border(Graphics g, Color color, int size, int x, int y, int w, int h) {
        g.setColor(color);
        for(int i = 0; i < size; i++) {
            g.drawRect(x + i, y + i, w - 1 - i * 2, h - 1 - i * 2);
        }
    }
    public void show(Graphics g) {
        int x = physics_.getX();
        int y = physics_.getY();
        int size = size_;
        if(!state_) {
            x += (int) (size * 1.0 / 6.0);
            y += (int) (size * 1.0 / 6.0);
            size -= (int) (size * 1.0 / 3.0);
        }
        //draw border
        Color borderColor;
        if(removed_) {
            borderColor = PuzzlePanel.relativelyTransparent(Color.white, alpha_);
        }
        else {
            borderColor = PuzzlePanel.lighten(color_, 0.2);
        }
        g.setColor(borderColor);
        g.fillRect(x, y, size, size);
        //draw main square
        Color mainColor = color_;
        if(alpha_ < 1.0f) {
            mainColor = PuzzlePanel.relativelyTransparent(mainColor, alpha_);
        }
        g.setColor(mainColor);
        int borderSize = (int) (size * 1.0 / 12);
        g.fillRect(x + borderSize, y + borderSize, size - borderSize * 2, size - borderSize * 2);
        //draw glow overlay
        if(glow_ > 0.0f) {
            Color glowColor = PuzzlePanel.relativelyTransparent(Color.white, glow_);
            g.setColor(glowColor);
            g.fillRect(x, y, size, size);
        }
    }
    //special pieces
    public boolean requiresOverlap() {
        return false;
    }
    public boolean requiresChain() {
        return false;
    }
}
