package gridgame.game;

import static gridgame.game.Constants.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;
import game.participants.Ally;
import game.participants.Enemy;
import game.participants.Person;
import gridgame.overgame.OverController;
import sounds.SoundDemo;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener
{
    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    private boolean end;

    private int x;
    
    private int xMemory;

    private int y;
    
    private int yMemory;
    
    private ParticipantHolder game;

    private String selectedCharacter;

    private String selectedLand;

    private Person selecting;
    
    private int[] delayedAttack;
    
    private int[] counterAttack;
    
    private boolean playerTurn;
    
    private boolean attacking;
    
    private boolean thinking;
    
    private SoundDemo sound;

    /** The game display */
    private Display display;
    
    private OverController over;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller (OverController over)
    {
        this.over = over;
        
        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;
        
        xMemory = 0;
        yMemory = 0;
        
        sound = new SoundDemo();
        
        game = new ParticipantHolder(sound);
        
        selecting = null;

        // Record the display object
        display = new Display(this, game);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();

        end = false;
        
        playerTurn = true;
        
        attacking = false;
        
        initialScreen();
    }

    public int getX ()
    {
        return x;
    }

    public int getY ()
    {
        return y;
    }

    public String getCharacter ()
    {
        return selectedCharacter;
    }

    public String getLand ()
    {
        return selectedLand;
    }

    public int[] getStats ()
    {
        if (game.getPerson(x, y).isReal())
        {
            return game.getPerson(x, y).getStats();
        }
        else
        {
            return null;
        }
    }

    public String getStats (int x, int y)
    {
        return game.getStats(x, y);
    }

    public Person checkSelected ()
    {
        return selecting;
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

    private void winScreen ()
    {
        display.setLegend(WINNER);
        end = true;
        scheduleTransition(5000);
    }

    private void loseScreen ()
    {
        display.setLegend(LOSER);
        end = true;
        scheduleTransition(5000);
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
        game.clear();
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        game.clear();

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
        
        display.setLegend("Ally Turn");
        scheduleTransition(800);
    }

    /**
     * Schedules a transition m msecs in the future
     */
    public void scheduleTransition (int m)
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
            if (display != null)
            {
                display.refresh();
            }
        }
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        try
        {
            selectedCharacter = game.getPerson(x, y).getName();
            selectedLand = game.getTerrain(x, y).getTileName();
        }
        catch (Exception e)
        {
            System.out.println("Nope");
        }
        
        // Do something only if the time has been reached
        if (display != null && transitionTime <= System.currentTimeMillis())
        {
            display.setLegend("");
            
            if (end)
            {
                display.dispose();
                display = null;
                over.reset();
            }
            
            boolean enemy = false;
            boolean ally = false;

            for (int i = 0; i < GRIDWIDTH; i++)
            {
                for (int j = 0; j < GRIDHEIGHT; j++)
                {
                    if (game.getPerson(i, j).isReal() && game.getPerson(i, j) instanceof Enemy)
                    {
                        enemy = true;
                    }
                    if (game.getPerson(i, j).isReal() && game.getPerson(i, j) instanceof Ally)
                    {
                        ally = true;
                    }
                }
            }

            if (ally && !enemy && !end)
            {
                winScreen();
                return;
            }
            if (!ally && enemy && !end)
            {
                loseScreen();
                return;
            }
            
            if (delayedAttack != null && game.getPerson(delayedAttack[0], delayedAttack[1]).isReal())
            {
                int t = game.battle(game.getPerson(delayedAttack[0], delayedAttack[1]), game.getPerson(delayedAttack[2], delayedAttack[3]));
                if (t >= 0)
                {
                    scheduleTransition(t);
                }
                else
                {
                    counterAttack = new int[] {delayedAttack[2], delayedAttack[3], delayedAttack[0], delayedAttack[1]};
                }
                delayedAttack = null;
            }
            
            if (counterAttack != null)
            {
                int t = game.battle(game.getPerson(counterAttack[0], counterAttack[1]), game.getPerson(counterAttack[2], counterAttack[3]));
                counterAttack = null;
                
                if (t >= 0)
                {
                    scheduleTransition(0);
                }
            }
            
            if (checkTurnOver())
            {
                if (playerTurn)
                {
                    game.refreshEnemies();
                    game.refreshAllies();
                    
                    if (display != null)
                    {
                        display.setLegend("Enemy Turn");
                        Person firstEnemy = game.getFirstEnemy();
                        xMemory = x;
                        x = firstEnemy.getX();
                        yMemory = y;
                        y = firstEnemy.getY();
                        scheduleTransition(800);
                    }
                    
                    playerTurn = false;
                    thinking = false;
                    return;
                }
                else
                {
                    game.refreshAllies();
                    game.refreshEnemies();
                    
                    if (display != null)
                    {
                        x = xMemory;
                        y = yMemory;
                        
                        display.setLegend("Ally Turn");
                        scheduleTransition(800);
                    }
                    
                    playerTurn = true;
                    return;
                }
            }
            
            if (!playerTurn)
            {
                enemyTurn();
                return;
            }

            // Clear the transition time
            transitionTime = Long.MAX_VALUE;
        }
    }
    
    private void enemyTurn ()
    {
        int[] instructions;
        HashMap<String, Boolean> enemies = game.getEnemies();
        HashMap<String, Person> enemiesReal = game.getEnemiesReal();
        
        for (String enemy : enemies.keySet())
        {
            if (enemies.get(enemy))
            {
                if (thinking)
                {
                    x = enemiesReal.get(enemy).getX();
                    y = enemiesReal.get(enemy).getY();
                    
                    thinking = false;
                    scheduleTransition(500);
                    break;
                }
                else
                {
                    int x = enemiesReal.get(enemy).getX();
                    int y = enemiesReal.get(enemy).getY();
                    
                    instructions = enemiesReal.get(enemy).getMove(game.getCharacterMap(), game.getMap());
                    if (instructions[0] == 0)
                    {
//                        // Will move randomly
//                        playSound("land.wav");
//                        
//                        int t = game.animateMove(x, y, instructions[1], instructions[2]);
//                        scheduleTransition(t);
                    }
                    else if (instructions[0] == 1)
                    {
                        //Will move to guy and attack
                        playSound("land.wav");
                        
                        moveAttack(x, y, instructions[1], instructions[2], instructions[3], instructions[4]);
                    }
                    else if (instructions[0] == 2)
                    {
                        //Directly next to guy and will attack
                        int t = game.battle(game.getPerson(x, y), game.getPerson(instructions[1], instructions[2]));
                        if (t >= 0)
                        {
                            scheduleTransition(t);
                        }
                    }
                    
                    enemies.replace(enemy, false);
                    thinking = true;
                    break;
                }
            }
        }
    }
    
    private void moveAttack (int x2, int y2, int i, int j, int a, int b)
    {
        int t = game.animateMove(x2, y2, i, j);
        scheduleTransition(t);
        delayedAttack = new int[] {i, j, a, b};
    }

    private boolean checkTurnOver ()
    {
        return game.checkTurnOver(playerTurn);
    }
    
    public boolean getAttacking ()
    {
        return attacking;
    }
    
    public boolean getTurn ()
    {
        return playerTurn;
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if (playerTurn)
        {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
            {
                if (x < GRIDWIDTH - 1)
                {
                    playSound("move.wav");
                    x++;
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
            {
                if (x > 0)
                {
                    playSound("move.wav");
                    x--;
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
            {
                if (y > 0)
                {
                    playSound("move.wav");
                    y--;
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
            {
                if (y < GRIDHEIGHT - 1)
                {
                    playSound("move.wav");
                    y++;
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                
                if (selecting == null && game.getPerson(x, y).isReal())
                {
                    if (game.getPerson(x, y) instanceof Enemy)
                    {
                        playSound("nope.wav");
                    }
                    else if (game.getPerson(x, y) instanceof Ally && game.isExpended(game.getPerson(x, y)))
                    {
                        playSound("select.wav");
                        selecting = game.getPerson(x, y);
                    }
                    else
                    {
                        playSound("nope.wav");
                    }
                }
                else if (!attacking && selecting != null && !game.getPerson(x, y).isReal())
                {
                    if (Math.abs(selecting.getX() - x) + Math.abs(selecting.getY() - y) <= selecting.getStats()[2])
                    {
                        playSound("land.wav");
                        if (!game.checkForEnemies(x, y))
                        {
                            game.move(selecting.getX(), selecting.getY(), x, y);
                            
                            game.endAlly(selecting.getName());
                            scheduleTransition(0);
                            
                            selecting = null;
                        }
                        else
                        {
                            game.move(selecting.getX(), selecting.getY(), x, y);
                            
                            selecting = game.getPerson(x, y);
                            attacking = true;
                        }
                        
                    }
                    else
                    {
                        playSound("nope.wav");
                    }
                }
                else if (selecting != null && game.getPerson(x, y).isReal() && 
                        ((game.getPerson(x, y) instanceof Enemy && selecting instanceof Ally) || 
                        (game.getPerson(x, y) instanceof Ally && selecting instanceof Enemy)))
                {
                    if (Math.abs(selecting.getX() - game.getPerson(x, y).getX()) <= 1 && Math.abs(selecting.getY() - 
                            game.getPerson(x, y).getY()) <= 1
                            && (Math.abs(selecting.getX() - game.getPerson(x, y).getX()) == 0 || 
                            Math.abs(selecting.getY() - game.getPerson(x, y).getY()) == 0))
                    {
                        int t = game.battle(selecting, game.getPerson(x, y));
                        if (t >= 0)
                        {
                            scheduleTransition(t);
                        }
                        else
                        {
                            counterAttack = new int [] {x, y, selecting.getX(), selecting.getY()};
                            scheduleTransition(0);
                        }
                        
                        game.endAlly(selecting.getName());
                        scheduleTransition(0);
                        
                        attacking = false;
                        selecting = null;
                    }
                    else
                    {
                        playSound("nope.wav");
                    }
                }
                else if (selecting != null && x == selecting.getX() && y == selecting.getY())
                {
                    playSound("back.wav");
                    
                    if (attacking)
                    {
                        game.endAlly(selecting.getName());
                        scheduleTransition(0);
                    }
                    
                    attacking = false;
                    selecting = null;
                }
                else
                {
                    playSound("nope.wav");
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            {
                if (selecting != null)
                {
                    playSound("back.wav");
                    
                    if (attacking)
                    {
                        game.endAlly(selecting.getName());
                        scheduleTransition(0);
                    }
                    
                    selecting = null;
                    attacking = false;
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                game.endAllies();
                if (selecting != null)
                {
                    attacking = false;
                    selecting = null;
                }
                
                scheduleTransition(0);
            }
            else if (e.getKeyCode() == KeyEvent.VK_L)
            {
                playSound("music.wav");
            }
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
