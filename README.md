# 📁 File Organizer Tool

A small command-line tool written in **Java** that cleans up messy folders by sorting files into category sub-folders (Images, Documents, Videos, and so on) based on their extension. It supports a dry-run preview, recursive scanning, and undoing the last run.

## Features

- **Automatic sorting** into `Images`, `Documents`, `Spreadsheets`, `Videos`, `Audio`, `Archives`, `Code`, `Installers`, and `Others`
- **Dry-run mode** to preview changes without touching any file
- **Recursive mode** to also organize files inside sub-folders
- **Undo** to revert the last run
- **Safe by design**: never overwrites files (duplicates become `name (1).ext`), skips hidden files, and ignores files that are already organized
- No external dependencies, just the JDK

## Requirements

- Java 17 or newer (JDK, for compiling)

## Build

```bash
git clone https://github.com/<your-username>/File-Organizer-Tool.git
cd File-Organizer-Tool
mkdir out
javac -d out src/*.java
```

## Usage

```bash
java -cp out Main <directory> [options]
```

| Option        | Description                                          |
|---------------|------------------------------------------------------|
| `--dry-run`   | Show what would happen without moving anything       |
| `--recursive` | Also organize files inside sub-folders               |
| `--undo`      | Revert the last organize run in that directory       |
| `--help`, `-h`| Show usage information                               |

### Examples

```bash
# Preview first (recommended)
java -cp out Main ~/Downloads --dry-run

# Organize the Downloads folder
java -cp out Main ~/Downloads

# Organize including sub-folders
java -cp out Main ~/Downloads --recursive

# Changed your mind? Undo it
java -cp out Main ~/Downloads --undo
```

### Sample output

```
photo.jpg  ->  Images/photo.jpg
report.pdf  ->  Documents/report.pdf
song.mp3  ->  Audio/song.mp3

--- Summary ---
Images         1
Documents      1
Audio          1
Total: 3 moved
```

## Categories

| Folder         | Extensions (examples)                         |
|----------------|-----------------------------------------------|
| Images         | jpg, png, gif, webp, svg, heic                |
| Documents      | pdf, docx, txt, md, pptx, epub                |
| Spreadsheets   | xlsx, xls, csv, ods                           |
| Videos         | mp4, mkv, avi, mov, webm                      |
| Audio          | mp3, wav, flac, aac, ogg                      |
| Archives       | zip, rar, 7z, tar, gz                         |
| Code           | java, py, js, ts, html, css, json, sql        |
| Installers     | exe, msi, deb, dmg, apk, iso                  |
| Others         | everything else                               |

To add or change categories, edit `src/FileCategory.java`.

## How it works

1. `Main` parses the command-line arguments.
2. `FileOrganizer` collects the files, picks a category with `FileCategory`, and moves each file with `java.nio.file.Files.move`.
3. Every move is written to a hidden `.organizer-log.txt` file in the target folder, which `--undo` reads to put everything back.

## Project structure

```
File-Organizer-Tool/
├── src/
│   ├── Main.java            # CLI entry point
│   ├── FileOrganizer.java   # Organize and undo logic
│   └── FileCategory.java    # Categories and extension mapping
├── .gitignore
├── LICENSE
└── README.md
```

## Notes

- Only the most recent run can be undone, since each run replaces the log file.
- Hidden files (names starting with `.`) are never moved.

## Ideas for the future

- Sort by date (e.g. `Images/2026/05/`)
- Custom categories through a config file
- A simple GUI

## License

Released under the [MIT License](LICENSE).
