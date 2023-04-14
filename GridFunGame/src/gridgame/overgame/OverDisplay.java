package gridgame.overgame;

import javax.swing.*;
import java.awt.*;

/**
 * Defines the top-level appearance of an Asteroids game.
 */
@SuppressWarnings("serial")
public class OverDisplay extends JFrame
{
    // Some formatting constants
    public final static int ROWS = 6;
    public final static int COLS = 7;
    public final static Color BACKGROUND_COLOR = Color.GRAY;
    public final static Color P1_COLOR = new Color(176, 0, 0);
    public final static String P1_COLOR_NAME = "Red";
    public final static Color P2_COLOR = Color.YELLOW;
    public final static String P2_COLOR_NAME = "Yellow";
    public final static Color BOARD_COLOR = Color.BLUE;
    public final static Color TIE_COLOR = Color.WHITE;
    public final static int BORDER = 5;
    public final static int FONT_SIZE = 20;
    
    /** The area where the action takes place */
    private OverScreen screen;
    
    /**
     * Lays out the game and creates the controller
     */
    public OverDisplay (OverController controller)
    {
        // Default behavior on closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // The main playing area and the controller
        screen = new OverScreen(controller);

        // This panel contains the screen to prevent the screen from being
        // resized
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.add(screen);

        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenPanel, "Center");
        setContentPane(mainPanel);
        pack();
    }
    
    /**
     * Called when it is time to update the screen display. This is what drives the animation.
     */
    public void refresh ()
    {
        screen.repaint();
    }

    /**
     * Sets the large legend
     */
    public void setLegend (String s)
    {
        screen.setLegend(s);
    }
}
