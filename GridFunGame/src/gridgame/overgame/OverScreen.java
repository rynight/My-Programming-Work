package gridgame.overgame;

import static gridgame.game.Constants.*;
import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import game.participants.Base;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class OverScreen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    /** Game controller */
    private OverController controller;

    /**
     * Creates an empty screen
     */
    public OverScreen (OverController controller)
    {
        this.controller = controller;
        legend = "";
        setPreferredSize(new Dimension(WSIZE, HSIZE));
        setMinimumSize(new Dimension(WSIZE, HSIZE));
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
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);
        
        
        Base TitleCard = new Base("GSoldier.png");
        if (TitleCard.getImage() != null)
        {
            g.drawImage(TitleCard.getImage(), 100, 250, null);
        }
        else
        {
            g.drawRect(100, 100, 100, 100);
        }
        
        
        g.setFont(new Font("Serif", Font.PLAIN, 70));
        
        g.drawString("Grid Fun Murder Game", 50, 100);
        
        g.setFont(new Font("Serif", Font.PLAIN, 30));
        
        g.drawString("Space - Select Unit/Move/Attack", 250, 230);
        g.drawString("Delete - Unselect Unit", 250, 280);
        g.drawString("WASD/Arrow Keys - Move Cursor", 250, 330);
        
        g.setFont(new Font("TimesRoman", Font.PLAIN, 45));
        
        g.drawString("Start Game", 100, 500);
        g.drawString("End Game", 100, 600);
        
        // Update info section
        if (controller.getY() == 0)
        {
            g.drawRect(95, 460, 215, 50);
        }
        else
        {
            g.drawRect(95, 560, 200, 50);
        }

        // Draw the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (WSIZE - size) / 2, HSIZE / 2);
    }
}
