/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.color.*;

/**
 *
 * @author kenschiller
 */
public class PuzzlePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener {
    private PuzzleGame game_;
    private java.util.Timer refreshTimer_;
    private RegularProcess refreshProcess_;
    private boolean paused_;
    private SafetyBoolean runCycle_;
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 480;
    public static final int FPS = 50;
    public PuzzlePanel() {
        runCycle_ = new SafetyBoolean(false);
        game_ = null;
        refreshTimer_ = new java.util.Timer();
        refreshProcess_ = new RegularProcess(this);
        refreshTimer_.schedule(refreshProcess_, 0, 1000 / FPS);
        paused_ = false;
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        addKeyListener(this);
    }
    public void run() {
        if(!paused_ && !runCycle_.status && game_ != null) {
            //System.out.println("~~~FRAME~~~");
            runCycle_.status = true;
            game_.go();
            runCycle_.status = false;
            repaint();
        }
    }
    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
        if(!paused_ && game_ != null) {
            game_.activate();
        }
    }
    public void mouseExited(MouseEvent e) {
        if(!paused_ && game_ != null) {
            game_.deactivate();
            repaint();
        }
    }
    public void mousePressed(MouseEvent e) {
        if(game_ != null) {
            game_.mousePress(e.getX(), e.getY());
        }
    }
    public void mouseReleased(MouseEvent e) {
        if(game_ == null) {
            game_ = new PuzzleGame(GAME_WIDTH, GAME_HEIGHT);
        }
        else {
            game_.mouseRelease(e.getX(), e.getY());
        }
    }
    public void mouseDragged(MouseEvent e) {
    }
    public void mouseMoved(MouseEvent e) {
        if(game_ != null) {
            game_.mouseOver(e.getX(), e.getY());
        }
    }
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            case KeyEvent.VK_P:
                paused_ = !paused_;
                repaint();
                break;
            default:
                if(!paused_ && game_ != null) {
                    game_.keyPress(e.getKeyCode());
                }
                break;
        }
    }
    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {
    }
    public static Color relativelyTransparent(Color original, float alpha) {
        ColorSpace srbg = ICC_ColorSpace.getInstance(ColorSpace.CS_sRGB);
        double originalAlpha = 1.0 * original.getAlpha() / 255;
        alpha *= originalAlpha;
        return new Color(srbg, original.getColorComponents(null), alpha);
    }
    public static Color lighten(Color original, double percent) {
        ColorSpace srbg = ICC_ColorSpace.getInstance(ColorSpace.CS_sRGB);
        float originalR = 1.0f * original.getRed() / 255;
        float originalG = 1.0f * original.getGreen() / 255;
        float originalB = 1.0f * original.getBlue() / 255;
        float originalAlpha = 1.0f * original.getAlpha() / 255;
        float newR = (float) (originalR + (255 - originalR) * percent / 255);
        float newG = (float) (originalG + (255 - originalG) * percent / 255);
        float newB = (float) (originalB + (255 - originalB) * percent / 255);
        if(newR < 0) newR = 0;
        if(newR > 1) newR = 1;
        if(newG < 0) newG = 0;
        if(newG > 1) newG = 1;
        if(newB < 0) newB = 0;
        if(newB > 1) newB = 1;
        float [] originalColorComponents = {newR, newG, newB};
        return new Color(srbg, originalColorComponents, originalAlpha);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        if(game_ == null) {
            g.setColor(Color.white);
            g.setFont(new Font("Monaco", Font.PLAIN, 10));
            g.drawString("click to start", 275, 245);
        }
        else {
            while(runCycle_.status) {
                System.out.println("sorry");
            }
            game_.show(g, runCycle_);
            if(paused_) {
                g.setColor(PuzzlePanel.relativelyTransparent(Color.black, 0.8f));
                g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
                g.setColor(PuzzlePanel.relativelyTransparent(Color.white, 0.1f));
                g.fillRect(0, GAME_HEIGHT / 8 * 3 , GAME_WIDTH, GAME_HEIGHT / 4);
                g.setColor(PuzzlePanel.relativelyTransparent(Color.white, 0.75f));
                g.setFont(new Font("Helvetica", Font.PLAIN, GAME_HEIGHT / 5));
                g.drawString("PAUSED", (GAME_WIDTH - 400) / 2, GAME_HEIGHT / 16 * 9 + 2);
            }
        }
    }
}
