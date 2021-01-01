package jet.login;

import jet.util.UserStrings;
import jet.util.Shower;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * A re-implementation of the LabWizard login screen,
 * as discussed in Chapter 7.
 */
public class LoginScreen implements Shower.Showable<ApplicationChoice> {

    public static enum LoginResult {
        ACCEPTED, UNKNOWN_USER, WRONG_PASSWORD, INSUFFICIENT_PRIVILEGES
    }

    public interface Handler {
        LoginResult login( LoginInfo info );
    }

    public static final String FRAME_NAME = "LOGIN_SCREEN";
    private Handler handler;
    private static UserStrings us = LoginUserStrings.instance();
    private ApplicationChoice chosen = null;
    private JTextField userNameField;
    private JTextField passwordField;
    private AbstractAction ok;
    private Map<String, ApplicationChoice> buttonToChoice = new HashMap<String, ApplicationChoice>();
    private AbstractAction cancel;
    private JFrame frame;
    private static Color ORANGE = new Color( 255, 90, 0 );

    public LoginScreen( Handler handler ) {
        this.handler = handler;
    }

    /**
     * Shows the login screen, in a swing thread-safe manner,
     * then waits for a successful login or a cancellation.
     */
    public ApplicationChoice show() {
        //Create and show the login screen, in the Swing event thread.
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    buildUI();
                    frame.setVisible( true );
                    frame.toFront();
                    userNameField.requestFocusInWindow();
                }
            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Wait for the login or cancellation.
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        //Return the selection, or null if cancelled.
        return chosen;
    }

    private void buildUI() {
        Box choicesBox = createChoicesBox();
        JComponent loginArea = loginBox();
        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets( 10, 10, 10, 10 );
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add( logoAndVersionPanel() );
        gbc.gridy = 1;
        panel.add( choicesBox, gbc );
        gbc.gridy = 2;
        panel.add( loginArea, gbc );

        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add( buttonsBox(), gbc );

        panel.setBorder( new LineBorder( Color.BLACK, 3 ) );
        frame = new JFrame();
        frame.setName( FRAME_NAME );
        frame.setUndecorated( true );//No title bar etc.
        frame.setSize( 330, 400 );
        frame.getContentPane().add( panel );
        registerEnterAndCancelKeys();
        frame.setLocationByPlatform( true );
    }

    private void disposeFrameAndWakeLaunchingThread() {
        frame.dispose();
        synchronized (this) {
            notify();
        }
    }

    private void ok() {
        String name = userNameField.getText();
        String password = passwordField.getText();
        LoginInfo loginInfo = new LoginInfo( name, password, chosen );
        LoginResult result;
        try {
            result = handler.login( loginInfo );
        } catch (Exception e) {
            String msg = us.message( LoginUserStrings.LOGIN_EXCEPTION_MSG0 );
            JOptionPane.showMessageDialog( frame, msg, "", JOptionPane.WARNING_MESSAGE );
            return;
        }
        switch (result) {
            case ACCEPTED:
                disposeFrameAndWakeLaunchingThread();
                break;
            case UNKNOWN_USER:
                String msg = us.message( LoginUserStrings.UNKNOWN_USER_MSG1, loginInfo.userName() );
                JOptionPane.showMessageDialog( frame, msg, "", JOptionPane.WARNING_MESSAGE );
                userNameField.requestFocusInWindow();
                break;
            case WRONG_PASSWORD:
                JOptionPane.showMessageDialog( frame, us.message( LoginUserStrings.WRONG_PASSWORD_MSG0 ), "", JOptionPane.WARNING_MESSAGE );
                passwordField.requestFocusInWindow();
                break;
            case INSUFFICIENT_PRIVILEGES: {
                msg = us.message( LoginUserStrings.INSUFFICIENT_PRIVILEGES_MSG2, loginInfo.userName(), us.label( chosen.toString() ) );
                JOptionPane.showMessageDialog( frame, msg, "", JOptionPane.WARNING_MESSAGE );
                userNameField.requestFocusInWindow();
                break;
            }
        }
    }

    private JComponent loginBox() {
        JPanel result = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel userNameLabel =
                new JLabel( us.label( LoginUserStrings.USER_NAME ) );
        userNameLabel.setDisplayedMnemonic(
                us.mnemonic( LoginUserStrings.USER_NAME ) );
        result.add( userNameLabel, gbc );

        userNameField = new JTextField( 8 );
        userNameField.setName( LoginUserStrings.USER_NAME );
        userNameField.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                userNameField.selectAll();
            }
        } );
        userNameField.getDocument().addDocumentListener( new okDocListener() );
        userNameLabel.setLabelFor( userNameField );
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets( 0, 10, 0, 0 );
        result.add( userNameField, gbc );

        JLabel passwordLabel = new JLabel( us.label( LoginUserStrings.PASSWORD ) );
        passwordLabel.setDisplayedMnemonic( us.mnemonic( LoginUserStrings.PASSWORD ) );
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets( 0, 0, 0, 0 );
        result.add( passwordLabel, gbc );

        passwordField = new JPasswordField( 8 );
        passwordField.setName( LoginUserStrings.PASSWORD );
        passwordField.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                passwordField.selectAll();
            }
        } );
        passwordField.getDocument().addDocumentListener( new okDocListener() );
        passwordLabel.setLabelFor( passwordField );
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets( 0, 10, 0, 0 );
        result.add( passwordField, gbc );

        return result;
    }

    private void registerEnterAndCancelKeys() {
        JRootPane rootPane = frame.getRootPane();
        InputMap inputMap = rootPane.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW );
        ActionMap rootActionMap = rootPane.getActionMap();

        // register keyboard action when Enter key is pressed
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "OK" );
        rootActionMap.put( "OK", ok );

        // register keyboard action when Escape key is pressed
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "CANCEL" );
        rootActionMap.put( "CANCEL", cancel );
    }

    private JComponent buttonsBox() {
        ok = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                ok();
            }
        };

        cancel = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                chosen = null;//This will be read by the launching thread, so set to null now to avoid a race condition.
                disposeFrameAndWakeLaunchingThread();
            }
        };
        Box okCancelBox = Box.createHorizontalBox();
        okCancelBox.add( Box.createHorizontalGlue() );
        JButton okButton = us.createJButton( ok, LoginUserStrings.OK );
        okCancelBox.add( okButton );
        setOkButtonState();
        JButton cancelButton = us.createJButton( cancel, LoginUserStrings.CANCEL );
        okCancelBox.add( cancelButton );
        return okCancelBox;
    }

    private void setOkButtonState() {
        ok.setEnabled( passwordField.getText().length() > 0 && userNameField.getText().length() > 0 );
    }

    private Box createChoicesBox() {
        Box box = Box.createVerticalBox();
        final ButtonGroup bg = new ButtonGroup();

        box.add( prettyLabel( LoginUserStrings.CLINICAL_REPORTER_MSG0 ) );

        ApplicationChoice option = ApplicationChoice.VALIDATOR;
        setupRadioButton( box, option, bg );

        option = ApplicationChoice.CLINICAL_KNOWLEDGE_BUILDER;
        setupRadioButton( box, option, bg );

        box.add( Box.createVerticalStrut( 10 ) );
        box.add( prettyLabel( LoginUserStrings.DATA_ENTRY_AUDITOR_MSG0 ) );

        option = ApplicationChoice.AUDITOR;
        setupRadioButton( box, option, bg );

        option = ApplicationChoice.AUDITOR_KNOWLEDGE_BUILDER;
        setupRadioButton( box, option, bg );

        box.add( Box.createVerticalStrut( 10 ) );
        box.add( prettyLabel( LoginUserStrings.ADMINISTRATION_MSG0 ) );

        option = ApplicationChoice.ADMINISTRATOR;
        setupRadioButton( box, option, bg );

        return box;
    }

    static JLabel prettyLabel( String textKey ) {
        JLabel label = new JLabel( us.message( textKey ) );
        Font font = new Font( "Dialog", Font.BOLD, 16 );
        label.setFont( font );
        label.setForeground( ORANGE );
        return label;
    }

    private JRadioButton setupRadioButton( Box toGoIn, ApplicationChoice choice, ButtonGroup bg ) {
        JRadioButton button = new JRadioButton( us.label( choice.toString() ) );
        button.setName( choice.toString() );
        button.setMnemonic( us.mnemonic( choice.toString() ) );
        bg.add( button );
        toGoIn.add( button );
        buttonToChoice.put( button.getText(), choice );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                chosen = buttonToChoice.get( e.getActionCommand() );
                userNameField.requestFocusInWindow();
            }
        } );
        //The validator choice is initially selected.
        if (choice.equals( ApplicationChoice.VALIDATOR )) {
            bg.setSelected( button.getModel(), true );
            chosen = choice;
        }
        return button;
    }

    private JComponent logoAndVersionPanel() {
        Icon logo = new ImageIcon( ClassLoader.getSystemResource( "jet/login/pkslogo.gif" ) );
        JLabel logoLabel = new JLabel( logo );
        JPanel productNameLabel = new LogoPanel();
        Box iconNameVersionBox = Box.createHorizontalBox();
        iconNameVersionBox.add( logoLabel );
        iconNameVersionBox.add( Box.createHorizontalStrut( 5 ) );
        iconNameVersionBox.add( productNameLabel );
        iconNameVersionBox.add( Box.createHorizontalGlue() );
        return iconNameVersionBox;
    }

    private class LogoPanel extends JPanel {
        private Dimension size = new Dimension( 200, 65 );

        public Dimension getMaximumSize() {
            return size;
        }

        public Dimension getMinimumSize() {
            return size;
        }

        public Dimension getPreferredSize() {
            return size;
        }

        public void paintComponent( Graphics g ) {
            super.paintComponent( g );

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            // Prints the initial instructions.
            Font logoFont = new Font( "Arial", Font.BOLD, 36 );
            TextLayout logoLayout = new TextLayout( "LabWizard", logoFont, g2.getFontRenderContext() );
            g2.setColor( ORANGE );
            logoLayout.draw( g2, 0, 40 );

            Font versionFont = new Font( "Arial", Font.PLAIN, 12 );
            TextLayout versionLayout = new TextLayout( "Version 5.51, January 2006", versionFont, g2.getFontRenderContext() );
            g2.setColor( Color.BLACK );
            versionLayout.draw( g2, 0, 60 );
        }
    }

    private class okDocListener implements DocumentListener {
        public void insertUpdate( DocumentEvent e ) {
            setOkButtonState();
        }

        public void removeUpdate( DocumentEvent e ) {
            setOkButtonState();
        }

        public void changedUpdate( DocumentEvent e ) {
            setOkButtonState();
        }
    }
}
