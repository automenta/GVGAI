package ontology.effects.unary;

import core.content.InteractionContent;
import core.game.Game;
import core.vgdl.VGDLRegistry;
import core.vgdl.VGDLSprite;
import ontology.effects.Effect;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 23/10/13
 * Time: 15:23
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class UndoAll extends Effect
{
    /**
     * List of sprites that do NOT respond to UndoAll. This list can be specified
     * with sprite string identifiers separated by commas.
     */
    public String notStype;

    //List of IDs of the sprites not affected by UndoAll. ArrayList for efficiency.
    private ArrayList<Integer> notItypes;

    public UndoAll(InteractionContent cnt)
    {
        this.parseParameters(cnt);
        int[] notItypesArray = VGDLRegistry.GetInstance().explode(notStype);
        notItypes = new ArrayList<>();
        for(Integer it : notItypesArray)
            notItypes.add(it);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        int[] gameSpriteOrder = game.getSpriteOrder();
        int spriteOrderCount = gameSpriteOrder.length;
        for (int spriteTypeInt : gameSpriteOrder) {
            if (notItypes.contains(spriteTypeInt))
                continue;

            Iterator<VGDLSprite> spriteIt = game.getSpriteGroup(spriteTypeInt);
            if (spriteIt != null) while (spriteIt.hasNext()) {
                VGDLSprite sp = spriteIt.next();
                sp.setRect(sp.lastrect);
            }
        }
    }
}