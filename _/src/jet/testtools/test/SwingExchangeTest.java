package jet.testtools.test;

import jet.testtools.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author Tim Lavers
 */
class SwingExchangeTest extends TestBase {
    private AtomicReference<String> se;

    private JButton button;

    public boolean valueTest() throws InterruptedException {
        final String[] texts = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final char[] mnemonics = new char[]{'P', 'I', 'O', 'K'};
        se = new AtomicReference<String>();
        Runnable setupTask = new Runnable() {
            public void run() {
                Box buttonBox = Box.createVerticalBox();
                for (int i = 0; i < texts.length; i++) {
                    buttonBox.add( createButton( texts[i], mnemonics[i] ) );
                }
                setupFrame( buttonBox, null );
            }
        };
        UI.runInEventThread( setupTask );
        Random random = new Random( System.currentTimeMillis() );
        Cyborg borg = new Cyborg();
        UselessWork[] workers = new UselessWork[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new UselessWork();
            workers[i].start();
        }
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt( texts.length );
            borg.altChar( mnemonic( mnemonics[index] ) );
            borg.type( texts[index] );
            borg.enter();
            assert se.get().equals( texts[index] ) : "Broke on i = " + i;
        }
        for (UselessWork worker : workers) {
            worker.flagToStop = true;
            worker.join();
        }
        cleanup();
        return true;
    }

    public boolean setValueTest() throws Exception {
        final String[] texts = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        se = new AtomicReference<String>();
        final AtomicInteger clickCount = new AtomicInteger( 0 );
        Runnable setupTask = new Runnable() {
            public void run() {
                button = new JButton( "Button" );
                button.setMnemonic( mnemonic( 'B' ) );
                button.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        frame.setTitle( se.get() );
                        clickCount.incrementAndGet();
                    }
                } );
                setupFrame( button, null );
            }
        };
        UI.runInEventThread( setupTask );
        Random random = new Random( System.currentTimeMillis() );
        Cyborg borg = new Cyborg();
        UselessWork[] workers = new UselessWork[Runtime.getRuntime().availableProcessors() + 1];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new UselessWork();
            workers[i].start();
        }
        /*
        With 1000 iterations, this test passed once
        and then broke on i=80, 48, 20, 28, 638, 12,
        30, 9, 59, 52, 132, 32, 34, 167, 650, 2, 32, 11, 0
         */
        for (int i = 0; i < 100; i++) {
            int index = random.nextInt( texts.length );
            se.set( texts[index] );
            borg.altChar( 'B' );
            String title = UI.getTitle( frame );
            assert title.equals( texts[index] ) : "Broke on i=" + i + ". Title should be: " + texts[index] + " but is: " + title + ", click count: " + clickCount.get();
        }
        for (UselessWork worker : workers) {
            worker.flagToStop = true;
            worker.join();
        }
        cleanup();
        return true;
    }

    class UselessWork extends Thread {
        volatile boolean flagToStop = false;
        double val = 100;

        public void run() {
            while (!flagToStop) {
                val = Math.sqrt( val ) * Math.sqrt( val );
            }
        }
    }

    private JButton createButton( final String text, char mnemonic ) {
        JButton b = new JButton( text );
        b.setMnemonic( mnemonic( mnemonic ) );
        b.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String entered = JOptionPane.showInputDialog( "SETest" );
                se.set( entered );
            }
        } );
        return b;
    }

    private Integer mnemonic( final char key ) {
        String fieldName = "VK_" + key;
        try {
            return KeyEvent.class.getField( fieldName ).getInt( null );
        } catch (Exception e) {
            return null;
        }
    }
}
