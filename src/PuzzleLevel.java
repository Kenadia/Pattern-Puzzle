/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

/**
 *
 * @author kenschiller
 */
public class PuzzleLevel {
    private int width_;
    private int height_;
    private int pointsToNext_;
    private PuzzlePattern [] patterns_;
    private boolean infinite_;
    private PuzzleLevel(int width, int height, int pointsToNext, PuzzlePattern [] patterns) {
        width_ = width;
        height_ = height;
        pointsToNext_ = pointsToNext;
        patterns_ = patterns;
        infinite_ = false;
    }
    public static PuzzleLevel getLevel(int stageNumber, int levelNumber) {
        switch(stageNumber) {
            case 0:
                switch(levelNumber) {
                    case 0: //small grid – rows of three: 0-1
                        PuzzlePattern [] patterns = new PuzzlePattern[2];
                        patterns[0] = PuzzlePattern.getPattern(0);
                        patterns[1] = PuzzlePattern.getPattern(1);
                        return new PuzzleLevel(3, 3, 500, patterns);
                    case 1: //rows of five: 2-3
                        patterns = new PuzzlePattern[2];
                        patterns[0] = PuzzlePattern.getPattern(2);
                        patterns[1] = PuzzlePattern.getPattern(3);
                        return new PuzzleLevel(5, 5, 1000, patterns);
                    case 2: //s and z tetraminos: 5-8
                        patterns = new PuzzlePattern[4];
                        for(int i = 0; i < 4; i++) {
                            patterns[i] = PuzzlePattern.getPattern(i + 5);
                        }
                        return new PuzzleLevel(6, 6, 2000, patterns);
                    case 3: //t tetraminos: 17-20
                        patterns = new PuzzlePattern[4];
                        for(int i = 0; i < 4; i++) {
                            patterns[i] = PuzzlePattern.getPattern(i + 17);
                        }
                        return new PuzzleLevel(6, 6, 4000, patterns);
                    case 4: //l tetraminos: 9-16
                        patterns = new PuzzlePattern[8];
                        for(int i = 0; i < 8; i++) {
                            patterns[i] = PuzzlePattern.getPattern(i + 9);
                        }
                        return new PuzzleLevel(7, 7, 6000, patterns);
                    case 5: //tetris pieces: 5-23
                        patterns = new PuzzlePattern[19];
                        patterns[0] = PuzzlePattern.getPattern(22);
                        patterns[1] = PuzzlePattern.getPattern(23);
                        for(int i = 0; i < 17; i++) {
                            patterns[i + 2] = PuzzlePattern.getPattern(i + 5);
                        }
                        return new PuzzleLevel(7, 7, 10000, patterns);
                    default:
                        return null;
                }
            case 1:
                switch(levelNumber) {
                    case 0: //plus shapes: 4
                        PuzzlePattern [] patterns = new PuzzlePattern[1];
                        patterns[0] = PuzzlePattern.getPattern(4);
                        return new PuzzleLevel(6, 6, 12000, patterns);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
    public static double [] getSpecial(int stageNumber) {
        double [] special = {0.0};
        switch(stageNumber) {
            case 0:
                special[0] = 0.05;
                break;
            case 1:
                special[0] = 0.5;
                break;
            default:
                break;
        }
        return special;
    }
    public int getWidth() {
        return width_;
    }
    public int getHeight() {
        return height_;
    }
    public int getPointsToNext() {
        return pointsToNext_;
    }
    public PuzzlePattern [] getPatterns() {
        return patterns_;
    }
    public boolean isInfinite() {
        return infinite_;
    }
    public void makeInfinite() {
        infinite_ = true;
    }
}
