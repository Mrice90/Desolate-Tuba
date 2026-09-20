package com.infiniteconquest.gui;

import com.infiniteconquest.cli.*;
import com.infiniteconquest.core.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

public final class InfiniteConquestGui extends JFrame {
    private static final Color INK = new Color(14, 20, 31);
    private static final Color PANEL = new Color(25, 35, 52);
    private static final Color PANEL_LIGHT = new Color(36, 49, 70);
    private static final Color GOLD = new Color(240, 191, 73);
    private static final Color HUMAN_PLOT = new Color(28, 62, 76);
    private static final Color BOT_PLOT = new Color(69, 38, 50);
    private static final Color SELECTED = new Color(91, 209, 255);
    private static final Color MOVE = new Color(72, 181, 230);
    private static final Color ATTACK = new Color(244, 92, 92);
    private static final Color DEPLOY = new Color(104, 211, 139);
    private static final Color BURROW = new Color(190, 121, 235);
    private static final Color CAST = new Color(246, 194, 78);

    private final JLabel turnLabel = new JLabel();
    private final JLabel humanLabel = new JLabel();
    private final JLabel botLabel = new JLabel();
    private final JLabel messageLabel = new JLabel("Select a card or unit, then choose a legal action.");
    private final JPanel boardPanel = new JPanel(new GridLayout(BoardPosition.HEIGHT, BoardPosition.WIDTH, 6, 6));
    private final JPanel boardStage = new JPanel(new GridBagLayout());
    private final JPanel handPanel = new JPanel();
    private final DefaultListModel<ActionOption> actionModel = new DefaultListModel<>();
    private final JList<ActionOption> actionList = new JList<>(actionModel);
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private final JList<String> historyList = new JList<>(historyModel);
    private final JTabbedPane actionTabs = new JTabbedPane();
    private final JLabel previewArt = new JLabel();
    private final JLabel previewText = new JLabel("<html><b>Hover over a card</b><br>Right-click a board stack to inspect it.</html>");
    private final Map<BoardPosition, JButton> boardButtons = new HashMap<>();
    private final Map<BoardPosition, EffectBadge> effectBadges = new HashMap<>();
    private final CombatOverlay combatOverlay = new CombatOverlay();
    private final List<JButton> handButtons = new ArrayList<>();

    private GameState state;
    private CommandProcessor commands;
    private final ActionHints hints = new ActionHints();
    private final BotPlayer bot = new BotPlayer();
    private final DemoMatchFactory matchFactory = new DemoMatchFactory();
    private final FactionDecks factionDecks = new FactionDecks(matchFactory.pool());
    private final CapitalPassiveRules passiveRules = new CapitalPassiveRules();
    private final DeckFileStore deckFiles = new DeckFileStore();
    private final Map<String, List<CardDefinition>> savedDecks = new HashMap<>();
    private final Path deckDirectory = Path.of(System.getProperty("user.home"), ".infinite-conquest", "decks");
    private Integer selectedHand;
    private BoardPosition selectedCell;
    private boolean botRunning;
    private String humanFaction = "ZEUS";
    private String botFaction = "ARES";
    private CardDefinition humanCapital;
    private CardDefinition botCapital;
    private DragSource dragSource;
    private int historyNumber;
    private long lastSystemEvent = -1;
    private boolean playerOneBot;
    private boolean winnerSoundPlayed;
    private boolean fullScreen;

    public InfiniteConquestGui() {
        super("Infinite Conquest");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 860));
        setSize(1500, 980);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        installTheme();
        setJMenuBar(buildMenuBar());
        setContentPane(buildScreen());
        setGlassPane(combatOverlay);
        combatOverlay.setVisible(true);
        loadSavedDecks();
        newMatch();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InfiniteConquestGui().setVisible(true));
    }

    private JComponent buildScreen() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(INK);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBoard(), BorderLayout.CENTER);
        root.add(buildActions(), BorderLayout.EAST);
        root.add(buildHand(), BorderLayout.SOUTH);
        return root;
    }

    private JComponent buildHeader() {
        JPanel header = panel(new BorderLayout(12, 4));
        JLabel title = new JLabel("INFINITE CONQUEST");
        title.setForeground(GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));

        JPanel meters = new JPanel(new GridLayout(1, 2, 12, 0));
        meters.setOpaque(false);
        styleMeter(humanLabel, new Color(87, 203, 234));
        styleMeter(botLabel, new Color(239, 106, 122));
        meters.add(humanLabel);
        meters.add(botLabel);

        JButton deckBuilder = button("Deck Builder", e -> openDeckEditor());
        JButton newMatch = button("New Match", e -> newMatch());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);
        left.add(title);
        left.add(turnLabel);
        header.add(left, BorderLayout.WEST);
        header.add(meters, BorderLayout.CENTER);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        controls.add(deckBuilder);
        controls.add(newMatch);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu game = new JMenu("Game");
        JMenuItem decks = new JMenuItem("Deck Builder...");
        decks.setAccelerator(KeyStroke.getKeyStroke("control D"));
        decks.addActionListener(event -> openDeckEditor());
        JMenuItem newGame = new JMenuItem("New Match...");
        newGame.setAccelerator(KeyStroke.getKeyStroke("control N"));
        newGame.addActionListener(event -> newMatch());
        JMenuItem fullscreen = new JMenuItem("Toggle Full Screen");
        fullscreen.setAccelerator(KeyStroke.getKeyStroke("F11"));
        fullscreen.addActionListener(event -> toggleFullScreen());
        game.add(decks); game.addSeparator(); game.add(newGame); game.add(fullscreen); bar.add(game);
        return bar;
    }

    private void toggleFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (fullScreen) device.setFullScreenWindow(null);
        dispose();
        fullScreen = !fullScreen;
        setUndecorated(fullScreen);
        setVisible(true);
        if (fullScreen && device.isFullScreenSupported()) device.setFullScreenWindow(this);
        else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    private JComponent buildBoard() {
        JPanel surround = panel(new BorderLayout(0, 8));
        JLabel enemy = section("PLAYER 2 — BOT TERRITORY", new Color(239, 106, 122));
        JLabel human = section("PLAYER 1 — YOUR TERRITORY", new Color(87, 203, 234));
        boardPanel.setOpaque(false);
        boardPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        for (int y = BoardPosition.HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < BoardPosition.WIDTH; x++) {
                BoardPosition position = new BoardPosition(x, y);
                JButton cell = new JButton();
                cell.setVerticalAlignment(SwingConstants.TOP);
                cell.setHorizontalAlignment(SwingConstants.LEFT);
                cell.setFocusPainted(false);
                cell.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                cell.setMargin(new Insets(2, 2, 2, 2));
                cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                cell.addMouseListener(dragListener(new DragSource(null, position)));
                boardButtons.put(position, cell);
                boardPanel.add(cell);
            }
        }
        surround.add(enemy, BorderLayout.NORTH);
        boardStage.setOpaque(false);
        boardStage.add(boardPanel);
        JScrollPane boardScroll = new JScrollPane(boardStage,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        boardScroll.setBorder(null);
        boardScroll.getVerticalScrollBar().setUnitIncrement(24);
        boardScroll.getHorizontalScrollBar().setUnitIncrement(24);
        boardScroll.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent event) {
                fitBoardToViewport(boardScroll.getViewport().getExtentSize());
            }
        });
        surround.add(boardScroll, BorderLayout.CENTER);
        surround.add(human, BorderLayout.SOUTH);
        return surround;
    }

    private void fitBoardToViewport(Dimension available) {
        int usableWidth = Math.max(640, available.width - 22);
        int tile = Math.max(160, usableWidth / BoardPosition.WIDTH);
        Dimension boardSize = new Dimension(tile * BoardPosition.WIDTH, tile * BoardPosition.HEIGHT);
        boardPanel.setPreferredSize(boardSize);
        boardPanel.setMinimumSize(boardSize);
        boardPanel.setMaximumSize(boardSize);
        boardStage.revalidate();
    }

    private JComponent buildActions() {
        JPanel side = panel(new BorderLayout(8, 8));
        side.setPreferredSize(new Dimension(350, 100));
        JPanel preview = new JPanel(new BorderLayout(8, 8));
        preview.setOpaque(false);
        preview.setPreferredSize(new Dimension(330, 105));
        previewArt.setHorizontalAlignment(SwingConstants.CENTER);
        previewText.setForeground(Color.WHITE);
        previewText.setVerticalAlignment(SwingConstants.TOP);
        preview.add(section("CARD INSPECTOR", GOLD), BorderLayout.NORTH);
        preview.add(previewArt, BorderLayout.CENTER);
        preview.add(previewText, BorderLayout.SOUTH);
        side.add(preview, BorderLayout.NORTH);
        actionList.setBackground(PANEL_LIGHT);
        actionList.setForeground(Color.WHITE);
        actionList.setSelectionBackground(new Color(48, 112, 137));
        actionList.setFixedCellHeight(34);
        actionList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        actionList.setBorder(new EmptyBorder(5, 5, 5, 5));
        actionList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) executeSelectedAction();
            }
        });
        historyList.setBackground(PANEL_LIGHT);
        historyList.setForeground(new Color(218, 226, 237));
        historyList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        historyList.setFixedCellHeight(28);
        historyList.setBorder(new EmptyBorder(5, 5, 5, 5));
        actionTabs.addTab("LEGAL MOVES", new JScrollPane(actionList));
        actionTabs.addTab("ACTION LOG", new JScrollPane(historyList));
        actionTabs.setMinimumSize(new Dimension(330, 145));
        side.add(actionTabs, BorderLayout.CENTER);

        JButton execute = button("Execute Selected", e -> executeSelectedAction());
        JButton clear = button("Clear Selection", e -> clearSelection());
        JButton end = button("End Turn", e -> executeHuman("end"));
        end.setBackground(new Color(143, 66, 71));
        JPanel controls = new JPanel(new GridLayout(2, 2, 7, 7));
        controls.setOpaque(false);
        controls.add(execute);
        controls.add(clear);
        controls.add(end);
        controls.add(new JLabel());

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        messageLabel.setForeground(new Color(205, 215, 229));
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        messageLabel.setVerticalAlignment(SwingConstants.TOP);
        messageLabel.setPreferredSize(new Dimension(320, 34));
        bottom.add(messageLabel, BorderLayout.NORTH);
        bottom.add(controls, BorderLayout.SOUTH);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private JComponent buildHand() {
        JPanel area = panel(new BorderLayout(8, 8));
        area.setPreferredSize(new Dimension(100, 205));
        area.add(section("YOUR HAND", new Color(87, 203, 234)), BorderLayout.NORTH);
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
        handPanel.setBackground(PANEL);
        JScrollPane scroll = new JScrollPane(handPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getHorizontalScrollBar().setUnitIncrement(24);
        area.add(scroll, BorderLayout.CENTER);
        return area;
    }

    private void newMatch() {
        MatchChoice choice = chooseMatch();
        if (choice == null && state != null) return;
        if (choice == null) choice = defaultChoice();
        long seed = System.nanoTime();
        humanFaction = choice.humanFaction();
        botFaction = choice.botFaction();
        humanCapital = choice.humanCapital();
        botCapital = choice.botCapital();
        BoardPosition botCapitalPosition = randomCapitalPosition(seed, 1);
        state = matchFactory.create(seed,
                deckForFaction(humanFaction), factionDecks.starter(botFaction),
                humanCapital, botCapital, choice.humanCapitalPosition(), botCapitalPosition);
        commands = new CommandProcessor(state);
        selectedHand = null;
        selectedCell = null;
        botRunning = false;
        winnerSoundPlayed = false;
        playerOneBot = choice.playerOneBot();
        lastSystemEvent = state.events().stream().mapToLong(GameEvent::sequence).max().orElse(-1);
        historyModel.clear();
        historyNumber = 0;
        showCoinFlip(state.startingPlayer());
        runOpeningMulligans();
        lastSystemEvent = state.events().stream().mapToLong(GameEvent::sequence).max().orElse(-1);
        addHistory("Match", title(humanFaction) + " vs " + title(botFaction)
                + " — Player " + (state.startingPlayer() + 1) + " won the coin flip");
        message("Player " + (state.startingPlayer() + 1)
                + " starts. Player 1 begins at 0 GP; Player 2 begins at 1 GP; Capitals generate 1 GP per turn.");
        refresh();
        if (isAutomatedPlayer(state.activePlayer())) SwingUtilities.invokeLater(this::runBotTurn);
    }

    private void runOpeningMulligans() {
        if (playerOneBot) completeBotMulligan(0);
        else showHumanMulligan();
        completeBotMulligan(1);
    }

    private void showHumanMulligan() {
        List<MulliganChoice> choices = state.player(0).hand().stream()
                .map(id -> new MulliganChoice(id, state.card(id).orElseThrow().definition())).toList();
        Set<UUID> discarded = new VisualMulliganDialog(choices).choose();
        state.mulligan(0, discarded);
        addHistory("You", "Mulligan — discarded and redrew " + discarded.size());
    }

    private void completeBotMulligan(int playerId) {
        List<CardInstance> cards = state.player(playerId).hand().stream()
                .map(id -> state.card(id).orElseThrow())
                .sorted(Comparator.comparingInt(this::openingKeepScore).reversed())
                .toList();
        Set<UUID> discarded = cards.stream().skip(Math.max(0, cards.size() - 3)).map(CardInstance::instanceId)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        state.mulligan(playerId, discarded);
        addHistory("Bot " + (playerId + 1), "Mulligan — discarded and redrew " + discarded.size());
    }

    private int openingKeepScore(CardInstance card) {
        CardDefinition def = card.definition();
        if ((def.type() == CardType.LAND || def.type() == CardType.STRUCTURE) && def.cost() <= 2) return 100 - def.cost();
        if (def.type() == CardType.CHARACTER && def.cost() <= 3) return 80 - def.cost();
        if (def.type() == CardType.SPELL && def.cost() <= 3) return 70 - def.cost();
        return 20 - def.cost();
    }

    private MatchChoice defaultChoice() {
        return new MatchChoice("ZEUS", matchFactory.capitals().forFaction("ZEUS").get(0),
                "ARES", matchFactory.capitals().forFaction("ARES").get(0), false,
                new BoardPosition(1, 0));
    }

    private List<CardDefinition> deckForFaction(String faction) {
        return savedDecks.getOrDefault(faction, factionDecks.starter(faction));
    }

    private void loadSavedDecks() {
        try {
            Files.createDirectories(deckDirectory);
            for (String faction : FactionDecks.FACTIONS) {
                Path file = deckDirectory.resolve(faction.toLowerCase(Locale.ROOT) + ".json");
                if (Files.exists(file)) savedDecks.put(faction, deckFiles.load(file, matchFactory.pool()));
            }
        } catch (RuntimeException | java.io.IOException exception) {
            JOptionPane.showMessageDialog(this, "Saved decks could not be loaded: " + exception.getMessage(),
                    "Deck Loading", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openDeckEditor() {
        String faction = (String) JOptionPane.showInputDialog(this, "Choose the faction deck to edit:",
                "Deck Builder", JOptionPane.PLAIN_MESSAGE, null,
                FactionDecks.FACTIONS.stream().sorted().toArray(), humanFaction);
        if (faction == null) return;
        List<CardDefinition> working = new ArrayList<>(deckForFaction(faction));
        List<CardDefinition> available = matchFactory.pool().cardsForFaction(faction).stream()
                .sorted(Comparator.comparing((CardDefinition card) -> card.type().ordinal())
                        .thenComparingInt(CardDefinition::cost).thenComparing(CardDefinition::name)).toList();
        DefaultListModel<CardDefinition> collectionModel = new DefaultListModel<>();
        available.forEach(collectionModel::addElement);
        DefaultListModel<DeckRow> deckModel = new DefaultListModel<>();
        JList<CardDefinition> collection = new JList<>(collectionModel);
        JList<DeckRow> deck = new JList<>(deckModel);
        collection.setCellRenderer(new CardDefinitionRenderer());
        deck.setCellRenderer(new DeckRowRenderer());
        collection.setFixedCellHeight(86);
        deck.setFixedCellHeight(86);
        JLabel status = new JLabel();
        status.setForeground(Color.WHITE);

        Runnable rebuild = () -> {
            deckModel.clear();
            working.stream().collect(java.util.stream.Collectors.groupingBy(CardDefinition::id,
                            LinkedHashMap::new, java.util.stream.Collectors.toList()))
                    .values().stream().map(group -> new DeckRow(group.get(0), group.size()))
                    .sorted(Comparator.comparing((DeckRow row) -> row.card().type().ordinal())
                            .thenComparing(row -> row.card().name()))
                    .forEach(deckModel::addElement);
            List<String> errors = new DeckValidator().validate(working);
            long development = working.stream().filter(card -> card.type() == CardType.LAND
                    || card.type() == CardType.STRUCTURE).count();
            status.setText("<html><b>" + working.size() + "/40 cards</b> • " + development
                    + " Lands/Structures" + (errors.isEmpty() ? " • READY TO SAVE"
                    : "<br><font color='#ff9b9b'>" + html(String.join("; ", errors)) + "</font>") + "</html>");
        };
        rebuild.run();

        JButton add = button("Add Copy →", e -> {
            CardDefinition card = collection.getSelectedValue();
            if (card == null) return;
            long copies = working.stream().filter(value -> value.id().equals(card.id())).count();
            if (working.size() >= DeckValidator.REQUIRED_SIZE || copies >= DeckValidator.MAX_COPIES) {
                Toolkit.getDefaultToolkit().beep(); return;
            }
            working.add(card); rebuild.run();
        });
        JButton remove = button("← Remove Copy", e -> {
            DeckRow row = deck.getSelectedValue();
            if (row == null) return;
            working.stream().filter(card -> card.id().equals(row.card().id())).findFirst().ifPresent(working::remove);
            rebuild.run();
        });
        JButton reset = button("Reset Starter", e -> { working.clear(); working.addAll(factionDecks.starter(faction)); rebuild.run(); });
        collection.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) add.doClick(); }
        });
        deck.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) remove.doClick(); }
        });

        JLabel help = new JLabel("<html>Remove a card before adding when the deck is full.<br>Double-click cards or use the buttons.</html>");
        help.setForeground(new Color(205, 215, 229));
        JPanel centerButtons = new JPanel(new GridLayout(4, 1, 5, 5));
        centerButtons.setOpaque(false); centerButtons.add(add); centerButtons.add(remove); centerButtons.add(reset);
        centerButtons.add(help);
        JPanel editor = panel(new BorderLayout(8, 8));
        JPanel lists = new JPanel(new GridLayout(1, 3, 8, 0)); lists.setOpaque(false);
        lists.add(titledScroll("AVAILABLE " + available.size(), collection));
        lists.add(centerButtons);
        lists.add(titledScroll("CURRENT DECK", deck));
        editor.add(lists, BorderLayout.CENTER); editor.add(status, BorderLayout.SOUTH);
        editor.setPreferredSize(new Dimension(1050, 650));
        int result = JOptionPane.showConfirmDialog(this, editor, "Deck Builder — " + title(faction),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        List<String> errors = new DeckValidator().validate(working);
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, String.join("\n", errors), "Deck Not Saved", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Path file = deckDirectory.resolve(faction.toLowerCase(Locale.ROOT) + ".json");
        deckFiles.save(file, title(faction) + " Custom", working);
        savedDecks.put(faction, List.copyOf(working));
        JOptionPane.showMessageDialog(this, "Saved " + title(faction) + " deck to:\n" + file,
                "Deck Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel titledScroll(String title, JList<?> list) {
        JPanel panel = new JPanel(new BorderLayout(4, 4)); panel.setOpaque(false);
        panel.add(section(title, GOLD), BorderLayout.NORTH); panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private MatchChoice chooseMatch() {
        List<String> factions = List.of("ZEUS", "POSEIDON", "HADES", "ARES", "ATHENA", "HEPHAESTUS");
        JComboBox<String> humanFactionBox = new JComboBox<>(factions.toArray(String[]::new));
        JComboBox<String> botFactionBox = new JComboBox<>(factions.toArray(String[]::new));
        humanFactionBox.setSelectedItem(humanFaction);
        botFactionBox.setSelectedItem(botFaction);
        JComboBox<CapitalChoice> humanCapitalBox = new JComboBox<>();
        JComboBox<CapitalChoice> botCapitalBox = new JComboBox<>();
        JComboBox<String> playerOneControl = new JComboBox<>(new String[]{"Human", "Bot (watch match)"});
        JLabel humanStrategy = setupDescription();
        JLabel botStrategy = setupDescription();
        JLabel humanPassive = setupDescription();
        JLabel botPassive = setupDescription();
        CapitalPlacementPicker capitalPlacement = new CapitalPlacementPicker();

        Runnable update = () -> {
            updateCapitalBox(humanCapitalBox, (String) humanFactionBox.getSelectedItem());
            updateCapitalBox(botCapitalBox, (String) botFactionBox.getSelectedItem());
            humanStrategy.setText(strategyHtml((String) humanFactionBox.getSelectedItem()));
            botStrategy.setText(strategyHtml((String) botFactionBox.getSelectedItem()));
            updatePassiveLabel(humanPassive, (CapitalChoice) humanCapitalBox.getSelectedItem());
            updatePassiveLabel(botPassive, (CapitalChoice) botCapitalBox.getSelectedItem());
        };
        humanFactionBox.addActionListener(e -> update.run());
        botFactionBox.addActionListener(e -> update.run());
        humanCapitalBox.addActionListener(e -> updatePassiveLabel(humanPassive,
                (CapitalChoice) humanCapitalBox.getSelectedItem()));
        botCapitalBox.addActionListener(e -> updatePassiveLabel(botPassive,
                (CapitalChoice) botCapitalBox.getSelectedItem()));
        update.run();

        JPanel setup = new JPanel(new GridBagLayout());
        setup.setBackground(PANEL);
        setup.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        addSetupRow(setup, c, 0, "YOUR FACTION", humanFactionBox, "BOT FACTION", botFactionBox);
        addSetupRow(setup, c, 1, "STRATEGY", humanStrategy, "STRATEGY", botStrategy);
        addSetupRow(setup, c, 2, "YOUR CAPITAL", humanCapitalBox, "BOT CAPITAL", botCapitalBox);
        addSetupRow(setup, c, 3, "PASSIVE", humanPassive, "PASSIVE", botPassive);
        addSetupRow(setup, c, 4, "PLAYER 1 CONTROL", playerOneControl, "PLAYER 2 CONTROL",
                new JLabel("Bot"));
        addSetupRow(setup, c, 5, "PLACE YOUR CAPITAL", capitalPlacement,
                "BOT CAPITAL POSITION", new JLabel("Chosen secretly at random"));
        JButton editDecks = button("Open Deck Builder", e -> openDeckEditor());
        addSetupRow(setup, c, 6, "CUSTOM DECKS", editDecks,
                "DISPLAY", new JLabel("F11 toggles full screen"));

        Rectangle usableScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int setupWidth = Math.max(620, Math.min(760, usableScreen.width - 80));
        int setupHeight = Math.max(420, Math.min(540, usableScreen.height - 140));
        JScrollPane setupScroll = new JScrollPane(setup,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setupScroll.setPreferredSize(new Dimension(setupWidth, setupHeight));
        setupScroll.setBorder(null);
        setupScroll.getViewport().setBackground(PANEL);
        setupScroll.getVerticalScrollBar().setUnitIncrement(20);

        int result = JOptionPane.showConfirmDialog(this, setupScroll, "Configure Conquest",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        CapitalChoice selectedHuman = (CapitalChoice) humanCapitalBox.getSelectedItem();
        CapitalChoice selectedBot = (CapitalChoice) botCapitalBox.getSelectedItem();
        return new MatchChoice((String) humanFactionBox.getSelectedItem(), selectedHuman.card(),
                (String) botFactionBox.getSelectedItem(), selectedBot.card(),
                playerOneControl.getSelectedIndex() == 1, capitalPlacement.selected());
    }

    private BoardPosition randomCapitalPosition(long seed, int player) {
        Random random = new Random(seed ^ 0xB07CA917L);
        return new BoardPosition(random.nextInt(BoardPosition.WIDTH), player == 0
                ? random.nextInt(BoardPosition.HEIGHT / 2)
                : BoardPosition.HEIGHT / 2 + random.nextInt(BoardPosition.HEIGHT / 2));
    }

    private void showCoinFlip(int winner) {
        CoinFlipPanel coin = new CoinFlipPanel(winner);
        coin.setPreferredSize(new Dimension(430, 310));
        JDialog dialog = new JDialog(this, "Determine First Player", true);
        dialog.getContentPane().setBackground(PANEL);
        dialog.add(coin);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        final int[] frame = {0};
        javax.swing.Timer animation = new javax.swing.Timer(75, e -> {
            frame[0]++;
            coin.setFrame(frame[0]);
            if (frame[0] >= 28) {
                ((javax.swing.Timer) e.getSource()).stop();
                javax.swing.Timer hold = new javax.swing.Timer(850, ignored -> dialog.dispose());
                hold.setRepeats(false); hold.start();
            }
        });
        animation.start();
        dialog.setVisible(true);
    }

    private final class CoinFlipPanel extends JPanel {
        private final int winner;
        private int frame;

        CoinFlipPanel(int winner) { this.winner = winner; setOpaque(false); }
        void setFrame(int value) { frame = value; repaint(); }

        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean settled = frame >= 24;
            int face = settled ? winner : (frame / 3) % 2;
            double squash = settled ? 1.0 : Math.max(.10, Math.abs(Math.cos(frame * Math.PI / 6.0)));
            int diameter = 150;
            int coinWidth = Math.max(15, (int) (diameter * squash));
            int x = (getWidth() - coinWidth) / 2;
            int y = 38 + (settled ? 0 : (int) (18 * Math.abs(Math.sin(frame * Math.PI / 6.0))));
            g.setPaint(new GradientPaint(x, y, new Color(255, 230, 132), x + coinWidth, y + diameter,
                    new Color(170, 105, 27)));
            g.fillOval(x, y, coinWidth, diameter);
            g.setColor(new Color(255, 244, 184));
            g.setStroke(new BasicStroke(5f));
            g.drawOval(x + 4, y + 4, Math.max(7, coinWidth - 8), diameter - 8);
            if (coinWidth > 70) {
                g.setFont(new Font(Font.SERIF, Font.BOLD, 58));
                String symbol = face == 0 ? "I" : "II";
                FontMetrics metrics = g.getFontMetrics();
                g.setColor(new Color(92, 55, 18));
                g.drawString(symbol, getWidth() / 2 - metrics.stringWidth(symbol) / 2,
                        y + diameter / 2 + metrics.getAscent() / 3);
            }
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, settled ? 25 : 20));
            String caption = settled ? "PLAYER " + (winner + 1) + " STARTS" : "FLIPPING FOR INITIATIVE";
            FontMetrics captionMetrics = g.getFontMetrics();
            g.setColor(settled ? GOLD : Color.WHITE);
            g.drawString(caption, (getWidth() - captionMetrics.stringWidth(caption)) / 2, 245);
            g.dispose();
        }
    }

    private void addSetupRow(JPanel panel, GridBagConstraints c, int row,
                             String leftTitle, JComponent left, String rightTitle, JComponent right) {
        c.gridy = row * 2;
        c.gridx = 0;
        panel.add(section(leftTitle, new Color(87, 203, 234)), c);
        c.gridx = 1;
        panel.add(section(rightTitle, new Color(239, 106, 122)), c);
        c.gridy = row * 2 + 1;
        c.gridx = 0;
        panel.add(left, c);
        c.gridx = 1;
        panel.add(right, c);
    }

    private JLabel setupDescription() {
        JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(330, 58));
        return label;
    }

    private void updateCapitalBox(JComboBox<CapitalChoice> box, String faction) {
        Object previous = box.getSelectedItem();
        box.removeAllItems();
        for (CardDefinition capital : matchFactory.capitals().forFaction(faction)) {
            box.addItem(new CapitalChoice(capital));
        }
        if (previous instanceof CapitalChoice old) {
            for (int i = 0; i < box.getItemCount(); i++) {
                if (box.getItemAt(i).card().id().equals(old.card().id())) box.setSelectedIndex(i);
            }
        }
    }

    private void updatePassiveLabel(JLabel label, CapitalChoice choice) {
        label.setText(choice == null ? "" : "<html>" + html(passiveRules.description(choice.card())) + "</html>");
    }

    private String strategyHtml(String faction) {
        return "<html><b>" + title(faction) + "</b> — "
                + FactionDecks.PRIMARY_TYPES.get(faction) + " / " + FactionDecks.SECONDARY_TYPES.get(faction)
                + "<br>Keywords: " + FactionDecks.PRIMARY_KEYWORDS.get(faction)
                + " / " + FactionDecks.SECONDARY_KEYWORDS.get(faction) + "</html>";
    }

    private void selectHand(int index) {
        if (playerOneBot || botRunning || state.activePlayer() != 0 || state.phase() == Phase.GAME_OVER) return;
        selectedHand = Objects.equals(selectedHand, index) ? null : index;
        selectedCell = null;
        refresh();
    }

    private void selectCell(BoardPosition position) {
        if (playerOneBot || botRunning || state.phase() == Phase.GAME_OVER) return;
        selectedCell = Objects.equals(selectedCell, position) ? null : position;
        selectedHand = null;
        refresh();
    }

    private void clearSelection() {
        selectedHand = null;
        selectedCell = null;
        refresh();
    }

    private void executeSelectedAction() {
        ActionOption option = actionList.getSelectedValue();
        if (option == null) {
            message("Choose an action from the list. Double-click also executes it.");
            return;
        }
        executeHuman(option.command());
    }

    private void executeHuman(String command) {
        if (playerOneBot || botRunning || state.activePlayer() != 0 || state.phase() == Phase.GAME_OVER) return;
        if (!confirmOpportunityRisk(command)) return;
        Map<UUID, BoardSnapshot> before = captureBoard();
        String result = commands.execute(command);
        showResolution(command, before);
        addHistory("You", describe(command));
        message(result);
        selectedHand = null;
        selectedCell = null;
        refresh();
        if (state.phase() != Phase.GAME_OVER && isAutomatedPlayer(state.activePlayer())) runBotTurn();
    }

    private void runBotTurn() {
        botRunning = true;
        refresh();
        javax.swing.Timer timer = new javax.swing.Timer(380, null);
        timer.addActionListener(e -> {
            if (state.phase() == Phase.GAME_OVER || !isAutomatedPlayer(state.activePlayer())) {
                timer.stop();
                botRunning = false;
                refresh();
                return;
            }
            int active = state.activePlayer();
            offerReaction(active);
            if (state.phase() == Phase.GAME_OVER || state.activePlayer() != active) return;
            Map<UUID, BoardSnapshot> before = captureBoard();
            BotPlayer.Decision decision = bot.takeNextAction(state, commands, active);
            showResolution(decision.command(), before);
            addHistory(active == 0 ? "Bot 1" : "Bot 2", describe(decision.command()));
            message((active == 0 ? "Bot 1: " : "Bot 2: ") + describe(decision.command()) + " — " + decision.result());
            refresh();
        });
        timer.start();
    }

    private boolean isAutomatedPlayer(int player) {
        return player == 1 || playerOneBot;
    }

    private void offerReaction(int active) {
        int reacting = 1 - active;
        if (isAutomatedPlayer(reacting)) {
            Map<UUID, BoardSnapshot> before = captureBoard();
            BotPlayer.Decision reaction = bot.react(state, commands, reacting);
            if (reaction != null) {
                showResolution(reaction.command(), before);
                addHistory(reacting == 0 ? "Bot 1" : "Bot 2", describe(reaction.command()));
            }
            return;
        }
        List<String> reactions = hints.spellActionsForPlayer(state, reacting);
        if (reactions.isEmpty()) return;
        List<ActionOption> options = new ArrayList<>();
        options.add(new ActionOption("Pass reaction", ""));
        reactions.forEach(command -> options.add(new ActionOption(describe(command), command)));
        String chosen = new VisualReactionDialog(reacting, reactions).choose();
        if (chosen != null && !chosen.isBlank()) {
            Map<UUID, BoardSnapshot> before = captureBoard();
            message(commands.execute(chosen));
            showResolution(chosen, before);
            addHistory("You", describe(chosen));
            refresh();
        }
    }

    private void refresh() {
        syncSystemEvents();
        turnLabel.setText("Turn " + state.turnNumber() + " • " + phaseText());
        PlayerState human = state.player(0);
        PlayerState enemy = state.player(1);
        humanLabel.setText((playerOneBot ? "BOT 1" : "YOU") + " • " + humanFaction + "   GP " + human.currentGp()
                + "  (+" + state.gpIncomePerTurn(0) + "/turn)"
                + "   Deck " + human.deck().size() + "   Discard " + human.discard().size());
        botLabel.setText("BOT 2 • " + botFaction + "   GP " + enemy.currentGp()
                + "  (+" + state.gpIncomePerTurn(1) + "/turn)"
                + "   Hand " + enemy.hand().size() + "   Deck " + enemy.deck().size());
        refreshBoard();
        refreshHand();
        refreshActions();
        if (state.phase() == Phase.GAME_OVER) showWinner();
    }

    private void refreshBoard() {
        for (BoardPosition position : state.board().positions()) {
            JButton cell = boardButtons.get(position);
            Optional<UUID> topId = state.board().topAt(position);
            Color base = position.isOnPlayerSide(0) ? HUMAN_PLOT : BOT_PLOT;
            cell.setBackground(base);
            cell.setForeground(Color.WHITE);
            Intent intent = destinationIntent(position);
            cell.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED,
                    base.brighter(), base.brighter(), base.darker(), base.darker()), new CompoundBorder(
                    new LineBorder(Objects.equals(selectedCell, position) ? SELECTED
                            : intent != null ? intent.color : base.brighter(),
                            Objects.equals(selectedCell, position) || intent != null ? 4 : 1, true),
                    new EmptyBorder(7, 7, 7, 7))));
            if (topId.isEmpty()) {
                cell.setIcon(null);
                cell.setText("<html><font color='#78899d'>" + position.x() + "," + position.y() + "</font>"
                        + (intent == null ? "" : "<br><b><font color='" + intent.hex + "'>" + intent.label + "</font></b>") + "</html>");
                cell.setToolTipText("Empty cell " + position.x() + "," + position.y() + " — drop a legal card or unit here");
                continue;
            }
            CardInstance card = state.card(topId.orElseThrow()).orElseThrow();
            CardDefinition def = card.definition();
            cell.setBackground(blend(base, factionColor(def.faction()), .42f));
            cell.setIcon(CardArtFactory.boardIconFor(def));
            cell.setHorizontalTextPosition(SwingConstants.CENTER);
            cell.setVerticalTextPosition(SwingConstants.BOTTOM);
            cell.setHorizontalAlignment(SwingConstants.CENTER);
            cell.setVerticalAlignment(SwingConstants.CENTER);
            int stack = state.board().stackAt(position).size();
            String stats = def.type() == CardType.CHARACTER
                    ? "ATK " + new GameEngine().effectiveAttack(state, card) + "  DEF " + card.defenseRemaining()
                    + "/" + card.effectiveDefense()
                    + (card.combatDamage() > 0 ? "  MARKED " + card.combatDamage() : "")
                    : "HP " + Math.max(0, def.hitPoints() - card.damage()) + "/" + def.hitPoints()
                    + (card.damage() > 0 ? "  DMG " + card.damage() : "");
            EffectBadge badge = effectBadges.get(position);
            cell.setText("<html><font color='#aebdd0'>" + position.x() + "," + position.y()
                    + " • " + def.type() + (stack > 1 ? " • STACK " + stack : "") + "</font><br>"
                    + "<b>" + html(def.name()) + "</b><br>" + stats
                    + (badge == null ? "" : "<br><b><font color='" + badge.color() + "'>" + html(badge.text()) + "</font></b>")
                    + (intent == null ? "" : "<br><b><font color='" + intent.hex + "'>" + intent.label + "</font></b>") + "</html>");
            cell.setToolTipText("<html><b>" + html(def.name()) + "</b><br>" + html(keywordLine(def))
                    + (developmentText(def).isBlank() ? "" : "<br>" + html(developmentText(def)))
                    + "<br>Click a highlighted cell or drag; right-click to inspect stack.</html>");
        }
    }

    private void refreshHand() {
        handPanel.removeAll();
        handButtons.clear();
        List<UUID> hand = state.player(0).hand();
        for (int index = 0; index < hand.size(); index++) {
            CardInstance card = state.card(hand.get(index)).orElseThrow();
            CardDefinition def = card.definition();
            JButton tile = new JButton(cardHtml(def), CardArtFactory.iconFor(def, 190, 78));
            tile.setPreferredSize(new Dimension(208, 170));
            tile.setMaximumSize(new Dimension(208, 170));
            tile.setMinimumSize(new Dimension(208, 170));
            tile.setVerticalAlignment(SwingConstants.TOP);
            tile.setHorizontalAlignment(SwingConstants.CENTER);
            tile.setHorizontalTextPosition(SwingConstants.CENTER);
            tile.setVerticalTextPosition(SwingConstants.BOTTOM);
            tile.setForeground(Color.WHITE);
            tile.setBackground(blend(PANEL_LIGHT, factionColor(def.faction()), .36f));
            tile.setFocusPainted(false);
            tile.setBorder(new CompoundBorder(
                    new LineBorder(Objects.equals(selectedHand, index) ? SELECTED : factionColor(def.faction()),
                            Objects.equals(selectedHand, index) ? 4 : 1, true),
                    new EmptyBorder(7, 7, 7, 7)));
            final int selectedIndex = index;
            tile.addMouseListener(dragListener(new DragSource(selectedIndex, null)));
            tile.setEnabled(!playerOneBot && !botRunning && state.activePlayer() == 0 && state.phase() != Phase.GAME_OVER);
            handButtons.add(tile);
            handPanel.add(tile);
            handPanel.add(Box.createHorizontalStrut(8));
        }
        handPanel.revalidate();
        handPanel.repaint();
    }

    private void refreshActions() {
        actionModel.clear();
        if (playerOneBot || botRunning || state.activePlayer() != 0 || state.phase() == Phase.GAME_OVER) return;
        List<String> legal = hints.forActivePlayer(state, new GameEngine());
        legal.stream().filter(this::matchesSelection)
                .map(command -> new ActionOption(describe(command), command))
                .forEach(actionModel::addElement);
        if (actionModel.isEmpty() && (selectedHand != null || selectedCell != null)) {
            actionModel.addElement(new ActionOption("No legal action for that selection", ""));
        }
    }

    private boolean matchesSelection(String command) {
        if (selectedHand != null) {
            return command.matches("(play|burrow|cast) " + selectedHand + "( |$)");
        }
        if (selectedCell != null) {
            String xy = selectedCell.x() + " " + selectedCell.y();
            return command.matches("(move|blink|attack) " + xy + " .*")
                    || command.matches("cast \\d+ " + xy + "( .*)?");
        }
        return true;
    }

    private MouseAdapter dragListener(DragSource source) {
        return new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    if (source.position() != null) showStackInspector(source.position());
                    return;
                }
                if (playerOneBot || botRunning || state.activePlayer() != 0 || state.phase() == Phase.GAME_OVER) return;
                if (source.position() != null && (selectedHand != null || selectedCell != null)
                        && !Objects.equals(selectedCell, source.position()) && isLegalDestination(source.position())) {
                    DragSource selectedSource = new DragSource(selectedHand, selectedCell);
                    dragSource = null;
                    executeDrop(selectedSource, source.position());
                    return;
                }
                dragSource = source;
                selectedHand = source.handIndex();
                selectedCell = source.position();
                refreshBoard();
                refreshActions();
                if (source.handIndex() != null && source.handIndex() < state.player(0).hand().size()) {
                    showPreview(state.card(state.player(0).hand().get(source.handIndex())).orElseThrow());
                } else if (source.position() != null) {
                    state.board().topAt(source.position()).flatMap(state::card).ifPresent(InfiniteConquestGui.this::showPreview);
                }
            }

            @Override public void mouseReleased(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event) || dragSource == null) return;
                Point boardPoint = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), boardPanel);
                BoardPosition destination = boardButtons.entrySet().stream()
                        .filter(entry -> entry.getValue().getBounds().contains(boardPoint))
                        .map(Map.Entry::getKey).findFirst().orElse(null);
                DragSource original = dragSource;
                dragSource = null;
                if (destination == null || Objects.equals(original.position(), destination)) {
                    refresh();
                    return;
                }
                executeDrop(original, destination);
            }

            @Override public void mouseEntered(MouseEvent event) {
                if (state == null) return;
                if (source.handIndex() != null && source.handIndex() < state.player(0).hand().size()) {
                    state.card(state.player(0).hand().get(source.handIndex())).ifPresent(InfiniteConquestGui.this::showPreview);
                } else if (source.position() != null) {
                    state.board().topAt(source.position()).flatMap(state::card).ifPresent(InfiniteConquestGui.this::showPreview);
                }
            }
        };
    }

    private void executeDrop(DragSource source, BoardPosition destination) {
        List<ActionOption> choices = legalCommands().stream()
                .filter(command -> startsAt(command, source))
                .filter(command -> endsAt(command, destination))
                .map(command -> new ActionOption(describe(command), command)).toList();
        if (choices.isEmpty()) {
            message("That is not a legal destination. Gold outlines show where this card can go.");
            refresh();
            return;
        }
        ActionOption choice = choices.get(0);
        if (choices.size() > 1) {
            String stack = stackSummary(destination);
            Object selected = JOptionPane.showInputDialog(this,
                    "Choose the action and stack position.\nCurrent stack (top first): " + stack,
                    "Choose Action / Stack Order",
                    JOptionPane.QUESTION_MESSAGE, null, choices.toArray(), choice);
            if (!(selected instanceof ActionOption selectedOption)) return;
            choice = selectedOption;
        }
        executeHuman(choice.command());
    }

    private List<String> legalCommands() {
        if (playerOneBot || botRunning || state.activePlayer() != 0 || state.phase() == Phase.GAME_OVER) return List.of();
        return hints.forActivePlayer(state, new GameEngine());
    }

    private boolean startsAt(String command, DragSource source) {
        String[] p = command.split("\\s+");
        if (source.handIndex() != null) {
            return (p[0].equals("play") || p[0].equals("burrow") || p[0].equals("cast"))
                    && Integer.parseInt(p[1]) == source.handIndex();
        }
        return p.length >= 3 && (p[0].equals("move") || p[0].equals("blink") || p[0].equals("attack"))
                && Integer.parseInt(p[1]) == source.position().x() && Integer.parseInt(p[2]) == source.position().y();
    }

    private boolean endsAt(String command, BoardPosition destination) {
        String[] p = command.split("\\s+");
        int xIndex = (p[0].equals("move") || p[0].equals("blink") || p[0].equals("attack")) ? 3 : 2;
        return p.length > xIndex + 1 && Integer.parseInt(p[xIndex]) == destination.x()
                && Integer.parseInt(p[xIndex + 1]) == destination.y();
    }

    private boolean isLegalDestination(BoardPosition destination) {
        if (selectedHand == null && selectedCell == null) return false;
        DragSource source = new DragSource(selectedHand, selectedCell);
        return legalCommands().stream().anyMatch(command -> startsAt(command, source) && endsAt(command, destination));
    }

    private Intent destinationIntent(BoardPosition destination) {
        if (selectedHand == null && selectedCell == null) return null;
        DragSource source = new DragSource(selectedHand, selectedCell);
        Set<Intent> intents = new LinkedHashSet<>();
        legalCommands().stream().filter(command -> startsAt(command, source) && endsAt(command, destination))
                .map(Intent::fromCommand).forEach(intents::add);
        if (intents.isEmpty()) return null;
        return intents.size() == 1 ? intents.iterator().next() : Intent.CHOOSE;
    }

    private String stackSummary(BoardPosition position) {
        List<UUID> stack = state.board().stackAt(position);
        if (stack.isEmpty()) return "empty";
        List<String> names = new ArrayList<>();
        for (int i = stack.size() - 1; i >= 0; i--) {
            names.add(state.card(stack.get(i)).orElseThrow().definition().name());
        }
        return String.join(" > ", names);
    }

    private Map<UUID, BoardSnapshot> captureBoard() {
        Map<UUID, BoardSnapshot> snapshot = new HashMap<>();
        for (BoardPosition position : state.board().positions()) {
            for (UUID id : state.board().stackAt(position)) {
                CardInstance card = state.card(id).orElseThrow();
                snapshot.put(id, new BoardSnapshot(id, position, card.damage(), card.definition().hitPoints(),
                        card.definition().defense(), card.definition().name(), card.definition().type(),
                        state.board().topAt(position).filter(id::equals).isPresent()));
            }
        }
        return snapshot;
    }

    private boolean confirmOpportunityRisk(String command) {
        String[] p = command.split("\\s+");
        if (!p[0].equals("move")) return true;
        BoardPosition from = position(p, 1);
        BoardPosition to = position(p, 3);
        Optional<UUID> moverId = state.board().topAt(from);
        if (moverId.isEmpty()) return true;
        List<GameEngine.OpportunityThreat> threats = new GameEngine()
                .opportunityThreats(state, moverId.get(), to);
        if (threats.isEmpty()) return true;
        StringBuilder warning = new StringBuilder("This route crosses enemy attack range:\n\n");
        for (GameEngine.OpportunityThreat threat : threats) {
            warning.append("• ").append(threat.attackerName()).append(" at (")
                    .append(threat.attackerPosition().x()).append(", ")
                    .append(threat.attackerPosition().y()).append(") — ATK ")
                    .append(threat.attack()).append(" vs DEF ").append(threat.moverDefense())
                    .append("; triggers at (").append(threat.triggerPosition().x()).append(", ")
                    .append(threat.triggerPosition().y()).append(")")
                    .append(threat.lethal() ? " — LETHAL" : " — survives").append('\n');
        }
        warning.append("\nEach listed enemy gets one free attack during this move. Continue?");
        return JOptionPane.showConfirmDialog(this, warning.toString(), "Opportunity Attack Warning",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void showResolution(String command, Map<UUID, BoardSnapshot> before) {
        String[] p = command.split("\\s+");
        switch (p[0]) {
            case "move", "blink" -> {
                BoardPosition from = position(p, 1);
                BoardPosition to = position(p, 3);
                badge(to, p[0].equals("blink") ? "BLINK" : "MOVE", "#71d7ff");
                combatOverlay.animate(from, to, new Color(91, 209, 255), false);
                SoundEffects.play(SoundEffects.Cue.MOVE);
            }
            case "play", "burrow" -> {
                BoardPosition to = position(p, 2);
                badge(to, p[0].equals("burrow") ? "BURROWED BELOW TOP" : "PLACED ON TOP", "#78e29a");
                combatOverlay.animate(null, to, p[0].equals("burrow") ? BURROW : DEPLOY, false);
                SoundEffects.play(SoundEffects.Cue.DEPLOY);
            }
            case "attack" -> {
                BoardPosition from = position(p, 1);
                BoardPosition target = position(p, 3);
                boolean ranged = from.distanceTo(target) > 1;
                showTargetResult(target, before, ranged ? "RANGED" : "MELEE", ranged ? "#ffb45b" : "#ff7373");
                combatOverlay.animate(from, target, ranged ? new Color(255, 180, 91) : ATTACK, false);
                BoardSnapshot originalAttacker = before.values().stream()
                        .filter(value -> value.position().equals(from) && value.top()).findFirst().orElse(null);
                if (originalAttacker != null) {
                    CardInstance surviving = state.card(originalAttacker.id()).orElse(null);
                    if (surviving == null || surviving.zone() != Zone.BATTLEFIELD) {
                        badge(from, "RETALIATION • DESTROYED", "#ff7373");
                        combatOverlay.animate(target, from, ATTACK, false);
                    }
                }
                SoundEffects.play(ranged ? SoundEffects.Cue.RANGED : SoundEffects.Cue.MELEE);
            }
            case "cast", "react" -> {
                int targetIndex = p[0].equals("react") ? 3 : 2;
                BoardPosition target = position(p, targetIndex);
                showTargetResult(target, before, "SPELL", "#df92ff");
                combatOverlay.animate(null, target, new Color(223, 146, 255), false);
                SoundEffects.play(SoundEffects.Cue.SPELL);
            }
            default -> { }
        }
        for (GameEvent event : state.events()) {
            if (event.sequence() <= lastSystemEvent) continue;
            if (event.type() == GameEvent.Type.OPPORTUNITY_ATTACK) {
                String[] detail = event.detail().split("\\s+");
                try {
                    UUID attackerId = UUID.fromString(detail[0]);
                    UUID moverId = UUID.fromString(detail[2]);
                    BoardSnapshot attacker = before.get(attackerId);
                    BoardSnapshot mover = before.get(moverId);
                    if (attacker != null && mover != null) {
                        String[] xy = detail[4].split(",");
                        BoardPosition trigger = new BoardPosition(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                        boolean destroyed = state.card(moverId).map(card -> card.zone() != Zone.BATTLEFIELD).orElse(true);
                        badge(trigger, "FREE ATTACK • " + (destroyed ? "DESTROYED" : "BLOCKED"), "#ff7373");
                        combatOverlay.animate(attacker.position(), trigger, ATTACK, false);
                        SoundEffects.play(SoundEffects.Cue.MELEE);
                    }
                } catch (IllegalArgumentException ignored) { }
            }
            if (event.type() != GameEvent.Type.EXHAUSTION_DAMAGE) continue;
            try {
                UUID id = UUID.fromString(event.detail().split("\\s+")[0]);
                BoardSnapshot old = before.get(id);
                if (old == null) continue;
                CardInstance current = state.card(id).orElse(null);
                int damage = current == null ? old.hitPoints() - old.damage()
                        : Math.max(1, current.damage() - old.damage());
                String cause = "EXHAUSTION";
                badge(old.position(), cause + " • " + damage + " DMG", "#ffcf5c");
                combatOverlay.animate(null, old.position(), new Color(255, 207, 92), true);
                SoundEffects.play(SoundEffects.Cue.PENALTY);
            } catch (IllegalArgumentException ignored) { }
        }
    }

    private void showTargetResult(BoardPosition target, Map<UUID, BoardSnapshot> before,
                                  String cause, String color) {
        BoardSnapshot old = before.values().stream()
                .filter(value -> value.position().equals(target) && value.top()).findFirst().orElse(null);
        if (old == null) { badge(target, cause, color); return; }
        CardInstance current = state.card(old.id()).orElse(null);
        if (current == null || current.zone() != Zone.BATTLEFIELD) {
            String outcome = cause.equals("SPELL") && current != null && current.zone() == Zone.HAND
                    ? "RETURNED TO HAND" : "DESTROYED";
            badge(target, cause + " • " + outcome, color);
            if (outcome.equals("DESTROYED")) SoundEffects.play(SoundEffects.Cue.DESTROY);
            return;
        }
        int damage = current.damage() - old.damage();
        String outcome = damage > 0 ? damage + " DMG" : cause.equals("SPELL") ? "RESOLVED" : "BLOCKED";
        badge(target, cause + " • " + outcome, color);
        if (damage > 0) SoundEffects.play(SoundEffects.Cue.DAMAGE);
    }

    private BoardPosition position(String[] parts, int index) {
        return new BoardPosition(Integer.parseInt(parts[index]), Integer.parseInt(parts[index + 1]));
    }

    private void badge(BoardPosition position, String text, String color) {
        EffectBadge badge = new EffectBadge(text, color);
        effectBadges.put(position, badge);
        javax.swing.Timer clear = new javax.swing.Timer(1600, e -> {
            if (effectBadges.get(position) == badge) effectBadges.remove(position);
            refreshBoard();
        });
        clear.setRepeats(false);
        clear.start();
    }

    private void syncSystemEvents() {
        if (state == null) return;
        for (GameEvent event : state.events()) {
            if (event.sequence() <= lastSystemEvent) continue;
            String actor = event.playerId() == 0 ? (playerOneBot ? "Bot 1" : "You")
                    : event.playerId() == 1 ? "Bot 2" : "Rules";
            String detail = switch (event.type()) {
                case EXHAUSTION_DAMAGE -> friendlyCardDetail(event.detail(), " takes 1 exhaustion damage (empty deck)");
                case GP_GENERATED -> event.detail();
                case CARD_DESTROYED -> friendlyCardDetail(event.detail(), " was destroyed");
                case CAPITAL_PASSIVE_TRIGGERED -> "Capital passive — " + event.detail().replace('_', ' ').toLowerCase(Locale.ROOT);
                case DEVELOPMENT_PASSIVE_TRIGGERED -> friendlyDevelopmentPassive(event.detail());
                case OPPORTUNITY_ATTACK -> friendlyOpportunityDetail(event.detail());
                case GAME_OVER -> "GAME OVER — " + event.detail();
                default -> null;
            };
            if (detail != null) {
                addHistory(actor, detail);
                if (event.type() == GameEvent.Type.EXHAUSTION_DAMAGE
                        || event.type() == GameEvent.Type.GAME_OVER) actionTabs.setSelectedIndex(1);
            }
            lastSystemEvent = event.sequence();
        }
    }

    private String friendlyCardDetail(String detail, String suffix) {
        String idText = detail.split("\\s+")[0];
        try {
            CardInstance card = state.card(UUID.fromString(idText)).orElse(null);
            if (card != null) {
                String amount = detail.contains(" takes ") ? detail.substring(detail.indexOf(" takes ")) : "";
                return card.definition().name() + suffix + (amount.isBlank() ? "" : " (" + amount.trim() + ")");
            }
        } catch (IllegalArgumentException ignored) { }
        return detail + suffix;
    }

    private String friendlyOpportunityDetail(String detail) {
        String[] parts = detail.split("\\s+");
        try {
            String attacker = state.card(UUID.fromString(parts[0])).map(card -> card.definition().name()).orElse("Enemy");
            String mover = state.card(UUID.fromString(parts[2])).map(card -> card.definition().name()).orElse("mover");
            return attacker + " made a free opportunity attack against " + mover;
        } catch (IllegalArgumentException exception) {
            return "Opportunity attack resolved";
        }
    }

    private String friendlyDevelopmentPassive(String detail) {
        String[] parts = detail.split("\\s+", 2);
        try {
            String name = state.card(UUID.fromString(parts[0]))
                    .map(card -> card.definition().name()).orElse("Development");
            return name + " passive — " + (parts.length > 1 ? parts[1] : "resolved");
        } catch (IllegalArgumentException exception) {
            return "Development passive resolved";
        }
    }

    private void showPreview(CardInstance card) {
        CardDefinition def = card.definition();
        previewArt.setIcon(CardArtFactory.iconFor(def, 300, 88));
        String stats = def.type() == CardType.CHARACTER
                ? "ATK " + new GameEngine().effectiveAttack(state, card) + "  DEF " + card.effectiveDefense()
                + "  MOVE " + def.movement() + "  RANGE " + new GameEngine().effectiveRange(state, card)
                : def.isPermanent() ? "HP " + Math.max(0, def.hitPoints() - card.damage()) + "/" + def.hitPoints()
                : effectLine(def);
        previewText.setText("<html><b>" + html(def.name()) + "</b> — " + html(playRequirement(def)) + " " + title(def.type().name())
                + "<br>" + html(stats) + (developmentText(def).isBlank() ? "" : "<br><font color='#67d890'><b>" + html(developmentText(def)) + "</b></font>")
                + "<br><font color='#d9b95f'>" + html(keywordLine(def)) + "</font></html>");
    }

    private void showStackInspector(BoardPosition position) {
        List<UUID> stack = state.board().stackAt(position);
        if (stack.isEmpty()) {
            message("Cell (" + position.x() + ", " + position.y() + ") is empty.");
            return;
        }
        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        for (int i = stack.size() - 1; i >= 0; i--) {
            CardInstance card = state.card(stack.get(i)).orElseThrow();
            CardDefinition def = card.definition();
            JLabel row = new JLabel("<html><b>" + (i == stack.size() - 1 ? "TOP" : "Layer " + (i + 1))
                    + " — " + html(def.name()) + "</b><br>" + title(def.type().name()) + " • "
                    + html(def.faction()) + " • " + html(keywordLine(def))
                    + (developmentText(def).isBlank() ? "" : "<br><font color='#67d890'>" + html(developmentText(def)) + "</font>")
                    + "</html>",
                    CardArtFactory.iconFor(def, 140, 58), SwingConstants.LEFT);
            row.setForeground(Color.WHITE);
            row.setBorder(new EmptyBorder(7, 7, 7, 7));
            cards.add(row);
        }
        cards.setBackground(PANEL);
        JScrollPane scroll = new JScrollPane(cards);
        scroll.setPreferredSize(new Dimension(510, Math.min(420, 95 * stack.size())));
        JOptionPane.showMessageDialog(this, scroll,
                "Stack at (" + position.x() + ", " + position.y() + ") — top first", JOptionPane.PLAIN_MESSAGE);
    }

    private void addHistory(String actor, String action) {
        historyModel.addElement(String.format("%02d  %s: %s", ++historyNumber, actor, action));
        int last = historyModel.size() - 1;
        if (last >= 0) historyList.ensureIndexIsVisible(last);
    }

    static String describe(String command) {
        String[] p = command.split("\\s+");
        return switch (p[0]) {
            case "play" -> "Place hand #" + p[1] + " on TOP at (" + p[2] + ", " + p[3] + ")";
            case "burrow" -> "Burrow hand #" + p[1] + " beneath (" + p[2] + ", " + p[3] + ")";
            case "move" -> "Move (" + p[1] + ", " + p[2] + ") → (" + p[3] + ", " + p[4] + ")";
            case "blink" -> "Blink (" + p[1] + ", " + p[2] + ") → (" + p[3] + ", " + p[4] + ")";
            case "attack" -> (Math.max(Math.abs(Integer.parseInt(p[1]) - Integer.parseInt(p[3])),
                    Math.abs(Integer.parseInt(p[2]) - Integer.parseInt(p[4]))) > 1 ? "Ranged attack " : "Melee attack ")
                    + "(" + p[1] + ", " + p[2] + ") → (" + p[3] + ", " + p[4] + ")";
            case "cast" -> "Cast hand #" + p[1] + " on (" + p[2] + ", " + p[3] + ")"
                    + (p.length > 4 ? " → (" + p[4] + ", " + p[5] + ")" : "");
            case "react" -> "React with hand #" + p[2] + " on (" + p[3] + ", " + p[4] + ")";
            case "end" -> "End your turn";
            default -> command;
        };
    }

    private String phaseText() {
        if (state.phase() == Phase.GAME_OVER) return "GAME OVER";
        if (botRunning || state.activePlayer() == 1) return "BOT TURN";
        return "YOUR TURN";
    }

    private void showWinner() {
        String result = state.winner().isEmpty() ? "DRAW"
                : state.winner().getAsInt() == 0 ? "VICTORY" : "DEFEAT";
        turnLabel.setText("Turn " + state.turnNumber() + " • " + result);
        String reason = state.events().stream().filter(event -> event.type() == GameEvent.Type.GAME_OVER)
                .reduce((first, second) -> second).map(GameEvent::detail).orElse("Match ended");
        message("<b>" + result + "</b> — " + reason
                + ". A player loses immediately when they have no permanents. Start a new match to play again.");
        if (!winnerSoundPlayed) {
            winnerSoundPlayed = true;
            boolean playerOneWon = state.winner().isPresent() && state.winner().getAsInt() == 0;
            SoundEffects.play(playerOneWon ? SoundEffects.Cue.VICTORY : SoundEffects.Cue.DEFEAT);
        }
    }

    private void message(String text) {
        messageLabel.setText("<html>" + html(text).replace("&lt;b&gt;", "<b>").replace("&lt;/b&gt;", "</b>") + "</html>");
    }

    private String cardHtml(CardDefinition def) {
        String stats = def.type() == CardType.CHARACTER
                ? "ATK " + def.attack() + "  DEF " + def.defense() + "  MOVE " + def.movement() + "  RANGE " + def.range()
                : def.isPermanent() ? "HP " + def.hitPoints() : effectLine(def);
        return "<html><font color='#f0bf49'><b>" + html(playRequirement(def)) + "</b></font> &nbsp; " + def.type()
                + "<br><b>" + html(def.name()) + "</b><br><br>" + stats
                + (developmentText(def).isBlank() ? "" : "<br><font color='#67d890'><b>" + html(developmentText(def)) + "</b></font>")
                + "<br><font color='#c9d5e4'>" + html(keywordLine(def)) + "</font></html>";
    }

    private String developmentText(CardDefinition definition) {
        if (definition.type() != CardType.LAND && definition.type() != CardType.STRUCTURE) return "";
        String passive = DevelopmentRules.passiveText(definition.developmentPassive());
        return "+" + definition.gpGeneration() + " GP/TURN" + (passive.isBlank() ? "" : " • " + passive);
    }

    private String playRequirement(CardDefinition definition) {
        if (definition.type() == CardType.LAND || definition.type() == CardType.STRUCTURE) {
            return "TURN " + Math.max(1, definition.cost()) + " • FREE";
        }
        return definition.cost() + " GP";
    }

    private String keywordLine(CardDefinition def) {
        if (def.keywords().isEmpty()) return def.faction();
        return def.faction() + " • " + def.keywords().stream()
                .map(keyword -> title(keyword.name().replace('_', ' ')))
                .collect(java.util.stream.Collectors.joining(" • "));
    }

    private String effectLine(CardDefinition def) {
        if (def.effects().isEmpty()) return def.faction();
        SpellEffect effect = def.effects().get(0);
        return title(effect.type().name().replace('_', ' ')) + " " + effect.amount()
                + " — " + title(effect.target().name().replace('_', ' '));
    }

    private JPanel panel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL);
        panel.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED,
                        new Color(67, 82, 107), new Color(52, 66, 89), new Color(8, 13, 22), new Color(12, 18, 29)),
                new EmptyBorder(10, 10, 10, 10)));
        return panel;
    }

    private JLabel section(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        return label;
    }

    private JButton button(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(new Color(48, 83, 108));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new EmptyBorder(6, 12, 6, 12)));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.addActionListener(listener);
        return button;
    }

    private void styleMeter(JLabel label, Color color) {
        label.setForeground(color);
        label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        label.setBorder(new CompoundBorder(new LineBorder(color.darker(), 1, true), new EmptyBorder(6, 8, 6, 8)));
    }

    private void installTheme() {
        UIManager.put("Button.arc", 12);
        UIManager.put("ScrollBar.thumb", new Color(68, 85, 109));
        UIManager.put("ScrollBar.track", PANEL);
        UIManager.put("ToolTip.background", PANEL_LIGHT);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("MenuBar.background", PANEL);
        UIManager.put("Menu.background", PANEL);
        UIManager.put("Menu.foreground", Color.WHITE);
        UIManager.put("MenuItem.background", PANEL_LIGHT);
        UIManager.put("MenuItem.foreground", Color.WHITE);
        UIManager.put("TabbedPane.background", PANEL);
        UIManager.put("TabbedPane.foreground", Color.WHITE);
    }

    private Color factionColor(String faction) {
        return switch (faction.toUpperCase(Locale.ROOT)) {
            case "ARES" -> new Color(193, 61, 55);
            case "ATHENA" -> new Color(85, 128, 177);
            case "HADES" -> new Color(111, 70, 143);
            case "HEPHAESTUS" -> new Color(191, 103, 45);
            case "POSEIDON" -> new Color(32, 137, 171);
            case "ZEUS" -> new Color(178, 154, 63);
            default -> new Color(93, 110, 130);
        };
    }

    private Color blend(Color first, Color second, float amount) {
        float keep = 1f - amount;
        return new Color((int) (first.getRed() * keep + second.getRed() * amount),
                (int) (first.getGreen() * keep + second.getGreen() * amount),
                (int) (first.getBlue() * keep + second.getBlue() * amount));
    }

    private String html(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String title(String value) {
        return value.charAt(0) + value.substring(1).toLowerCase(Locale.ROOT);
    }

    private record ActionOption(String label, String command) {
        @Override public String toString() { return label; }
    }

    private record CapitalChoice(CardDefinition card) {
        @Override public String toString() { return card.name() + " • " + card.hitPoints() + " HP"; }
    }

    private record MulliganChoice(UUID id, CardDefinition card) {
        @Override public String toString() {
            String requirement = card.type() == CardType.LAND || card.type() == CardType.STRUCTURE
                    ? "Turn " + Math.max(1, card.cost()) + ", +" + card.gpGeneration() + " GP/turn"
                    : card.cost() + " GP";
            return card.name() + " — " + card.type() + " — " + requirement;
        }
    }

    private record DeckRow(CardDefinition card, int copies) { }

    private final class CardDefinitionRenderer extends JLabel implements ListCellRenderer<CardDefinition> {
        CardDefinitionRenderer() { setOpaque(true); setBorder(new EmptyBorder(5, 7, 5, 7)); }
        @Override public Component getListCellRendererComponent(JList<? extends CardDefinition> list,
                CardDefinition card, int index, boolean selected, boolean focused) {
            setIcon(CardArtFactory.iconFor(card, 126, 68));
            setText("<html><b>" + html(card.name()) + "</b><br>" + card.type() + " • "
                    + html(playRequirement(card)) + (developmentText(card).isBlank() ? "" : "<br>" + html(developmentText(card))) + "</html>");
            setBackground(selected ? new Color(48, 112, 137) : blend(PANEL_LIGHT, factionColor(card.faction()), .25f));
            setForeground(Color.WHITE); return this;
        }
    }

    private final class DeckRowRenderer extends JLabel implements ListCellRenderer<DeckRow> {
        DeckRowRenderer() { setOpaque(true); setBorder(new EmptyBorder(5, 7, 5, 7)); }
        @Override public Component getListCellRendererComponent(JList<? extends DeckRow> list,
                DeckRow row, int index, boolean selected, boolean focused) {
            CardDefinition card = row.card();
            setIcon(CardArtFactory.iconFor(card, 126, 68));
            setText("<html><b>×" + row.copies() + " &nbsp; " + html(card.name()) + "</b><br>"
                    + card.type() + " • " + html(playRequirement(card)) + "</html>");
            setBackground(selected ? new Color(48, 112, 137) : blend(PANEL_LIGHT, factionColor(card.faction()), .25f));
            setForeground(Color.WHITE); return this;
        }
    }

    private JButton visualChoiceCard(CardDefinition card, int width, int height) {
        JButton result = new JButton(cardHtml(card), CardArtFactory.iconFor(card, width - 16, 78));
        Dimension size = new Dimension(width, height);
        result.setPreferredSize(size); result.setMinimumSize(size); result.setMaximumSize(size);
        result.setVerticalAlignment(SwingConstants.TOP);
        result.setHorizontalAlignment(SwingConstants.CENTER);
        result.setHorizontalTextPosition(SwingConstants.CENTER);
        result.setVerticalTextPosition(SwingConstants.BOTTOM);
        result.setForeground(Color.WHITE);
        result.setBackground(blend(PANEL_LIGHT, factionColor(card.faction()), .40f));
        result.setBorder(new CompoundBorder(new LineBorder(factionColor(card.faction()), 2, true),
                new EmptyBorder(6, 6, 6, 6)));
        result.setFocusPainted(false);
        return result;
    }

    private final class VisualReactionDialog extends JDialog {
        private final int reacting;
        private final List<String> commands;
        private final JPanel spellTray = new JPanel();
        private final Map<BoardPosition, JButton> targets = new LinkedHashMap<>();
        private final JLabel instruction = new JLabel("Choose a spell, then click or drag it to a glowing target.");
        private Integer selectedHandIndex;
        private String result;

        VisualReactionDialog(int reacting, List<String> commands) {
            super(InfiniteConquestGui.this, "Reaction Window", true);
            this.reacting = reacting;
            this.commands = commands;
            spellTray.setLayout(new BoxLayout(spellTray, BoxLayout.X_AXIS));
            spellTray.setBackground(PANEL);
            JPanel board = new JPanel(new GridLayout(BoardPosition.HEIGHT, BoardPosition.WIDTH, 5, 5));
            board.setOpaque(false);
            for (int y = BoardPosition.HEIGHT - 1; y >= 0; y--) for (int x = 0; x < BoardPosition.WIDTH; x++) {
                BoardPosition position = new BoardPosition(x, y);
                JButton cell = new JButton(reactionCellText(position));
                cell.setPreferredSize(new Dimension(135, 82));
                cell.setForeground(Color.WHITE); cell.setBackground(position.isOnPlayerSide(0) ? HUMAN_PLOT : BOT_PLOT);
                state.board().topAt(position).flatMap(state::card)
                        .ifPresent(card -> cell.setIcon(CardArtFactory.iconFor(card.definition(), 72, 42)));
                cell.setHorizontalTextPosition(SwingConstants.CENTER);
                cell.setVerticalTextPosition(SwingConstants.BOTTOM);
                cell.addActionListener(e -> chooseTarget(position));
                cell.addMouseListener(new MouseAdapter() {
                    @Override public void mouseReleased(MouseEvent e) { if (selectedHandIndex != null) chooseTarget(position); }
                });
                targets.put(position, cell); board.add(cell);
            }
            commands.stream().map(this::handIndex).distinct().forEach(index -> {
                CardDefinition spell = state.card(state.player(reacting).hand().get(index)).orElseThrow().definition();
                JButton card = visualChoiceCard(spell, 190, 170);
                card.addActionListener(e -> selectSpell(index));
                card.addMouseListener(new MouseAdapter() {
                    @Override public void mousePressed(MouseEvent e) { selectSpell(index); }
                    @Override public void mouseReleased(MouseEvent e) {
                        Point point = SwingUtilities.convertPoint(card, e.getPoint(), board);
                        targets.entrySet().stream().filter(entry -> entry.getValue().getBounds().contains(point))
                                .map(Map.Entry::getKey).findFirst().ifPresent(VisualReactionDialog.this::chooseTarget);
                    }
                });
                spellTray.add(card); spellTray.add(Box.createHorizontalStrut(8));
            });
            JButton pass = button("Pass Reaction", e -> dispose());
            instruction.setForeground(Color.WHITE);
            JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
            header.add(instruction, BorderLayout.CENTER); header.add(pass, BorderLayout.EAST);
            JPanel content = panel(new BorderLayout(8, 8)); content.setBorder(new EmptyBorder(12, 12, 12, 12));
            content.add(header, BorderLayout.NORTH);
            content.add(board, BorderLayout.CENTER);
            JScrollPane spells = new JScrollPane(spellTray, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            spells.setBorder(new TitledBorder(new LineBorder(CAST, 2), "REACTION SPELLS", TitledBorder.LEFT,
                    TitledBorder.TOP, getFont(), CAST));
            spells.setPreferredSize(new Dimension(800, 220)); content.add(spells, BorderLayout.SOUTH);
            setContentPane(content); setSize(900, 790); setLocationRelativeTo(InfiniteConquestGui.this);
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); refreshTargets();
        }

        String choose() { setVisible(true); return result; }

        private int handIndex(String command) { return Integer.parseInt(command.split("\\s+")[2]); }
        private BoardPosition target(String command) {
            String[] p = command.split("\\s+"); return new BoardPosition(Integer.parseInt(p[3]), Integer.parseInt(p[4]));
        }
        private void selectSpell(int index) {
            selectedHandIndex = index;
            CardDefinition spell = state.card(state.player(reacting).hand().get(index)).orElseThrow().definition();
            instruction.setText("<html><b>" + html(spell.name()) + " selected.</b> Drop or click a gold target.</html>");
            refreshTargets();
        }
        private void refreshTargets() {
            targets.forEach((position, button) -> {
                boolean legal = selectedHandIndex != null && commands.stream()
                        .anyMatch(command -> handIndex(command) == selectedHandIndex && target(command).equals(position));
                button.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED),
                        new LineBorder(legal ? CAST : PANEL_LIGHT, legal ? 4 : 1, true)));
                button.setEnabled(selectedHandIndex == null || legal);
            });
        }
        private void chooseTarget(BoardPosition position) {
            if (selectedHandIndex == null) return;
            List<String> matches = commands.stream().filter(command -> handIndex(command) == selectedHandIndex
                    && target(command).equals(position)).toList();
            if (matches.isEmpty()) return;
            if (matches.size() == 1) result = matches.get(0);
            else result = chooseTeleportDestination(matches);
            if (result != null) dispose();
        }
        private String chooseTeleportDestination(List<String> matches) {
            JPanel grid = new JPanel(new GridLayout(BoardPosition.HEIGHT, BoardPosition.WIDTH, 4, 4));
            grid.setBackground(PANEL);
            final String[] selected = {null};
            JDialog picker = new JDialog(this, "Choose teleport destination", true);
            for (int y = BoardPosition.HEIGHT - 1; y >= 0; y--) for (int x = 0; x < BoardPosition.WIDTH; x++) {
                BoardPosition position = new BoardPosition(x, y);
                String match = matches.stream().filter(command -> {
                    String[] p = command.split("\\s+");
                    return Integer.parseInt(p[5]) == position.x() && Integer.parseInt(p[6]) == position.y();
                }).findFirst().orElse(null);
                JButton cell = new JButton(state.board().isEmpty(position) ? position.x() + "," + position.y() : "OCCUPIED");
                cell.setEnabled(match != null); cell.setBackground(match == null ? PANEL_LIGHT : MOVE); cell.setForeground(Color.WHITE);
                cell.addActionListener(e -> { selected[0] = match; picker.dispose(); }); grid.add(cell);
            }
            picker.setContentPane(grid); picker.setSize(620, 520); picker.setLocationRelativeTo(this); picker.setVisible(true);
            return selected[0];
        }
        private String reactionCellText(BoardPosition position) {
            Optional<UUID> top = state.board().topAt(position);
            if (top.isEmpty()) return "<html>" + position.x() + "," + position.y() + "<br>EMPTY</html>";
            CardDefinition card = state.card(top.get()).orElseThrow().definition();
            return "<html>" + position.x() + "," + position.y() + " • " + card.type() + "<br><b>" + html(card.name()) + "</b></html>";
        }
    }

    private final class VisualMulliganDialog extends JDialog {
        private final List<MulliganChoice> choices;
        private final Set<UUID> discarded = new LinkedHashSet<>();
        private final JPanel handTray = new JPanel();
        private final JPanel discardTray = new JPanel();
        private final JLabel count = new JLabel();
        private UUID dragging;

        VisualMulliganDialog(List<MulliganChoice> choices) {
            super(InfiniteConquestGui.this, "Opening Mulligan", true);
            this.choices = choices;
            handTray.setLayout(new BoxLayout(handTray, BoxLayout.X_AXIS));
            discardTray.setLayout(new BoxLayout(discardTray, BoxLayout.X_AXIS));
            handTray.setBackground(new Color(24, 72, 58));
            discardTray.setBackground(new Color(78, 42, 50));
            count.setForeground(Color.WHITE);
            JButton confirm = button("Confirm Mulligan", e -> dispose());
            JPanel content = panel(new BorderLayout(8, 8));
            content.setBorder(new EmptyBorder(12, 12, 12, 12));
            JLabel directions = new JLabel("<html><b>Choose up to 3 cards to discard and redraw.</b> Click a card or drag it between trays. Unselected cards stay in your hand.</html>");
            directions.setForeground(Color.WHITE);
            JPanel trays = new JPanel(new GridLayout(2, 1, 0, 10)); trays.setOpaque(false);
            trays.add(tray("OPENING HAND — THESE CARDS STAY", handTray, new Color(104, 211, 139)));
            trays.add(tray("DISCARD & REDRAW — UP TO 3", discardTray, new Color(239, 106, 122)));
            JPanel footer = new JPanel(new BorderLayout()); footer.setOpaque(false);
            footer.add(count, BorderLayout.WEST); footer.add(confirm, BorderLayout.EAST);
            content.add(directions, BorderLayout.NORTH); content.add(trays, BorderLayout.CENTER); content.add(footer, BorderLayout.SOUTH);
            setContentPane(content); setSize(1150, 610); setLocationRelativeTo(InfiniteConquestGui.this);
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            rebuild();
        }

        Set<UUID> choose() { setVisible(true); return Set.copyOf(discarded); }

        private JPanel tray(String title, JPanel cards, Color color) {
            JPanel result = new JPanel(new BorderLayout(5, 5)); result.setOpaque(false);
            result.add(section(title, color), BorderLayout.NORTH);
            JScrollPane scroll = new JScrollPane(cards, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.setBorder(new LineBorder(color, 2, true)); result.add(scroll, BorderLayout.CENTER); return result;
        }

        private void rebuild() {
            handTray.removeAll(); discardTray.removeAll();
            for (MulliganChoice choice : choices) {
                JPanel destination = discarded.contains(choice.id()) ? discardTray : handTray;
                JButton card = visualChoiceCard(choice.card(), 180, 185);
                card.addMouseListener(new MouseAdapter() {
                    @Override public void mousePressed(MouseEvent e) { dragging = choice.id(); }
                    @Override public void mouseReleased(MouseEvent e) {
                        Point handPoint = SwingUtilities.convertPoint(card, e.getPoint(), handTray);
                        Point discardPoint = SwingUtilities.convertPoint(card, e.getPoint(), discardTray);
                        if (discardTray.contains(discardPoint)) moveToDiscard(choice.id());
                        else if (handTray.contains(handPoint)) discarded.remove(choice.id());
                        else toggle(choice.id());
                        dragging = null; rebuild();
                    }
                });
                destination.add(card); destination.add(Box.createHorizontalStrut(7));
            }
            count.setText("DISCARDING " + discarded.size() + "/3 • KEEPING " + (choices.size() - discarded.size()));
            handTray.revalidate(); discardTray.revalidate(); handTray.repaint(); discardTray.repaint();
        }

        private void toggle(UUID id) { if (!discarded.remove(id)) moveToDiscard(id); }
        private void moveToDiscard(UUID id) {
            if (discarded.size() >= 3 && !discarded.contains(id)) { Toolkit.getDefaultToolkit().beep(); return; }
            discarded.add(id);
        }
    }

    private record MatchChoice(String humanFaction, CardDefinition humanCapital,
                               String botFaction, CardDefinition botCapital, boolean playerOneBot,
                               BoardPosition humanCapitalPosition) { }

    private final class CapitalPlacementPicker extends JPanel {
        private BoardPosition selected = new BoardPosition(1, 0);
        private final Map<BoardPosition, JButton> cells = new LinkedHashMap<>();

        CapitalPlacementPicker() {
            super(new GridLayout(3, 4, 4, 4));
            setOpaque(false);
            for (int y = 2; y >= 0; y--) for (int x = 0; x < 4; x++) {
                BoardPosition position = new BoardPosition(x, y);
                JButton cell = new JButton((x + 1) + "," + (y + 1));
                cell.setToolTipText("Your plot — column " + (x + 1) + ", row " + (y + 1));
                cell.addActionListener(e -> { selected = position; refreshSelection(); });
                cells.put(position, cell);
                add(cell);
            }
            refreshSelection();
        }

        BoardPosition selected() { return selected; }

        private void refreshSelection() {
            cells.forEach((position, cell) -> {
                boolean chosen = position.equals(selected);
                cell.setText(chosen ? "CAPITAL" : (position.x() + 1) + "," + (position.y() + 1));
                cell.setBackground(chosen ? DEPLOY : PANEL_LIGHT);
                cell.setForeground(Color.WHITE);
            });
        }
    }
    private record DragSource(Integer handIndex, BoardPosition position) { }
    private record EffectBadge(String text, String color) { }
    private record BoardSnapshot(UUID id, BoardPosition position, int damage, int hitPoints,
                                 int defense, String name, CardType type, boolean top) { }

    private enum Intent {
        MOVE("MOVE", InfiniteConquestGui.MOVE, "#48b5e6"),
        ATTACK("ATTACK", InfiniteConquestGui.ATTACK, "#f45c5c"),
        DEPLOY("PLACE ON TOP", InfiniteConquestGui.DEPLOY, "#68d38b"),
        BURROW("BURROW BELOW TOP", InfiniteConquestGui.BURROW, "#be79eb"),
        CAST("SPELL TARGET", InfiniteConquestGui.CAST, "#f6c24e"),
        BLINK("BLINK", InfiniteConquestGui.SELECTED, "#5bd1ff"),
        CHOOSE("CHOOSE ACTION", Color.WHITE, "#ffffff");

        private final String label;
        private final Color color;
        private final String hex;
        Intent(String label, Color color, String hex) { this.label = label; this.color = color; this.hex = hex; }
        static Intent fromCommand(String command) {
            return switch (command.substring(0, command.indexOf(' '))) {
                case "move" -> MOVE; case "attack" -> ATTACK; case "burrow" -> BURROW;
                case "cast" -> CAST; case "blink" -> BLINK; default -> DEPLOY;
            };
        }
    }

    private final class CombatOverlay extends JComponent {
        private Animation animation;
        private final ArrayDeque<Animation> queued = new ArrayDeque<>();
        private javax.swing.Timer timer;

        @Override public boolean contains(int x, int y) { return false; }

        void animate(BoardPosition from, BoardPosition to, Color color, boolean fromRules) {
            Animation requested = new Animation(from, to, color, fromRules, 0L);
            if (animation != null) {
                queued.addLast(requested);
                return;
            }
            start(requested);
        }

        private void start(Animation requested) {
            animation = new Animation(requested.from(), requested.to(), requested.color(),
                    requested.fromRules(), System.nanoTime());
            timer = new javax.swing.Timer(28, event -> {
                repaint();
                if (animation != null && animation.progress() >= 1f) {
                    if (queued.isEmpty()) {
                        ((javax.swing.Timer) event.getSource()).stop();
                        animation = null;
                        repaint();
                    } else {
                        Animation next = queued.removeFirst();
                        animation = new Animation(next.from(), next.to(), next.color(),
                                next.fromRules(), System.nanoTime());
                    }
                }
            });
            timer.start();
            repaint();
        }

        @Override protected void paintComponent(Graphics graphics) {
            if (animation == null) return;
            JButton targetButton = boardButtons.get(animation.to());
            if (targetButton == null || !targetButton.isShowing()) return;
            Point target = SwingUtilities.convertPoint(targetButton,
                    targetButton.getWidth() / 2, targetButton.getHeight() / 2, this);
            Point source;
            if (animation.fromRules()) {
                source = new Point(target.x, 8);
            } else if (animation.from() == null) {
                source = SwingUtilities.convertPoint(handPanel,
                        Math.max(20, handPanel.getWidth() / 2), 0, this);
            } else {
                JButton sourceButton = boardButtons.get(animation.from());
                if (sourceButton == null || !sourceButton.isShowing()) return;
                source = SwingUtilities.convertPoint(sourceButton,
                        sourceButton.getWidth() / 2, sourceButton.getHeight() / 2, this);
            }

            float progress = animation.progress();
            float fade = progress < .72f ? 1f : Math.max(0f, (1f - progress) / .28f);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setComposite(AlphaComposite.SrcOver.derive(.85f * fade));
            g.setColor(animation.color());
            g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(source.x, source.y, target.x, target.y);

            double angle = Math.atan2(target.y - source.y, target.x - source.x);
            int arrow = 15;
            Path2D head = new Path2D.Double();
            head.moveTo(target.x, target.y);
            head.lineTo(target.x - arrow * Math.cos(angle - .48), target.y - arrow * Math.sin(angle - .48));
            head.lineTo(target.x - arrow * Math.cos(angle + .48), target.y - arrow * Math.sin(angle + .48));
            head.closePath();
            g.fill(head);

            float travel = Math.min(1f, progress / .72f);
            int orbX = Math.round(source.x + (target.x - source.x) * travel);
            int orbY = Math.round(source.y + (target.y - source.y) * travel);
            g.setColor(Color.WHITE);
            g.fillOval(orbX - 7, orbY - 7, 14, 14);
            g.setColor(animation.color());
            g.setStroke(new BasicStroke(4f));
            int pulse = 22 + Math.round(26 * progress);
            g.drawOval(target.x - pulse / 2, target.y - pulse / 2, pulse, pulse);
            g.dispose();
        }
    }

    private record Animation(BoardPosition from, BoardPosition to, Color color,
                             boolean fromRules, long startedAt) {
        float progress() {
            return Math.min(1f, (System.nanoTime() - startedAt) / 800_000_000f);
        }
    }
}
