/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

import java.util.*;
import java.awt.*;

/**
 *
 * @author kenschiller
 */
public class PuzzlePieceO extends PuzzlePiece {
    private ArrayList<PuzzleText> pendingScores_;
    public PuzzlePieceO(int colorInt, boolean state, Coordinates c, int size) {
        super(colorInt, state, c, size);
        pendingScores_ = new ArrayList<PuzzleText>();
    }
    public boolean requiresOverlap() {
        return true;
    }
    public int getBonus() {
        return 2;
    }
    public void addPendingScore(PuzzleText score) {
        pendingScores_.add(score);
    }
    public ArrayList<PuzzleText> getPendingScores() {
        return pendingScores_;
    }
    public void clearPendingScores() {
        pendingScores_.clear();
    }
    public void show(Graphics g) {
        super.show(g);
        g.drawString("O", getX(), getY());
    }
}
