package gridgame.game;

import java.awt.Color;
import java.util.Random;

/**
 * Provides constants that govern the game.
 */
public class Constants
{
    /**
     * A shared random number generator for general use.
     */
    public final static Random RANDOM = new Random();

    /**
     * Size of the Squares in the grid
     */
    public final static Color BROWN = new Color (139, 69, 19);
    
    /**
     * Size of the Squares in the grid
     */
    public final static int SQUARESIZE = 64;
    
    /**
     * Offset play area Horizontally
     */
    public final static int WIDEOFFSET = 10;
    
    /**
     * Offset play area Vertically
     */
    public final static int TALLOFFSET = 10;
    
    /**
     * Offset of the health bar Horizontally
     */
    public final static int WIDEOFFSETHBAR = 5;
    
    /**
     * Offset of the health bar Horizontally
     */
    public final static int WIDTHHBAR = SQUARESIZE - 2 * WIDEOFFSETHBAR;
    
    /**
     * Offset of the health bar Vertically
     */
    public final static int HEIGHTHBAR = 5;
    
    /**
     * Offset of the health bar Vertically
     */
    public final static int TALLOFFSETHBAR = 5;
    
    /**
     * Game title
     */
    public final static String TITLE = "Ryan First Game";

    /**
     * The number of milliseconds between the beginnings of frame refreshes
     */
    public final static int FRAME_INTERVAL = 32;
    
    /**
     * The number squares wide the grid will be
     */
    public final static int GRIDWIDTH = 20;
    
    /**
     * The number squares tall the grid will be
     */
    public final static int GRIDHEIGHT = 15;
    
    /**
     * The number squares tall the grid will be
     */
    public final static int SPECIALWIDTH = 250;
    
    /**
     * The height and width of the game area.
     */
    public final static int HSIZE = TALLOFFSET * 2 + SQUARESIZE * GRIDHEIGHT;
    
    /**
     * The height and width of the game area.
     */
    public final static int WSIZE = WIDEOFFSET * 2 + SQUARESIZE * GRIDWIDTH + SPECIALWIDTH;
    
    /**
     * Pixels moved by characters each frame while moving
     */
    public final static int MOVESPEED = 8;
    
    /**
     * Pixels moved by characters each frame while attacking
     */
    public final static int ATTACKSPEED = 4;

    /**
     * The game over message
     */
    public final static String WINNER = "You Won!";
    
    /**
     * The game over message
     */
    public final static String LOSER = "You Lost";
}
