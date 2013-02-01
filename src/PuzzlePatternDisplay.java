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
public class PuzzlePatternDisplay {
    private PuzzlePattern [] patterns_;
    private int currentPattern_;
    private int frame_;
    private float alpha_;
    private int spacing_;
    private int numberOfFrames_;
    public PuzzlePatternDisplay(PuzzlePattern [] patterns) {
        patterns_ = patterns;
        currentPattern_ = 0;
        frame_ = 0;//-numberOfFrames_ / 2;
        alpha_ = 1.0f;
        spacing_ = 24;
        if(patterns.length < 5) {
            numberOfFrames_ = 50;
        }
        else {
            numberOfFrames_ = 200 / patterns.length;
        }
    }
    public boolean finished() {
        return currentPattern_ >= patterns_.length;
    }
    public void show(Graphics g, int centerX, int centerY) {
        g.setColor(PuzzlePanel.relativelyTransparent(Color.white, alpha_));
        if(!finished()) {
            if(frame_ >= 0) {
                PuzzlePattern pattern = patterns_[currentPattern_];
                int width = pattern.getWidth();
                int height = pattern.getHeight();
                int x = centerX - (width * spacing_) / 2;
                int y = centerY - (height * spacing_) / 2;
                for(int i = 0; i < width; i++)
                    for(int j = 0; j < height; j++)
                        if(pattern.get(i, j)) {
                            g.drawRect(x + i * spacing_ + 1, y + j * spacing_ + 1, spacing_ - 3, spacing_ - 3);
                            g.drawRect(x + i * spacing_ + 2, y + j * spacing_ + 2, spacing_ - 5, spacing_ - 5);
                        }
                frame_++;
                alpha_ -= 1.0f / numberOfFrames_;
                if(alpha_ < 0.0f) {
                    alpha_ = 0.0f;
                }
                if(frame_ == numberOfFrames_) {
                    frame_ = 0;
                    alpha_ = 1.0f;
                    currentPattern_++;
                }
            }
            else {
                frame_++;
            }
        }
    }
}
