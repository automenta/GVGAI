package tools;

import core.competition.CompetitionParameters;
import core.optimization.OptimizationObjective;
import core.optimization.ucbOptimization.UCBEvoEquation;
import core.optimization.ucbOptimization.UCBOptimization;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class OptEvaluateJar {
    private static void writeOuputs(String outputPath, int currentRuns, double[] values)
			throws IOException {
	if (values == null) {
	    PrintWriter writer = new PrintWriter(outputPath + "output" + currentRuns + ".txt", StandardCharsets.UTF_8);
	    writer.println("FALSE");
	    writer.close();
	    return;
	}
	PrintWriter writer = new PrintWriter(outputPath + "output" + currentRuns + ".txt", StandardCharsets.UTF_8);
	writer.println("TRUE");
		for (double value : values) {
			writer.println(1 - value);
		}
	writer.close();
    }

    public static void main(String[] args) throws IOException {
	String gamesPath = "examples/gridphysics/";
	String parameterFilePath = "examples/parameters.txt";
	String dataRuns = "examples/dataRuns.txt";
	String outputPath = "outputs/";

	String[] engineParameters = new tools.IO().readFile(parameterFilePath);
	ArrayList<String> games = new ArrayList<>();
	ArrayList<String> levels = new ArrayList<>();
	ArrayList<String> uniqueGames = new ArrayList<>();
	ArrayList<Integer> numLevels = new ArrayList<>();
		for (String engineParameter : engineParameters) {
			String type = engineParameter.split(":")[0].toLowerCase().trim();
			String value = engineParameter.split(":")[1].toLowerCase().trim();
			switch (type) {
				case "game" -> {
					String[] parts = value.split(",");
					uniqueGames.add(gamesPath + parts[0].trim() + ".txt");
					numLevels.add(parts.length - 1);
					for (int j = 1; j < parts.length; j++) {
						games.add(gamesPath + parts[0].trim() + ".txt");
						levels.add(gamesPath + parts[0].trim() + "_lvl" + parts[j].trim() + ".txt");
					}
				}
				case "maxsteps" ->
					//CompetitionParameters.MAX_TIMESTEPS = Integer.parseInt(value);
						System.out.println("Warning: CompetitionParameters.MAX_TIMESTEPS was not changed.");
				case "repetitions" ->
					//CompetitionParameters.OPTIMIZATION_REPEATITION = Integer.parseInt(value);
						System.out.println("Warning: CompetitionParameters.OPTIMIZATION_REPEATITION was not changed.");
				case "evaluations" ->
					//CompetitionParameters.OPTIMIZATION_EVALUATION = Integer.parseInt(value);
						System.out.println("Warning: CompetitionParameters.OPTIMIZATION_EVALUATION was not changed.");
				case "safetymargin" ->
						tracks.singlePlayer.tools.ucbOptimizerAgent.Agent.safetyMargin = Integer.parseInt(value);
				case "scoretowin" -> UCBOptimization.SCORE_WIN = Double.parseDouble(value);
				case "sigmoidwidth" -> UCBOptimization.SIGMOID_WIDTH = Double.parseDouble(value);
				case "sigmoidshift" -> UCBOptimization.SIGMOID_SHIFT = Double.parseDouble(value);
				case "randomseed" -> {
					int temp = Integer.parseInt(value);
					if (temp < 0) {
						temp = new Random().nextInt();
					}
					UCBOptimization.RANDOM_OBJ = temp;
				}
			}
		}

	String[] data = new tools.IO().readFile(dataRuns);
	int currentRuns = Integer.parseInt(data[1].split(":")[1].trim());
	currentRuns += 1;
	if (currentRuns <= CompetitionParameters.OPTIMIZATION_EVALUATION) {
	    PrintWriter writer = new PrintWriter(dataRuns, StandardCharsets.UTF_8);
	    writer.println(data[0]);
	    writer.println("current runs: " + currentRuns);
	    writer.close();
	} else {
	    writeOuputs(outputPath, currentRuns, null);
	    return;
	}

	// run optimization process on ucb equation for an MCTS player
	double[] parameters = new double[args.length];
	for (int i = 0; i < args.length; i++) {
	    parameters[i] = 2 * Double.parseDouble(args[i]) - 1;
	}

	String[] tempGames = new String[games.size()];
	String[] tempLevels = new String[levels.size()];
	for (int i = 0; i < games.size(); i++) {
	    tempGames[i] = games.get(i);
	    tempLevels[i] = levels.get(i);
	}

	OptimizationObjective obj = new UCBOptimization(tempGames, tempLevels,
		CompetitionParameters.OPTIMIZATION_REPEATITION, CompetitionParameters.OPTIMIZATION_EVALUATION,
		new UCBEvoEquation());
	if(parameters.length < obj.getNumberOfParameters()){
	    writeOuputs(outputPath, currentRuns, null);
	    return;
	}
	double[] results = obj.evaluate(parameters);
	ArrayList<Double> correctResults = new ArrayList<>();
	int index = 0;
	for (int i = 0; i < uniqueGames.size(); i++) {
	    correctResults.add(0.0);
	    for (int j = 0; j < numLevels.get(i); j++) {
		correctResults.set(i, correctResults.get(i) + results[index]);
		index += 1;
	    }
	}
	results = new double[uniqueGames.size()];
	for (int i = 0; i < results.length; i++) {
	    results[i] = correctResults.get(i);
	}
	writeOuputs(outputPath, currentRuns, results);
    }
}