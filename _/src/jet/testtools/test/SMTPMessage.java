/******************************************************************************
 * $Workfile: SMTPMessage.java $
 * $Revision: 1.4 $
 * $Author: edaugherty $
 * $Date: 2003/12/24 02:26:35 $
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
import java.util.*;

/**
 * Cut down version of Eric Daugherty's class of the same name.
 */
public class SMTPMessage implements Serializable {

    private EmailAddress fromAddress;
    private List<EmailAddress> toAddresses = new ArrayList<EmailAddress>();
    private List<String> dataLines = new ArrayList<String>();
    private long size = 0;

    public EmailAddress getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress( EmailAddress fromAddress ) {
        this.fromAddress = fromAddress;
    }

    public List<EmailAddress> getToAddresses() {
        return toAddresses;
    }

    public void addToAddress( EmailAddress toAddress ) {
        toAddresses.add( toAddress );
    }

    public List<String> getDataLines() {
        return dataLines;
    }

    public void addDataLine( String line ) {
        size += line.length();
        dataLines.add( line );
    }

    public long getSize() {
        if (size == 0) {
            for (String dataLine : dataLines) {
                size += (dataLine).length();
            }
        }
        return size;
    }

    public String data() {
        StringBuilder result = new StringBuilder();
        for (String dataLine : dataLines) {
            result.append( dataLine );
        }
        return result.toString();
    }
}
