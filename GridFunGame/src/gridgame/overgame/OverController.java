package gridgame.overgame;

import static gridgame.game.Constants.*;
import java.awt.event.*;
import javax.swing.*;
import gridgame.game.Controller;
import sounds.SoundDemo;

/**
 * Controls a game of Asteroids.
 */
public class OverController implements KeyListener, ActionListener
{
    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    private int y;
    
    private SoundDemo sound;

    /** The game display */
    private OverDisplay display;
    
    @SuppressWarnings("unused")
    private Controller game;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public OverController ()
    {
        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;
        
        sound = new SoundDemo();
        
        y = 0;

        // Record the display object
        display = new OverDisplay(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
        
        initialScreen();
    }
    
    public void reset ()
    {
        game = null;
        
        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;
        
        sound = new SoundDemo();
        
        y = 0;

        // Record the display object
        display = new OverDisplay(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
        
        initialScreen();
    }

    public int getY ()
    {
        return y;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Grid Battle");
    }

    public void playSound (String name)
    {
        sound.playClip(name);
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        display.setLegend("");
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        clear();

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Schedules a transition m msecs in the future
     */
    @SuppressWarnings("unused")
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // Time to refresh the screen and deal with keyboard input
        if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();

            // Refresh screen
            display.refresh();
        }
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {

            // Clear the transition time
            transitionTime = Long.MAX_VALUE;
        }
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
        {
            if (y > 0)
            {
                playSound("move.wav");
                y--;
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
        {
            if (y < 1)
            {
                playSound("move.wav");
                y++;
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            if (y == 0)
            {
                playSound("select.wav");
                display.dispose();
                game = new Controller(this);
            }
            else
            {
                System.exit(0);
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
            playSound("back.wav");
        }
        else if (e.getKeyCode() == KeyEvent.VK_L)
        {
            playSound("music.wav");
        }
    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * Releases key from arraylist.
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
    }
}
