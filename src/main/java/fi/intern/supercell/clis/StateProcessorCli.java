package fi.intern.supercell.clis;

import fi.intern.supercell.processors.UserGraphStateProcessor;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

public class StateProcessorCli {

    private static final Option ARG_HELP = new Option("h", "help", false, "help");
    private static final Option ARG_INPUT = new Option("i", "input", true, "input");
    private static final Option ARG_CONCURRENT = new Option("c", "concurrent", false, "enable concurrent run");
    private static final UserGraphStateProcessor processor = new UserGraphStateProcessor();

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
        formatter.printUsage(writer, 100, "-c --concurrent    enable concurrent run");
        formatter.printUsage(writer, 100, "-h --help          print help");
        writer.close();
    }

    public static void main (String[] args) {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption(ARG_INPUT);
        options.addOption(ARG_HELP);
        options.addOption(ARG_CONCURRENT);

        try {
            CommandLine commandLine = parser.parse(options, args);

            // print the help
            if (commandLine.hasOption(ARG_HELP.getOpt()) || !commandLine.hasOption(ARG_INPUT.getOpt())) {
                printHelp();
                return;
            }

             String inputFile = commandLine.getOptionValue(ARG_INPUT.getOpt());

             List<String> inputLines = readFile(inputFile);
             if (commandLine.hasOption(ARG_CONCURRENT.getOpt())) {
                 processor.processConcurrent(inputLines);
             } else {
                 processor.processSequential(inputLines);
             }

        } catch (ParseException | IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
