/******************************************************************************
 * $Workfile: EmailAddress.java $
 * $Revision: 1.2 $
 * $Author: edaugherty $
 * $Date: 2003/10/01 19:30:24 $
 *
 ******************************************************************************
 * This program is a 100% Java Email Server.
 ******************************************************************************
 * Copyright (C) 2001, Eric Daugherty
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 ******************************************************************************
 * For current versions and more information, please visit:
 * http://www.ericdaugherty.com/java/mail
 *
 * or contact the author at:
 * java@ericdaugherty.com
 *
 ******************************************************************************
 * This program is based on the CSRMail project written by Calvin Smith.
 * http://crsemail.sourceforge.net/
 *****************************************************************************/
package jet.testtools.test;

import java.io.*;

/**
 * Represents a full email address, including username and domain.
 * Adapted from Eric Daugherty's package/
 */
public class EmailAddress implements Serializable {

    private String username = "";
    private String domain = "";

    /**
     * Creates an empty email address.  This is possible form
     * SMTP messages that have no MAIL FROM address.
     */
    public EmailAddress() {
    }

    /**
     * Creates a new instance of this class using a single string
     * that contains the full email address (joe@mydomain.com).
     *
     * @param fullAddress in the form user@domain.
     */
    public EmailAddress( String fullAddress ) {
        //Parse toAddress into username and domain.
        int index = fullAddress.indexOf( "@" );
        if (index == -1) {
            throw new IllegalArgumentException( "Not an email address: '" + fullAddress + "'" );
        }
        username = fullAddress.substring( 0, index ).trim();
        domain = fullAddress.substring( index + 1 ).trim();
    }

    /**
     * The full address
     */
    public String toString() {
        if (domain.isEmpty() && username.isEmpty()) {
            return "";
        } else {
            StringBuffer fullAddress = new StringBuffer( username );
            fullAddress.append( "@" );
            fullAddress.append( domain );
            return fullAddress.toString();
        }
    }

    public String getDomain() {
        return domain;
    }
}