package com.filecli;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

public class FileCLI {

    // ── Colores ANSI ──────────────────────────────────────────────────
    static final String RESET  = "\033[0m";
    static final String BOLD   = "\033[1m";
    static final String RED    = "\033[31m";
    static final String GREEN  = "\033[32m";
    static final String YELLOW = "\033[33m";
    static final String BLUE   = "\033[34m";
    static final String CYAN   = "\033[36m";
    static final String WHITE  = "\033[37m";
    static final String PURPLE = "\033[35m";

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String command = args[0].toLowerCase();
        String path    = args.length > 1 ? args[1] : ".";

        switch (command) {
            case "ls"       -> listFiles(path, false);
            case "ls-l"     -> listFiles(path, true);
            case "stats"    -> showStats(path);
            case "tree"     -> printTree(Path.of(path), "", true);
            case "find"     -> {
                if (args.length < 3) { System.out.println(RED + "Uso: find <directorio> <patrón>" + RESET); }
                else findFiles(path, args[2]);
            }
            case "organize" -> organizeFiles(path);
            case "top"      -> topFiles(path);
            case "help"     -> printHelp();
            default         -> {
                System.out.println(RED + "Comando desconocido: " + command + RESET);
                printHelp();
            }
        }
    }

    // ── ls / ls-l ─────────────────────────────────────────────────────
    static void listFiles(String pathStr, boolean detailed) {
        File dir = new File(pathStr);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println(RED + "No es un directorio válido: " + pathStr + RESET);
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println(YELLOW + "(directorio vacío)" + RESET);
            return;
        }

        Arrays.sort(files, Comparator.comparing(File::isFile).thenComparing(File::getName));

        System.out.println(BOLD + BLUE + "\n📁 " + dir.getAbsolutePath() + RESET);
        System.out.println(WHITE + "─".repeat(60) + RESET);

        if (detailed) {
            System.out.printf(BOLD + "%-6s %-12s %-20s %s%n" + RESET,
                    "Tipo", "Tamaño", "Modificado", "Nombre");
            System.out.println(WHITE + "─".repeat(60) + RESET);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (File f : files) {
            String icon  = f.isDirectory() ? "📂" : iconForFile(f.getName());
            String color = f.isDirectory() ? CYAN : colorForFile(f.getName());
            String name  = f.getName() + (f.isDirectory() ? "/" : "");

            if (detailed) {
                String type = f.isDirectory() ? "DIR" : "FILE";
                String size = f.isDirectory() ? "-" : formatSize(f.length());
                String date = sdf.format(new Date(f.lastModified()));
                System.out.printf("%-6s %-12s %-20s %s%s%s%s%n",
                        color + type + RESET, size, date,
                        color + icon + " ", name, RESET, "");
            } else {
                System.out.print(color + icon + " " + name + RESET + "   ");
            }
        }
        if (!detailed) System.out.println();

        long total = Arrays.stream(files).mapToLong(f -> f.isFile() ? f.length() : 0).sum();
        System.out.println(WHITE + "─".repeat(60) + RESET);
        System.out.printf(YELLOW + "  %d elementos  |  Tamaño total: %s%n" + RESET,
                files.length, formatSize(total));
    }

    // ── stats ─────────────────────────────────────────────────────────
    static void showStats(String pathStr) {
        File dir = new File(pathStr);
        if (!dir.exists()) { System.out.println(RED + "Ruta no encontrada." + RESET); return; }

        Map<String, Long[]> extMap = new TreeMap<>();
        long[] totals = {0, 0}; // {archivos, bytes}

        try {
            Files.walk(dir.toPath())
                 .filter(Files::isRegularFile)
                 .forEach(p -> {
                     String ext = getExtension(p.getFileName().toString()).toUpperCase();
                     if (ext.isEmpty()) ext = "(sin extensión)";
                     long size = p.toFile().length();
                     extMap.computeIfAbsent(ext, k -> new Long[]{0L, 0L});
                     extMap.get(ext)[0]++;
                     extMap.get(ext)[1] += size;
                     totals[0]++;
                     totals[1] += size;
                 });
        } catch (IOException e) { System.out.println(RED + "Error: " + e.getMessage() + RESET); return; }

        System.out.println(BOLD + PURPLE + "\n Estadísticas de: " + dir.getAbsolutePath() + RESET);
        System.out.println(WHITE + "─".repeat(60) + RESET);
        System.out.printf(BOLD + "%-20s %8s %15s %10s%n" + RESET,
                "Extensión", "Archivos", "Tamaño total", "% del dir");
        System.out.println(WHITE + "─".repeat(60) + RESET);

        extMap.entrySet().stream()
              .sorted((a, b) -> Long.compare(b.getValue()[1], a.getValue()[1]))
              .forEach(e -> {
                  double pct = totals[1] == 0 ? 0 : (e.getValue()[1] * 100.0 / totals[1]);
                  String bar = "█".repeat((int)(pct / 5));
                  System.out.printf(GREEN + "%-20s" + RESET + " %8d %15s  %s%.1f%%%n",
                          e.getKey(), e.getValue()[0],
                          formatSize(e.getValue()[1]), YELLOW + bar + RESET + " ", pct);
              });

        System.out.println(WHITE + "─".repeat(60) + RESET);
        System.out.printf(BOLD + "TOTAL: %d archivos  |  %s%n" + RESET,
                totals[0], formatSize(totals[1]));
    }

    // ── tree ─────────────────────────────────────────────────────────
    static void printTree(Path path, String prefix, boolean isRoot) {
        if (isRoot) System.out.println(BOLD + BLUE + "📁 " + path.getFileName() + RESET);
        File dir = path.toFile();
        File[] files = dir.listFiles();
        if (files == null) return;
        Arrays.sort(files, Comparator.comparing(File::isFile).thenComparing(File::getName));
        for (int i = 0; i < files.length; i++) {
            boolean last  = (i == files.length - 1);
            String branch = last ? "└── " : "├── ";
            String child  = last ? "    " : "│   ";
            File f = files[i];
            if (f.isDirectory()) {
                System.out.println(prefix + branch + CYAN + "📂 " + f.getName() + "/" + RESET);
                printTree(f.toPath(), prefix + child, false);
            } else {
                String color = colorForFile(f.getName());
                System.out.println(prefix + branch + color + iconForFile(f.getName()) + " " + f.getName() + RESET);
            }
        }
    }

    // ── find ──────────────────────────────────────────────────────────
    static void findFiles(String pathStr, String pattern) {
        System.out.println(BOLD + CYAN + "\n🔍 Buscando \"" + pattern + "\" en " + pathStr + RESET);
        System.out.println(WHITE + "─".repeat(60) + RESET);
        String glob = "*" + pattern + "*";
        try {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
            long[] count = {0};
            Files.walk(Path.of(pathStr))
                 .filter(p -> matcher.matches(p.getFileName()))
                 .forEach(p -> {
                     count[0]++;
                     String color = Files.isDirectory(p) ? CYAN : colorForFile(p.getFileName().toString());
                     String icon  = Files.isDirectory(p) ? "📂" : iconForFile(p.getFileName().toString());
                     System.out.println(color + icon + " " + p + RESET);
                 });
            System.out.println(WHITE + "─".repeat(60) + RESET);
            System.out.println(YELLOW + count[0] + " resultado(s) encontrado(s)" + RESET);
        } catch (IOException e) { System.out.println(RED + "Error: " + e.getMessage() + RESET); }
    }

    // ── organize ──────────────────────────────────────────────────────
    static void organizeFiles(String pathStr) {
        File dir = new File(pathStr);
        if (!dir.isDirectory()) { System.out.println(RED + "No es un directorio." + RESET); return; }
        File[] files = dir.listFiles(File::isFile);
        if (files == null || files.length == 0) { System.out.println(YELLOW + "No hay archivos para organizar." + RESET); return; }

        System.out.println(BOLD + GREEN + "\n📦 Organizando archivos en: " + dir.getAbsolutePath() + RESET);
        System.out.println(WHITE + "─".repeat(60) + RESET);

        Map<String, String> categoryMap = new LinkedHashMap<>();
        categoryMap.put("Imágenes",    "jpg,jpeg,png,gif,bmp,svg,webp,ico,tiff");
        categoryMap.put("Documentos",  "pdf,doc,docx,xls,xlsx,ppt,pptx,odt,txt,md,csv");
        categoryMap.put("Videos",      "mp4,mkv,avi,mov,wmv,flv,webm");
        categoryMap.put("Audio",       "mp3,wav,flac,aac,ogg,m4a");
        categoryMap.put("Código",      "java,py,js,ts,html,css,c,cpp,h,go,rs,sh,json,xml,yaml,yml");
        categoryMap.put("Comprimidos", "zip,tar,gz,bz2,7z,rar,xz");
        categoryMap.put("Otros",       "");

        int moved = 0;
        for (File f : files) {
            String ext = getExtension(f.getName()).toLowerCase();
            String category = "Otros";
            for (Map.Entry<String, String> e : categoryMap.entrySet()) {
                if (Arrays.asList(e.getValue().split(",")).contains(ext)) {
                    category = e.getKey();
                    break;
                }
            }
            File destDir = new File(dir, category);
            destDir.mkdirs();
            File dest = new File(destDir, f.getName());
            if (f.renameTo(dest)) {
                System.out.println(GREEN + "  ✔ " + RESET + f.getName() + "  →  " + CYAN + category + "/" + RESET);
                moved++;
            } else {
                System.out.println(RED + "  ✘ No se pudo mover: " + f.getName() + RESET);
            }
        }
        System.out.println(WHITE + "─".repeat(60) + RESET);
        System.out.println(YELLOW + moved + " archivo(s) organizados." + RESET);
    }

    // ── top (archivos más grandes) ────────────────────────────────────
    static void topFiles(String pathStr) {
        System.out.println(BOLD + RED + "\n🏆 Top 10 archivos más grandes en: " + pathStr + RESET);
        System.out.println(WHITE + "─".repeat(60) + RESET);
        try {
            Files.walk(Path.of(pathStr))
                 .filter(Files::isRegularFile)
                 .sorted((a, b) -> Long.compare(b.toFile().length(), a.toFile().length()))
                 .limit(10)
                 .forEach(p -> {
                     String size  = formatSize(p.toFile().length());
                     String color = colorForFile(p.getFileName().toString());
                     System.out.printf("  %s%-12s%s  %s%s%n",
                             YELLOW, size, RESET, color, p + RESET);
                 });
        } catch (IOException e) { System.out.println(RED + "Error: " + e.getMessage() + RESET); }
    }

    // ── help ──────────────────────────────────────────────────────────
    static void printHelp() {
        System.out.println(BOLD + CYAN + """

        ╔══════════════════════════════════════════╗
        ║         FileCLI — Herramienta de         ║
        ║        Gestión de Archivos en Java        ║
        ╚══════════════════════════════════════════╝
        """ + RESET);
        System.out.println(BOLD + "Uso: filecli <comando> [ruta] [opciones]" + RESET);
        System.out.println();
        System.out.printf("  %sls%s       <ruta>          Listar archivos%n",        GREEN, RESET);
        System.out.printf("  %sls-l%s     <ruta>          Listar con detalles%n",    GREEN, RESET);
        System.out.printf("  %sstats%s    <ruta>          Estadísticas por extensión%n", GREEN, RESET);
        System.out.printf("  %stree%s     <ruta>          Árbol de directorios%n",   GREEN, RESET);
        System.out.printf("  %sfind%s     <ruta> <patrón> Buscar archivos%n",        GREEN, RESET);
        System.out.printf("  %sorganize%s <ruta>          Organizar por categoría%n",GREEN, RESET);
        System.out.printf("  %stop%s      <ruta>          Top 10 archivos más grandes%n", GREEN, RESET);
        System.out.printf("  %shelp%s                     Mostrar esta ayuda%n",     GREEN, RESET);
        System.out.println();
    }

    // ── utilidades ───────────────────────────────────────────────────
    static String getExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }

    static String iconForFile(String name) {
        String ext = getExtension(name).toLowerCase();
        return switch (ext) {
            case "jpg","jpeg","png","gif","bmp","svg","webp" -> "🖼️";
            case "pdf"                                       -> "📄";
            case "doc","docx"                               -> "📝";
            case "xls","xlsx","csv"                         -> "📊";
            case "ppt","pptx"                               -> "📽️";
            case "mp4","mkv","avi","mov"                    -> "🎬";
            case "mp3","wav","flac","ogg"                   -> "🎵";
            case "zip","tar","gz","7z","rar"                -> "🗜️";
            case "java","py","js","ts","c","cpp","go","rs"  -> "💻";
            case "sh","bash"                                -> "⚙️";
            case "json","xml","yaml","yml"                  -> "🔧";
            case "txt","md"                                 -> "📃";
            case "html","css"                               -> "🌐";
            default                                         -> "📎";
        };
    }

    static String colorForFile(String name) {
        String ext = getExtension(name).toLowerCase();
        return switch (ext) {
            case "jpg","jpeg","png","gif","bmp","svg"        -> PURPLE;
            case "pdf","doc","docx","txt","md"               -> WHITE;
            case "mp4","mkv","avi","mov"                     -> YELLOW;
            case "mp3","wav","flac","ogg"                    -> CYAN;
            case "zip","tar","gz","7z","rar"                 -> RED;
            case "java","py","js","ts","c","cpp","go","rs",
                 "sh","json","xml","yaml","html","css"        -> GREEN;
            default                                          -> WHITE;
        };
    }

    static String formatSize(long bytes) {
        if (bytes < 1024)       return bytes + " B";
        if (bytes < 1048576)    return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1073741824) return String.format("%.1f MB", bytes / 1048576.0);
        return                         String.format("%.2f GB", bytes / 1073741824.0);
    }
}
