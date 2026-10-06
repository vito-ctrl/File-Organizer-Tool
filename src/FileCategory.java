import java.util.Arrays;
import java.util.List;

/**
 * File categories and the extensions that belong to each one.
 */
public enum FileCategory {
    IMAGES("Images", "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "tiff", "ico", "heic"),
    DOCUMENTS("Documents", "pdf", "doc", "docx", "txt", "rtf", "odt", "md", "epub", "ppt", "pptx"),
    SPREADSHEETS("Spreadsheets", "xls", "xlsx", "csv", "ods", "tsv"),
    VIDEOS("Videos", "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm"),
    AUDIO("Audio", "mp3", "wav", "flac", "aac", "ogg", "m4a", "wma"),
    ARCHIVES("Archives", "zip", "rar", "7z", "tar", "gz", "bz2", "xz"),
    CODE("Code", "java", "py", "js", "ts", "c", "cpp", "h", "cs", "go", "rs", "html", "css", "json", "xml", "sh", "sql", "class"),
    INSTALLERS("Installers", "exe", "msi", "deb", "rpm", "dmg", "apk", "iso"),
    OTHERS("Others");

    private final String folderName;
    private final List<String> extensions;

    FileCategory(String folderName, String... extensions) {
        this.folderName = folderName;
        this.extensions = Arrays.asList(extensions);
    }

    public String getFolderName() {
        return folderName;
    }

    /** Returns the category for a file name, based on its extension. */
    public static FileCategory fromFileName(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot <= 0 || dot == fileName.length() - 1) {
            return OTHERS;
        }
        String ext = fileName.substring(dot + 1).toLowerCase();
        for (FileCategory category : values()) {
            if (category.extensions.contains(ext)) {
                return category;
            }
        }
        return OTHERS;
    }

    /** True if the given folder name is one of the category folders. */
    public static boolean isCategoryFolder(String name) {
        for (FileCategory category : values()) {
            if (category.folderName.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
