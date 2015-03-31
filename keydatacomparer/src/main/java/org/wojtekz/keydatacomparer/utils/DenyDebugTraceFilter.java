/*
 Licensed to the Simple Public License (SimPL) 2.0. You may obtain
 a copy of the License at http://opensource.org/licenses/Simple-2.0

 You get the royalty free right to use the software for any purpose;
 make derivative works of it (this is called a "Derived Work");
 copy and distribute it and any Derived Work.
 You get NO WARRANTIES. None of any kind. If the software damages you
 in any way, you may only recover direct damages up to the amount you
 paid for it (that is zero if you did not pay anything).
 */
package org.wojtekz.keydatacomparer.utils;

// import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Level;

/**
 * The class filters logging levels DEBUG and TRACE.
 * 
 * @author Wojtek Zaręba
 */
public class DenyDebugTraceFilter extends org.apache.log4j.spi.Filter {
    
    /**
     *  The default constructor from class Filter.
     */
    public DenyDebugTraceFilter() {
        super();
    }
    
    @Override
    public int decide(LoggingEvent event) {
        int decision = NEUTRAL;
        Level lev = event.getLevel();
        if (lev == Level.DEBUG || lev == Level.TRACE) {
            decision = DENY;
        }
        return decision;
    }
}
