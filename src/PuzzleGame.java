/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package patternpuzzle;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import sun.audio.*;
import java.io.*;

/**
 *
 * @author kenschiller
 */
public class PuzzleGame {
    private int xSize_;
    private int ySize_;
    private int stageNumber_; //the number of the current stage
    private int levelNumber_; //the number of the current level
    private PuzzleLevel currentLevel_; //the current level
    private int width_; //number of columns for current level
    private int height_; //number of rows for current level
    private int spacing_; //spacing between pieces
    private int xGlobal_; //x coordinate of game grid
    private int yGlobal_; //y coordinate of game grid
    private PuzzlePiece [] [] map_; //map of pieces
    private ArrayList<PuzzlePiece> falling_; //the pieces recently removed
    private ArrayList<PuzzleText> texts_; //temporary text for score and combos
    private ArrayList<PuzzlePiece> [] entryQueue_;
    private int [] entryWait_;
    private int score_; //player score
    private int last_; //score from last combo
    private int combo_; //current combo multiplier
    private ArrayList<PuzzleMatch> matches_; //temporary array for matches made
    private ArrayList<Coordinates> piecesToRemove_; //temporary array for pieces removed
    private int overlappingCombo_;
    private Rectangle selection_; //selection rectangle
    private Rectangle selection2_; //second selection rectangle
    private int controlMode_;
    private boolean verticalSelection_; //false: horizontal, true: vertical
    private boolean active_; //mouse in frame
    private int lastMouseX_; //last mouse movement x coordinate
    private int lastMouseY_; //last mouse movement y coordinate
    private int currentLine_; //current selected row or column
    private ArrayList<PuzzleMove> moves_;
    private boolean playMode_;
    private int shadowTop_;
    private int shadowBottom_;
    private float shadowTransparency_;
    private PuzzleText messageText_;
    private PuzzlePatternDisplay patternDisplay_;
    public static final int TEXT_SIZE = 12;
    public static final int CLICK_SOUND = 0;
    public PuzzleGame(int xSize, int ySize) {
        xSize_ = xSize;
        ySize_ = ySize;
        xGlobal_ = (xSize - ySize) / 2;
        yGlobal_ = 0;
        falling_ = new ArrayList<PuzzlePiece>();
        texts_ = new ArrayList<PuzzleText>();
        score_ = 0;
        last_ = 0;
        combo_ = 1;
        matches_ = new ArrayList<PuzzleMatch>();
        piecesToRemove_ = new ArrayList<Coordinates>();
        selection_ = new Rectangle(Color.white);
        selection2_ = new Rectangle(Color.white);
        controlMode_ = 1;
        verticalSelection_ = false;
        active_ = false;
        moves_ = new ArrayList<PuzzleMove>();
        playMode_ = false;
        shadowTop_ = 0;
        shadowBottom_ = 0;
        shadowTransparency_ = 0.0f;
        messageText_ = null;
        patternDisplay_ = null;
        startUp();
    }
    private Coordinates toCoordinates(int column, int row) {
        int x = xGlobal_ + column * spacing_ + 10;
        int y = 0 + row * spacing_ + 10;
        return new Coordinates(x, y);
    }
    private void initializeMap() {
        entryQueue_ = new ArrayList [width_];
        entryWait_ = new int [width_];
        map_ = new PuzzlePiece [width_] [height_];
        for(int i = 0; i < width_; i++) {
            for(int j = 0; j < height_; j++) {
                Coordinates c = toCoordinates(i, j);
                int size = spacing_ - 20;
                map_[i][j] = getRandomPiece(c, size);
            }
            entryQueue_[i] = new ArrayList<PuzzlePiece>();
            entryWait_[i] = 0;
        }
    }
    private void goToLevel(int stageNumber, int levelNumber) {
        PuzzleLevel newLevel = PuzzleLevel.getLevel(stageNumber, levelNumber);
        if(newLevel != null) {
            System.out.println("Loading Level " + stageNumber + ", " + levelNumber);
            //next level text / animation
            playMode_ = false;
            shadowTop_ = 80;
            shadowBottom_ = 400;
            shadowTransparency_ = 1.0f;
            messageText_ = new PuzzleText("LEVEL " + (levelNumber + 1), (xSize_ - 400) / 2, ySize_ / 16 * 6 + 2, ySize_ / 5, Color.white, 1);
            messageText_.setDecay(0);
            patternDisplay_ = new PuzzlePatternDisplay(newLevel.getPatterns());
            //
            loadLevel(stageNumber, levelNumber, newLevel);
            initializeMap();
        }
        else {
            newLevel = PuzzleLevel.getLevel(++stageNumber, 0);
            if(newLevel != null) {
                System.out.println("~Loading Next Stage: " + stageNumber);
                //stage completed text / animation
                playMode_ = false;
                crumble();
                messageText_ = new PuzzleText("STAGE COMPLETE", (xSize_ - 460) / 2, ySize_ / 16 * 6 + 2, ySize_ / 9, Color.white, 1);
                messageText_.setDecay(0.9);
                //
                loadLevel(stageNumber, 0, newLevel);
            }
            else {
                System.out.println("GAME OVER");
                //game over text / animation
                crumble();
                //
                currentLevel_.makeInfinite();
            }
        }
    }
    private void loadLevel(int stageNumber, int levelNumber, PuzzleLevel newLevel) {
        stageNumber_ = stageNumber;
        levelNumber_ = levelNumber;
        currentLevel_ = newLevel;
        width_ = currentLevel_.getWidth();
        height_ = currentLevel_.getHeight();
        spacing_ = ySize_ / height_;
    }
    private PuzzlePiece getRandomPiece(Coordinates c, int size) {
        return PuzzlePiece.randomPiece(c, size, stageNumber_ + 3, PuzzleLevel.getSpecial(stageNumber_));
    }
    private void crumble() {
        for(int i = 0; i < width_; i++)
            for(int j = 0; j < height_; j++) {
                map_[i][j].setRemoved();
                map_[i][j].setMovement(1.5 * (Math.random() - 0.5), -1, 0, 0.15);
            }
        deactivateSelections();
        moves_.clear();
    }
    private void toggleControl() {
        verticalSelection_ = !verticalSelection_;
        mouseOver(lastMouseX_, lastMouseY_);
    }
    public void activate() {
        active_ = true;
    }
    public void deactivate() {
        active_ = false;
        deactivateSelections();
    }
    private boolean overPiece(int x, int y) {
        boolean inX = x >= xGlobal_ && x < xGlobal_ + width_ * spacing_;
        boolean inY = y >= yGlobal_ && y < yGlobal_ + height_ * spacing_;
        return inX && inY;
    }
    private int xToColumn(int x) {
        int relativeX = x - xGlobal_;
        int columnX = relativeX / spacing_;
        return columnX;
    }
    private int yToRow(int y) {
        int relativeY = y - yGlobal_;
        int rowY = relativeY / spacing_;
        return rowY;
    }
    private int columnToX(int columnX) {
        int relativeX = columnX * spacing_;
        int x = relativeX + xGlobal_;
        return x;
    }
    private int rowToY(int rowY) {
        int relativeY = rowY * spacing_;
        int y = relativeY + yGlobal_;
        return y;
    }
    private void findCurrentLine(boolean verticalSelection, int x, int y) {
        if(verticalSelection) {
            currentLine_ = xToColumn(x);
        }
        else {
            currentLine_ = yToRow(y);
        }
    }
    private void setGhost(int x, int y) {
        if(controlMode_ > 0) {
            int currentColumn = xToColumn(x);
            selection_.setX(columnToX(currentColumn));
            selection_.setY(yGlobal_);
            selection_.setW(spacing_);
            selection_.setH(height_ * spacing_);
            int currentRow = yToRow(y);
            selection2_.setX(xGlobal_);
            selection2_.setY(rowToY(currentRow));
            selection2_.setW(width_ * spacing_);
            selection2_.setH(spacing_);
            findCurrentLine(verticalSelection_, x, y);
        }
        else {
            if(verticalSelection_) {
                currentLine_ = xToColumn(x);
                selection_.setX(columnToX(currentLine_));
                selection_.setY(yGlobal_);
                selection_.setW(spacing_);
                selection_.setH(height_ * spacing_);
            }
            else {
                currentLine_ = yToRow(y);
                selection_.setX(xGlobal_);
                selection_.setY(rowToY(currentLine_));
                selection_.setW(width_ * spacing_);
                selection_.setH(spacing_);
            }
        }
    }
    private void activateSelections() {
        selection_.activate();
        if(controlMode_ > 0) {
            selection2_.activate();
        }
    }
    private void deactivateSelections() {
        selection_.deactivate();
        if(controlMode_ > 0) {
            selection2_.deactivate();
        }
    }
    public void mouseOver(int x, int y) {
        if(overPiece(x, y)) {
            setGhost(x, y);
            if(playMode_) {
                activateSelections();
            }
        }
        else {
            deactivateSelections();
        }
        lastMouseX_ = x;
        lastMouseY_ = y;
    }
    public void mousePress(int x, int y) {

    }
    public void mouseRelease(int x, int y) {

    }
    private boolean anyInTransit() {
        for(int i = 0; i < width_; i++)
            for(int j = 0; j < height_; j++) {
                if(map_[i][j].inTransit()) {
                    return true;
                }
            }
        return false;
    }
    /*public boolean anyBeingMoved() {
        boolean anyBeingMoved = false;
        for(int i = 0; i < width_; i++)
            for(int j = 0; j < height_; j++) {
                if(map_[i][j].beingMoved()) {
                    anyBeingMoved = true;
                }
            }
        return anyBeingMoved;
    }*/
    private void resetLast() {
        last_ = 0;
    }
    private void resetCombo() {
        combo_ = 1;
    }
    public void keyPress(int keyCode) {
        if(playMode_) {
            /*boolean newCombo = false;
            if(!anyInTransit()) {
                newCombo = true;
            }
            boolean moveMade = true;*/
            if(controlMode_ == 0) {
                switch(keyCode) {
                    case KeyEvent.VK_SPACE:
                        toggleControl();
                        break;
                    case KeyEvent.VK_S:
                        if(selection_.active()) {
                            moves_.add(new PuzzleMove(verticalSelection_, currentLine_, 0));
                        }
                        break;
                    case KeyEvent.VK_A:
                        if(selection_.active()) {
                            moves_.add(new PuzzleMove(verticalSelection_, currentLine_, 1));
                        }
                        break;
                    case KeyEvent.VK_D:
                        if(selection_.active()) {
                            moves_.add(new PuzzleMove(verticalSelection_, currentLine_, 2));
                        }
                    break;
                }
            }
            else if(controlMode_ == 1) {
                switch(keyCode) {
                    case KeyEvent.VK_Q:
                        if(selection_.active()) {
                            findCurrentLine(false, lastMouseX_, lastMouseY_);
                            moves_.add(new PuzzleMove(false, currentLine_, 0));
                        }
                        break;
                    case KeyEvent.VK_E:
                        if(selection_.active()) {
                            findCurrentLine(true, lastMouseX_, lastMouseY_);
                            moves_.add(new PuzzleMove(true, currentLine_, 0));
                        }
                        break;
                    case KeyEvent.VK_W:
                        if(selection_.active()) {
                            findCurrentLine(true, lastMouseX_, lastMouseY_);
                            moves_.add(new PuzzleMove(true, currentLine_, 1));
                        }
                        break;
                    case KeyEvent.VK_S:
                        if(selection_.active()) {
                            findCurrentLine(true, lastMouseX_, lastMouseY_);
                            moves_.add(new PuzzleMove(true, currentLine_, 2));
                        }
                        break;
                    case KeyEvent.VK_A:
                        if(selection_.active()) {
                            findCurrentLine(false, lastMouseX_, lastMouseY_);
                            moves_.add(new PuzzleMove(false, currentLine_, 1));
                        }
                        break;
                    case KeyEvent.VK_D:
                        if(selection_.active()) {
                            findCurrentLine(false, lastMouseX_, lastMouseY_);
                            moves_.add(new PuzzleMove(false, currentLine_, 2));
                        }
                    break;
                }
            }
            switch(keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_S:
                case KeyEvent.VK_A:
                case KeyEvent.VK_D:
                case KeyEvent.VK_SPACE:
                        break;
                case KeyEvent.VK_I:
                    moves_.add(new PuzzleMove(true, -1, 1));
                    break;
                case KeyEvent.VK_K:
                    moves_.add(new PuzzleMove(true, -1, 2));
                    break;
                case KeyEvent.VK_J:
                    moves_.add(new PuzzleMove(false, -1, 1));
                    break;
                case KeyEvent.VK_L:
                    moves_.add(new PuzzleMove(false, -1, 2));
                    break;
                default:
                    //moveMade = false;
                    break;
            }
            /*if(moveMade && newCombo) {
                resetCombo();
            }*/
        }
    }
    private boolean columnMovable(int a) {
        for(int i = 0; i < height_; i++)
            if(map_[a][i].inTransit())
                return false;
        glowColumn(a);
        return true;
    }
    private boolean rowMovable(int a) {
        for(int i = 0; i < width_; i++)
            if(map_[i][a].inTransit())
                return false;
        glowRow(a);
        return true;
    }
    private void glowColumn(int a) {
        for(int i = 0; i < height_; i++)
            map_[a][i].addGlow(0.2f);
    }
    private void glowRow(int a) {
        for(int i = 0; i < width_; i++)
            map_[i][a].addGlow(0.2f);
    }
    private void flipColumn(int a) {
        for(int i = 0; i < height_; i++) {
            map_[a][i].toggle();
        }
    }
    private void flipRow(int a) {
        for(int i = 0; i < width_; i++) {
            map_[i][a].toggle();
        }
    }
    private void shiftColumnUp(int a) {
        int i = 0;
        PuzzlePiece temp = map_[a][i];
        for(; i < height_ - 1; i++) {
            map_[a][i] = map_[a][i + 1];
            map_[a][i].setMovement(0, -60 / width_, 0, 0);
            map_[a][i].setDestination(toCoordinates(a, i));
        }
        map_[a][i] = temp;
        Coordinates c = toCoordinates(a, i);
        map_[a][i].setPhysics(c.x, c.y + spacing_, 0, -60 / width_, 0, 0);
        map_[a][i].setDestination(c);
    }
    private void shiftRowLeft(int a) {
        int i = 0;
        PuzzlePiece temp = map_[i][a];
        for(; i < width_ - 1; i++) {
            map_[i][a] = map_[i + 1][a];
            map_[i][a].setMovement(-60 / width_, 0, 0, 0);
            map_[i][a].setDestination(toCoordinates(i, a));
        }
        map_[i][a] = temp;
        Coordinates c = toCoordinates(i, a);
        map_[i][a].setPhysics(c.x + spacing_, c.y, -60 / width_, 0, 0, 0);
        map_[i][a].setDestination(c);
    }
    private void shiftColumnDown(int a) {
        int i = height_ - 1;
        PuzzlePiece temp = map_[a][i];
        for(; i > 0; i--) {
            map_[a][i] = map_[a][i - 1];
            map_[a][i].setMovement(0, 60 / width_, 0, 0);
            map_[a][i].setDestination(toCoordinates(a, i));
        }
        map_[a][i] = temp;
        Coordinates c = toCoordinates(a, i);
        map_[a][i].setPhysics(c.x, c.y - spacing_, 0, 60 / width_, 0, 0);
        map_[a][i].setDestination(c);
    }
    private void shiftRowRight(int a) {
        int i = width_ - 1;
        PuzzlePiece temp = map_[i][a];
        for(; i > 0; i--) {
            map_[i][a] = map_[i - 1][a];
            map_[i][a].setMovement(60 / width_, 0, 0, 0);
            map_[i][a].setDestination(toCoordinates(i, a));
        }
        map_[i][a] = temp;
        Coordinates c = toCoordinates(i, a);
        map_[i][a].setPhysics(c.x - spacing_, c.y, 60 / width_, 0, 0, 0);
        map_[i][a].setDestination(c);
    }
    private PuzzlePiece removePiece(int column, int row) {
        int i = row;
        PuzzlePiece removed = map_[column][i];
        for(; i > 0; i--) {
            map_[column][i] = map_[column][i - 1];
            if(!entryQueue_[column].contains(map_[column][i])) {
                map_[column][i].setAcceleration(0, 1);
            }
            map_[column][i].setDestination(toCoordinates(column, i));
        }
        Coordinates c = toCoordinates(column, i - 1);
        int size = spacing_ - 20;
        PuzzlePiece newPiece = getRandomPiece(c, size);
        entryQueue_[column].add(newPiece);
        map_[column][i] = newPiece;
        newPiece.setDestination(toCoordinates(column, i));
        removed.setMovement(1.5 * (Math.random() - 0.5), -1, 0, 0.15);
        return removed;
    }
    /*private void resetHighlights() {
        for(int i = 0; i < width_; i++)
            for(int j = 0; j < height_; j++) {
                map_[i][j].resetHighlight();
            }
    }*/
    public void checkForPatterns() {
        //resetHighlights();
        PuzzlePattern [] patterns = currentLevel_.getPatterns();
        for(PuzzlePattern pattern : patterns) {
            checkForPattern(pattern);
        }
    }
    private void checkForPattern(PuzzlePattern pattern) {
        int patternWidth = pattern.getWidth();
        int patternHeight = pattern.getHeight();
        for(int i = 0; i <= width_ - patternWidth; i++)
            for(int j = 0; j <= height_ - patternHeight; j++) {
                boolean match = true;
                boolean stateToMatch = false;
                int colorToMatch = 0;
                boolean matchSet = false;
                for(int a = 0; a < patternWidth; a++) {
                    for(int b = 0; b < patternHeight; b++) {
                        if(pattern.get(a, b)) {
                            PuzzlePiece piece = map_[i + a][j + b];
                            if(!piece.inTransit()) {
                                if(!matchSet) {
                                    stateToMatch = piece.getState();
                                    colorToMatch = piece.getColorInt();
                                    matchSet = true;
                                }
                                else if(!(piece.getState() == stateToMatch && piece.getColorInt() == colorToMatch)) {
                                    match = false;
                                }
                            }
                            else {
                                match = false;
                            }
                        }
                        if(!match) break;
                    }
                    if(!match) break;
                }
                if(match) {
                    matches_.add(new PuzzleMatch(i, j, pattern));
                }
            }
    }
    /*private void validateMatches() {
        ArrayList<PuzzleMatch> invalidMatches = new ArrayList<PuzzleMatch>();
        ArrayList<PuzzlePiece> overlapPieces = new ArrayList<PuzzlePiece>();
        ArrayList<PuzzleMatch> overlapMatches = new ArrayList<PuzzleMatch>();
        ArrayList<PuzzlePiece> checkedPieces = new ArrayList<PuzzlePiece>();
        for(PuzzleMatch match : matches_) {
            PuzzlePiece matchedPiece;
            for(int i = 0; i < match.pattern.getWidth(); i++) {
                for(int j = 0; j < match.pattern.getHeight(); j++) {
                    if(match.pattern.get(i + match.column, j + match.row)) {
                        matchedPiece = map_[i + match.column][j + match.row];
                        if(matchedPiece.requiresOverlap()) {
                            overlapPieces.add(matchedPiece);
                            overlapMatches.add(match);
                        }
                    }
                }
            }
        }
        for(int i = 0; i < overlapPieces.size(); i++) {
            PuzzlePiece piece = overlapPieces.get(i);
            if(!checkedPieces.contains(piece)) {
                boolean overlap = false;
                for(int j = i + 1; j < overlapPieces.size(); j++) {
                    if(overlapPieces.get(j) == piece) {
                        overlap = true;
                    }
                }
                if(!overlap) {
                    invalidMatches.add(overlapMatches.get(i));
                }
                checkedPieces.add(piece);
            }
        }
        for(PuzzleMatch match : invalidMatches) {
            matches_.remove(match);
        }
    }*/
    public void scorePattern(PuzzleMatch match, ArrayList<PuzzlePiece> allPendingPieces) {
        int score = 0;
        if(combo_ == 1) {
            resetLast();
        }
        int column = match.column;
        int row = match.row;
        PuzzlePattern pattern = match.pattern;
        int width = pattern.getWidth();
        int height = pattern.getHeight();
        boolean overlap = false;
        int piecesRemoved = 0, bonus = 0;
        ArrayList<PuzzlePiece> pendingPieces = new ArrayList<PuzzlePiece>();
        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++) {
                if(pattern.get(i, j)) {
                    PuzzlePiece matchedPiece = map_[column + i][row + j];
                    if(!matchedPiece.requiresOverlap() || matchedPiece.getOverlapCounter() != 0) {
                        piecesRemoved++;
                        boolean pieceAlreadyRemoved = false;
                        for(Coordinates c : piecesToRemove_)
                            if(matchedPiece == map_[c.x][c.y]) {
                                pieceAlreadyRemoved = true;
                                break;
                            }
                        if(pieceAlreadyRemoved) {
                            overlap = true;
                        }
                        else {
                            matchedPiece.setRemoved();
                            piecesToRemove_.add(new Coordinates(column + i, row + j));
                            if(matchedPiece.requiresOverlap()) {
                                bonus += matchedPiece.getBonus();
                                int newBonus = matchedPiece.getBonus() * (stageNumber_ + 1) * combo_ * 10;
                                for(PuzzleText scoreText : ((PuzzlePieceO)matchedPiece).getPendingScores()) {
                                    scoreText.setString("" + (Integer.parseInt(scoreText.getString()) + newBonus));
                                    score += newBonus;
                                }
                            }
                        }
                    }
                    matchedPiece.incrementOverlapCounter();
                    if(matchedPiece.requiresOverlap()) {
                        pendingPieces.add(matchedPiece);
                    }
                }
            }
        if(overlap) {
            overlappingCombo_++;
            combo_ -= 2;
        }
        score += (piecesRemoved + bonus) * (stageNumber_ + 1) * combo_ * 10;
        Coordinates c = toCoordinates(column, row);
        c.x += (int) (width * spacing_ / 2.0 + Math.random() * 40 - 20);
        c.y += (int) (height * spacing_ / 2.0 + Math.random() * 40 - 20);
        PuzzleText newScore = new PuzzleText("" + score, c.x, c.y, 20, Color.white, 2);
        texts_.add(newScore);
        for(PuzzlePiece piece : pendingPieces) {
            ((PuzzlePieceO) piece).addPendingScore(newScore);
            allPendingPieces.add(piece);
        }
        score_ += score;
        last_ += score;
        combo_ += 2;
    }
    public void startUp() {
        score_ = 0;
        goToLevel(0, 2);
        go();
    }
    public void tryNextMove() {
        if(moves_.size() > 0) {
            playSound(CLICK_SOUND);
            resetCombo();
            PuzzleMove move = moves_.get(0);
            boolean noneInTransit = !anyInTransit();
            boolean moveCompleted = false;
            int line = move.line;
            if(move.vertical) {
                switch(move.action) {
                    case 0:
                        if(line > -1) {
                            if(columnMovable(line)) {
                                flipColumn(line);
                                moveCompleted = true;
                            }
                        }
                        else {
                            if(noneInTransit) {
                                for(int i = 0; i < width_; i++) {
                                    flipColumn(i);
                                }
                                moveCompleted = true;
                            }
                        }
                        break;
                    case 1:
                        if(line > -1) {
                            if(columnMovable(line)) {
                                shiftColumnUp(line);
                                moveCompleted = true;
                            }
                        }
                        else {
                            if(noneInTransit) {
                                for(int i = 0; i < width_; i++) {
                                    shiftColumnUp(i);
                                }
                                moveCompleted = true;
                            }
                        }
                        break;
                    case 2:
                        if(line > -1) {
                            if(columnMovable(line)) {
                                shiftColumnDown(line);
                                moveCompleted = true;
                            }
                        }
                        else {
                            if(noneInTransit) {
                                for(int i = 0; i < width_; i++) {
                                    shiftColumnDown(i);
                                }
                                moveCompleted = true;
                            }
                        }
                        break;
                }
            }
            else {
                switch(move.action) {
                    case 0:
                        if(line > -1) {
                            if(rowMovable(line)) {
                                flipRow(line);
                                moveCompleted = true;
                            }
                        }
                        else {
                            if(noneInTransit) {
                                for(int i = 0; i < height_; i++) {
                                    flipRow(i);
                                }
                                moveCompleted = true;
                            }
                        }
                        break;
                    case 1:
                        if(line > -1) {
                            if(rowMovable(line)) {
                                shiftRowLeft(line);
                                moveCompleted = true;
                            }
                        }
                        else {
                            if(noneInTransit) {
                                for(int i = 0; i < height_; i++) {
                                    shiftRowLeft(i);
                                }
                                moveCompleted = true;
                            }
                        }
                        break;
                    case 2:
                        if(line > -1) {
                            if(rowMovable(line)) {
                                shiftRowRight(line);
                                moveCompleted = true;
                            }
                        }
                        else {
                            if(noneInTransit) {
                                for(int i = 0; i < height_; i++) {
                                    shiftRowRight(i);
                                }
                                moveCompleted = true;
                            }
                        }
                        break;
                }
            }
            if(moveCompleted) {
                moves_.remove(0);
            }
        }
    }
    public void go() {
        //calculate piece movement and reset overlap counter
        for(int i = 0; i < width_; i++) {
            for(int j = 0; j < height_; j++) {
                map_[i][j].go();
                map_[i][j].resetOverlapCounter();
            }
        }
        //remove expired falling pieces and texts
        ArrayList<PuzzlePiece> piecesToDelete = new ArrayList<PuzzlePiece>();
        ArrayList<PuzzleText> textsToDelete = new ArrayList<PuzzleText>();
        for(PuzzlePiece piece : falling_) {
            float alpha = piece.getAlpha();
            if(alpha == 0) {
                piecesToDelete.add(piece);
            }
            else {
                piece.go();
            }
        }
        for(PuzzleText text : texts_) {
            float alpha = text.getAlpha();
            if(alpha == 0) {
                textsToDelete.add(text);
            }
            else {
                text.go();
            }
        }
        for(PuzzlePiece piece : piecesToDelete) {
            falling_.remove(piece);
        }
        for(PuzzleText text : textsToDelete) {
            texts_.remove(text);
        }
        //calculate new pattern matches and player score and display texts
        overlappingCombo_ = 1;
        checkForPatterns();
        int numberOfMatches = matches_.size();
        PuzzleMatch match = null;
        boolean patternScored = false;
        ArrayList<PuzzlePiece> allPendingPieces = new ArrayList<PuzzlePiece>();
        for(int i = 0; i < numberOfMatches; i++) {
            match = matches_.remove(0);
            scorePattern(match, allPendingPieces);
            patternScored = true;
        }
        if(patternScored) {
            Coordinates c = toCoordinates(match.column, match.row);
            c.x += (int) (match.pattern.getWidth() * spacing_ / 2.0);
            c.y += (int) (match.pattern.getHeight() * spacing_ / 2.0);
            if(overlappingCombo_ > 1) {
                texts_.add(new PuzzleText("x " + overlappingCombo_, c.x, c.y, overlappingCombo_ * 5 + 10, Color.orange, 1.5));
            }
            if(combo_ > 3) {
                int comboNumber = (combo_ - 1) / 2;
                texts_.add(new PuzzleText("COMBO " + comboNumber, c.x, c.y, comboNumber * 10, Color.yellow, 1));
            }
        }
        //remove newly matched pieces
        int numberOfPiecesToRemove = piecesToRemove_.size();
        Coordinates [] sortedList = new Coordinates[numberOfPiecesToRemove];
        for(int i = 0; i < numberOfPiecesToRemove; i++) {
            int topI = 0;
            int topRow = piecesToRemove_.get(0).y;
            for(int j = 1; j < numberOfPiecesToRemove - i; j++) {
                int row = piecesToRemove_.get(j).y;
                if(row < topRow) {
                    topI = j;
                    topRow = row;
                }
            }
            sortedList[i] = piecesToRemove_.remove(topI);
        }
        for(int i = 0; i < numberOfPiecesToRemove; i++) {
            falling_.add(removePiece(sortedList[i].x, sortedList[i].y));
        }
        //drop new pieces where needed
        for(int i = 0; i < width_; i++) {
            if(entryWait_[i] == 0) {
                if(entryQueue_[i].size() > 0) {
                    entryQueue_[i].remove(0).setAcceleration(0, 1);
                    entryWait_[i] = 12;
                }
            }
            else {
                entryWait_[i]--;
            }
        }
        //check for level completion
        if(score_ >= currentLevel_.getPointsToNext() && !currentLevel_.isInfinite()) {
            goToLevel(stageNumber_, levelNumber_ + 1);
        }
        //make player move if targeted pieces are not moving
        tryNextMove();
    }
    private void printLine(Graphics g, String s, int l) {
        g.drawString(s, 0, l * TEXT_SIZE);
    }
    private void drawInfo(Graphics g) {
        g.setFont(new Font("Garamond", Font.PLAIN, TEXT_SIZE));
        printLine(g, "Stage: " + (stageNumber_ + 1), 1);
        printLine(g, "Level: " + (levelNumber_ + 1), 2);
        int pointsToNext = 0;
        if(!currentLevel_.isInfinite()) {
            pointsToNext = currentLevel_.getPointsToNext() - score_;
        }
        printLine(g, "Next in: " + pointsToNext, 3);
        printLine(g, "Score: " + score_, 5);
        printLine(g, "Last: " + last_, 6);
        printLine(g, "Combo: " + (combo_ - 1) / 2, 7);
    }
    private void drawPatterns(Graphics g) {
        int row = 0;
        int squareSize = 9;
        int rightMargin = 640;
        for(PuzzlePattern pattern : currentLevel_.getPatterns()) {
            if((row + pattern.getHeight()) * squareSize > ySize_) {
                rightMargin -= squareSize * 5;
                row = 0;
            }
            for(int i = 0; i < pattern.getHeight(); i++, row++) {
                for(int j = 0; j < pattern.getWidth(); j++) {
                    if(pattern.get(j, i)) {
                        g.drawRect(rightMargin - squareSize - j * squareSize + 1, row * squareSize + 1, squareSize - 2, squareSize - 2);
                    }
                }
            }
            row++;
        }
    }
    public void show(Graphics g, SafetyBoolean runStatus) {
        boolean premature;
        //falling
        premature = false;
        int i = 0;
        do {
            for(; i < falling_.size(); i++) {
                if(runStatus.status) {
                    premature = true;
                    break;
                }
                falling_.get(i).show(g);
            }
            if(runStatus.status) {
                //System.out.println("WARNED YOU");
                premature = false;
            }
        } while(premature);
        //
        selection_.show(g);
        if(controlMode_ > 0) {
            selection2_.show(g);
        }
        for(i = 0; i < width_; i++)
            for(int j = 0; j < height_; j++) {
                map_[i][j].show(g);
            }
        g.setColor(Color.white);
        drawInfo(g);
        drawPatterns(g);
        //text
        premature = false;
        i = 0;
        do {
            for(; i < texts_.size(); i++) {
                if(runStatus.status) {
                    premature = true;
                    break;
                }
                texts_.get(i).show(g);
            }
            if(runStatus.status) {
                //System.out.println("WARNED YOU");
                premature = false;
            }
        } while(premature);
        //
        if(shadowTransparency_ > 0.0f) {
            g.setColor(PuzzlePanel.relativelyTransparent(Color.black, shadowTransparency_ * 0.8f));
            g.fillRect(0, 0, xSize_, ySize_);
            g.setColor(PuzzlePanel.relativelyTransparent(Color.white, shadowTransparency_ * 0.1f));
            g.fillRect(0, shadowTop_, xSize_, shadowBottom_ - shadowTop_);
        }
        if(messageText_ != null) {
            if(messageText_.getAlpha() > 0.0f) {
                messageText_.show(g);
                shadowTransparency_ = messageText_.getAlpha();
                messageText_.go();
            }
            else {
                messageText_ = null;
                if(!playMode_) { //end of stage rather than level
                    initializeMap();
                    playMode_ = true;
                    setGhost(lastMouseX_, lastMouseY_);
                    activateSelections();
                }
            }
        }
        if(patternDisplay_ != null) {
            if(!patternDisplay_.finished()) {
                patternDisplay_.show(g, xSize_ / 2, ySize_ / 16 * 10);
            }
            else {
                patternDisplay_ = null;
                messageText_.setDecay(3);
                playMode_ = true;
                setGhost(lastMouseX_, lastMouseY_);
                activateSelections();
            }
        }
    }
    public static void playSound(int soundID) {
        //System.out.println("play sound");
        /*String soundString;
        switch(soundID) {
            case CLICK_SOUND:
                soundString = "click.wav";
                break;
            default:
                return;
        }
        try {
            InputStream in = new FileInputStream("sound/" + soundString);
            AudioStream as = new AudioStream(in);
            AudioPlayer.player.start(as);
        }
        catch(FileNotFoundException e) {
            System.out.println("error loading sound[1]: " + e);
        }
        catch(IOException e) {
            System.out.println("error loading sound[2]: " + e);
        }*/
    }
}
