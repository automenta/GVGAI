package tracks.levelGeneration.constraints;

import ontology.Types;
import ontology.Types.WINNER;
import tracks.levelGeneration.geneticLevelGenerator.SharedData;

public class WinConstraint extends AbstractConstraint {

	/**
	 * the winning state of the player
	 */
	public Types.WINNER bestPlayer;
	
	/**
	 * check if the player win the game
	 * @return 	1 if the automated player wins and 0 otherwise
	 */
	@Override
	public double checkConstraint() {
		double result = 0;
		if(bestPlayer == WINNER.PLAYER_WINS){
			result += 1;
		}
		if(bestPlayer == WINNER.NO_WINNER){
			result += SharedData.DRAW_FITNESS;
		}
		return result;
	}
}