package gridgame.overgame;

import javax.swing.SwingUtilities;

/**
 * The main class for the application.
 */
public class GridFunMurderGame
{
    /**
     * Launches a dialog that lets the user choose between a classic and an enhanced game of Asteroids.
     */
    public static void main (String[] args)
    {
        SwingUtilities.invokeLater( () -> chooseVersion());
    }

    /**
     * Interacts with the user to determine whether to run classic Asteroids or enhanced Asteroids.
     */
    private static void chooseVersion ()
    {
        new OverController();
    }
}
