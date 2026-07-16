package dev.gravesown.launcher;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/** Windows-first development launcher for the Gravesown ModDev client. */
public final class GravesownLauncher {
    private static final Color BONE = new Color(0xD7, 0xE6, 0xF2);
    private static final Color MUTED = new Color(0x8F, 0xAF, 0xC8);
    private static final Color ACID = new Color(0x4C, 0xA8, 0xE8);
    private static final Color ACID_HOVER = new Color(0x78, 0xC5, 0xF5);
    private static final Color SIGNAL_YELLOW = new Color(0xE6, 0xC8, 0x4F);
    private static final Color BLOOD = new Color(0xC7, 0x65, 0x70);
    private static final Color PANEL = new Color(0x10, 0x24, 0x3B, 230);
    private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ExecutorService worker = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "gravesown-launcher-worker");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicBoolean processRunning = new AtomicBoolean(false);
    private final Path projectRoot;
    private final BufferedImage background;
    private final BufferedImage icon;
    private final JFrame frame = new JFrame("Gravesown: After the Silence");
    private final JTextArea progressLog = new JTextArea();
    private final JLabel statusLabel = new JLabel();
    private final StatusLight statusLight = new StatusLight();
    private final ActionButton playButton = new ActionButton(tr("ИГРАТЬ", "PLAY"), true);
    private final ActionButton verifyButton = new ActionButton(tr("ПРОВЕРИТЬ", "VERIFY"), false);
    private final ActionButton logsButton = new ActionButton(tr("ПАПКА ЛОГОВ", "LOGS FOLDER"), false);

    private GravesownLauncher() {
        projectRoot = locateProjectRoot();
        background = loadBackground(projectRoot);
        icon = loadImage(projectRoot, "launcher_icon.png");
    }

    public static void main(String[] args) {
        if (args.length == 1 && "--diagnose".equals(args[0])) {
            Path root = locateProjectRoot();
            if (root == null) {
                System.err.println("GRAVESOWN_LAUNCHER_DIAGNOSTIC status=FAIL reason=project_root_not_found");
                System.exit(2);
            }
            boolean scriptPresent = Files.isRegularFile(root.resolve("scripts").resolve("bootstrap-and-play.ps1"));
            boolean backgroundPresent = Files.isRegularFile(root.resolve("launcher").resolve("assets").resolve("launcher_background.png"));
            System.out.println("GRAVESOWN_LAUNCHER_DIAGNOSTIC status=PASS root=" + root
                    + " run_client=" + scriptPresent + " background=" + backgroundPresent);
            return;
        }
        if ((args.length == 2 || args.length == 4) && "--render-preview".equals(args[0])) {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            try {
                int previewWidth = args.length == 4 ? Integer.parseInt(args[2]) : 1180;
                int previewHeight = args.length == 4 ? Integer.parseInt(args[3]) : 760;
                SwingUtilities.invokeAndWait(() -> new GravesownLauncher().renderPreview(
                        Paths.get(args[1]),
                        previewWidth,
                        previewHeight
                ));
                System.out.println("GRAVESOWN_LAUNCHER_PREVIEW status=PASS path=" + Paths.get(args[1]).toAbsolutePath());
                System.exit(0);
            } catch (Exception exception) {
                System.err.println("GRAVESOWN_LAUNCHER_PREVIEW status=FAIL reason=" + exception.getMessage());
                System.exit(3);
            }
        }
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("Label.font", UI_FONT);
                UIManager.put("Button.font", UI_FONT);
            } catch (Exception ignored) {
                // The custom controls do not require a particular installed look and feel.
            }
            new GravesownLauncher().show();
        });
    }

    private void show() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1100, 700));
        frame.setSize(1180, 760);
        if (icon != null) frame.setIconImage(icon);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(buildContent());
        frame.setVisible(true);

        if (projectRoot == null) {
            setStatus(tr("ПРОЕКТ НЕ НАЙДЕН", "PROJECT NOT FOUND"), BLOOD);
            playButton.setEnabled(false);
            verifyButton.setEnabled(false);
            appendLog(tr(
                    "Не найден корень проекта. Запускай launcher.cmd из папки Gravesown.",
                    "Project root not found. Run launcher.cmd from the Gravesown folder."
            ));
        } else {
            setStatus(tr("ГОТОВ К ЗАПУСКУ", "READY TO LAUNCH"), ACID);
            appendLog(tr("Корень проекта: ", "Project root: ") + projectRoot);
            appendLog(background == null
                    ? tr("Фоновый PNG не найден — включён фирменный градиент.",
                            "Background PNG is missing; using the built-in gradient.")
                    : tr("Фон launcher/assets/launcher_background.png загружен.",
                            "Loaded launcher/assets/launcher_background.png."));
            appendLog(tr(
                    "Клиент использует существующий ModDev/Gradle-кэш; лаунчер не скачивает Minecraft сам.",
                    "The client reuses the existing ModDev/Gradle cache; the launcher does not download Minecraft."
            ));
        }
    }

    private JPanel buildContent() {
        BackdropPanel root = new BackdropPanel(background);
        root.setLayout(new BorderLayout(0, 18));
        root.setBorder(new EmptyBorder(34, 48, 26, 48));
        root.add(buildSimpleHeader(), BorderLayout.NORTH);
        root.add(buildSimpleHero(), BorderLayout.CENTER);
        root.add(buildSimpleFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildSimpleHeader() {
        JPanel titles = transparent();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel product = label("GRAVESOWN", 34, Font.BOLD, BONE);
        JLabel subtitle = label("AFTER THE SILENCE", 13, Font.BOLD, ACID);
        product.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        titles.add(product);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);
        return titles;
    }

    private JPanel buildSimpleHero() {
        JPanel hero = transparent(new GridBagLayout());
        RoundedPanel actions = new RoundedPanel(new Color(0x07, 0x14, 0x23, 218), new Color(0x4C, 0xA8, 0xE8, 198), 24);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBorder(new EmptyBorder(24, 26, 22, 26));

        playButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        playButton.setPreferredSize(new Dimension(340, 72));
        playButton.setMaximumSize(new Dimension(340, 72));
        playButton.addActionListener(this::play);
        actions.add(playButton);
        actions.add(Box.createVerticalStrut(13));

        JPanel utility = transparent();
        utility.setLayout(new BoxLayout(utility, BoxLayout.X_AXIS));
        utility.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        verifyButton.setPreferredSize(new Dimension(162, 40));
        verifyButton.setMaximumSize(new Dimension(162, 40));
        verifyButton.addActionListener(this::verify);
        logsButton.setPreferredSize(new Dimension(162, 40));
        logsButton.setMaximumSize(new Dimension(162, 40));
        logsButton.addActionListener(this::openLogs);
        utility.add(verifyButton);
        utility.add(Box.createHorizontalStrut(12));
        utility.add(logsButton);
        actions.add(utility);
        hero.add(actions);
        return hero;
    }

    private JPanel buildSimpleFooter() {
        RoundedPanel footer = new RoundedPanel(new Color(0x07, 0x14, 0x23, 236), new Color(0x3F, 0x63, 0x85, 210), 22);
        footer.setLayout(new BorderLayout());
        footer.setBorder(new EmptyBorder(12, 14, 12, 14));
        footer.setPreferredSize(new Dimension(800, 142));

        progressLog.setEditable(false);
        progressLog.setRows(4);
        progressLog.setLineWrap(true);
        progressLog.setWrapStyleWord(true);
        progressLog.setFont(new Font("Cascadia Mono", Font.PLAIN, 11));
        progressLog.setForeground(BONE);
        progressLog.setBackground(new Color(0x07, 0x14, 0x23));
        progressLog.setCaretColor(ACID);
        progressLog.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(progressLog);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x29, 0x47, 0x64), 1));
        scroll.getViewport().setBackground(progressLog.getBackground());
        footer.add(scroll, BorderLayout.CENTER);
        return footer;
    }

    /** Produces a no-game, no-visible-window composition preview for CI and agents. */
    private void renderPreview(Path output, int width, int height) {
        if (width < 900 || height < 620) {
            throw new IllegalArgumentException("preview dimensions are below the supported launcher layout");
        }
        setStatus(tr("ГОТОВ К ЗАПУСКУ", "READY TO LAUNCH"), ACID);
        JPanel content = buildContent();
        content.setSize(width, height);
        layoutRecursively(content);
        BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = preview.createGraphics();
        try {
            content.printAll(graphics);
        } finally {
            graphics.dispose();
        }
        try {
            Path parent = output.toAbsolutePath().normalize().getParent();
            if (parent != null) Files.createDirectories(parent);
            ImageIO.write(preview, "png", output.toFile());
        } catch (IOException exception) {
            throw new IllegalStateException("could not write launcher preview", exception);
        }
    }

    private static void layoutRecursively(Container container) {
        container.doLayout();
        for (java.awt.Component child : container.getComponents()) {
            if (child instanceof Container nested) {
                layoutRecursively(nested);
            }
        }
    }

    private JPanel buildHeader() {
        JPanel header = transparent(new BorderLayout(18, 0));
        header.add(new BrandMark(), BorderLayout.WEST);

        JPanel titles = transparent();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel product = label("GRAVESOWN", 23, Font.BOLD, BONE);
        JLabel subtitle = label("AFTER THE SILENCE  /  DEVELOPMENT CLIENT", 11, Font.BOLD, MUTED);
        titles.add(product);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitle);
        header.add(titles, BorderLayout.CENTER);

        RoundedPanel status = new RoundedPanel(new Color(0x07, 0x14, 0x23, 214), new Color(0x29, 0x47, 0x64, 180), 18);
        status.setLayout(new BorderLayout(10, 0));
        status.setBorder(new EmptyBorder(10, 15, 10, 15));
        status.add(statusLight, BorderLayout.WEST);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.add(statusLabel, BorderLayout.CENTER);
        header.add(status, BorderLayout.EAST);
        return header;
    }

    private JPanel buildHero() {
        JPanel hero = transparent(new BorderLayout(34, 0));
        hero.add(new ChapterRail(), BorderLayout.WEST);
        JPanel copy = transparent();
        copy.setBorder(new EmptyBorder(38, 0, 12, 0));
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

        JLabel eyebrow = label(tr(
                "СВЯЗЬ ОБОРВАЛАСЬ. ПЛАНЕТА ПРОДОЛЖАЕТ ЖИТЬ.",
                "CONTACT IS LOST. THE PLANET LIVES ON."
        ), 12, Font.BOLD, ACID);
        JLabel title = label(tr("ПОСЛЕ\nТИШИНЫ", "AFTER\nTHE SILENCE"), 64, Font.BOLD, BONE);
        title.setText(tr(
                "<html>ПОСЛЕ<br><font color='#4ca8e8'>ТИШИНЫ</font></html>",
                "<html>AFTER<br><font color='#4ca8e8'>THE SILENCE</font></html>"
        ));
        JLabel pitch = label("", 16, Font.PLAIN, MUTED);
        pitch.setText(tr(
                "<html>Чужая планета. Живая природная экосистема.<br>Выживай, изучай и заново строй технологии.</html>",
                "<html>An alien world. A living native ecosystem.<br>Survive, learn and rebuild technology.</html>"
        ));
        copy.add(eyebrow);
        copy.add(Box.createVerticalStrut(12));
        copy.add(title);
        copy.add(Box.createVerticalStrut(14));
        copy.add(pitch);
        copy.add(Box.createVerticalStrut(16));
        copy.add(buildWorldContractStrip());
        copy.add(Box.createVerticalGlue());
        hero.add(copy, BorderLayout.CENTER);
        hero.add(buildInfoCard(), BorderLayout.EAST);
        return hero;
    }

    private JPanel buildWorldContractStrip() {
        JPanel strip = transparent(new GridLayout(1, 3, 9, 0));
        strip.setMaximumSize(new Dimension(540, 66));
        strip.add(new SignalCard(tr("МИР", "WORLD"), tr("БЕЗ ВАНИЛИ", "NO VANILLA")));
        strip.add(new SignalCard(tr("ЛОГИКА", "LOGIC"), tr("СЕРВЕР", "SERVER")));
        strip.add(new SignalCard(tr("ПУТЬ", "PATH"), tr("КОДЕКС", "CODEX")));
        return strip;
    }

    private JPanel buildInfoCard() {
        RoundedPanel card = new RoundedPanel(PANEL, new Color(0x3F, 0x63, 0x85, 170), 24);
        card.setPreferredSize(new Dimension(330, 330));
        card.setBorder(new EmptyBorder(27, 27, 24, 27));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        Properties properties = readProjectProperties();
        card.add(label(tr("СОСТОЯНИЕ СБОРКИ", "BUILD STATUS"), 11, Font.BOLD, MUTED));
        card.add(Box.createVerticalStrut(14));
        card.add(label(tr("РАЗРАБОТКА", "DEVELOPMENT"), 26, Font.BOLD, BONE));
        card.add(Box.createVerticalStrut(24));
        card.add(infoRow(tr("ВЕРСИЯ", "VERSION"), properties.getProperty(
                "mod_version",
                tr("неизвестно", "unknown")
        )));
        card.add(Box.createVerticalStrut(11));
        card.add(infoRow("MINECRAFT", properties.getProperty("minecraft_version", "1.21.1")));
        card.add(Box.createVerticalStrut(11));
        card.add(infoRow("NEOFORGE", properties.getProperty("neo_version", "21.1.x")));
        card.add(Box.createVerticalGlue());
        JLabel note = label("", 12, Font.PLAIN, MUTED);
        note.setText(tr(
                "<html>Локальный ModDev-клиент<br>Microsoft-авторизация не изменяется</html>",
                "<html>Local ModDev client<br>Microsoft authentication is unchanged</html>"
        ));
        card.add(note);
        return card;
    }

    private JPanel infoRow(String key, String value) {
        JPanel row = transparent(new BorderLayout());
        row.add(label(key, 11, Font.BOLD, MUTED), BorderLayout.WEST);
        row.add(label(value, 13, Font.BOLD, BONE), BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return row;
    }

    private JPanel buildFooter() {
        RoundedPanel footer = new RoundedPanel(new Color(0x07, 0x14, 0x23, 236), new Color(0x29, 0x47, 0x64, 190), 22);
        footer.setLayout(new BorderLayout(20, 0));
        footer.setBorder(new EmptyBorder(17, 18, 17, 18));

        JPanel actions = transparent();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        playButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        playButton.setPreferredSize(new Dimension(250, 64));
        playButton.setMaximumSize(new Dimension(250, 64));
        playButton.addActionListener(this::play);
        actions.add(playButton);
        actions.add(Box.createVerticalStrut(10));

        JPanel utility = transparent();
        utility.setLayout(new BoxLayout(utility, BoxLayout.X_AXIS));
        verifyButton.setPreferredSize(new Dimension(120, 37));
        verifyButton.setMaximumSize(new Dimension(120, 37));
        verifyButton.addActionListener(this::verify);
        logsButton.setPreferredSize(new Dimension(120, 37));
        logsButton.setMaximumSize(new Dimension(120, 37));
        logsButton.addActionListener(this::openLogs);
        utility.add(verifyButton);
        utility.add(Box.createHorizontalStrut(10));
        utility.add(logsButton);
        actions.add(utility);
        footer.add(actions, BorderLayout.WEST);

        progressLog.setEditable(false);
        progressLog.setRows(5);
        progressLog.setLineWrap(true);
        progressLog.setWrapStyleWord(true);
        progressLog.setFont(new Font("Cascadia Mono", Font.PLAIN, 11));
        progressLog.setForeground(MUTED);
        progressLog.setBackground(new Color(0x07, 0x14, 0x23));
        progressLog.setCaretColor(ACID);
        progressLog.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(progressLog);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x29, 0x47, 0x64), 1));
        scroll.getViewport().setBackground(progressLog.getBackground());
        footer.add(scroll, BorderLayout.CENTER);
        return footer;
    }

    private void play(ActionEvent ignored) {
        if (projectRoot == null) return;
        Path script = projectRoot.resolve("scripts").resolve("bootstrap-and-play.ps1");
        runProcess(
                List.of(powershellExecutable(), "-NoLogo", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", script.toString()),
                tr("ЗАПУСК КЛИЕНТА", "STARTING CLIENT"),
                tr("КЛИЕНТ ЗАКРЫТ", "CLIENT CLOSED"),
                tr("ИГРА ЗАПУЩЕНА", "GAME RUNNING"),
                true);
    }

    private void verify(ActionEvent ignored) {
        if (projectRoot == null) return;
        Path doctor = projectRoot.resolve("doctor.cmd");
        runProcess(
                List.of(System.getenv().getOrDefault("ComSpec", "cmd.exe"), "/d", "/c", doctor.toString()),
                tr("ПРОВЕРКА ПРОЕКТА", "VERIFYING PROJECT"),
                tr("ПРОВЕРКА ПРОЙДЕНА", "VERIFICATION PASSED"),
                tr("ПРОВЕРКА ЗАВЕРШЕНА", "VERIFICATION COMPLETE"),
                false);
    }

    private void runProcess(List<String> command, String starting, String success, String active, boolean game) {
        if (!processRunning.compareAndSet(false, true)) {
            appendLog(tr("Другой процесс уже выполняется.", "Another process is already running."));
            return;
        }
        setControlsEnabled(false);
        if (game) playButton.setBusy(true);
        setStatus(starting, SIGNAL_YELLOW);
        appendLog("> " + String.join(" ", command));

        worker.submit(() -> {
            int exitCode = -1;
            try {
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.directory(projectRoot.toFile());
                builder.redirectErrorStream(true);
                builder.environment().put("GRAVESOWN_LAUNCHER", "1");
                Process process = builder.start();
                SwingUtilities.invokeLater(() -> setStatus(active, game ? ACID : SIGNAL_YELLOW));
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) appendLog(line);
                }
                exitCode = process.waitFor();
            } catch (IOException exception) {
                appendLog(tr("Ошибка запуска: ", "Launch error: ") + exception.getMessage());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                appendLog(tr("Процесс прерван.", "Process interrupted."));
            }

            int result = exitCode;
            SwingUtilities.invokeLater(() -> {
                processRunning.set(false);
                if (game) playButton.setBusy(false);
                setControlsEnabled(true);
                if (result == 0) {
                    setStatus(success, ACID);
                    appendLog(tr("Готово, код завершения 0.", "Done, exit code 0."));
                } else {
                    setStatus(tr("ОШИБКА · КОД ", "ERROR · CODE ") + result, BLOOD);
                    appendLog(tr("Процесс завершился с кодом ", "Process exited with code ")
                            + result + tr(". Смотри последние строки выше.", ". See the final lines above."));
                }
            });
        });
    }

    private void openLogs(ActionEvent ignored) {
        if (projectRoot == null) return;
        List<Path> candidates = List.of(
                externalDataHome().resolve("runs").resolve("client").resolve("logs"),
                projectRoot.resolve("build").resolve("reports"),
                projectRoot);
        Path target = candidates.stream().filter(Files::isDirectory).findFirst().orElse(projectRoot);
        try {
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(target.toFile());
            else new ProcessBuilder("explorer.exe", target.toString()).start();
            appendLog(tr("Открыта папка: ", "Opened folder: ") + target);
        } catch (IOException exception) {
            appendLog(tr("Не удалось открыть папку: ", "Could not open folder: ") + exception.getMessage());
        }
    }

    private void setControlsEnabled(boolean enabled) {
        playButton.setEnabled(enabled && projectRoot != null);
        verifyButton.setEnabled(enabled && projectRoot != null);
        logsButton.setEnabled(projectRoot != null);
    }

    private void setStatus(String text, Color color) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setStatus(text, color));
            return;
        }
        statusLabel.setText(text);
        statusLabel.setForeground(color);
        statusLight.setColor(color);
    }

    private void appendLog(String line) {
        SwingUtilities.invokeLater(() -> {
            progressLog.append("[" + CLOCK.format(LocalTime.now()) + "] " + line + System.lineSeparator());
            if (progressLog.getDocument().getLength() > 80_000) {
                try {
                    progressLog.getDocument().remove(0, 20_000);
                } catch (Exception ignored) {
                    // Keep logging even if a concurrent document trim loses a race.
                }
            }
            progressLog.setCaretPosition(progressLog.getDocument().getLength());
        });
    }

    private Properties readProjectProperties() {
        Properties properties = new Properties();
        if (projectRoot == null) return properties;
        try (var reader = Files.newBufferedReader(projectRoot.resolve("gradle.properties"), StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException ignored) {
            // The card has explicit fallback values.
        }
        return properties;
    }

    private static Path locateProjectRoot() {
        Set<Path> starts = new LinkedHashSet<>();
        addCandidate(starts, System.getProperty("gravesown.projectRoot"));
        addCandidate(starts, System.getProperty("user.dir"));
        String appPath = System.getProperty("jpackage.app-path");
        if (appPath != null) addCandidate(starts, Paths.get(appPath).getParent() == null ? null : Paths.get(appPath).getParent().toString());
        try {
            Path location = Paths.get(GravesownLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            addCandidate(starts, Files.isDirectory(location) ? location.toString() : location.getParent().toString());
        } catch (URISyntaxException | RuntimeException ignored) {
            // Other candidates still cover ordinary and packaged launches.
        }

        for (Path start : starts) {
            Path cursor = start.toAbsolutePath().normalize();
            for (int depth = 0; depth < 10 && cursor != null; depth++, cursor = cursor.getParent()) {
                if (Files.isRegularFile(cursor.resolve("gradlew.bat"))
                        && Files.isRegularFile(cursor.resolve("scripts").resolve("bootstrap-and-play.ps1"))
                        && Files.isRegularFile(cursor.resolve("gradle.properties"))) {
                    return cursor;
                }
            }
        }
        return null;
    }

    private static void addCandidate(Set<Path> starts, String value) {
        if (value == null || value.isBlank()) return;
        try {
            starts.add(Paths.get(value));
        } catch (RuntimeException ignored) {
            // Ignore malformed optional hints.
        }
    }

    private static BufferedImage loadBackground(Path root) {
        return loadImage(root, "launcher_background.png");
    }

    private static BufferedImage loadImage(Path root, String fileName) {
        List<Path> files = new ArrayList<>();
        if (root != null) files.add(root.resolve("launcher").resolve("assets").resolve(fileName));
        files.add(Paths.get("launcher", "assets", fileName).toAbsolutePath());
        for (Path file : files) {
            if (!Files.isRegularFile(file)) continue;
            try {
                return ImageIO.read(file.toFile());
            } catch (IOException ignored) {
                // Try the packaged fallback next.
            }
        }
        try (InputStream stream = GravesownLauncher.class.getResourceAsStream("/" + fileName)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (IOException ignored) {
            return null;
        }
    }

    private static String powershellExecutable() {
        String systemRoot = System.getenv("SystemRoot");
        if (systemRoot != null) {
            Path executable = Paths.get(systemRoot, "System32", "WindowsPowerShell", "v1.0", "powershell.exe");
            if (Files.isRegularFile(executable)) return executable.toString();
        }
        return "powershell.exe";
    }

    private static Path externalDataHome() {
        String configured = System.getenv("GRAVESOWN_HOME");
        if (configured != null && !configured.isBlank()) return Paths.get(configured);
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            return Paths.get(localAppData, "Gravesown");
        }
        return Paths.get(System.getProperty("user.home"), ".gravesown");
    }

    private static String tr(String russian, String english) {
        return "ru".equalsIgnoreCase(Locale.getDefault().getLanguage()) ? russian : english;
    }

    private static JPanel transparent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel transparent(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        return label;
    }

    @SuppressWarnings("serial")
    private static final class BackdropPanel extends JPanel {
        private final BufferedImage image;

        private BackdropPanel(BufferedImage image) {
            this.image = image;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            if (image == null) {
                g.setPaint(new GradientPaint(0, 0, new Color(0x10, 0x24, 0x3B), getWidth(), getHeight(), new Color(0x07, 0x14, 0x23)));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(0x4C, 0xA8, 0xE8, 28));
                for (int x = -getHeight(); x < getWidth(); x += 92) g.fillRect(x, 0, 2, getHeight());
            } else {
                double scale = Math.max((double) getWidth() / image.getWidth(), (double) getHeight() / image.getHeight());
                int width = (int) Math.ceil(image.getWidth() * scale);
                int height = (int) Math.ceil(image.getHeight() * scale);
                int x = (getWidth() - width) / 2;
                int y = (getHeight() - height) / 2;
                g.drawImage(image, x, y, width, height, null);
            }
            g.setPaint(new GradientPaint(0, 0, new Color(0x07, 0x14, 0x23, 34), getWidth(), 0, new Color(0x07, 0x14, 0x23, 112)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setPaint(new GradientPaint(0, getHeight() / 2f, new Color(0x07, 0x14, 0x23, 0), 0, getHeight(), new Color(0x07, 0x14, 0x23, 132)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.dispose();
        }
    }

    @SuppressWarnings("serial")
    private static final class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color stroke;
        private final int radius;

        private RoundedPanel(Color fill, Color stroke, int radius) {
            this.fill = fill;
            this.stroke = stroke;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            g.setColor(stroke);
            g.setStroke(new BasicStroke(1f));
            g.draw(new RoundRectangle2D.Float(.5f, .5f, getWidth() - 2, getHeight() - 2, radius, radius));
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    @SuppressWarnings("serial")
    private static final class BrandMark extends JPanel {
        private BrandMark() {
            setOpaque(false);
            setPreferredSize(new Dimension(48, 48));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0x07, 0x14, 0x23, 230));
            g.fillRoundRect(1, 1, 46, 46, 13, 13);
            g.setColor(ACID);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(2, 2, 43, 43, 12, 12);
            g.setFont(new Font("Segoe UI", Font.BOLD, 25));
            FontMetrics metrics = g.getFontMetrics();
            String mark = "G";
            g.drawString(mark, (getWidth() - metrics.stringWidth(mark)) / 2, 33);
            g.dispose();
        }
    }

    /** Compact story index that makes the launcher read like recovered equipment. */
    @SuppressWarnings("serial")
    private static final class ChapterRail extends JPanel {
        private ChapterRail() {
            setOpaque(false);
            setPreferredSize(new Dimension(58, 360));
            setMinimumSize(new Dimension(58, 240));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = getWidth() / 2;
            int top = 70;
            int spacing = 82;
            g.setColor(new Color(0x4C, 0xA8, 0xE8, 82));
            g.fillRect(x - 1, top, 2, spacing * 3);
            g.setFont(new Font("Cascadia Mono", Font.BOLD, 10));
            for (int i = 0; i < 4; i++) {
                int y = top + i * spacing;
                boolean active = i == 0;
                g.setColor(active ? new Color(0x4C, 0xA8, 0xE8, 80) : new Color(0x07, 0x14, 0x23, 205));
                g.fillRect(x - 18, y - 17, 36, 34);
                g.setColor(active ? ACID : new Color(0x3F, 0x63, 0x85));
                g.setStroke(new BasicStroke(active ? 2f : 1f));
                g.drawRect(x - 18, y - 17, 35, 33);
                String number = "0" + (i + 1);
                FontMetrics metrics = g.getFontMetrics();
                g.drawString(number, x - metrics.stringWidth(number) / 2, y + 4);
            }
            g.dispose();
        }
    }

    @SuppressWarnings("serial")
    private static final class SignalCard extends JPanel {
        private final String caption;
        private final String value;

        private SignalCard(String caption, String value) {
            this.caption = caption;
            this.value = value;
            setOpaque(false);
            setPreferredSize(new Dimension(150, 62));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0x07, 0x14, 0x23, 212));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(0x4C, 0xA8, 0xE8, 135));
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g.fillRect(0, 0, 17, 2);
            g.fillRect(0, 0, 2, 17);
            g.fillRect(getWidth() - 17, getHeight() - 2, 17, 2);
            g.fillRect(getWidth() - 2, getHeight() - 17, 2, 17);
            g.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g.setColor(MUTED);
            g.drawString(caption, 11, 21);
            g.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g.setColor(BONE);
            g.drawString(value, 11, 42);
            g.dispose();
        }
    }

    @SuppressWarnings("serial")
    private static final class StatusLight extends JPanel {
        private Color color = MUTED;

        private StatusLight() {
            setOpaque(false);
            setPreferredSize(new Dimension(12, 12));
        }

        private void setColor(Color color) {
            this.color = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 55));
            g.fillOval(0, 0, 12, 12);
            g.setColor(color);
            g.fillOval(3, 3, 6, 6);
            g.dispose();
        }
    }

    @SuppressWarnings("serial")
    private static final class ActionButton extends JButton {
        private final boolean primary;
        private boolean hovered;
        private boolean busy;
        private int pulse;
        private final javax.swing.Timer animation = new javax.swing.Timer(45, event -> {
            pulse = (pulse + 5) % 180;
            repaint();
        });

        private ActionButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setFont(new Font("Segoe UI", Font.BOLD, primary ? 17 : 11));
            setForeground(primary ? new Color(0x07, 0x14, 0x23) : BONE);
            setBorder(new EmptyBorder(0, 15, 0, 15));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent event) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent event) { hovered = false; repaint(); }
            });
        }

        private void setBusy(boolean busy) {
            this.busy = busy;
            if (busy) animation.start();
            else {
                animation.stop();
                pulse = 0;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill;
            if (!isEnabled()) fill = primary ? new Color(0x29, 0x47, 0x64) : new Color(0x10, 0x24, 0x3B);
            else if (primary) fill = hovered ? ACID_HOVER : ACID;
            else fill = hovered ? new Color(0x29, 0x47, 0x64) : new Color(0x10, 0x24, 0x3B);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), primary ? 16 : 11, primary ? 16 : 11);
            if (primary && busy) {
                int sweep = (int)((getWidth() + 90) * (pulse / 180.0)) - 90;
                g.setColor(new Color(0xD7, 0xE6, 0xF2, 78));
                g.fillRoundRect(sweep, 3, 88, Math.max(1, getHeight() - 6), 14, 14);
                g.setColor(new Color(0xD7, 0xE6, 0xF2, 205));
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
            }
            if (!primary) {
                g.setColor(new Color(0x4C, 0xA8, 0xE8));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 11, 11);
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
