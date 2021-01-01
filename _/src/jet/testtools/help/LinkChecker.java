package jet.testtools.help;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is a very rudimentary tool for checking simple html files for
 * broken links and missing images. It's main purpose is as a stub for
 * a better tool. It has only been used on JavaHelp systems where all
 * of the images are in the same directory as the file being checked.
 * Another shortcoming is that it does not check for in-file links.
 * <p/>
 * The code here is based on that in a Java Technical Tip,
 * "Validating URL Links and Reusing Exceptions" April 2003 by
 * John Zukowski.
 * <p>
 * This class is discssed in Chapter 11.
 *
 * @author Tim Lavers
 */
public class LinkChecker {

    private File toCheck;

    public static Set<String> invalidLinks( File toCheck ) throws IOException {
        return new LinkChecker( toCheck ).invalidLinks();
    }

    private LinkChecker( File toCheck ) {
        this.toCheck = toCheck;
    }

    private Set<String> invalidLinks() throws IOException {
        Set<String> result = new HashSet<String>();
        EditorKit editorKit = new HTMLEditorKit();
        Document document = editorKit.createDefaultDocument();
        document.putProperty( "IgnoreCharsetDirective", true );
        BufferedReader reader = new BufferedReader( new FileReader( toCheck ) );
        try {
            editorKit.read( reader, document, 0 );
        } catch (BadLocationException e) {
            e.printStackTrace();
            assert false : "Problem parsing " + toCheck.getName();
        }
        ElementIterator itor = new ElementIterator( document );
        Element element = itor.next();
        while (element != null) {
            SimpleAttributeSet s = (SimpleAttributeSet) element.getAttributes().getAttribute( HTML.Tag.A );
            if (s != null) {
                String urlString = (String) s.getAttribute( HTML.Attribute.HREF );
                if (urlString != null && !urlString.startsWith( "#")) {
                    boolean ok = checkLink( urlString );
                    if (!ok) result.add( urlString );
                }
            }

            //I would prefer to do this: SimpleAttributeSet s2 = (SimpleAttributeSet) element.getAttributes().getAttribute( HTML.Tag.IMG );
            //but it doesn't work. I haven't had time to investigate.
            if (element.getName().equals( "img" )) {
                String srcString = (String) element.getAttributes().getAttribute( HTML.Attribute.SRC );
                if (srcString != null) {
                    boolean ok = checkImage( srcString );
                    if (!ok) result.add( srcString );
                }
            }
            element = itor.next();
        }
        return result;
    }

    private boolean checkLink( String urlString ) {
        if (urlString.startsWith( "http://" )) {
            try {
                URL url = new URL( urlString );
                URLConnection connection = url.openConnection();
                if (connection instanceof HttpURLConnection) {
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    httpConnection.setRequestMethod( "HEAD" );
                    httpConnection.connect();
                    int response = httpConnection.getResponseCode();
                    System.out.println( "[" + response + "]" + urlString );
                    if (response >= 400 && response < 500) {
                        return false;
                    }
                }
            } catch (IOException e) {
                return false;
            }
        } else {
            File file = new File( toCheck.getParentFile(), urlString );
            if (!file.exists()) return false;
        }
        return true;
    }

    private boolean checkImage( String src ) {
        //At PKS we have the problem that our source control system
        //and image editing software collude to corrupt our image
        //file names by turning the extension to upper case.
        //This results in broken links in the JavaHelp system
        //that are difficult to find using file-based tests
        //because Windows ignores case in filenames.
        //So we ALWAYS regard an uppercase file extension as a broken image.
        String[] fileNameParts = src.split( "\\." );
        if (fileNameParts.length != 2) return false;
        String extension = fileNameParts[1];
        if (extension.length() == 0) return false;
        if (Character.isUpperCase( extension.charAt( 0 ) )) return false;

        File file = new File( toCheck.getParentFile(), src );
        try {
            ImageIcon icon = new ImageIcon( file.getAbsolutePath() );
            return (icon.getIconHeight() > 0 && icon.getIconWidth() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
