/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

/**
 *
 * @author kenschiller
 */
public class PuzzlePattern {
    private int width_;
    private int height_;
    private boolean [] [] map_;
    private PuzzlePattern(int width, int height, boolean [] [] map) {
        width_ = width;
        height_ = height;
        map_ = map;
    }
    public static PuzzlePattern getPattern(int patternNumber) {
        PuzzlePattern pattern = null;
        switch(patternNumber) {
            case 0: //horizontal row of 3
                int width = 3;
                int height = 1;
                boolean [] [] map = map(width, height, true);
                return new PuzzlePattern(width, height, map);
            case 1: //vertical row of 3
                width = 1;
                height = 3;
                map = map(width, height, true);
                return new PuzzlePattern(width, height, map);
            case 2: //horizontal row of 5
                width = 5;
                height = 1;
                map = map(width, height, true);
                return new PuzzlePattern(width, height, map);
            case 3: //vertical row of 5
                width = 1;
                height = 5;
                map = map(width, height, true);
                return new PuzzlePattern(width, height, map);
            case 4: //pentamino cross
                width = 3;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 0, 0);
                setFalse(map, 0, 2);
                setFalse(map, 2, 0);
                setFalse(map, 2, 2);
                return new PuzzlePattern(width, height, map);
            case 5: //s tetramino horizontal
                width = 3;
                height = 2;
                map = map(width, height, true);
                setFalse(map, 0, 0);
                setFalse(map, 2, 1);
                return new PuzzlePattern(width, height, map);
            case 6: //s tetramino vertical
                width = 2;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 1, 0);
                setFalse(map, 0, 2);
                return new PuzzlePattern(width, height, map);
            case 7: //z tetramino horizontal
                width = 3;
                height = 2;
                map = map(width, height, true);
                setFalse(map, 0, 1);
                setFalse(map, 2, 0);
                return new PuzzlePattern(width, height, map);
            case 8: //z tetramino vertical
                width = 2;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 0, 0);
                setFalse(map, 1, 2);
                return new PuzzlePattern(width, height, map);
            case 9: //l tetramino type 1 horizontal facing up
                width = 3;
                height = 2;
                map = map(width, height, true);
                setFalse(map, 1, 0);
                setFalse(map, 2, 0);
                return new PuzzlePattern(width, height, map);
            case 10: //l tetramino type 1 horizontal facing down
                width = 3;
                height = 2;
                map = map(width, height, true);
                setFalse(map, 0, 1);
                setFalse(map, 1, 1);
                return new PuzzlePattern(width, height, map);
            case 11: //l tetramino type 1 vertical facing left
                width = 2;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 0, 0);
                setFalse(map, 0, 1);
                return new PuzzlePattern(width, height, map);
            case 12: //l tetramino type 1 vertical facing right
                width = 2;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 1, 1);
                setFalse(map, 1, 2);
                return new PuzzlePattern(width, height, map);
            case 13: //l tetramino type 2 horizontal facing up
                width = 3;
                height = 2;
                map = map(width, height, true);
                setFalse(map, 0, 0);
                setFalse(map, 1, 0);
                return new PuzzlePattern(width, height, map);
            case 14: //l tetramino type 2 horizontal facing down
                width = 3;
                height = 2;
                map = map(width, height, true);
                setFalse(map, 1, 1);
                setFalse(map, 2, 1);
                return new PuzzlePattern(width, height, map);
            case 15: //l tetramino type 2 vertical facing left
                width = 2;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 0, 1);
                setFalse(map, 0, 2);
                return new PuzzlePattern(width, height, map);
            case 16: //l tetramino type 2 vertical facing right
                width = 2;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 1, 0);
                setFalse(map, 1, 1);
                return new PuzzlePattern(width, height, map);
            case 17: //t tetramino facing up
                width = 3;
                height = 2;
                map = map(width, height, true);
                setFalse(map, 0, 0);
                setFalse(map, 2, 0);
                return new PuzzlePattern(width, height, map);
            case 18: //t tetramino facing down
                width = 3;
                height = 2;
                map = map(width, height, true);
                setFalse(map, 0, 1);
                setFalse(map, 2, 1);
                return new PuzzlePattern(width, height, map);
            case 19: //t tetramino facing left
                width = 2;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 0, 0);
                setFalse(map, 0, 2);
                return new PuzzlePattern(width, height, map);
            case 20: //t tetramino facing right
                width = 2;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 1, 0);
                setFalse(map, 1, 2);
                return new PuzzlePattern(width, height, map);
            case 21: //square tetramino
                width = 2;
                height = 2;
                map = map(width, height, true);
                return new PuzzlePattern(width, height, map);
            case 22: //horizontal line tetramino
                width = 4;
                height = 1;
                map = map(width, height, true);
                return new PuzzlePattern(width, height, map);
            case 23: //vertical line tetramino
                width = 1;
                height = 4;
                map = map(width, height, true);
                return new PuzzlePattern(width, height, map);
            case 24: //diagonal of 3 top-left to bottom-right
                width = 3;
                height = 3;
                map = map(width, height, false);
                for(int i = 0; i < 3; i++) {
                    setTrue(map, i, i);
                }
                return new PuzzlePattern(width, height, map);
            case 25: //diagonal of 3 top-right to bottom-left
                width = 3;
                height = 3;
                map = map(width, height, false);
                for(int i = 0; i < 3; i++) {
                    setTrue(map, i, 2 - i);
                }
                return new PuzzlePattern(width, height, map);
            case 26: //small x
                width = 3;
                height = 3;
                map = map(width, height, false);
                for(int i = 0; i < 3; i++) {
                    setTrue(map, i, i);
                    setTrue(map, i, 2 - i);
                }
                return new PuzzlePattern(width, height, map);
            case 27: //3 by 3 donut
                width = 3;
                height = 3;
                map = map(width, height, true);
                setFalse(map, 2, 2);
                return new PuzzlePattern(width, height, map);
        }
        return pattern;
    }
    private static boolean [] [] map(int width, int height) {
        return new boolean[width][height];
    }
    private static void setTrue(boolean [] [] map, int c, int r) {
        map[c][r] = true;
    }
    private static void setFalse(boolean [] [] map, int c, int r) {
        map[c][r] = false;
    }
    private static void trueAll(int width, int height, boolean [] [] map) {
        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++) {
                map[i][j] = true;
            }
    }
    private static void falseAll(int width, int height, boolean [] [] map) {
        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++) {
                map[i][j] = false;
            }
    }
    private static boolean [] [] map(int width, int height, boolean fill) {
        boolean [] [] map = map(width, height);
        if(fill == true) {
            trueAll(width, height, map);
        }
        else {
            falseAll(width, height, map);
        }
        return map;
    }
    public int getWidth() {
        return width_;
    }
    public int getHeight() {
        return height_;
    }
    public boolean get(int column, int row) {
        if(column >= 0 && column < width_ && row >= 0 && row < height_) {
            return map_[column][row];
        }
        return false;
    }
    public int size() {
        int size = 0;
        for(int i = 0; i < width_; i++)
            for(int j = 0; j < height_; j++) {
                if(map_[i][j]) {
                    size++;
                }
            }
        return size;
    }
}
