package tracks.ruleGeneration.randomRuleGenerator;

import core.game.GameDescription.SpriteData;
import core.game.SLDescription;
import core.generator.AbstractRuleGenerator;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

public class RuleGenerator extends AbstractRuleGenerator {
	/**
	 * Array contains all the simple interactions
	 */
	private String[] interactions = { "killSprite", "killAll", "killIfHasMore", "killIfHasLess",
			"killIfFromAbove", "killIfOtherHasMore", "spawnBehind", "stepBack", "spawnIfHasMore", "spawnIfHasLess",
			"cloneSprite", "transformTo", "undoAll", "flipDirection", "transformToRandomChild", "updateSpawnType",
			"removeScore", "addHealthPoints", "addHealthPointsToMax", "reverseDirection", "subtractHealthPoints",
			"increaseSpeedToAll", "decreaseSpeedToAll", "attractGaze", "align", "turnAround", "wrapAround",
			"pullWithIt", "bounceForward", "teleportToExit", "collectResource", "setSpeedForAll", "undoAll",
			"reverseDirection", "changeResource" };
	/**
	 * A list of all the useful sprites in the game without the avatar
	 */
	private ArrayList<String> usefulSprites;
	/**
	 * the avatar sprite name
	 */
	private String avatar;
	/**
	 * Random object to help in generation
	 */
	private Random random;
	/**
	 * Parameter used to fix the number of interations in the game
	 */
	private int FIXED = 5;

	/**
	 * This is a random rule generator
	 *
	 * @param sl
	 *            contains information about sprites and current level
	 * @param time
	 *            amount of time allowed
	 */
	public RuleGenerator(SLDescription sl, ElapsedCpuTimer time) {
		this.usefulSprites = new ArrayList<>();
		this.random = new Random();
		String[][] currentLevel = sl.getCurrentLevel();

		// Just get the useful sprites from the current level
        for (String[] strings : currentLevel) {
            for (String string : strings) {
                String[] parts = string.split(",");
                for (String part : parts) {
                    if (!part.trim().isEmpty()) {
                        // Add the sprite if it doesn't exisit
                        if (!usefulSprites.contains(part.trim())) {
                            usefulSprites.add(part.trim());
                        }
                    }
                }
            }
        }
		this.usefulSprites.add("EOS");
		this.avatar = this.getAvatar(sl);
	}

	/**
	 * convert the arraylist of string to a normal array of string
	 *
	 * @param list
	 *            input arraylist
	 * @return string array
	 */
	private String[] getArray(ArrayList<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}

	/**
	 * Get the avatar sprite from SLDescription
	 *
	 * @param sl
	 *            SLDescription object contains all the game info
	 * @return the avatar sprite name
	 */
	private String getAvatar(SLDescription sl) {
		SpriteData[] sprites = sl.getGameSprites();
        for (String usefulSprite : this.usefulSprites) {
            SpriteData s = this.getSpriteData(sprites, usefulSprite);
            if (s != null && s.isAvatar) {
                return usefulSprite;
            }
        }
		return "";
	}

	/**
	 * Get SpriteData for certain sprite name
	 *
	 * @param sprites
	 *            list of all game sprites
	 * @param name
	 *            current sprite name
	 * @return current sprite data
	 */
	private SpriteData getSpriteData(SpriteData[] sprites, String name) {
        for (SpriteData sprite : sprites) {
            if (sprite.name.equalsIgnoreCase(name)) {
                return sprite;
            }
        }

		return null;
	}

	/**
	 * Generate random interaction rules and termination conditions
	 *
	 * @param sl
	 *            contains information about sprites and current level
	 * @param time
	 *            amount of time allowed
	 */
	@Override
	public String[][] generateRules(SLDescription sl, ElapsedCpuTimer time) {
		ArrayList<String> interaction = new ArrayList<>();
		ArrayList<String> termination = new ArrayList<>();

		// number of interactions in the game based on the number of sprites
		int numberOfInteractions = (int) (this.usefulSprites.size() * (0.5 + 0.5 * this.random.nextDouble()));
		if (this.FIXED > 0) {
			numberOfInteractions = this.FIXED;
		}
		for (int i = 0; i < numberOfInteractions; i++) {
			// get two random indeces for the two sprites in the interaction
			int i1 = this.random.nextInt(this.usefulSprites.size());
			int i2 = (i1 + 1 + this.random.nextInt(this.usefulSprites.size() - 1)) % this.usefulSprites.size();
			// add score change parameter for interactions
			String scoreChange = "";
			if(this.random.nextBoolean()){
				scoreChange += "scoreChange=" + (this.random.nextInt(5) - 2);
			}
			// add the new random interaction that doesn't produce errors
			interaction.add(this.usefulSprites.get(i1) + " " + this.usefulSprites.get(i2) + " > " +
					this.interactions[this.random.nextInt(this.interactions.length)] + " " + scoreChange);
			sl.testRules(getArray(interaction), getArray(termination));
			while(!sl.getErrors().isEmpty()){
				interaction.remove(i);
				interaction.add(this.usefulSprites.get(i1) + " " + this.usefulSprites.get(i2) + " > " +
						this.interactions[this.random.nextInt(this.interactions.length)] + " " + scoreChange);
				sl.testRules(getArray(interaction), getArray(termination));
			}
		}
		
		// Add a winning termination condition
		if (this.random.nextBoolean()) {
		    termination.add("Timeout limit=" + (800 + this.random.nextInt(500)) + " win=True");
		} else {
		    String chosen = this.usefulSprites.get(this.random.nextInt(this.usefulSprites.size()));
		    sl.testRules(getArray(interaction), getArray(termination));
		    while(!sl.getErrors().isEmpty()){
			termination.remove(termination.size() - 1);
			termination.add("SpriteCounter stype=" + chosen + " limit=0 win=True");
			sl.testRules(getArray(interaction), getArray(termination));
		    }
		}
		// Add a losing termination condition
		termination.add("SpriteCounter stype=" + this.avatar + " limit=0 win=False");

		return new String[][] { getArray(interaction), getArray(termination) };
	}


}