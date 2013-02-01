/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

/**
 *
 * @author kenschiller
 */
public class PuzzleMove {
    public boolean vertical;
    public int line;
    public int action;
    public PuzzleMove(boolean _vertical, int _line, int _action) {
        vertical = _vertical;
        line = _line;
        action = _action;
    }
}
