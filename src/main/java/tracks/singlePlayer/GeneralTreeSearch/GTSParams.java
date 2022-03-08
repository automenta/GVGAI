// Code written by Wells Lucas Santo
package tracks.singlePlayer.GeneralTreeSearch;

public class GTSParams {
    public enum EXPLORATION     { FIRST, HIGH, LOW, LAST, UCT }

    public enum EXPANSION       { ALL, FIRST, RANDOM }

    public enum REMOVAL         { YES, NO }

    public enum SIMULATION      { RANDOM, NONE }

    public enum EVALUATION      { POINTS, WIN, WINLOSSPOINTS, DISTANCE }

    public enum BACKPROPAGATION { HIGH, LOW, INCREMENT }

    public enum SELECTION       { HIGH, LOW, MOSTVISITS }
}