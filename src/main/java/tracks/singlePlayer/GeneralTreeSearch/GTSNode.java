// Code written by Wells Lucas Santo
package tracks.singlePlayer.GeneralTreeSearch;

import core.game.StateObservation;
import ontology.Types;

// Nodes in the decision tree
public class GTSNode {

    // Based on the parameters needed for the SampleMCTS SingleTreeNode
    public GTSNode (StateObservation s, GTSNode par) {
        thisState = s;
        totalValue = 0;
        visitCount = 0;
        parent = par;
        children = new GTSNode [thisState.getAvailableActions().size()];
        if (par != null)
            depth = par.depth+1;
        else
            depth = 0;
    }

    public Types.ACTIONS getAction () {
        return thisState.getAvailableActions().get(0);
    }

    public int getVisitCount () {
        return visitCount;
    }

    public void incrementVisitCount () {
        visitCount++;
    }

    public int getDepth () {
        return depth;
    }

    public void setRewardMax (double reward) {
        if (reward > totalValue) totalValue = reward;
    }

    public void setRewardMin (double reward) {
        if (reward < totalValue) totalValue = reward;
    }

    public double getReward () {
        return totalValue;
    }

    public void incrementReward (double reward) {
        totalValue += reward;
    }

    public StateObservation thisState;
    double totalValue;
    int visitCount;
    GTSNode parent;
    public GTSNode[] children;
    int depth;
}