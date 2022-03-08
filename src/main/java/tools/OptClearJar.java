package tools;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class OptClearJar {
    public static void main(String[] args) throws IOException {
	String dataRuns = "examples/dataRuns.txt";
	String outputPath = "outputs/";

	String[] data = new tools.IO().readFile(dataRuns);
	PrintWriter writer = new PrintWriter(dataRuns, StandardCharsets.UTF_8);
	writer.println(data[0]);
	writer.println("current runs: 0");
	writer.close();

	File[] files = new File(outputPath).listFiles();
        for (File file : files) {
            file.delete();
        }
    }
}