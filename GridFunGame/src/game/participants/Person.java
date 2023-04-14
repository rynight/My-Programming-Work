package game.participants;

import static gridgame.game.Constants.*;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import gridgame.game.MinHeap;
import resources.ImageFinder;

public class Person extends Base {
	
	private int[] stats;
	
	private int healthMax;
	
	private int[] location;
	
	private String name;
	
	private boolean isReal;
	
	private boolean isMoving;
	
	private int[] movingTo;
	
	private int[] movingPath;
	
	private int xMove;
	
	private int yMove;
	
	private int move;
	
	private int[] attacking;
	
	private ImageFinder lightImage;
	
	private ImageFinder darkImage;
	
	private ImageFinder leftImage;
	
	private ImageFinder rightImage;

	public Person(String name, String picture, int[] stats, int x, int y) {
		super(picture);
		this.stats = stats;
		this.name = name;
		
		healthMax = stats[0];
		
		location = new int[2];
		location[0] = x;
		location[1] = y;
		
		if (!picture.equals("Empty.png"))
		{
		    String name1 = picture.substring(0, picture.length() - 4);
		    String name2 = picture.substring(picture.length() - 4, picture.length());
		    
		    lightImage = new ImageFinder (name1 + "Light" + name2);
		    darkImage = new ImageFinder (name1 + "Dark.png");
		    if (this instanceof Enemy)
		    {
		        leftImage = new ImageFinder (name1 + "WalkLeft" + name2);
	            rightImage = new ImageFinder (name1 + "WalkRight" + name2);
		    }
		}
		
		isReal = true;
		
		xMove = 0;
		yMove = 0;
	}
	
	public void setNull ()
    {
	    name = "";
	    stats = null;
        isReal = false;
    }
	
	public int[] getStats ()
	{
		return stats;
	}
	
	public void changeStats (int[] stats)
    {
        this.stats = stats;
    }
	
	public int getHealthMax()
	{
	    return healthMax;
	}
	
	public String getName ()
    {
        return name;
    }
	
	public int getX ()
    {
        return location[0];
    }
	
	public void changeX (int x)
    {
        location[0] = x;
    }
	
	public int getY ()
    {
        return location[1];
    }
	
	public void changeY (int y)
    {
        location[1] = y;
    }
	
	public boolean isReal ()
    {
        return isReal;
    }
	
	public boolean getIsMoving ()
	{
	    return isMoving;
	}
	
	public void startMoving (int x, int y, Terrain[][] tmap)
	{
	    movingTo = new int[] {x, y};
	    movingPath = getShortestPath(location[0], location[1], x, y, tmap);
	    isMoving = true;
	}
	
	public void stopMoving ()
    {
	    movingPath = null;
        isMoving = false;
    }
	
	public int[] getMovingPath ()
    {
        return movingPath;
    }
	
	public void changeMovingPath (int[] newPath)
    {
        movingPath = newPath;
    }
	
	public int[] getMovingTo ()
    {
        return movingTo;
    }
	
	public void startAttacking (int[] target)
	{
	    if (target[0] != location[0])
	    {
	        move = (target[0] - location[0]) * 50;
	    }
	    else
	    {
	        move = (target[1] - location[1]) * 50;
	    }
	    attacking = target;
	}
	
	public int[] getAttacking ()
	{
	    return attacking;
	}
	
	public void stopAttacking ()
    {
        attacking = null;
    }
	
	public int getMove ()
    {
        return move;
    }
	
	public void changeMove (int move)
    {
        this.move = move;
    }
	
	/**
     * If instructions[0] == 0 no enemy in range and moves randomly
     * instructions[1] == x to move, instructions[2] == y to move
     * 
     * If instructions[0] == 1 enemy is a distance away and will move to attack them :
     * instructions[1] == x to move, instructions[2] == y to move, instructions[3] == x to attack, instructions[4] == y to attack
     * 
     * If instructions[0] == 2 enemy is a directly next to unit and unit will attack:
     * instructions[1] == x to attack, instructions[2] == y to attack
     */
    public int[] getMove (Person[][] map, Terrain[][] tmap)
    {
        ArrayList<int[]> moveRange = getMoveRangeMap (tmap, location[0], location[1], stats[2]);
        for (int[] location : moveRange)
        {
            if (map[location[0]][location[1]] instanceof Ally)
            {
                if (Math.abs(location[0] - this.location[0]) + Math.abs(location[1] - this.location[1]) == 1)
                {
                    return new int[] {2, location[0], location[1]};
                }
                
                int[] canMove = checkIfCanMove(map, location[0], location[1], moveRange);
                if (canMove[0] == 1)
                {
                    return canMove;
                }
            }
        }
        
        Collections.shuffle(moveRange);
        
        for (int[] location : moveRange)
        {
            if (!(map[location[0]][location[1]] instanceof Enemy))
            {
                return new int[] {0, location[0], location[1]};
            }
        }
        
        return null;
    }
    
    private int[] checkIfCanMove (Person[][] map, int x, int y, ArrayList<int[]> moveRange)
    {
        for (int[] location : moveRange)
        {
            if (Math.abs(location[0] - x) + Math.abs(location[1] - y) == 1)
            {
                if (!(map[location[0]][location[1]] instanceof Ally) && !(map[location[0]][location[1]] instanceof Enemy))
                {
                    return new int[] {1, location[0], location[1], x, y};
                }
            }
        }
        
        return new int[] {-1};
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

    private int[] getShortestPath (int x1, int y1, int x2, int y2, Terrain[][] graph)
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
            
            //If destination node found end search and return path used to get to this node
            int newx = path[path.length - 2];
            int newy = path[path.length - 1];
                    
            if (newx == x2 && newy == y2)
            {
                return path;
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
    
    public Image getLightImage ()
	{
	    return lightImage.getImage();
	}

    public Image getDarkImage ()
    {
        return darkImage.getImage();
    }
    
    public Image getLeftImage ()
    {
        return leftImage.getImage();
    }
    
    public Image getRightImage ()
    {
        return rightImage.getImage();
    }

    public int getXMove ()
    {
        return xMove;
    }
    
    public void changeXMove (int x)
    {
        xMove = x;
    }

    public int getYMove ()
    {
        return yMove;
    }
    
    public void changeYMove (int y)
    {
        yMove = y;
    }
}
