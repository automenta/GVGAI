package ontology.sprites.missile;

import core.content.SpriteContent;
import core.game.Game;
import core.vgdl.VGDLSprite;
import ontology.Types;
import tools.Direction;
import tools.Utils;
import tools.Vector2d;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 21/10/13
 * Time: 18:18
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class RandomMissile extends Missile
{
    public RandomMissile(){}

    public RandomMissile(Vector2d position, Dimension size, SpriteContent cnt)
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
        orientation = Types.DNIL;
    }

    public void update(Game game)
    {
        if(orientation.equals(Types.DNIL))
        {
            orientation = (Direction) Utils.choice(Types.DBASEDIRS, game.getRandomGenerator());
        }

        this.updatePassive();
    }

    public VGDLSprite copy()
    {
        RandomMissile newSprite = new RandomMissile();
        this.copyTo(newSprite);
        return newSprite;
    }

    public void copyTo(VGDLSprite target)
    {
        RandomMissile targetSprite = (RandomMissile) target;
        super.copyTo(targetSprite);
    }
}