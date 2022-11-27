package fi.intern.supercell.clis;

import fi.intern.supercell.processors.UserGraphProcessor;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

/**
 * Command line interface for problem 1 solver
 */
public class NotificationProcessorCli {

    private static final Option ARG_HELP = new Option("h", "help", false, "help");
    private static final Option ARG_INPUT = new Option("i", "input", true, "input");
    private static final UserGraphProcessor processor = new UserGraphProcessor();

    /**
     * Reads file
     *
     * @param filename file name
     */
    private static List<String> readFile (String filename) throws IOException {
        File inputFile = new File(filename);
        return Files.readAllLines(inputFile.toPath());
    }

    /**
     * Prints help
     */
    private static void printHelp () {
        HelpFormatter formatter = new HelpFormatter();
        PrintWriter writer = new PrintWriter(System.out);

        writer.println("User graph state processor cli manual");
        writer.println();
        formatter.printUsage(writer, 100, "java -jar UserGraphStateCli.jar -i inputFile");
        formatter.printUsage(writer, 100, "-i --input         provide input file");
        formatter.printUsage(writer, 100, "-h --help          print help");
        writer.close();
    }

    public static void main (String[] args) {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption(ARG_INPUT);
        options.addOption(ARG_HELP);

        try {
            CommandLine commandLine = parser.parse(options, args);

            // print the help
            if (commandLine.hasOption(ARG_HELP.getOpt()) || !commandLine.hasOption(ARG_INPUT.getOpt())) {
                printHelp();
                return;
            }

             String inputFile = commandLine.getOptionValue(ARG_INPUT.getOpt());

             List<String> inputLines = readFile(inputFile);
             processor.process(inputLines);

        } catch (ParseException | IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
