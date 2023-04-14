package gridgame.game;

import javax.swing.*;
import static gridgame.game.Constants.*;
import java.awt.*;

/**
 * Defines the top-level appearance of an Asteroids game.
 */
@SuppressWarnings("serial")
public class Display extends JFrame
{
    // Some formatting constants
    public final static Color P1_COLOR = new Color(176, 0, 0);
    public final static Color P2_COLOR = Color.YELLOW;
    public final static Color TIE_COLOR = Color.WHITE;
    public final static int BORDER = SPECIALWIDTH;
    public final static int FONT_SIZE = 20;
    
    /** The area where the action takes place */
    private Screen screen;
    
    /** The area where the side actions take place */
    private SideScreen sideScreen;
    
    /**
     * Lays out the game and creates the controller
     */
    public Display (Controller controller, ParticipantHolder game)
    {
        // Default behavior on closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The main playing area and the controller
        screen = new Screen(controller, game);
        
        sideScreen = new SideScreen (controller, game);

        // This panel contains the screen to prevent the screen from being resized
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.add(screen);
        
        // This panel contains the side screen to prevent the screen from being resized
        JPanel sideScreenPanel = new JPanel();
        sideScreenPanel.setLayout(new GridBagLayout());
        sideScreenPanel.add(sideScreen);

        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenPanel, "West");
        mainPanel.add(sideScreenPanel, "East");
        setContentPane(mainPanel);
        pack();
        
    }

    /**
     * Called when it is time to update the screen display. This is what drives the animation.
     */
    public void refresh ()
    {
        screen.repaint();
        sideScreen.repaint();
    }

    /**
     * Sets the large legend
     */
    public void setLegend (String s)
    {
        screen.setLegend(s);
    }
}
