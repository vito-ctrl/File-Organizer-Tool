import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0 || hasFlag(args, "--help") || hasFlag(args, "-h")) {
            printUsage();
            return;
        }

        Path dir = Paths.get(args[0]);
        if (!Files.isDirectory(dir)) {
            System.err.println("Error: '" + args[0] + "' is not a valid directory.");
            System.exit(1);
        }

        boolean dryRun = hasFlag(args, "--dry-run");
        boolean recursive = hasFlag(args, "--recursive");
        boolean undo = hasFlag(args, "--undo");

        FileOrganizer organizer = new FileOrganizer(dir, dryRun, recursive);
        try {
            if (undo) {
                organizer.undo();
            } else {
                organizer.organize();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static boolean hasFlag(String[] args, String flag) {
        for (String a : args) {
            if (a.equalsIgnoreCase(flag)) return true;
        }
        return false;
    }

    private static void printUsage() {
        System.out.println("File Organizer Tool");
        System.out.println("Usage: java Main <directory> [options]\n");
        System.out.println("Options:");
        System.out.println("  --dry-run    Show what would happen without moving anything");
        System.out.println("  --recursive  Also organize files inside sub-folders");
        System.out.println("  --undo       Revert the last organize run in this directory");
        System.out.println("  --help, -h   Show this message");
    }
}
