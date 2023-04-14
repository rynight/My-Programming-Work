package game.participants;

public class Moveable extends Terrain {

	private int moveRestrictor;
	
	public Moveable(String name, String picture, int moveRestrictor) {
		super(name, picture, moveRestrictor);
		this.moveRestrictor = moveRestrictor;
	}
	
	public int getMoveRestrictor ()
	{
		return moveRestrictor;
	}
	
}
