/******************************************************************************
 * $Workfile: SMTPProcessor.java $
 * $Revision: 1.11 $
 * $Author: edaugherty $
 * $Date: 2003/11/08 01:14:15 $
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

import jet.testtools.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Implementation of RFC 821 for testing.
 * Adapted from Eric Daugherty's jes software.
 */
public class SMTPProcessor {

    private static final String WELCOME_MESSAGE = "220 Welcome to SMTPProcessor.";
    private static final String MESSAGE_DISCONNECT = "221 SMTP server signing off.";
    private static final String MESSAGE_OK = "250 OK";
    private static final String MESSAGE_COMMAND_ORDER_INVALID = "503 Command not allowed here.";
    private static final String MESSAGE_USER_INVALID = "451 Address is invalid.";
    private static final String MESSAGE_SEND_DATA = "354 Start mail input; end with <CRLF>.<CRLF>";
    private static final String MESSAGE_INVALID_COMMAND = "500 Command Unrecognized: ";
    private static final String MESSAGE_MESSAGE_TOO_LARGE = "552 Message size exceeds fixed maximum message size.";

    //Commands
    private static final String COMMAND_HELO = "HELO";
    private static final String COMMAND_RSET = "RSET";
    private static final String COMMAND_NOOP = "NOOP";
    private static final String COMMAND_QUIT = "QUIT";
    private static final String COMMAND_MAIL_FROM = "MAIL";
    private static final String COMMAND_RCPT_TO = "RCPT";
    private static final String COMMAND_DATA = "DATA";

    //SMTP Commands
    public int NONE = 0;
    public int HELO = 1;
    public int QUIT = 2;
    public int MAIL_FROM = 3;
    public int RCPT_TO = 4;
    public int DATA = 5;
    public int DATA_FINISHED = 6;
    public int RSET = 7;

    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private Socket socket;
    private String clientIp;
    private SMTPMessage message;
    private PrintWriter out;
    private BufferedReader in;
    private Worker worker;
    private List<SMTPMessage> messagesReceived = Collections.synchronizedList( new LinkedList<SMTPMessage>() );
    private boolean debug = false;

    public SMTPProcessor( boolean debug ) throws IOException {
        serverSocket = new ServerSocket( 25 );
        //Set the socket to timeout every 10 seconds so it does not
        //just block forever.
        serverSocket.setSoTimeout( 10000 );
        worker = new Worker();
        this.debug = debug;
    }

    public void start() {
        worker.start();
    }

    public Collection<SMTPMessage> messagesReceived() {
        return messagesReceived;
    }

    public void deleteAllMessages() {
        messagesReceived().clear();
    }

    public boolean checkForMessage( final String to, final String subject, final String body, int timeoutInSeconds ) {
        if (debug) {
            System.out.println( "checkForMessage..." );
            System.out.println( "to = " + to );
            System.out.println( "subject = " + subject );
            System.out.println( "body = " + body );
            System.out.println( "timeoutInSeconds = " + timeoutInSeconds );
        }
        return Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return gotMessageMatching( to, subject, body );
            }
        }, timeoutInSeconds * 1000, 1000 );
    }

    private boolean gotMessageMatching( String to, String subject, String body ) {
        System.out.println( "gotMessageMatching...." );
        System.out.println( "messagesReceived = " + messagesReceived );
        for (SMTPMessage message : messagesReceived) {
            System.out.println( "message.data() = " + message.data() );
            if (messageMatches( message, to, subject, body )) {
                return true;
            } else {
                System.out.println( "Does not match: " + message.data() );
            }
        }
        return false;
    }

    private boolean messageMatches( SMTPMessage message, String to, String subject, String body ) {
        if (message.getToAddresses().size() != 1) {
            if (debug) System.out.println( "Wrong number of addresses" );
            return false;
        }
        EmailAddress address = message.getToAddresses().get( 0 );
        if (!address.toString().equals( to )) {
            if (debug) System.out.println( "Wrong address: " + address );
            return false;
        }
        if (!message.data().contains( "Subject: " + subject )) {
            if (debug) System.out.println( "Subject not in data." );
            return false;
        }
        if (!message.data().contains( body )) {
            if (debug) System.out.println( "Body not in data." );
            return false;
        }
        return true;
    }


    /**
     * Notifies the worker thread to stop processing and exit and waits
     * up to 20 seconds for the thread to finish.
     */
    public void shutdown() {
        logWarn( "Shutting down SMTPProcessor." );
        running = false;
        closeServerSocket();
        closeClientSocket();
        try {
            worker.join( 20000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * Handles all the commands related the the sending of mail.
     */
    private void handleCommands() throws IOException {
        String inputString = read();
        int lastCommand = NONE;

        while (inputString != null) {
            String command = parseCommand( inputString );
            String argument = parseArgument( inputString );
            if (command.equals( COMMAND_QUIT )) {
                write( MESSAGE_DISCONNECT );
                break;
            } else if (command.equals( COMMAND_HELO )) {
                write( "250 Hello " + argument );
                lastCommand = HELO;
            } else if (command.equals( COMMAND_NOOP )) {
                //NOOP - Do Nothing.
                write( MESSAGE_OK );
            } else if (command.equals( COMMAND_RSET )) {
                //Resets the state of the server back to the initial state.
                message = new SMTPMessage();
                write( MESSAGE_OK );
                lastCommand = RSET;
            } else if (command.equals( COMMAND_MAIL_FROM ) && inputString.toUpperCase().startsWith( "MAIL FROM:" )) {
                //Not only check the command, but the full string, since the prepare command
                //method only returns the text before the first string, and this is a two
                //word command.
                if (lastCommand == HELO || lastCommand == NONE || lastCommand == RSET) {
                    if (handleMailFrom( inputString )) {
                        lastCommand = MAIL_FROM;
                    }
                } else {
                    write( MESSAGE_COMMAND_ORDER_INVALID );
                }
            } else if (command.equals( COMMAND_RCPT_TO ) && inputString.toUpperCase().startsWith( "RCPT TO:" )) {
                //Not only check the command, but the full string, since the prepare command
                //method only returns the text before the first string, and this is a two
                //word command.
                if (lastCommand == MAIL_FROM || lastCommand == RCPT_TO) {
                    handleRcptTo( inputString );
                    lastCommand = RCPT_TO;
                } else {
                    write( MESSAGE_COMMAND_ORDER_INVALID );
                }
            } else if (command.equals( COMMAND_DATA )) {
                if (lastCommand == RCPT_TO && message.getToAddresses().size() > 0) {
                    handleData();
                    // Reset for another message
                    message = new SMTPMessage();
                    lastCommand = RSET;
                } else {
                    write( MESSAGE_COMMAND_ORDER_INVALID );
                }
            } else {
                write( MESSAGE_INVALID_COMMAND + command );
            }
            inputString = read();
        }
    }

    private boolean handleMailFrom( String inputString ) {
        String fromAddress = stripLessThanAndGreaterThan( inputString.substring( 10 ) );
        try {
            //It is legal for the MAIL FROM address to be empty.
            if (fromAddress == null || fromAddress.trim().equals( "" )) {
                message.setFromAddress( new EmailAddress() );
                logDebug( "MAIL FROM is empty" );
            } else {
                EmailAddress address = new EmailAddress( fromAddress );
                message.setFromAddress( address );
                logDebug( "MAIL FROM: " + fromAddress );
            }
            write( MESSAGE_OK );
            return true;
        } catch (Exception iae) {
            logDebug( "Unable to parse From Address: " + fromAddress );
            write( MESSAGE_USER_INVALID );
            return false;
        }
    }

    /*
     * Handle the "RCPT TO:" command, which defines one of the recieving addresses.
     */
    private void handleRcptTo( String inputString ) {
        String toAddress = stripLessThanAndGreaterThan( inputString.substring( 8 ) );
        EmailAddress address = new EmailAddress( toAddress );
        message.addToAddress( address );
        write( MESSAGE_OK );
        logDebug( "RCTP TO: " + toAddress + " accepted." );
    }

    private void handleData() throws IOException {
        // Get the current maxSize setting and convert to bytes.
        long maxSize = 5 * 1024 * 1024;
        write( MESSAGE_SEND_DATA );
        //Add a datestamp to the message to track when the message arrived.
        message.addDataLine( "X-RecievedDate: " + new Date() );
        //Add a line to the message to track that the message when through this server.
        message.addDataLine( "Received: by EricDaugherty's JES SMTP from client: " + clientIp );
        String inputString = in.readLine();
        while (!inputString.equals( "." )) {
            logDebug( "Read Data: " + inputString );
            message.addDataLine( inputString );
            inputString = in.readLine();

            // Check message size
            if (message.getSize() > maxSize) {
                logWarn( "Message Rejected.  Message larger than max allowed size (" + maxSize + " MB)" );
                write( MESSAGE_MESSAGE_TOO_LARGE );
                throw new RuntimeException( "Aborting Connection.  Message size too large." );
            }
        }
        logDebug( "Data Input Complete." );

        messagesReceived.add( message );
        write( MESSAGE_OK );
        logInfo( "Message accepted for delivery." );
    }

    private String read() throws IOException {
        String inputLine = in.readLine();
        if (inputLine != null) inputLine = inputLine.trim();
        logDebug( "Read Input: " + inputLine );
        return inputLine;
    }

    /*
     * Parses the input stream for the command.  The command is the
     * begining of the input stream to the first space.  If there is
     * space found, the entire input string is returned.
     * <p/>
     * This method converts the returned command to uppercase to allow
     * for easier comparison.
     * <p/>
     * Additinally, this method checks to verify that the quit command
     * was not issued.  If it was, a SystemException is thrown to terminate
     * the connection.
     */
    private String parseCommand( String inputString ) {
        int index = inputString.indexOf( " " );
        if (index == -1) {
            return inputString.toUpperCase();
        } else {
            return inputString.substring( 0, index ).toUpperCase();
        }
    }

    /*
     * Parses the input stream for the argument.  The argument is the
     * text starting afer the first space until the end of the inputstring.
     * If there is no space found, an empty string is returned.
     * <p/>
     * This method does not convert the case of the argument.
     */
    private String parseArgument( String inputString ) {
        int index = inputString.indexOf( " " );
        if (index == -1) {
            return "";
        } else {
            return inputString.substring( index + 1 );
        }
    }

    /*
     * Parses an address argument into a real email address.  This
     * method strips off any &gt; or &lt; symbols.
     */
    private String stripLessThanAndGreaterThan( String address ) {
        int index = address.indexOf( "<" );
        if (index != -1) {
            address = address.substring( index + 1 );
        }
        index = address.indexOf( ">" );
        if (index != -1) {
            address = address.substring( 0, index );
        }
        return address;
    }

    private class Worker extends Thread {
        public void run() {
            while (running) {
                try {
                    socket = serverSocket.accept();
                    //Prepare the input and output streams.
                    out = new PrintWriter( socket.getOutputStream(), true );
                    in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

                    InetAddress remoteAddress = socket.getInetAddress();
                    clientIp = remoteAddress.getHostAddress();
                    logInfo( remoteAddress.getHostName() + "(" + clientIp + ") socket connected via SMTP." );

                    write( WELCOME_MESSAGE );

                    //Initialize the input message.
                    message = new SMTPMessage();

                    //Parses the input for commands and delegates to the appropriate methods.
                    handleCommands();
                    closeClientSocket();
                } catch (InterruptedIOException iioe) {
                    //This is fine, it should time out every 10 seconds if
                    //a connection is not made.
                } catch (SocketException se) {
                    //If the running flag is false, then this is probably because we've
                    //closed the socket, so we don't bother reporting the exception.
                    if (running) logError( "Exception while worker running.", se );
                } catch (Throwable e) {
                    //If any exception gets to here uncaught, it means we should just disconnect.
                    logDebug( "Disconnecting Exception:", e );
                    closeClientSocket();
                }
            }
            logWarn( "SMTPProcessor shut down gracefully" );
        }
    }

    private void closeClientSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            logDebug( "Error closing client socket.", ioe );
        }
    }

    private void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioe) {
            logDebug( "Error closing server socket.", ioe );
        }
    }

    private void write( String message ) {
        logDebug( "Writing: " + message );
        out.print( message + "\r\n" );
        out.flush();
    }

    private void logInfo( String s ) {
        if (!debug) return;
        System.out.println( s );
    }

    private void logWarn( String s ) {
        if (!debug) return;
        System.out.println( s );
    }

    private void logError( String s, IOException ioe ) {
        if (!debug) return;
        ioe.printStackTrace();
        System.out.println( s );
    }

    private void logDebug( String s, Throwable... e ) {
        if (!debug) return;
        for (Throwable throwable : e) {
            throwable.printStackTrace();
        }
        System.out.println( s );
    }
}