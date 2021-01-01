package org.grandtestauto.distributed;

import java.lang.annotation.*;

/**
 * This annotation is for indicating the <code>Grade</code>
 * of a package of unit tests. To set the grade for a package
 * of unit tests, annotate the package's <code>UnitTester</code>.
 *
 * @author Tim Lavers
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleGrade {
    int grade();
}
