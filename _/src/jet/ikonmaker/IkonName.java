package jet.ikonmaker;


/**
 * Helper class that makes it easy to ensure
 * that ikons have the case that the user wants,
 * but that no two ikons that differ only in case can exist.
 *
 * @author Tim Lavers
 */
public class IkonName implements Comparable<IkonName> {

    private String realName;
    private String lowerCaseName;

    public IkonName( String realName ) {
        if (realName.length() ==0) {
            throw new IllegalArgumentException( );
        }
        char[] chars = realName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isJavaIdentifierPart( chars[i] )) {
                throw new IllegalArgumentException( "" + chars[i] );
            }
        }
        this.realName = realName;
        lowerCaseName = realName.toLowerCase();
    }

    public int compareTo( IkonName ikonName ) {
        return lowerCaseName.compareTo( ikonName.lowerCaseName );
    }

    public String toString() {
        return realName;
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (!(o instanceof IkonName)) return false;

        final IkonName ikonName = (IkonName) o;

        if (!lowerCaseName.equals( ikonName.lowerCaseName )) return false;

        return true;
    }

    public int hashCode() {
        return lowerCaseName.hashCode();
    }
}
