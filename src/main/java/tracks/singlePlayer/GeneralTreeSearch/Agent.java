// Code written by Wells Lucas Santo

package tracks.singlePlayer.GeneralTreeSearch;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {

    GTSAlgo algo;

    // Initialize the Generic Tree Search, manually set which file to read from
    public Agent (StateObservation so, ElapsedCpuTimer elapsedTimer) {
        // String filename = "TSDLAlgorithms/BFS.txt";
        // String filename = "TSDLAlgorithms/DFS.txt";
        // String filename = "TSDLAlgorithms/Astar.txt";
        String filename = "TSDLAlgorithms/MCTS.txt";
        algo = new GTSAlgo(filename);
    }

    // Initialize the Generic Tree Search based on the file that's passed in
    public Agent (StateObservation so, ElapsedCpuTimer elapsedTimer, String filename) {
        algo = new GTSAlgo(filename);
    }

    // Called at each step, will return the action to perform
    public Types.ACTIONS act (StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return algo.eval(stateObs, elapsedTimer);
    }
}