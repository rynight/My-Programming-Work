package gridgame.game;

import static gridgame.game.Constants.*;
import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import game.participants.Ally;
import game.participants.Base;
import game.participants.Enemy;
import game.participants.Person;
import game.participants.Terrain;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    /** Game controller */
    private Controller controller;
    
    private ParticipantHolder game;
    
    private Graphics2D g;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller, ParticipantHolder game)
    {
        this.controller = controller;
        this.game = game;
        legend = "";
        setPreferredSize(new Dimension(WSIZE - SPECIALWIDTH, HSIZE));
        setMinimumSize(new Dimension(WSIZE - SPECIALWIDTH, HSIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
     // Use better resolution
        g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Do the default painting
        super.paintComponent(g);
        if (controller.getCharacter() != null)
        {
            //Draws Background
            showBackground();
            
            //Draws Terrain
            showTerrain();
            
            //Shows grid lines
            showGridLines();
            
            //Shows move range of selected unit
            showMoveRange();
            
            //Draws the cursor
            showCursor();
            
            //Draw characters
            showCharacters();

            //Draws turn marker
            showTurn();
        }
    }
    
    private void showBackground ()
    {
//      Base background = new Base ("Background.jpg");
//      g.drawImage(background.getImage(), 0, 0, null);
        g.setColor(new Color (205, 133, 63));
        g.fillRect(0, 0, WSIZE - SPECIALWIDTH, HSIZE);
    }
    
    private void showTerrain ()
    {
        Terrain[][] tMap = game.getMap();
        
        for (int i = 0; i < GRIDWIDTH; i++)
        {
            for (int j = 0; j < GRIDHEIGHT; j++)
            {
                g.drawImage(tMap[i][j].getImage(), WIDEOFFSET + i * SQUARESIZE, TALLOFFSET + j * SQUARESIZE, null);
            }
        }
    }
    
    private void showGridLines ()
    {
        Color grid = new Color (0, 0, 0, 125);
        g.setColor(grid);
        
        for (int i = 0; i < GRIDHEIGHT; i++)
        {
            g.drawRect(WIDEOFFSET, TALLOFFSET, GRIDWIDTH * SQUARESIZE, GRIDHEIGHT * SQUARESIZE - i * SQUARESIZE);
        }
        
        for (int i = 0; i < GRIDWIDTH; i++)
        {
            g.drawRect(WIDEOFFSET, TALLOFFSET, GRIDWIDTH * SQUARESIZE - i * SQUARESIZE, GRIDHEIGHT * SQUARESIZE);
        }
    }
    
    private void showMoveRange ()
    {
        if (!controller.getCharacter().equals("") && game.getCharacterMap()[controller.getX()][controller.getY()].isReal() && controller.checkSelected() == null)
        {
            fillMoveRange();
        }
        
        if (controller.checkSelected() != null)
        {
            fillMoveRange(controller.checkSelected());
        }
    }
    
    private void showCursor ()
    {
        Base selector;
        
        if (controller.checkSelected() == null)
        {
            selector = new Base ("UnselectedCursor.gif");
            g.drawImage(selector.getImage(), controller.getX() * SQUARESIZE + WIDEOFFSET, controller.getY() * SQUARESIZE + TALLOFFSET, null);
        }
        else
        {
            createPath();
            
            selector = new Base ("SelectedCursor.png");
            g.drawImage(selector.getImage(), controller.getX() * SQUARESIZE + WIDEOFFSET, controller.getY() * SQUARESIZE + TALLOFFSET, null);
            
            g.drawImage(controller.checkSelected().getLightImage(), controller.getX() * SQUARESIZE + WIDEOFFSET, controller.getY() * SQUARESIZE + TALLOFFSET, null);
        }
    }
    
    private void showCharacters ()
    {
        Person[][] map = game.getCharacterMap();
        
        HashMap<String, Boolean> allies = game.getAllies();
        HashMap<String, Boolean> enemies = game.getEnemies();
                
        for (int i = 0; i < GRIDWIDTH; i++)
        {
            for (int j = 0; j < GRIDHEIGHT; j++)
            {
                Person p = map[i][j];
                int[] path = p.getMovingPath();;
                if (p.getIsMoving() && p.getXMove() == 0 && p.getYMove() == 0)
                {
                    if (path[3] == path[1] && path[4] == path[2])
                    {
                        int[] newPath = new int[path.length - 2];
                        newPath[0] = path[0];
                        for (int l = 3; l < path.length; l++)
                        {
                            newPath[l - 2] = path[l];
                        }
                        p.changeMovingPath(newPath);
                        path = newPath;
                    }
                    p.changeXMove((path[3] - path[1]) * SQUARESIZE);
                    p.changeYMove((path[4] - path[2]) * SQUARESIZE);
                }
                if (p.isReal())
                {
                    path = p.getMovingPath();
                    
                    if (p.getIsMoving())
                    {
                        int xMove = p.getXMove();
                        int yMove = p.getYMove();
                        int x = WIDEOFFSET + path[3] * SQUARESIZE - xMove;
                        int y = TALLOFFSET + path[4] * SQUARESIZE - yMove;
                        
                        if (xMove != 0)
                        {
                            g.drawImage(p.getRightImage(), x, y, null);
                            
                            showHealthBarPoint(x, y, game.getPerson(i, j));
                            
                            if (xMove < - MOVESPEED) 
                            {
                                p.changeXMove(xMove + MOVESPEED);
                                xMove = p.getXMove();
                            }
                            else if (xMove > MOVESPEED)
                            {
                                p.changeXMove(xMove - MOVESPEED);
                                xMove = p.getXMove();
                            }
                            else
                            {
                                p.changeXMove(0);
                                xMove = p.getXMove();
                            }
                        }
                        else if (yMove != 0)
                        {
                            g.drawImage(p.getRightImage(), x, y, null);
                            
                            showHealthBarPoint(x, y, game.getPerson(i, j));
                              
                            if (yMove < - MOVESPEED) 
                            {
                                p.changeYMove(yMove + MOVESPEED);
                                yMove = map[i][j].getYMove();
                            }
                            else if (yMove > MOVESPEED)
                            {
                                p.changeYMove(yMove - MOVESPEED);
                                yMove = p.getYMove();
                            }
                            else
                            {
                                p.changeYMove(0);
                                yMove = p.getYMove();
                            }
                        }
                        
                        if (xMove == 0 && yMove == 0)
                        {
                            if (path.length <= 5)
                            {
                                p.stopMoving();
                                game.move(i, j, p.getMovingTo()[0], p.getMovingTo()[1]);
                            }
                            else
                            {
                                path[1] = path[3];
                                path[2] = path[4];
                                p.changeMovingPath(path);
                            }
                        }
                    }
                    else if (p.getAttacking() != null)
                    {
                        Person attacking = map[p.getAttacking()[0]][p.getAttacking()[1]];
                        int x = p.getX() * SQUARESIZE + p.getMove()/ 2 + WIDEOFFSET;
                        int y = p.getY() * SQUARESIZE + p.getMove()/ 2 + TALLOFFSET;
                        if (attacking.getX() != p.getX())
                        {
                            g.drawImage(p.getImage(), x, p.getY() * SQUARESIZE + TALLOFFSET, null);
                        }
                        else
                        {
                            g.drawImage(p.getImage(), p.getX() * SQUARESIZE + WIDEOFFSET, y, null);
                        }
                        
                        if (p.getMove() < -ATTACKSPEED)
                        {
                            p.changeMove(p.getMove() + ATTACKSPEED);
                        }
                        else if (p.getMove() > ATTACKSPEED)
                        {
                            p.changeMove(p.getMove() - ATTACKSPEED);
                        }
                        else
                        {
                            p.changeMove(0);
                        }
                        
                        if (p.getMove() == 0)
                        {
                            p.stopAttacking();
                        }
                    }
                    else if (p instanceof Ally && allies.get(p.getName()))
                    {
                        g.drawImage(p.getImage(), WIDEOFFSET + i * SQUARESIZE, TALLOFFSET + j * SQUARESIZE, null);
                        
                        showHealthBar(i, j);
                    }
                    else if (p instanceof Enemy && enemies.get(p.getName()))
                    {
                        g.drawImage(p.getImage(), WIDEOFFSET + i * SQUARESIZE, TALLOFFSET + j * SQUARESIZE, null);
                        
                        showHealthBar(i, j);
                    }
                    else
                    {
                        g.drawImage(p.getDarkImage(), WIDEOFFSET + i * SQUARESIZE, TALLOFFSET + j * SQUARESIZE, null);
                        
                        showHealthBar(i, j);
                    }
                }
            }
        }
    }
    
    private void showTurn ()
    {
        // Draw the legend across the middle of the panel
        if (controller.getTurn())
        {
            g.setColor(Color.BLUE);
        }
        else
        {
            g.setColor(Color.RED);
        }
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (WSIZE - size) / 2, HSIZE / 2);
    }
    
    private void showHealthBar (int x, int y)
    {
        GradientPaint gradiant;
        Person p = game.getPerson(x, y);
                
        double healthRemaining = p.getStats()[0] / (double) p.getHealthMax();
        
        int xPlace = WIDEOFFSET + x * SQUARESIZE + WIDEOFFSETHBAR;
        int yPlace = TALLOFFSET + y * SQUARESIZE + TALLOFFSETHBAR;
        
        g.setColor(Color.BLACK);
        g.fillRect(xPlace, yPlace, WIDTHHBAR, HEIGHTHBAR);
        
        Color health;
        Color AHealth;
        if (p.getHealthMax() >= 40)
        {
            health = Color.BLUE;
        }
        else if (p.getHealthMax() >= 20)
        {
            health = Color.GREEN;
        }
        else if (p.getHealthMax() >= 10)
        {
            health = Color.ORANGE;
        }
        else
        {
            health = Color.YELLOW;
        }
        
        AHealth = new Color (0, 191, 255);

        if (p instanceof Ally)
        {
            gradiant = new GradientPaint(xPlace - 5, yPlace, AHealth, (float) (xPlace + WIDTHHBAR), yPlace + HEIGHTHBAR, health);
        }
        else
        {
            gradiant = new GradientPaint(xPlace - 5, yPlace, Color.RED, (float) (xPlace + WIDTHHBAR), yPlace + HEIGHTHBAR, health);
        }
        
        g.setPaint(gradiant);
        g.fill(new Rectangle2D.Double(xPlace + 1, yPlace + 1, WIDTHHBAR - 2, HEIGHTHBAR - 2));
        
        g.setColor(Color.BLACK);
        g.fillRect(xPlace + (int) (WIDTHHBAR * healthRemaining), yPlace, WIDTHHBAR - (int) (WIDTHHBAR * healthRemaining), HEIGHTHBAR);
    }
    
    private void showHealthBarPoint (int x, int y, Person p)
    {
        GradientPaint gradiant;
        double healthRemaining = p.getStats()[0] / (double) p.getHealthMax();
        
        int xPlace = x + WIDEOFFSETHBAR;
        int yPlace = y + TALLOFFSETHBAR;
        
        g.setColor(Color.BLACK);
        g.fillRect(xPlace, yPlace, WIDTHHBAR, HEIGHTHBAR);
        
        Color health;
        Color AHealth;
        if (p.getHealthMax() >= 30)
        {
            health = Color.BLUE;
        }
        else if (p.getHealthMax() >= 20)
        {
            health = Color.GREEN;
        }
        else if (p.getHealthMax() >= 10)
        {
            health = Color.ORANGE;
        }
        else
        {
            health = Color.YELLOW;
        }
        
        AHealth = new Color (0, 191, 255);
        
        if (p instanceof Ally)
        {
            gradiant = new GradientPaint(xPlace - 5, yPlace, AHealth, (float) (xPlace + WIDTHHBAR), yPlace + HEIGHTHBAR, health);
        }
        else
        {
            gradiant = new GradientPaint(xPlace - 5, yPlace, Color.RED, (float) (xPlace + WIDTHHBAR), yPlace + HEIGHTHBAR, health);
        }
        
        g.setPaint(gradiant);
        g.fill(new Rectangle2D.Double(xPlace + 1, yPlace + 1, WIDTHHBAR - 2, HEIGHTHBAR - 2));
        
        g.setColor(Color.BLACK);
        g.fillRect(xPlace + (int) (WIDTHHBAR * healthRemaining), yPlace, WIDTHHBAR - (int) (WIDTHHBAR * healthRemaining), HEIGHTHBAR);
    }
    
    private void fillMoveRange ()
    {
        int range = game.getPerson(controller.getX(), controller.getY()).getStats()[2];
        
        Base rangeMarker = new Base("BlueMove.png");
        
        Base attackable = new Base ("Attackable.png");
        
        Base EnemyMarker = new Base ("EnemyMarker.png");
        
        ArrayList<int[]> markedSpaces = getMoveRangeMap (game.getMap(), controller.getX(), controller.getY(), range);
        
        for (int i = 0; i < markedSpaces.size(); i++)
        {
            int[] location = markedSpaces.get(i);
            Person p = game.getPerson(controller.getX(), controller.getY());
            Person p2 = game.getPerson(location[0], location[1]);
            
            if (p instanceof Ally)
            {
                if (p2 instanceof Enemy && Math.abs(location[0] - p.getX()) + Math.abs(location[1] - p.getY()) == 1)
                {
                    g.drawImage(attackable.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
                }
                else if (p2 instanceof Enemy)
                {
                    g.drawImage(EnemyMarker.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
                }
                else
                {
                    g.drawImage(rangeMarker.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
                } 
            }
            else if (p instanceof Enemy)
            {
                if (p2 instanceof Ally && Math.abs(location[0] - p.getX()) + Math.abs(location[1] - p.getY()) == 1)
                {
                    g.drawImage(attackable.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
                }
                else if (p2 instanceof Ally)
                {
                    g.drawImage(EnemyMarker.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
                }
                else
                {
                    g.drawImage(rangeMarker.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
                } 
            }
        } 
    }
    
    private void fillMoveRange (Person p)
    {
        int range = p.getStats()[2];
        
        Base rangeMarker = new Base("BlueMove.png");
        
        Base attackable = new Base ("Attackable.png");
        
        Base EnemyMarker = new Base ("EnemyMarker.png");
        
        ArrayList<int[]> markedSpaces = getMoveRangeMap (game.getMap(), p.getX(), p.getY(), range);
        
        for (int i = 0; i < markedSpaces.size(); i++)
        {
            int[] location = markedSpaces.get(i);
            Person p2 = game.getPerson(location[0], location[1]);
            
            if (p2 instanceof Enemy && Math.abs(location[0] - p.getX()) + Math.abs(location[1] - p.getY()) == 1)
            {
                g.drawImage(attackable.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
            }
            else if (p2 instanceof Enemy)
            {
                g.drawImage(EnemyMarker.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
            }
            else
            {
                g.drawImage(rangeMarker.getImage(), location[0] * SQUARESIZE + WIDEOFFSET, location[1] * SQUARESIZE + TALLOFFSET, null);
            } 
        } 
    }
    
    private ArrayList<int[]> getMoveRangeMap (Terrain[][] graph, int x, int y, int range)
    {
        MinHeap paths = new MinHeap (GRIDWIDTH * GRIDHEIGHT * 10);
        
        ArrayList<int[]> moveRange = new ArrayList<int[]> ();
        
        boolean[][] booleanGraph = new boolean[GRIDWIDTH][GRIDHEIGHT];
        
        paths.insert(new int[] {0, x, y});
        
        while (true)
        {
            //Take first node in paths array
            int[] path;
            while (true)
            {
                path = paths.remove();
                if (!booleanGraph[path[path.length - 2]][path[path.length - 1]])
                {
                    break;
                }
            }
            
            //If first int greater than range end search and say cannot reach
            if (path[0] > range)
            {
                return moveRange;
            }
            
            //If destination node found end search and return path used to get to this node
            int newx = path[path.length - 2];
            int newy = path[path.length - 1];
            
            moveRange.add(new int[] {newx, newy});
            
            //Change found node to true on boolean graph
            booleanGraph[newx][newy] = true;
            
            //Add new nodes as new pathes
            int[] newNode = new int[path.length + 2];
            
            if (newx - 1 >= 0 && !booleanGraph[newx - 1][newy])
            {
                newNode[0] = path[0] + graph[newx - 1][newy].getMoveRestrictor();
                
                for (int i = 1; i < path.length; i++)
                {
                    newNode[i] = path[i];
                }
                
                newNode[path.length] = newx - 1;
                newNode[path.length + 1] = newy;
                
                paths.insert(newNode);
                
                newNode = new int[path.length + 2];
            }
            if (newx + 1 < GRIDWIDTH && !booleanGraph[newx + 1][newy])
            {
                newNode[0] = path[0] + graph[newx + 1][newy].getMoveRestrictor();
                
                for (int i = 1; i < path.length; i++)
                {
                    newNode[i] = path[i];
                }
                
                newNode[path.length] = newx + 1;
                newNode[path.length + 1] = newy;
                
                paths.insert(newNode);
                
                newNode = new int[path.length + 2];
            }
            if (newy - 1 >= 0 && !booleanGraph[newx][newy - 1])
            {
                newNode[0] = path[0] + graph[newx][newy - 1].getMoveRestrictor();
                
                for (int i = 1; i < path.length; i++)
                {
                    newNode[i] = path[i];
                }
                
                newNode[path.length] = newx;
                newNode[path.length + 1] = newy - 1;
                
                paths.insert(newNode);
                
                newNode = new int[path.length + 2];
            }
            if (newy + 1 < GRIDHEIGHT && !booleanGraph[newx][newy + 1])
            {
                newNode[0] = path[0] + graph[newx][newy + 1].getMoveRestrictor();
                
                for (int i = 1; i < path.length; i++)
                {
                    newNode[i] = path[i];
                }
                
                newNode[path.length] = newx;
                newNode[path.length + 1] = newy + 1;
                
                paths.insert(newNode);
                
            }
        }
    }
    
    private void createPath ()
    {
        ArrayList<ArrayList<Integer>> path = getShortestPath (controller.checkSelected().getX(), controller.checkSelected().getY(), 
                controller.getX(), controller.getY(), game.getMap(), controller.checkSelected().getStats()[2]);
        
        ArrayList<Integer> xVals = path.get(0);
        ArrayList<Integer> yVals = path.get(1);
        ArrayList<Integer> directions = path.get(2);
        
        for (int i = 0; i < xVals.size(); i++)
        {
            Base tile;
            switch(directions.get(i))
            {
                case 0:
                    tile = new Base("Hori.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 1:
                    tile = new Base("Vert.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 2:
                    tile = new Base("Right.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 3:
                    tile = new Base("Left.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 4:
                    tile = new Base("Down.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 5:
                    tile = new Base("Up.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 6:
                    tile = new Base("TurnDL.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 7:
                    tile = new Base("TurnUL.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 8:
                    tile = new Base("TurnDR.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                case 9:
                    tile = new Base("TurnUR.png");
                    g.drawImage(tile.getImage(), xVals.get(i) * SQUARESIZE + WIDEOFFSET, yVals.get(i) * SQUARESIZE + TALLOFFSET, null);
                    break;
                
            }
        }
    }
    
    private ArrayList<ArrayList<Integer>> getShortestPath (int x1, int y1, int x2, int y2, Terrain[][] graph, int range)
    {
        MinHeap paths = new MinHeap (GRIDWIDTH * GRIDHEIGHT * 10);
        
        boolean[][] booleanGraph = new boolean[GRIDWIDTH][GRIDHEIGHT];
        
        paths.insert(new int[] {0, x1, y1});
        
        while (true)
        {
            //Take first node in paths array
            int[] path;
            while (true)
            {
                path = paths.remove();
                if (!booleanGraph[path[path.length - 2]][path[path.length - 1]])
                {
                    break;
                }
            }
            
            //If first int greater than range end search and say cannot reach
            if (path[0] > range)
            {
                return parsePath(new int[] {});
            }
            
            //If destination node found end search and return path used to get to this node
            int newx = path[path.length - 2];
            int newy = path[path.length - 1];
                    
            if (newx == x2 && newy == y2)
            {
                return parsePath(path);
            }
            
            //Change found node to true on boolean graph
            booleanGraph[newx][newy] = true;
            
            //Add new nodes as new pathes
            int[] newNode = new int[path.length + 2];
            
            if (newx - 1 >= 0 && !booleanGraph[newx - 1][newy])
            {
                newNode[0] = path[0] + graph[newx - 1][newy].getMoveRestrictor();
                
                for (int i = 1; i < path.length; i++)
                {
                    newNode[i] = path[i];
                }
                
                newNode[path.length] = newx - 1;
                newNode[path.length + 1] = newy;
                
                paths.insert(newNode);
                
                newNode = new int[path.length + 2];
            }
            if (newx + 1 < GRIDWIDTH && !booleanGraph[newx + 1][newy])
            {
                newNode[0] = path[0] + graph[newx + 1][newy].getMoveRestrictor();
                
                for (int i = 1; i < path.length; i++)
                {
                    newNode[i] = path[i];
                }
                
                newNode[path.length] = newx + 1;
                newNode[path.length + 1] = newy;
                
                paths.insert(newNode);
                
                newNode = new int[path.length + 2];
            }
            if (newy - 1 >= 0 && !booleanGraph[newx][newy - 1])
            {
                newNode[0] = path[0] + graph[newx][newy - 1].getMoveRestrictor();
                
                for (int i = 1; i < path.length; i++)
                {
                    newNode[i] = path[i];
                }
                
                newNode[path.length] = newx;
                newNode[path.length + 1] = newy - 1;
                
                paths.insert(newNode);
                
                newNode = new int[path.length + 2];
            }
            if (newy + 1 < GRIDHEIGHT && !booleanGraph[newx][newy + 1])
            {
                newNode[0] = path[0] + graph[newx][newy + 1].getMoveRestrictor();
                
                for (int i = 1; i < path.length; i++)
                {
                    newNode[i] = path[i];
                }
                
                newNode[path.length] = newx;
                newNode[path.length + 1] = newy + 1;
                
                paths.insert(newNode);
                
            }
        }
    }
    
    private ArrayList<ArrayList<Integer>> parsePath (int[] path)
    {
        ArrayList<ArrayList<Integer>> parsedPath = new ArrayList<ArrayList<Integer>> ();
        ArrayList<Integer> directions = new ArrayList<Integer> ();
        ArrayList<Integer> xList = new ArrayList<Integer> ();
        ArrayList<Integer> yList = new ArrayList<Integer> ();
        
        int lastX;
        int lastY;
        int beforeLastX = -1;
        int beforeLastY = -1;
        int nextX;
        int nextY;
        
        for (int i = 1; i < path.length - 2; i += 2)
        {
            lastX = path[i];
            lastY = path[i + 1];
            nextX = path[i + 2];
            nextY = path[i + 3];
            if (i > 2)
            {
                beforeLastX = path[i - 2];
                beforeLastY = path[i - 1];
            }
            
            if (nextX > lastX)
            {
                //Connect right
                if (beforeLastX == -1 || lastX > beforeLastX)
                {
                    //Connect left
                    directions.add(0);
                }
                else if (lastY > beforeLastY)
                {
                    //Connect down
                    directions.add(9);
                }
                else if (lastY < beforeLastY)
                {
                    //Connect up
                    directions.add(8);
                }
            }
            else if (nextX < lastX)
            {
                //Connect left
                if (beforeLastX == -1 || lastX < beforeLastX)
                {
                    //Connect right
                    directions.add(0);
                }
                else if (lastY > beforeLastY)
                {
                    //Connect down
                    directions.add(7);
                }
                else if (lastY < beforeLastY)
                {
                    //Connect up
                    directions.add(6);
                }
            }
            else if (nextY > lastY)
            {
                //Connect down
                if (beforeLastY == -1 || lastY > beforeLastY)
                {
                    //Connect Up
                    directions.add(1);
                }
                else if (lastX > beforeLastX)
                {
                    //Connect right
                    directions.add(6);
                }
                else if (lastX < beforeLastX)
                {
                    //Connect left
                    directions.add(8);
                }
            }
            else if (nextY < lastY)
            {
                //Connect up
                if (beforeLastY == -1 || lastY < beforeLastY)
                {
                    //Connect down
                    directions.add(1);
                }
                else if (lastX > beforeLastX)
                {
                    //Connect right
                    directions.add(7);
                }
                else if (lastX < beforeLastX)
                {
                    //Connect left
                    directions.add(9);
                }
            }
            xList.add(lastX);
            yList.add(lastY);
        }
        
        if (path.length > 3)
        {
            lastX = path[path.length - 2];
            lastY = path[path.length - 1];
            beforeLastX = path[path.length - 4];
            beforeLastY = path[path.length - 3];
            
            if (beforeLastX < lastX)
            {
                directions.add(2);
            }
            else if (beforeLastX > lastX)
            {
                directions.add(3);
            }
            else if (beforeLastY < lastY)
            {
                directions.add(4);
            }
            else if (beforeLastY > lastY)
            {
                directions.add(5);
            }
            xList.add(lastX);
            yList.add(lastY);
        }
        
        parsedPath.add(xList);
        parsedPath.add(yList);
        parsedPath.add(directions);
        
        return parsedPath;
    }
    
}