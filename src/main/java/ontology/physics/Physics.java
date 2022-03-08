package ontology.physics;

import core.vgdl.VGDLSprite;
import ontology.Types;
import tools.Direction;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 17/10/13
 * Time: 11:21
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public interface Physics
{
    Types.MOVEMENT passiveMovement(VGDLSprite sprite);
    Types.MOVEMENT activeMovement(VGDLSprite sprite, Direction action, double speed);
    double distance(Rectangle r1, Rectangle r2);
}