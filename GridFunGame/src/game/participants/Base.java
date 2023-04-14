package game.participants;

import java.awt.Image;
import resources.ImageFinder;

public class Base {
	
	private ImageFinder image;
	
	public Base (String pictureLocation)
	{
		image = new ImageFinder (pictureLocation);
	}
	
	public Image getImage ()
	{
		return image.getImage();
	}
	
	public String getImageLocation ()
    {
        return image.getImageLocation();
    }
	
}
