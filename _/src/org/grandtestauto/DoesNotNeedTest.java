package org.grandtestauto;

import java.lang.annotation.*;

/**
 * This annotation is for marking classes that do not
 * need to be tested.
 *
 * @author Tim Lavers
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DoesNotNeedTest {
    String reason();
}
