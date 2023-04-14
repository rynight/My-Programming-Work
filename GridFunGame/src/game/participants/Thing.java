package game.participants;

public class Thing extends Terrain {
	
	private int[] stats;
	
	public Thing (String name, String picture, int[] stats, int moveRestrictor)
	{
		super(name, picture, moveRestrictor);
		this.stats = stats;
	}
	
	public int[] getStats ()
	{
		return stats;
	}
	
}
