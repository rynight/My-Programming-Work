package sounds;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.sound.sampled.*;

/**
 * Demonstrates how to put sound files into a project so that they will be included when the project is exported, and
 * demonstrates how to play sounds.
 * 
 * @author Ryan Mark
 */
public class SoundDemo
{

    private HashMap<String, Clip> clipMap;
    
    /** A Clip that, when played, sounds like a weapon being fired */
    private Clip playClip;
    
    private String location;

    /**
     * Creates the demo.
     */
    public SoundDemo ()
    {
        clipMap = new HashMap<String, Clip> ();
    }

    /**
     * Creates an audio clip from a sound file.
     */
    public void playClip (String name)
    {
        name = "/sounds/" + name;
        location = name;
        if (clipMap.get(name) != null)
        {
            playClip(clipMap.get(name));
            
        }
        else
        {
            playClip = createClip(name);
            clipMap.put(name, playClip);
            playClip(playClip);
        }
    }
    
    private void playClip (Clip clip)
    {
        if (clip.isRunning())
        {
            playClip(createClip(getFileName()));
            return;
        }
        clip.setFramePosition(0);
        clip.start();
    }
    
    public String getFileName()
    {
        return location;
    }
    
    /**
     * Creates an audio clip from a sound file.
     */
    private Clip createClip (String soundFile)
    {
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            return clip;
        }
        catch (LineUnavailableException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (UnsupportedAudioFileException e)
        {
            return null;
        }
    }
}
