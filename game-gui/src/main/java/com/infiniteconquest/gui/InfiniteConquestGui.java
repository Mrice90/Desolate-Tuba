package com.infiniteconquest.gui;

import com.infiniteconquest.cli.*;
import com.infiniteconquest.core.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private final JPanel handPanel = new JPanel();
    private final DefaultListModel<ActionOption> actionModel = new DefaultListModel<>();
    private final JList<ActionOption> actionList = new JList<>(actionModel);
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private final JList<String> historyList = new JList<>(historyModel);
    private final JTabbedPane actionTabs = new JTabbedPane();
    private final JLabel previewArt = new JLabel();
    private final JLabel previewText = new JLabel("<html><b>Hover over a card</b><br>Right-click a board stack to inspect it.</html>");
    private final Map<BoardPosition, JButton> boardButtons = new HashMap<>();
    private final List<JButton> handButtons = new ArrayList<>();

    private GameState state;
    private CommandProcessor commands;
    private final ActionHints hints = new ActionHints();
    private final BotPlayer bot = new BotPlayer();
    private final DemoMatchFactory matchFactory = new DemoMatchFactory();
    private final FactionDecks factionDecks = new FactionDecks(matchFactory.pool());
    private final CapitalPassiveRules passiveRules = new CapitalPassiveRules();
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

    public InfiniteConquestGui() {
        super("Infinite Conquest");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1380, 900);
        setLocationRelativeTo(null);
        installTheme();
        setContentPane(buildScreen());
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

        JButton newMatch = button("New Match", e -> newMatch());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);
        left.add(title);
        left.add(turnLabel);
        header.add(left, BorderLayout.WEST);
        header.add(meters, BorderLayout.CENTER);
        header.add(newMatch, BorderLayout.EAST);
        return header;
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
                cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                cell.addMouseListener(dragListener(new DragSource(null, position)));
                boardButtons.put(position, cell);
                boardPanel.add(cell);
            }
        }
        surround.add(enemy, BorderLayout.NORTH);
        surround.add(boardPanel, BorderLayout.CENTER);
        surround.add(human, BorderLayout.SOUTH);
        return surround;
    }

    private JComponent buildActions() {
        JPanel side = panel(new BorderLayout(8, 8));
        side.setPreferredSize(new Dimension(350, 100));
        JPanel preview = new JPanel(new BorderLayout(8, 8));
        preview.setOpaque(false);
        preview.setPreferredSize(new Dimension(330, 140));
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
        side.add(actionTabs, BorderLayout.CENTER);

        JButton execute = button("Execute Selected", e -> executeSelectedAction());
        JButton clear = button("Clear Selection", e -> clearSelection());
        JButton end = button("End Turn", e -> executeHuman("end"));
        end.setBackground(new Color(143, 66, 71));
        JPanel controls = new JPanel(new GridLayout(3, 1, 0, 7));
        controls.setOpaque(false);
        controls.add(execute);
        controls.add(clear);
        controls.add(end);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        messageLabel.setForeground(new Color(205, 215, 229));
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        messageLabel.setVerticalAlignment(SwingConstants.TOP);
        messageLabel.setPreferredSize(new Dimension(320, 42));
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
        state = matchFactory.create(seed,
                factionDecks.starter(humanFaction), factionDecks.starter(botFaction),
                humanCapital, botCapital);
        commands = new CommandProcessor(state);
        selectedHand = null;
        selectedCell = null;
        botRunning = false;
        playerOneBot = choice.playerOneBot();
        lastSystemEvent = state.events().stream().mapToLong(GameEvent::sequence).max().orElse(-1);
        historyModel.clear();
        historyNumber = 0;
        addHistory("Match", title(humanFaction) + " vs " + title(botFaction));
        message(humanFaction + " vs " + botFaction + " started. Deploy a card or move a unit.");
        refresh();
        if (playerOneBot) SwingUtilities.invokeLater(this::runBotTurn);
    }

    private MatchChoice defaultChoice() {
        return new MatchChoice("ZEUS", matchFactory.capitals().forFaction("ZEUS").get(0),
                "ARES", matchFactory.capitals().forFaction("ARES").get(0), false);
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

        int result = JOptionPane.showConfirmDialog(this, setup, "Configure Conquest",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        CapitalChoice selectedHuman = (CapitalChoice) humanCapitalBox.getSelectedItem();
        CapitalChoice selectedBot = (CapitalChoice) botCapitalBox.getSelectedItem();
        return new MatchChoice((String) humanFactionBox.getSelectedItem(), selectedHuman.card(),
                (String) botFactionBox.getSelectedItem(), selectedBot.card(),
                playerOneControl.getSelectedIndex() == 1);
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
        String result = commands.execute(command);
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
            BotPlayer.Decision decision = bot.takeNextAction(state, commands, active);
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
            BotPlayer.Decision reaction = bot.react(state, commands, reacting);
            if (reaction != null) addHistory(reacting == 0 ? "Bot 1" : "Bot 2", describe(reaction.command()));
            return;
        }
        List<String> reactions = hints.spellActionsForPlayer(state, reacting);
        if (reactions.isEmpty()) return;
        List<ActionOption> options = new ArrayList<>();
        options.add(new ActionOption("Pass reaction", ""));
        reactions.forEach(command -> options.add(new ActionOption(describe(command), command)));
        Object choice = JOptionPane.showInputDialog(this,
                "Player 2 acted. Spend saved GP on a reaction?", "Reaction Window",
                JOptionPane.QUESTION_MESSAGE, null, options.toArray(), options.get(0));
        if (choice instanceof ActionOption option && !option.command().isBlank()) {
            message(commands.execute(option.command()));
            addHistory("You", describe(option.command()));
            refresh();
        }
    }

    private void refresh() {
        syncSystemEvents();
        turnLabel.setText("Turn " + state.turnNumber() + " • " + phaseText());
        PlayerState human = state.player(0);
        PlayerState enemy = state.player(1);
        humanLabel.setText((playerOneBot ? "BOT 1" : "YOU") + " • " + humanFaction + "   GP " + human.currentGp() + "/" + human.maximumGp()
                + "   Deck " + human.deck().size() + "   Discard " + human.discard().size());
        botLabel.setText("BOT 2 • " + botFaction + "   GP " + enemy.currentGp() + "/" + enemy.maximumGp()
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
            cell.setBorder(new CompoundBorder(
                    new LineBorder(Objects.equals(selectedCell, position) ? SELECTED
                            : intent != null ? intent.color : base.brighter(),
                            Objects.equals(selectedCell, position) || intent != null ? 4 : 1, true),
                    new EmptyBorder(7, 7, 7, 7)));
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
            cell.setIcon(CardArtFactory.iconFor(def, 72, 46));
            cell.setHorizontalTextPosition(SwingConstants.RIGHT);
            int stack = state.board().stackAt(position).size();
            String stats = def.type() == CardType.CHARACTER
                    ? "ATK " + card.effectiveAttack() + "  DEF " + card.effectiveDefense()
                    : "HP " + Math.max(0, def.hitPoints() - card.damage()) + "/" + def.hitPoints();
            cell.setText("<html><font color='#aebdd0'>" + position.x() + "," + position.y()
                    + " • " + def.type() + (stack > 1 ? " • STACK " + stack : "") + "</font><br>"
                    + "<b>" + html(def.name()) + "</b><br><br>" + stats
                    + "<br><font color='#d9b95f'>" + html(keywordLine(def)) + "</font>"
                    + (intent == null ? "" : "<br><b><font color='" + intent.hex + "'>" + intent.label + "</font></b>") + "</html>");
            cell.setToolTipText(def.name() + " — drag to a highlighted cell; right-click to inspect stack");
        }
    }

    private void refreshHand() {
        handPanel.removeAll();
        handButtons.clear();
        List<UUID> hand = state.player(0).hand();
        for (int index = 0; index < hand.size(); index++) {
            CardInstance card = state.card(hand.get(index)).orElseThrow();
            CardDefinition def = card.definition();
            JButton tile = new JButton(cardHtml(def), CardArtFactory.iconFor(def, 168, 68));
            tile.setPreferredSize(new Dimension(185, 155));
            tile.setMaximumSize(new Dimension(185, 155));
            tile.setMinimumSize(new Dimension(185, 155));
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

    private void syncSystemEvents() {
        if (state == null) return;
        for (GameEvent event : state.events()) {
            if (event.sequence() <= lastSystemEvent) continue;
            String actor = event.playerId() == 0 ? (playerOneBot ? "Bot 1" : "You")
                    : event.playerId() == 1 ? "Bot 2" : "Rules";
            String detail = switch (event.type()) {
                case CONQUEST_PRESSURE -> friendlyCardDetail(event.detail(), " takes late-game Conquest Pressure");
                case EXHAUSTION_DAMAGE -> friendlyCardDetail(event.detail(), " takes 1 exhaustion damage (empty deck)");
                case CARD_DESTROYED -> friendlyCardDetail(event.detail(), " was destroyed");
                case CAPITAL_PASSIVE_TRIGGERED -> "Capital passive — " + event.detail().replace('_', ' ').toLowerCase(Locale.ROOT);
                case GAME_OVER -> "GAME OVER — " + event.detail();
                default -> null;
            };
            if (detail != null) {
                addHistory(actor, detail);
                if (event.type() == GameEvent.Type.CONQUEST_PRESSURE
                        || event.type() == GameEvent.Type.EXHAUSTION_DAMAGE
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

    private void showPreview(CardInstance card) {
        CardDefinition def = card.definition();
        previewArt.setIcon(CardArtFactory.iconFor(def, 300, 88));
        String stats = def.type() == CardType.CHARACTER
                ? "ATK " + card.effectiveAttack() + "  DEF " + card.effectiveDefense()
                + "  MOVE " + def.movement() + "  RANGE " + def.range()
                : def.isPermanent() ? "HP " + Math.max(0, def.hitPoints() - card.damage()) + "/" + def.hitPoints()
                : effectLine(def);
        previewText.setText("<html><b>" + html(def.name()) + "</b> — " + def.cost() + " GP " + title(def.type().name())
                + "<br>" + html(stats) + "<br><font color='#d9b95f'>" + html(keywordLine(def)) + "</font></html>");
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
                    + html(def.faction()) + " • " + html(keywordLine(def)) + "</html>",
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
    }

    private void message(String text) {
        messageLabel.setText("<html>" + html(text).replace("&lt;b&gt;", "<b>").replace("&lt;/b&gt;", "</b>") + "</html>");
    }

    private String cardHtml(CardDefinition def) {
        String stats = def.type() == CardType.CHARACTER
                ? "ATK " + def.attack() + "  DEF " + def.defense() + "  MOVE " + def.movement() + "  RANGE " + def.range()
                : def.isPermanent() ? "HP " + def.hitPoints() : effectLine(def);
        return "<html><font color='#f0bf49'><b>" + def.cost() + " GP</b></font> &nbsp; " + def.type()
                + "<br><b>" + html(def.name()) + "</b><br><br>" + stats
                + "<br><font color='#c9d5e4'>" + html(keywordLine(def)) + "</font></html>";
    }

    private String keywordLine(CardDefinition def) {
        return def.keywords().isEmpty() ? def.faction() : def.faction() + " • " + def.keywords();
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
        panel.setBorder(new CompoundBorder(new LineBorder(new Color(55, 70, 94), 1, true),
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

    private record MatchChoice(String humanFaction, CardDefinition humanCapital,
                               String botFaction, CardDefinition botCapital, boolean playerOneBot) { }
    private record DragSource(Integer handIndex, BoardPosition position) { }

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
}
