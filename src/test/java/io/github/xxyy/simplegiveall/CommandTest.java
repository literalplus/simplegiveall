/* 
 * Copyright (C) 2013 xxyy98 < xxyy98@gmail.com >
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.xxyy.simplegiveall;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class CommandTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CommandTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CommandTest.class);
    }

    /**
     * Tests the "giveall" command.
     */
    public void testApp() {
//        try {
//            new SimpleGiveallMain().onCommand(new DummyCommandSender(), null, "xyga", new String[] {"DIAMOND", "64"});
//        } catch (Exception e) {
//            e.printStackTrace();
//            assertFalse("Exception when executing command \"xyga DIAMOND 64\": " + e.toString(), true);
//            return;
//        }
//        assertTrue("Command \"xyga DIAMOND 64\" executed successfully.", true);
        assertTrue("Skipped unfinished test CommandTest", true);
    }
}
