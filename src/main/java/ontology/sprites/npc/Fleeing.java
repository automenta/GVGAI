package ontology.sprites.npc;

import core.content.SpriteContent;
import core.vgdl.VGDLSprite;
import tools.Vector2d;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 21/10/13
 * Time: 18:33
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Fleeing extends Chaser
{
    public Fleeing(){}

    public Fleeing(Vector2d position, Dimension size, SpriteContent cnt)
    {
        //Init the sprite
        this.init(position, size);

        //Specific class default parameter values.
        loadDefaults();

        //Parse the arguments.
        this.parseParameters(cnt);
    }

    protected void loadDefaults()
    {
        super.loadDefaults();
        fleeing = true;
    }

    public VGDLSprite copy()
    {
        Fleeing newSprite = new Fleeing();
        this.copyTo(newSprite);
        return newSprite;
    }

    public void copyTo(VGDLSprite target)
    {
        Fleeing targetSprite = (Fleeing) target;
        super.copyTo(targetSprite);
    }
}