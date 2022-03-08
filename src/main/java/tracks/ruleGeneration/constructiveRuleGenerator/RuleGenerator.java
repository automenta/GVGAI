package tracks.ruleGeneration.constructiveRuleGenerator;

import core.game.GameDescription.SpriteData;
import core.game.SLDescription;
import core.generator.AbstractRuleGenerator;
import tools.ElapsedCpuTimer;
import tools.LevelAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This is a constructive rule generator it depends 
 * @author AhmedKhalifa
 */
public class RuleGenerator extends AbstractRuleGenerator{
	/**
	 * a Level Analyzer object used to analyze the game sprites
	 */
	private LevelAnalyzer la;

	/**
	 * array of different interactions that movable objects (contains also npcs) can do when hitting the walls
	 */
	private String[] movableWallInteraction = {"stepBack", "flipDirection", "reverseDirection",
			"turnAround", "wrapAround"};

	/**
	 * percentages used to decide
	 */
	private double wallPercentageProb = 0.5;
	private double spikeProb = 0.5;
	private double doubleNPCsProb = 0.5;
	private double harmfulMovableProb = 0.5;
	private double firewallProb = 0.1;
	private double scoreSpikeProb = 0.1;
	private double randomNPCProb = 0.5;
	private double spawnedProb = 0.5;
	private double bomberProb = 0.5;

	/**
	 * a list of suggested interactions for the generated game
	 */
	private ArrayList<String> interactions;
	/**
	 * a list of suggested temination conditions for the generated game
	 */
	private ArrayList<String> terminations;

	/**
	 * the sprite that the generator think is a wall sprite
	 */
	private SpriteData wall;
	/**
	 * array of all door sprites
	 */
	private ArrayList<SpriteData> exit;
	/**
	 * array of all collectible sprites
	 */
	private ArrayList<String> collectible;
	/**
	 * a certain unmovable object that is used as a collectible object
	 */
	private SpriteData score;
	/**
	 * a certain unmovable object that is used as a spike object
	 */
	private SpriteData spike;

	/**
	 * random object used in generating different games
	 */
	private Random random;

	/**
	 * Array of all different types of harmful objects (can kill the player)
	 */
	private ArrayList<String> harmfulObjects;
	/**
	 * Array of all different types of fleeing NPCs
	 */
	private ArrayList<String> fleeingNPCs;

	/**
	 * Constructor that initialize the constructive algorithm
	 * @param sl	SLDescription object contains information about the
	 * 			current game and level
	 * @param time	the amount of time allowed for initialization
	 */
	public RuleGenerator(SLDescription sl, ElapsedCpuTimer time){
		//Initialize everything
		la = new LevelAnalyzer(sl);

		interactions = new ArrayList<>();
		terminations = new ArrayList<>();

		random = new Random();
		harmfulObjects = new ArrayList<>();
		fleeingNPCs = new ArrayList<>();
		collectible = new ArrayList<>();

		//Identify the wall object
		wall = null;
		SpriteData[] temp = la.getBorderObjects((1.0 * la.getPerimeter()) / la.getArea(), this.wallPercentageProb);
		if(temp.length > 0){
			wall = temp[0];
			for (SpriteData spriteData : temp) {
				if (la.getNumberOfObjects(spriteData.name) < la.getNumberOfObjects(wall.name)) {
					wall = spriteData;
				}
			}
		}

		//identify the exit sprite
		exit = new ArrayList<>();
		temp = la.getPortals(true);
		for (SpriteData data : temp) {
			if (!data.type.equalsIgnoreCase("portal")) {
				exit.add(data);
			}
		}

		//identify the score and spike sprites
		ArrayList<SpriteData> tempList = new ArrayList<>();
		score = null;
		spike = null;
		temp = la.getImmovables(1, (int)(scoreSpikeProb * la.getArea()));
		if (temp.length > 0) {
			if (wall == null) {
				score = temp[random.nextInt(temp.length)];
				spike = temp[random.nextInt(temp.length)];
			}
			else {
				tempList = new ArrayList<>();
				SpriteData[] relatedSprites = la.getSpritesOnSameTile(wall.name);
				for (SpriteData spriteData : temp) {
					for (SpriteData relatedSprite : relatedSprites) {
						if (!spriteData.name.equals(relatedSprite.name)) {
							tempList.add(spriteData);
						}
					}
					if (relatedSprites.length == 0) {
						tempList.add(spriteData);
					}
				}

				score = tempList.get(random.nextInt(tempList.size()));
				spike = tempList.get(random.nextInt(tempList.size()));
			}
		}
	}

	/**
	 * Check if this spritename is the avatar
	 * @param spriteName	the input sprite name
	 * @return			true if its the avatar or false otherwise
	 */
	private boolean isAvatar(String spriteName){
		SpriteData[] avatar = la.getAvatars(false);
		for (SpriteData spriteData : avatar) {
			if (spriteData.equals(spriteName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get the interactions of everything with wall sprites
	 */
	private void getWallInteractions(){
		String wallName = "EOS";
		if (wall != null) {
			wallName = wall.name;
		}
		//Is walls acts like fire (harmful for everyone)
		boolean isFireWall = this.random.nextDouble() < firewallProb &&
				wall != null && fleeingNPCs.isEmpty();

		//Avatar interaction with wall or EOS
		String action = "stepBack";
		if(isFireWall){
			action = "killSprite";
		}
		SpriteData[] temp = la.getAvatars(false);
		for (SpriteData value : temp) {
			interactions.add(value.name + " " + wallName + " > " + action);
		}

		//Get the interaction between all movable objects (including npcs) with wall or EOS
		action = movableWallInteraction[random.nextInt(movableWallInteraction.length)];
		if(isFireWall){
			action = "killSprite";
		}
		temp = la.getMovables(false);
		for (SpriteData data : temp) {
			interactions.add(data.name + " " + wallName + " > " + action);
		}
		action = movableWallInteraction[random.nextInt(movableWallInteraction.length)];
		if(isFireWall){
			action = "killSprite";
		}
		temp = la.getNPCs(false);
		for (SpriteData spriteData : temp) {
			interactions.add(spriteData.name + " " + wallName + " > " + action);
		}
	}

	/**
	 * get the interactions of all sprites with resource sprites
	 */
	private void getResourceInteractions(){
		SpriteData[] avatar = la.getAvatars(false);
		SpriteData[] resources = la.getResources(true);

		//make the avatar collect the resources
		for (SpriteData spriteData : avatar) {
			for (SpriteData resource : resources) {
				interactions.add(resource.name + " " + spriteData.name + " > collectResource");
			}
		}
	}

	/**
	 * get the interactions of all sprites with spawner sprites
	 */
	private void getSpawnerInteractions(){
		SpriteData[] avatar = la.getAvatars(false);
		SpriteData[] spawners = la.getSpawners(true);

		//make the spawned object harmful to the avatar with a chance to be useful
		if(random.nextDouble() < spawnedProb){
			for (SpriteData spriteData : avatar) {
				for (SpriteData spawner : spawners) {
					for (int k = 0; k < spawner.sprites.size(); k++) {
						harmfulObjects.add(spawner.sprites.get(k));
						interactions.add(spriteData.name + " " + spawner.sprites.get(k) + " > killSprite");
					}
				}
			}
		}
		else{
			for (SpriteData spriteData : avatar) {
				for (SpriteData spawner : spawners) {
					for (int k = 0; k < spawner.sprites.size(); k++) {
						if (!harmfulObjects.contains(spawner.sprites.get(k))) {
							collectible.add(spawner.sprites.get(k));
							interactions.add(spawner.sprites.get(k) + " " + spriteData.name + " > killSprite scoreChange=1");
						}
					}
				}
			}
		}

		for (SpriteData spriteData : spawners) {
			for (int k = 0; k < spriteData.sprites.size(); k++) {
				if (harmfulObjects.contains(spriteData.sprites.get(k))) {
					harmfulObjects.add(spriteData.name);
					break;
				}
			}
		}

		for (SpriteData spawner : spawners) {
			for (int k = 0; k < spawner.sprites.size(); k++) {
				if (collectible.contains(spawner.sprites.get(k))) {
					collectible.add(spawner.name);
					break;
				}
			}
		}
	}

	/**
	 * get the interactions of all sprites with immovable sprites
	 */
	private void getImmovableInteractions(){
		SpriteData[] avatar = la.getAvatars(false);

		//If we have a score object make the avatar can collect it
		if(score != null){
			for (SpriteData spriteData : avatar) {
				collectible.add(score.name);
				interactions.add(score.name + " " + spriteData.name + " > killSprite scoreChange=1");
			}
		}

		//If we have a spike object make it kill the avatar with a change to be a super collectible sprite
		if (spike != null && !spike.name.equalsIgnoreCase(score.name)) {
			if (random.nextDouble() < spikeProb) {
				harmfulObjects.add(spike.name);
				for (SpriteData spriteData : avatar) {
					interactions.add(spriteData.name + " " + spike.name + " > killSprite");
				}
			}
			else {
				for (SpriteData spriteData : avatar) {
					collectible.add(spike.name);
					interactions.add(spike.name + " " + spriteData.name + " > killSprite scoreChange=2");
				}
			}
		}
	}

	/**
	 * get the interactions of all sprites with avatar sprites
	 */
	private void getAvatarInteractions(){
		SpriteData[] avatar = la.getAvatars(false);

		//Kill the avatar bullet kill any harmful objects
		for (SpriteData spriteData : avatar) {
			for (String harmfulObject : harmfulObjects) {
				for (int k = 0; k < spriteData.sprites.size(); k++) {
					interactions.add(harmfulObject + " " + spriteData.sprites.get(k) + " > killSprite scoreChange=1");
					interactions.add(spriteData.sprites.get(k) + " " + harmfulObject + " > killSprite");
				}
			}
		}
	}

	/**
	 * get the interactions of all sprites with portal sprites
	 */
	private void getPortalInteractions() {
		SpriteData[] avatar = la.getAvatars(false);
		SpriteData[] portals = la.getPortals(true);

		//make the exits die with collision of the player (going through them)
		for (SpriteData data : avatar) {
			for (SpriteData spriteData : exit) {
				interactions.add(spriteData.name + " " + data.name + " > killSprite");
			}
		}
		//If they are Portal type then u can teleport toward it
		for (SpriteData portal : portals) {
			for (SpriteData spriteData : avatar) {
				if (portal.type.equalsIgnoreCase("Portal")) {
					interactions.add(spriteData.name + " " + portal.name + " > teleportToExit");
				}
			}
		}
	}

	/**
	 * get the interactions of all sprites with npc sprites
	 */
	private void getNPCInteractions(){
		SpriteData[] avatar = la.getAvatars(false);
		SpriteData[] npc = la.getNPCs(false);

		for (SpriteData data : npc) {
			//If its fleeing object make it useful
			if (data.type.equalsIgnoreCase("fleeing")) {
				for (int j = 0; j < data.sprites.size(); j++) {
					fleeingNPCs.add(data.sprites.get(j));
					interactions.add(data.name + " " + data.sprites.get(j) + " > killSprite scoreChange=1");
				}
			} else if (data.type.equalsIgnoreCase("bomber") || data.type.equalsIgnoreCase("randombomber")) {
				//make the bomber harmful for the player
				for (SpriteData spriteData : avatar) {
					harmfulObjects.add(data.name);
					interactions.add(spriteData.name + " " + data.name + " > killSprite");
				}
				//make the spawned object harmful
				if (this.random.nextDouble() < bomberProb) {
					for (int j = 0; j < data.sprites.size(); j++) {
						harmfulObjects.add(data.sprites.get(j));
						interactions.add(avatar[j].name + " " + data.sprites.get(j) + " > killSprite");
					}
				}
				//make the spawned object useful
				else {
					for (int j = 0; j < data.sprites.size(); j++) {
						interactions.add(data.sprites.get(j) + " " + avatar[j].name + " > killSprite scoreChange=1");
					}
				}
			} else if (data.type.equalsIgnoreCase("chaser") || data.type.equalsIgnoreCase("AlternateChaser")
					|| data.type.equalsIgnoreCase("RandomAltChaser")) {
				//make chasers harmful for the avatar
				for (int j = 0; j < data.sprites.size(); j++) {
					if (isAvatar(data.sprites.get(j))) {
						for (SpriteData spriteData : avatar) {
							harmfulObjects.add(data.name);
							interactions.add(spriteData.name + " " + data.name + " > killSprite");
						}
					} else {
						if (random.nextDouble() < doubleNPCsProb) {
							interactions.add(data.sprites.get(j) + " " + data.name + " > killSprite");
						} else {
							interactions.add(data.sprites.get(j) + " " + data.name + " > transformTo stype=" + data.name);
						}

					}
				}
			} else if (data.type.equalsIgnoreCase("randomnpc")) {
				//random npc are harmful to the avatar
				if (this.random.nextDouble() < randomNPCProb) {
					for (SpriteData spriteData : avatar) {
						harmfulObjects.add(data.name);
						interactions.add(spriteData.name + " " + data.name + " > killSprite");
					}
				}
				//random npc are userful to the avatar
				else {
					for (SpriteData spriteData : avatar) {
						collectible.add(data.name);
						interactions.add(data.name + " " + spriteData.name + " > killSprite scoreChange=1");
					}
				}
			}
		}
	}

	/**
	 * get the interactions of all sprites with movable sprites
	 */
	private void getMovableInteractions(){
		SpriteData[] movables = la.getMovables(false);
		SpriteData[] avatar = la.getAvatars(false);
		SpriteData[] spawners = la.getSpawners(false);

		for (SpriteData movable : movables) {
			//Check if the movable object is not avatar or spawned child
			boolean found = false;
			for (SpriteData data : avatar) {
				if (data.sprites.contains(movable.name)) {
					found = true;
				}
			}
			for (SpriteData spawner : spawners) {
				if (spawner.sprites.contains(movable.name)) {
					found = true;
				}
			}
			if (!found) {
				//Either make them harmful or useful
				if (random.nextDouble() < harmfulMovableProb) {
					for (SpriteData spriteData : avatar) {
						harmfulObjects.add(movable.name);
						interactions.add(spriteData.name + " " + movable.name + " > killSprite");
					}
				} else {
					for (SpriteData spriteData : avatar) {
						collectible.add(movable.name);
						interactions.add(movable.name + " " + spriteData.name + " > killSprite scoreChange=1");
					}
				}
			}
		}
	}

	/**
	 * get the termination condition for the generated game
	 */
	private void getTerminations(){
		//If you have a door object make it the winning condition
		if(!exit.isEmpty()){
			SpriteData door = null;
			for (SpriteData spriteData : exit) {
				if (spriteData.type.equalsIgnoreCase("door")) {
					door = spriteData;
					break;
				}
			}

			if(door != null){
				terminations.add("SpriteCounter stype=" + door.name + " limit=0 win=True");
			}
			//otherwise pick any other exit object
			else if(!collectible.isEmpty()){
				terminations.add("SpriteCounter stype=collectible limit=0 win=True");
			}
		}
		else {
			//If we have feeling NPCs use them as winning condition
			if (!fleeingNPCs.isEmpty()) {
				terminations.add("SpriteCounter stype=fleeing limit=0 win=True");
			}
			else if(!harmfulObjects.isEmpty() && !this.la.getAvatars(true)[0].sprites.isEmpty()){
				terminations.add("SpriteCounter stype=harmful limit=0 win=True");
			}
			//Otherwise use timeout as winning condition
			else {
				terminations.add("Timeout limit=" + (500 + random.nextInt(5) * 100) + " win=True");
			}
		}

		//Add the losing condition which is the player dies
		if(!harmfulObjects.isEmpty()){
			SpriteData[] usefulAvatar = this.la.getAvatars(true);
			for (SpriteData spriteData : usefulAvatar) {
				terminations.add("SpriteCounter stype=" + spriteData.name + " limit=0 win=False");
			}
		}
	}

    
    /**
     * get the generated interaction rules and termination rules
     * @param sl	SLDescription object contain information about the game
     * 			sprites and the current level
     * @param time	the amount of time allowed for the rule generator
     * @return		two arrays the first contains the interaction rules
     * 			while the second contains the termination rules
     */
    @Override
    public String[][] generateRules(SLDescription sl, ElapsedCpuTimer time) {
	this.interactions.clear();
	this.terminations.clear();
	this.collectible.clear();
	this.harmfulObjects.clear();
	this.fleeingNPCs.clear();
	
	this.getResourceInteractions();
	this.getImmovableInteractions();
	this.getNPCInteractions();
	this.getSpawnerInteractions();
	this.getPortalInteractions();
	this.getMovableInteractions();
	this.getWallInteractions();
	this.getAvatarInteractions();
	
	this.getTerminations();
	
	return new String[][]{interactions.toArray(new String[0]), terminations.toArray(new String[0])};
    }
    
    @Override
    public HashMap<String, ArrayList<String>> getSpriteSetStructure() {
        HashMap<String, ArrayList<String>> struct = new HashMap<>();
        HashMap<String, Boolean> testing = new HashMap<>();
        
        if(!fleeingNPCs.isEmpty()){
            struct.put("fleeing", new ArrayList<>());
        }
		for (String fleeingNPC : this.fleeingNPCs) {
			if (!testing.containsKey(fleeingNPC)) {
				testing.put(fleeingNPC, true);
				struct.get("fleeing").add(fleeingNPC);
			}
		}
        
        if(!harmfulObjects.isEmpty()){
            struct.put("harmful", new ArrayList<>());
        }
		for (String harmfulObject : this.harmfulObjects) {
			if (!testing.containsKey(harmfulObject)) {
				testing.put(harmfulObject, true);
				struct.get("harmful").add(harmfulObject);
			}
		}
        if(!collectible.isEmpty()){
            struct.put("collectible", new ArrayList<>());
        }
		for (String s : this.collectible) {
			if (!testing.containsKey(s)) {
				testing.put(s, true);
				struct.get("collectible").add(s);
			}
		}
        
        return struct;
    }

}