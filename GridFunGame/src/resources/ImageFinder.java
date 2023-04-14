package resources;

import java.awt.Image;
import java.net.URL;
import javax.swing.*; 

/**
 * Demonstrates how to put sound files into a project so that they will be included when the project is exported, and
 * demonstrates how to play sounds.
 * 
 * @author Ryan Mark
 */
public class ImageFinder
{
    
    /** A Clip that, when played, sounds like a weapon being fired */
    private Image image;
    
    private String location;

    /**
     * Creates the demo.
     */
    public ImageFinder (String location)
    {
        this.location = location;
        
        
        URL url = ImageFinder.class.getResource(location);
        ImageIcon icon = new ImageIcon(url);
        
        image = icon.getImage();
    }
    
    public Image getImage ()
    {
        return image;
    }
    
    public String getImageLocation()
    {
        return location;
    }
}
