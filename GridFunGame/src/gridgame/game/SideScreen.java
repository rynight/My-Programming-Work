package gridgame.game;

import static gridgame.game.Constants.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import game.participants.Ally;
import game.participants.Enemy;
import game.participants.Person;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class SideScreen extends JPanel
{
    /** Game controller */
    private Controller controller;
    
    private ParticipantHolder game;
    
    private Graphics2D g;

    /**
     * Creates an empty screen
     */
    public SideScreen (Controller controller, ParticipantHolder game)
    {
        this.controller = controller;
        this.game = game;
        
        setPreferredSize(new Dimension(SPECIALWIDTH, HSIZE));
        setMinimumSize(new Dimension(SPECIALWIDTH, HSIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
    }
    
    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);
        
        if (controller.getCharacter() != null)
        {
            g.setColor(BROWN);
            g.fillRect(0, 0, SPECIALWIDTH, HSIZE);
            
            if (controller.checkSelected() != null)
            {
                Person p = game.getPerson(controller.getX(), controller.getY());
                
                if (p instanceof Ally)
                {
                    g.setColor(Color.BLUE);
                    g.fillRect(0, 2 * HSIZE / 3, SPECIALWIDTH, HSIZE - (2 * HSIZE / 3));
                    
                    fillTerrainBackground(false, game.getTerrain(p.getX(), p.getY()).getImage());
                    
                    g.setColor(new Color (55, 55, 55));
                    g.fillRect(0, HSIZE / 3, SPECIALWIDTH, 2 * HSIZE / 3 - HSIZE / 3);
                    
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                    g.setColor(Color.WHITE);
                    
                    int[] stats = p.getStats();
                    g.drawString("HP " + stats[0], 5, 2 * HSIZE / 3 + 60);
                    g.drawString("Atk " + stats[1], 5, 2 * HSIZE / 3 + 80);
                    g.drawString("Mov " + stats[2], 5, 2 * HSIZE / 3 + 100);
                    
                    g.drawString(p.getName().substring(0, p.getName().length() - 1), 100, 2 * HSIZE / 3 + 30);
                    
                    g.drawString(game.getTerrain(p.getX(), p.getY()).getTileName(), 100, 2 * HSIZE / 3 + 140);
                    
                    g.drawImage(p.getImage(), 150, 2 * HSIZE / 3 + 60, null);
                }
                else if (p instanceof Enemy)
                {
                    g.setColor(Color.RED);
                    g.fillRect(0, 2 * HSIZE / 3, SPECIALWIDTH, HSIZE - (2 * HSIZE / 3));
                    
                    fillTerrainBackground(false, game.getTerrain(p.getX(), p.getY()).getImage());
                    
                    g.setColor(new Color (55, 55, 55));
                    g.fillRect(0, HSIZE / 3, SPECIALWIDTH, 2 * HSIZE / 3 - HSIZE / 3);
                    
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                    g.setColor(Color.WHITE);
                    
                    int[] stats = p.getStats();
                    g.drawString("HP " + stats[0], 5, 2 * HSIZE / 3 + 60);
                    g.drawString("Atk " + stats[1], 5, 2 * HSIZE / 3 + 80);
                    g.drawString("Mov " + stats[2], 5, 2 * HSIZE / 3 + 100);
                    
                    g.drawString(p.getName().substring(0, p.getName().length() - 1), 100, 2 * HSIZE / 3 + 30);
                    
                    g.drawString(game.getTerrain(p.getX(), p.getY()).getTileName(), 100, 2 * HSIZE / 3 + 140);
                    
                    g.drawImage(p.getImage(), 150, 2 * HSIZE / 3 + 60, null);
                }
                else
                {
                    g.setColor(BROWN);
                    g.fillRect(0, 2 * HSIZE / 3, SPECIALWIDTH, HSIZE - (2 * HSIZE / 3));
                    
                    fillTerrainBackground(false, game.getTerrain(p.getX(), p.getY()).getImage());
                    
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                    g.setColor(Color.WHITE);
                    g.drawString(game.getTerrain(controller.getX(), controller.getY()).getTileName(), 100, 2 * HSIZE / 3 + 140);
                }
                
                g.setColor(Color.BLUE);
                g.fillRect(0, 0, SPECIALWIDTH, HSIZE / 3);
                
                Person selected = controller.checkSelected();
                
                fillTerrainBackground(true, game.getTerrain(selected.getX(), selected.getY()).getImage());
                
                g.setColor(new Color (55, 55, 55));
                g.fillRect(0, HSIZE / 3, SPECIALWIDTH, 2 * HSIZE / 3 - HSIZE / 3);
                
                g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                g.setColor(Color.WHITE);
                
                int[] stats = selected.getStats();
                g.drawString("HP " + stats[0], 5, 60);
                g.drawString("Atk " + stats[1], 5, 80);
                g.drawString("Mov " + stats[2], 5, 100);
                
                g.drawString(selected.getName().substring(0, selected.getName().length() - 1), 100, 30);
                
                g.drawString(game.getTerrain(selected.getX(), selected.getY()).getTileName(), 100, 140);
                
                g.drawImage(selected.getImage(), 150, 60, null);
            }
            else
            {
                Person p = game.getPerson(controller.getX(), controller.getY());
                if (p instanceof Ally)
                {
                    g.setColor(Color.BLUE);
                    g.fillRect(0, 0, SPECIALWIDTH, HSIZE / 3);
                }
                else if (p instanceof Enemy)
                {
                    g.setColor(Color.RED);
                    g.fillRect(0, 0, SPECIALWIDTH, HSIZE / 3);
                }
                else
                {
                    g.setColor(BROWN);
                    g.fillRect(0, 0, SPECIALWIDTH, HSIZE / 3);
                }
                
                fillTerrainBackground(true, game.getTerrain(p.getX(), p.getY()).getImage());
                fillTerrainBackground(false, game.getTerrain(p.getX(), p.getY()).getImage());
                
                g.setColor(new Color (55, 55, 55));
                g.fillRect(0, HSIZE / 3, SPECIALWIDTH, 2 * HSIZE / 3 - HSIZE / 3);
                
                g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                g.setColor(Color.WHITE);
                
                if (p.isReal())
                {
                    int[] stats = p.getStats();
                    g.drawString("HP " + stats[0], 5, 60);
                    g.drawString("Atk " + stats[1], 5, 80);
                    g.drawString("Mov " + stats[2], 5, 100);
                    
                    g.drawString(p.getName().substring(0, p.getName().length() - 1), 100, 30);
                }
                
                g.drawString(game.getTerrain(p.getX(), p.getY()).getTileName(), 100, 140);
                
                g.drawImage(p.getImage(), 150, 60, null);
            }
        }
    }
    
    private void fillTerrainBackground (boolean top, Image image)
    {
        for (int i = 0; i < SPECIALWIDTH / SQUARESIZE + 1; i++)
        {
            for (int j = 0; j < HSIZE / SQUARESIZE / 3 + 1; j++)
            {
                if (top)
                {
                    g.drawImage(image, i * SQUARESIZE, j * SQUARESIZE, null);
                }
                else
                {
                    g.drawImage(image, i * SQUARESIZE, 2 * HSIZE / 3 + j * SQUARESIZE, null);
                }
            }
        }
    }
}
