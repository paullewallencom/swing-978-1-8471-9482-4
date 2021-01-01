/****************************************************************************
*
* Name: UnitTesterIF.java
*
* Synopsis: See javadoc class comments.
*
* Description: See javadoc class comments.
*
* Copyright 2002 Timothy Gordon Lavers (Australia)
*
*                          The Wide Open License (WOL)
*
* Permission to use, copy, modify, distribute and sell this software and its
* documentation for any purpose is hereby granted without fee, provided that
* the above copyright notice and this license appear in all source copies.
* THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT EXPRESS OR IMPLIED WARRANTY OF
* ANY KIND. See http://www.dspguru.com/wol.htm for more information.
*
*****************************************************************************/
package org.grandtestauto;

/**
 * All <code>UnitTester</code>s must implement this interface.
 * Additionally, they must have a public constructor from a
 * <code>GrandTestAuto</code> or a no-args constructor.
 *
 * @author Tim Lavers
 */
public interface UnitTesterIF {

	/**
	 * Runs all of the unit tests for the package tested by <code>this</code>.
	 *
	 * @return <code>true</code> if and only if all the tests passed.
	 */
	public boolean runTests();
}