package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Single window for the whole application.
 *
 * Every screen is a JPanel registered against a CardLayout. Navigation
 * happens via {@link #show(String)} and {@link #back()}. A top navigation
 * bar with Back / Home / Logout is shown only after sign-in.
 */
public class MainFrame extends JFrame {

    /* ===== Card names ===== */
    public static final String WELCOME   = "welcome";
    public static final String LOGIN     = "login";
    public static final String SIGNUP    = "signup";
    public static final String HOME      = "home";
    public static final String CITY      = "city";
    public static final String TOURISM   = "tourism";
    public static final String HOTELS    = "hotels";
    public static final String PLACES    = "places";
    public static final String FOOD      = "food";
    public static final String TRANSPORT = "transport";
    public static final String SHOPPING  = "shopping";

    private final CardLayout cards = new CardLayout();
    private final JPanel cardHost = new JPanel(cards);
    private final NavBar navBar;

    private final Deque<String> history = new ArrayDeque<>();
    private String currentCard;

    private String currentDisplayName;
    private String currentUsername;

    public MainFrame() {
        setTitle("Smart City Guide");
        setSize(1080, 720);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(UI.BG);
        cardHost.setBackground(UI.BG);

        navBar = new NavBar(this);
        navBar.setVisible(false); // hidden before login

        setLayout(new BorderLayout());
        add(navBar, BorderLayout.NORTH);
        add(cardHost, BorderLayout.CENTER);

        registerPanels();
        show(WELCOME);
    }

    private void registerPanels() {
        cardHost.add(new WelcomePanel(this), WELCOME);
        cardHost.add(new LoginPanel(this),   LOGIN);
        cardHost.add(new SignUpPanel(this),  SIGNUP);
        cardHost.add(new HomePanel(this),    HOME);
        cardHost.add(new CityInfoPanel(),    CITY);
        cardHost.add(new TourismPanel(this), TOURISM);
        cardHost.add(new HotelsPanel(),      HOTELS);
        cardHost.add(new TouristPlacesPanel(), PLACES);
        cardHost.add(new RestaurantsPanel(),   FOOD);
        cardHost.add(new TransportationPanel(), TRANSPORT);
        cardHost.add(new ShoppingMallsPanel(),  SHOPPING);
    }

    /** Navigate forward to a named card. Pushes the prior card onto history. */
    public void show(String card) {
        navigate(card, true);
    }

    /** Pop one level of history. Falls back to HOME if history is empty. */
    public void back() {
        if (!history.isEmpty()) {
            navigate(history.pop(), false);
        } else {
            navigate(HOME, false);
        }
    }

    private void navigate(String card, boolean recordHistory) {
        if (recordHistory && currentCard != null && !currentCard.equals(card)) {
            history.push(currentCard);
        }
        cards.show(cardHost, card);
        currentCard = card;
        navBar.setActive(card);
        navBar.setBackEnabled(!history.isEmpty() && !isRootCard(card));
    }

    private boolean isRootCard(String card) {
        return WELCOME.equals(card) || LOGIN.equals(card)
            || SIGNUP.equals(card)  || HOME.equals(card);
    }

    /** Called by LoginPanel on successful auth. */
    public void onLoginSuccess(String displayName, String username) {
        this.currentDisplayName = displayName;
        this.currentUsername = username;
        navBar.setUser(displayName);
        navBar.setVisible(true);
        history.clear();
        for (Component c : cardHost.getComponents()) {
            if (c instanceof HomePanel) {
                ((HomePanel) c).refreshGreeting();
            }
        }
        revalidate();
        navigate(HOME, false);
    }

    /** Called by NavBar on logout. */
    public void logout() {
        this.currentDisplayName = null;
        this.currentUsername = null;
        history.clear();
        navBar.setVisible(false);
        revalidate();
        navigate(WELCOME, false);
    }

    public String getCurrentDisplayName() { return currentDisplayName; }
    public String getCurrentUsername()    { return currentUsername; }

    public static void main(String[] args) {
        UI.applyDarkLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            if (!Bootstrap.run()) {
                System.exit(0);
            }
            new MainFrame().setVisible(true);
        });
    }
}
