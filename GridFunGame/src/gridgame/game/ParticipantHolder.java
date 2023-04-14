package gridgame.game;

import static gridgame.game.Constants.*;
import java.util.HashMap;
import game.participants.Ally;
import game.participants.Enemy;
import game.participants.Moveable;
import game.participants.Person;
import game.participants.Terrain;
import sounds.SoundDemo;

public class ParticipantHolder
{
    private Terrain[][] map;

    private Person[][] characterLocations;
    
    private HashMap<String, Boolean> allies;
    
    private HashMap<String, Person> alliesReal;
    
    private HashMap<String, Boolean> enemies;
    
    private HashMap<String, Person> enemiesReal;
    
    private SoundDemo sound;
    
    public ParticipantHolder (SoundDemo sound)
    {
        this.sound = sound;
    }
    
    public Person getFirstEnemy()
    {
        return enemiesReal.get(enemiesReal.keySet().iterator().next());
    }
    
    public void endAllies()
    {
        for (String ally : allies.keySet())
        {
            allies.replace(ally, false);
        }
    }

    public void endAlly(String ally)
    {
        allies.replace(ally, false);
    }
    
    public boolean isExpended(Person p)
    {
        if (p instanceof Ally)
        {
            return allies.get(p.getName());
        }
        else
        {
            return enemies.get(p.getName());
        }
    }
    
    public Person getPerson (int x, int y)
    {
        return characterLocations[x][y];
    }
    
    public Terrain getTerrain (int x, int y)
    {
        return map[x][y];
    }
    
    public Terrain[][] getMap ()
    {
        return map;
    }

    public Person[][] getCharacterMap ()
    {
        return characterLocations;
    }
    
    public String getStats (int x, int y)
    {
        if (characterLocations[x][y].getStats() != null)
        {
            return "HP " + characterLocations[x][y].getStats()[0] + "\nAtk " + characterLocations[x][y].getStats()[1] + 
                    "\nMov " + characterLocations[x][y].getStats()[2];
        }
        return "";
    }
    
    public int battle (Person a, Person b)
    {
        int[] aStats = a.getStats();
        int[] bStats = b.getStats();

        bStats[0] = bStats[0] - aStats[1];
        a.startAttacking(new int[] {b.getX(), b.getY()});

        if (bStats[0] <= 0)
        {
            if (b instanceof Enemy)
            {
                enemies.remove(characterLocations[b.getX()][b.getY()].getName());
                enemiesReal.remove(characterLocations[b.getX()][b.getY()].getName());
            }
            else
            {
                allies.remove(characterLocations[b.getX()][b.getY()].getName());
                alliesReal.remove(characterLocations[b.getX()][b.getY()].getName());
            }
            
            characterLocations[b.getX()][b.getY()] = new Person("Noone", "Empty.png", new int[3], b.getX(), b.getY());
            characterLocations[b.getX()][b.getY()].setNull();
            playSound("bangShip.wav");
            return 0;
        }

        playSound("attack.wav");
        b.changeStats(bStats);
        return -1;
    }
    
    public boolean checkTurnOver (boolean playerTurn)
    {
        if (playerTurn)
        {
            for (String p : allies.keySet())
            {
                if (allies.get(p))
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            for (String p : enemies.keySet())
            {
                if (enemies.get(p))
                {
                    return false;
                }
            }
            return true;
        }
    }
    
    public void refreshAllies()
    {
        for (String p : allies.keySet())
        {
            allies.replace(p, true);
        }
    }
    
    public void refreshEnemies()
    {
        for (String p : enemies.keySet())
        {
            enemies.replace(p, true);
        }
    }
    
    public HashMap<String, Boolean> getAllies()
    {
        return allies;
    }
    
    public HashMap<String, Person> getAlliesReal()
    {
        return alliesReal;
    }
    
    public HashMap<String, Boolean> getEnemies()
    {
        return enemies;
    }
    
    public HashMap<String, Person> getEnemiesReal()
    {
        return enemiesReal;
    }
    
    public boolean checkForEnemies (int x, int y)
    {
        if (x - 1 >= 0 && characterLocations[x - 1][y] instanceof Enemy)
        {
            return true;
        }
        else if (x + 1 < GRIDWIDTH && characterLocations[x + 1][y] instanceof Enemy)
        {
            return true;
        }
        else if (y - 1 >= 0 && characterLocations[x][y - 1] instanceof Enemy)
        {
            return true;
        }
        else if (y + 1 < GRIDHEIGHT && characterLocations[x][y + 1] instanceof Enemy)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void move(int x, int y, int newx, int newy)
    {
        characterLocations[newx][newy] = characterLocations[x][y];
        characterLocations[x][y] = new Person ("Noone", "Empty.png", new int[3], x, y);
        characterLocations[x][y].setNull();
        characterLocations[newx][newy].changeX(newx);
        characterLocations[newx][newy].changeY(newy);
    }
    
    public int animateMove(int x, int y, int newx, int newy)
    {
        characterLocations[x][y].startMoving(newx, newy, map);
        return (Math.abs(x - newx) + Math.abs(y - newy)) * SQUARESIZE * (FRAME_INTERVAL / MOVESPEED + 1);
    }
    
    public void clear()
    {
        clearMap();
        placeCharacters();
    }
    
    private void clearMap()
    {
        map = new Terrain[GRIDWIDTH][GRIDHEIGHT];
        for (int i = 0; i < GRIDWIDTH; i++)
        {
            for (int j = 0; j < GRIDHEIGHT; j++)
            {
                map[i][j] = new Moveable("Plains", "Stone.png", 1);
            }
        }
        
        for (int i = 0; i < 15; i++)
        {
            map[0][i] = new Moveable("Brick", "Brick.png", 10);
            map[19][i] = new Moveable("Brick", "Brick.png", 10);
        }
        for (int i = 0; i < 20; i++)
        {
            map[i][0] = new Moveable("Brick", "Brick.png", 10);
            map[i][14] = new Moveable("Brick", "Brick.png", 10);
        }
        
        for (int i = 0; i < 11; i++)
        {
            map[5][i] = new Moveable("Brick", "Brick.png", 10);
        }
        
        for (int i = 5; i < 15; i++)
        {
            map[i][9] = new Moveable("Brick", "Brick.png", 10);
        }
        
        map[1][5] = new Moveable("Brick", "Brick.png", 10);
        map[4][5] = new Moveable("Brick", "Brick.png", 10);
        map[5][13] = new Moveable("Brick", "Brick.png", 10);
        map[12][10] = new Moveable("Brick", "Brick.png", 10);
        map[12][13] = new Moveable("Brick", "Brick.png", 10);
        map[18][9] = new Moveable("Brick", "Brick.png", 10);
        map[13][1] = new Moveable("Brick", "Brick.png", 10);
        map[13][2] = new Moveable("Brick", "Brick.png", 10);
        map[13][8] = new Moveable("Brick", "Brick.png", 10);
        map[13][7] = new Moveable("Brick", "Brick.png", 10);
        map[6][5] = new Moveable("Rock", "Rocks.png", 2);
        map[6][3] = new Moveable("Rock", "Rocks.png", 2);

        characterLocations = new Person[GRIDWIDTH][GRIDHEIGHT];
        for (int i = 0; i < GRIDWIDTH; i++)
        {
            for (int j = 0; j < GRIDHEIGHT; j++)
            {
                characterLocations[i][j] = new Person("Noone", "Empty.png", new int[3], i, j);
                characterLocations[i][j].setNull();
            }
        }
    }
    
    private void placeCharacters ()
    {
        allies = new HashMap<String, Boolean>();
        alliesReal = new HashMap<String, Person>();
        
        enemies = new HashMap<String, Boolean>();
        enemiesReal = new HashMap<String, Person>();
        
        createAlly ("Ryan1", "Ryan.png", new int[] { 40, 4, 4 }, 2, 2);
        createAlly ("Dave1", "Dave.png", new int[] { 30, 3, 6 }, 3, 3);
        createAlly ("Deneen1", "Deneen.png", new int[] { 30, 3, 6 }, 3, 1);
        createAlly ("Daniel1", "Daniel.png", new int[] { 17, 5, 8 }, 1, 3);
        createAlly ("Kelley1", "Kelley.png", new int[] { 24, 3, 7 }, 4, 2);
        createAlly ("Toby1", "Toby.png", new int[] { 24, 3, 7 }, 2, 4);
        

        createEnemy ("BasicBadGuyr", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 1, 8);
        createEnemy ("BasicBadGuyq", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 4, 8);
        createEnemy ("BasicBadGuyp", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 3, 9);
        createEnemy ("BasicBadGuyo", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 2, 9);
        createEnemy ("BasicBadGuyn", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 1, 10);
        createEnemy ("BasicBadGuym", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 4, 10);
        createEnemy ("BasicBadGuyt", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 3, 13);
        createEnemy ("BasicBadGuys", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 2, 13);
        createEnemy ("BasicBadGuyl", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 8, 10);
        createEnemy ("BasicBadGuyk", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 8, 13);
        createEnemy ("BasicBadGuyj", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 9, 11);
        createEnemy ("BasicBadGuyi", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 9, 12);
        createEnemy ("BasicBadGuyh", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 16, 12);
        createEnemy ("BasicBadGuy1", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 16, 11);
        createEnemy ("CaptainBadGuy2", "BasicBadGuy.gif", new int[] { 10, 4, 5 }, 16, 7);
        createEnemy ("BasicBadGuy3", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 15, 6);
        createEnemy ("BasicBadGuy4", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 17, 6);
        createEnemy ("BasicBadGuy5", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 15, 2);
        createEnemy ("BasicBadGuy6", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 17, 2);
        createEnemy ("BasicBadGuy7", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 10, 1);
        createEnemy ("BasicBadGuy8", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 12, 1);
        createEnemy ("CaptainBadGuy9", "BasicBadGuy.gif", new int[] { 10, 4, 5 }, 11, 2);
        createEnemy ("BasicBadGuy0", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 10, 8);
        createEnemy ("BasicBadGuya", "BasicBadGuy.gif", new int[] { 4, 3, 5 }, 12, 8);
        createEnemy ("CaptainBadGuyb", "BasicBadGuy.gif", new int[] { 10, 4, 5 }, 11, 7);
        createEnemy ("CaptainBadGuyc", "BasicBadGuy.gif", new int[] { 10, 4, 5 }, 6, 6);
        createEnemy ("CaptainBadGuyd", "BasicBadGuy.gif", new int[] { 10, 4, 5 }, 6, 2);
        createEnemy ("CaptainBadGuye", "BasicBadGuy.gif", new int[] { 10, 4, 5 }, 7, 3);
        createEnemy ("CaptainBadGuyf", "BasicBadGuy.gif", new int[] { 10, 4, 5 }, 7, 5);
        createEnemy ("LeaderBadGuyg", "BasicBadGuy.gif", new int[] { 20, 4, 5 }, 6, 4);
    }
    
    private void createAlly (String name, String location, int[] stats, int x, int y)
    {
        characterLocations[x][y] = new Ally(name, location, stats, x, y);
        allies.put(name, true);
        alliesReal.put(name, characterLocations[x][y]);
    }
    
    private void createEnemy (String name, String location, int[] stats, int x, int y)
    {
        characterLocations[x][y] = new Enemy(name, location, stats, x, y);
        enemies.put(name, true);
        enemiesReal.put(name, characterLocations[x][y]);
    }
    
    private void playSound (String name)
    {
        sound.playClip(name);
    }
}
