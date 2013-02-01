/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

/**
 *
 * @author kenschiller
 */
public class PuzzleMatch {
    public int column;
    public int row;
    public PuzzlePattern pattern;
    public PuzzleMatch(int _column, int _row, PuzzlePattern _pattern) {
        column = _column;
        row = _row;
        pattern = _pattern;
    }
}
