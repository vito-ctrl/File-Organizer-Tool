import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Sorts files in a directory into category sub-folders, and can undo the last run.
 */
public class FileOrganizer {
    public static final String LOG_FILE = ".organizer-log.txt";

    private final Path root;
    private final boolean dryRun;
    private final boolean recursive;

    public FileOrganizer(Path root, boolean dryRun, boolean recursive) {
        this.root = root.toAbsolutePath().normalize();
        this.dryRun = dryRun;
        this.recursive = recursive;
    }

    /** Organizes the files. Returns the number of files moved. */
    public int organize() throws IOException {
        List<Path> files = collectFiles();
        List<String> logLines = new ArrayList<>();
        Map<FileCategory, Integer> summary = new EnumMap<>(FileCategory.class);
        int moved = 0;

        for (Path file : files) {
            FileCategory category = FileCategory.fromFileName(file.getFileName().toString());
            Path targetDir = root.resolve(category.getFolderName());
            Path target = uniqueTarget(targetDir, file.getFileName().toString());

            System.out.println((dryRun ? "[dry-run] " : "") + root.relativize(file) + "  ->  " + root.relativize(target));

            if (!dryRun) {
                Files.createDirectories(targetDir);
                Files.move(file, target);
                logLines.add(file + "\t" + target);
            }
            summary.merge(category, 1, Integer::sum);
            moved++;
        }

        if (!dryRun && !logLines.isEmpty()) {
            Files.write(root.resolve(LOG_FILE), logLines, StandardCharsets.UTF_8);
        }
        printSummary(summary, moved);
        return moved;
    }

    /** Reverts the last organize run using the log file. Returns the number of files restored. */
    public int undo() throws IOException {
        Path log = root.resolve(LOG_FILE);
        if (!Files.exists(log)) {
            System.out.println("Nothing to undo: no log file found in " + root);
            return 0;
        }
        List<String> lines = Files.readAllLines(log, StandardCharsets.UTF_8);
        Collections.reverse(lines);
        int restored = 0;
        for (String line : lines) {
            String[] parts = line.split("\t");
            if (parts.length != 2) continue;
            Path original = Paths.get(parts[0]);
            Path current = Paths.get(parts[1]);
            if (!Files.exists(current)) {
                System.out.println("Skipped (missing): " + current);
                continue;
            }
            Path restoreTo = Files.exists(original) ? uniqueTarget(original.getParent(), original.getFileName().toString()) : original;
            if (!dryRun) {
                Files.createDirectories(restoreTo.getParent());
                Files.move(current, restoreTo);
            }
            System.out.println((dryRun ? "[dry-run] " : "") + "Restored: " + root.relativize(restoreTo));
            restored++;
        }
        if (!dryRun) {
            Files.delete(log);
            removeEmptyCategoryFolders();
        }
        System.out.println("\nRestored " + restored + " file(s).");
        return restored;
    }

    private List<Path> collectFiles() throws IOException {
        List<Path> result = new ArrayList<>();
        int depth = recursive ? Integer.MAX_VALUE : 1;
        try (Stream<Path> stream = Files.walk(root, depth)) {
            stream.filter(Files::isRegularFile)
                  .filter(p -> !p.getFileName().toString().startsWith("."))   // skip hidden files + log
                  .filter(p -> !isAlreadyOrganized(p))
                  .sorted()
                  .forEach(result::add);
        }
        return result;
    }

    /** A file sitting directly inside a category folder (e.g. root/Images/a.jpg) is already organized. */
    private boolean isAlreadyOrganized(Path file) {
        Path parent = file.getParent();
        return parent != null
                && parent.getParent() != null
                && parent.getParent().equals(root)
                && FileCategory.isCategoryFolder(parent.getFileName().toString());
    }

    /** If a file with this name exists, append (1), (2)... so nothing is overwritten. */
    private Path uniqueTarget(Path dir, String fileName) {
        Path target = dir.resolve(fileName);
        if (!Files.exists(target)) return target;

        int dot = fileName.lastIndexOf('.');
        String base = dot > 0 ? fileName.substring(0, dot) : fileName;
        String ext = dot > 0 ? fileName.substring(dot) : "";
        int counter = 1;
        while (Files.exists(target)) {
            target = dir.resolve(base + " (" + counter++ + ")" + ext);
        }
        return target;
    }

    private void removeEmptyCategoryFolders() throws IOException {
        for (FileCategory category : FileCategory.values()) {
            Path dir = root.resolve(category.getFolderName());
            if (Files.isDirectory(dir)) {
                try (Stream<Path> s = Files.list(dir)) {
                    if (s.findAny().isEmpty()) Files.delete(dir);
                }
            }
        }
    }

    private void printSummary(Map<FileCategory, Integer> summary, int total) {
        System.out.println("\n--- Summary ---");
        if (total == 0) {
            System.out.println("No files to organize.");
            return;
        }
        summary.forEach((cat, n) -> System.out.println(String.format("%-14s %d", cat.getFolderName(), n)));
        System.out.println("Total: " + total + (dryRun ? " (nothing was moved)" : " moved"));
    }
}
