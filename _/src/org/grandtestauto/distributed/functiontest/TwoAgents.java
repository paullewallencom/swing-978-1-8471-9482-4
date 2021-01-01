package org.grandtestauto.distributed.functiontest;

import jet.testtools.*;
import jet.testtools.test.org.grandtestauto.*;
import jet.testtools.test.org.grandtestauto.distributed.*;
import org.apache.commons.io.*;
import org.grandtestauto.util.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class TwoAgents extends DistributorFunctionTest {
    public boolean runTest() throws Exception {

        //Start an Agent with name Agent86.
        Process process86 = startTestAgentInSeparateJVM( "Agent86");

        //Start an Agent with name Agent99.
        Process process99 = startTestAgentInSeparateJVM( "Agent99");

        //Start a GTADistributor to run the codebase in package a1 and its sub-packages.
        Process distributorProcess = startDistributorInSeparateJVM( new File( Grandtestauto.test1_zip ) );

        //Read the output (this also waits for the distributor to finish).
        ProcessReader.readProcess( distributorProcess );

        //Shut down the agents.
        process86.destroy();
        process99.destroy();

        //Check from the distributor log file that all tests have been done.
        //Do this by comparing to a configured file known to be good.
        File collatedResultsFile = new File(collatedResultsFileName() );
        File expected = new File( Distributed.CollatedResults1_txt );
        if (!FileUtils.contentEquals( collatedResultsFile, expected )) {
            System.out.println( "expected: '" + FileUtils.readFileToString( expected ) +"'");
            System.out.println( "     got: '" + FileUtils.readFileToString( collatedResultsFile ) +"'");
            assert false : "Wrong file contents, as shown.";
        }

        distributorProcess.destroy();
        return true;
    }
}