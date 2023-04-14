package game.participants;

public class Terrain extends Base {
	
    private String tileName;
    
    private int moveRestrictor;
    
	public Terrain(String name, String picture, int moveRestrictor) {
		super(picture);
		this.tileName = name;
		this.moveRestrictor = moveRestrictor;
	}
	
	public String getTileName ()
    {
        return tileName;
    }
	
	public int getMoveRestrictor ()
	{
	    return moveRestrictor;
	}
}
