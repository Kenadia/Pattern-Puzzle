/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

/**
 *
 * @author kenschiller
 */
public class PuzzlePieceC extends PuzzlePiece {
    public PuzzlePieceC(int colorInt, boolean state, Coordinates c, int size) {
        super(colorInt, state, c, size);
    }
    public boolean requiresChain() {
        return true;
    }
}
