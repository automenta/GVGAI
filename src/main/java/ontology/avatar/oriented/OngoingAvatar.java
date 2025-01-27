package ontology.avatar.oriented;

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
 * Time: 17:35
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class OngoingAvatar extends OrientedAvatar
{
    public OngoingAvatar(){}

    public OngoingAvatar(Vector2d position, Dimension size, SpriteContent cnt)
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
        speed = 1;
        is_oriented = true;
    }

    /**
     * This update call is for the game tick() loop.
     * @param game current state of the game.
     */
    public void updateAvatar(Game game, boolean requestInput, boolean[] actionMask)
    {
        lastMovementType = Types.MOVEMENT.MOVE;

        Direction action;

        if (requestInput || actionMask == null) {
            //Get the input from the player.
            requestPlayerInput(game);
            //Map from the action mask to a Vector2D action.
            action = Utils.processMovementActionKeys(getKeyHandler().getMask(), getPlayerID());
        } else {
            action = Utils.processMovementActionKeys(actionMask, getPlayerID());
        }

        //Update the orientation for this cycle's movement,
        // but only if there was a direction indicated.
        if(!(action.equals(Types.DNONE)))
            this._updateOrientation(action);

        //Update movement.
        super.updatePassive();
    }

    public VGDLSprite copy()
    {
        OngoingAvatar newSprite = new OngoingAvatar();
        this.copyTo(newSprite);
        return newSprite;
    }

    public void copyTo(VGDLSprite target)
    {
        OngoingAvatar targetSprite = (OngoingAvatar) target;
        super.copyTo(targetSprite);
    }
}