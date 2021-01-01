package org.grandtestauto;

import java.util.*;
import java.text.*;

/**
 * Filters strings based on alphabetical comparison with an initial value and a final value;
 * this comparison is over-ridden if an exact value to match is defined.
 */
public class NameFilter {
    public static enum Type {PACKAGE, CLASS, METHOD}
    public static final String SINGLE_SELECTION_META_KEY = "NF_SINGLE";
    public static final String INITIAL_SELECTION_META_KEY = "NF_INITIAL";
    public static final String FINAL_SELECTION_META_KEY = "NF_FINAL";
    public static final String INITIAL_AND_FINAL_SELECTION_META_KEY = "NF_INITIAL_AND_FINAL";
    private String lowerBound;
    private String upperBound;
    private String exactValue;
    private Type type;

    public NameFilter( Type type, String lowerBound, String upperBound, String exactValue ) {
        this.type = type;
        this.exactValue = exactValue;
        if (exactValue == null) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }
    }

    public boolean allowsASinglePackageOnly() {
        return exactValue != null;
    }

    public String loggingMessage() {
        if (alwaysTrue()) return "";
        ResourceBundle rb = ResourceBundle.getBundle( getClass().getName() );
        //Note that exact values always over-ride other limits, and that we've already excluded
        //the possiblility that all limits are null.
        if (exactValue != null) {
            String key = MessageFormat.format( rb.getString( SINGLE_SELECTION_META_KEY ), type );
            return MessageFormat.format( rb.getString( key ), exactValue );
        } else if (lowerBound != null && upperBound != null) {
            String key = MessageFormat.format( rb.getString( INITIAL_AND_FINAL_SELECTION_META_KEY ), type );
            return MessageFormat.format( rb.getString( key ), lowerBound, upperBound );
        } else if (lowerBound != null ) {
            String key = MessageFormat.format( rb.getString( INITIAL_SELECTION_META_KEY ), type );
            return MessageFormat.format( rb.getString( key ), lowerBound );
        } else {
            String key = MessageFormat.format( rb.getString( FINAL_SELECTION_META_KEY ), type );
            return MessageFormat.format( rb.getString( key ), upperBound );
        }
    }

    public boolean accept( String name ) {
        if (exactValue != null) return name.equals( exactValue );
        if (lowerBound != null && name.compareTo( lowerBound ) < 0) return false;
        return !(upperBound != null && name.compareTo( upperBound ) > 0);
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NameFilter that = (NameFilter) o;

        if (exactValue != null ? !exactValue.equals( that.exactValue ) : that.exactValue != null) return false;
        if (lowerBound != null ? !lowerBound.equals( that.lowerBound ) : that.lowerBound != null) return false;
        if (type != that.type) return false;
        if (upperBound != null ? !upperBound.equals( that.upperBound ) : that.upperBound != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (lowerBound != null ? lowerBound.hashCode() : 0);
        result = 29 * result + (upperBound != null ? upperBound.hashCode() : 0);
        result = 29 * result + (exactValue != null ? exactValue.hashCode() : 0);
        result = 29 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder( "[");
        sb.append( type );
        sb.append( ", ");
        sb.append( lowerBound );
        sb.append( ", ");
        sb.append( upperBound );
        sb.append( ", ");
        sb.append( exactValue );
        sb.append( ']');
        return sb.toString();
    }

    private boolean alwaysTrue() {
        return lowerBound  == null && upperBound == null && exactValue == null;
    }
}
