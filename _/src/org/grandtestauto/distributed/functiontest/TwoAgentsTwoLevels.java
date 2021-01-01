package org.grandtestauto.distributed.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import jet.testtools.test.org.grandtestauto.distributed.*;
import org.grandtestauto.util.*;
import org.apache.commons.io.*;

import java.io.*;

/**
 * See the GrandTestAuto test specification.
 */
public class TwoAgentsTwoLevels extends DistributorFunctionTest {
    public boolean runTest() throws Exception {

        //Start an Agent with name Agent86.
        Process process86 = startTestAgentInSeparateJVM( "Agent86", 0 );

        //Start an Agent with name Agent99.
        Process process99 = startTestAgentInSeparateJVM( "Agent99", 1);

        //Start a GTADistributor to run the codebase in package a1 and its sub-packages.
        Process distributorProcess = startDistributorInSeparateJVM( new File( Grandtestauto.test90_zip ) );
        ProcessReader.readProcess( distributorProcess );

        //Wait for the distributor to have exited.
        process86.destroy();
        process99.destroy();

        //Check from the distributor log file that all tests have been done.
        //Do this by comparing to a configured file known to be good.
        File collatedResultsFile = new File(collatedResultsFileName() );
        File expected = new File( Distributed.CollatedResults2_txt );
        if (!FileUtils.contentEquals( collatedResultsFile, expected )) {
            System.out.println( "expected: '" + FileUtils.readFileToString( expected ) +"'");
            System.out.println( "     got: '" + FileUtils.readFileToString( collatedResultsFile ) +"'");
            assert false : "Wrong file contents, as shown.";
        }

        //Check from the log files of Agent86 and Agent99 that
        //* all of the tests have been done
        //* some of the tests have been done by Agent86
        //* some of the tests have been done by Agent99
        //* none of the tests done by Agent86 were also done by Agent99
        //* none of the tests done by Agent99 were also done by Agent86.
        File agent86LogDir = new File(agentNameToBaseDir.get( "Agent86" ), "logs");
        File agent86LogFile = new File( agent86LogDir, "TestAgent.log.0");
        String agent86Log = FileUtils.readFileToString( agent86LogFile );
        assert !agent86Log.contains( ">>>> Results of Unit Tests for a90.g2: passed. <<<<");
        assert !agent86Log.contains( ">>>> Results of Unit Tests for a90.g4: passed. <<<<");

        File agent99LogDir = new File(agentNameToBaseDir.get( "Agent99" ), "logs");
        File agent99LogFile = new File( agent99LogDir, "TestAgent.log.0");
        String agent99Log = FileUtils.readFileToString( agent99LogFile );
        assert agent99Log.contains( ">>>> Results of Unit Tests for a90.g2: passed. <<<<");
        assert agent99Log.contains( ">>>> Results of Unit Tests for a90.g4: passed. <<<<");

        if (agent86Log.contains( ">>>> Results of Unit Tests for a90.g1: passed. <<<<" )) {
            assert !agent99Log.contains( ">>>> Results of Unit Tests for a90.g1: passed. <<<<");
        } else {
            assert agent99Log.contains( ">>>> Results of Unit Tests for a90.g1: passed. <<<<");
        }

        if (agent86Log.contains( ">>>> Results of Unit Tests for a90.g3: passed. <<<<")) {
            assert !agent99Log.contains( ">>>> Results of Unit Tests for a90.g3: passed. <<<<");
        } else {
            assert agent99Log.contains( ">>>> Results of Unit Tests for a90.g3: passed. <<<<");
        }
        distributorProcess.destroy();
        return true;
    }
}