/*
  $Id: $

  Copyright (C) 2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.cas.ldap;

import java.util.Date;

/**
 * Description of Sample.
 *
 * @author Middleware Services
 * @version $Revision: $
 */
public class Sample {

    public enum Result {
        SUCCESS,
        FAILURE
    }

    private final Date start;

    private final Date end;

    private final Result result;


    public Sample(final Date start, final Date end, final Result result) {
        this.start = start;
        this.end = end;
        this.result = result;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public Result getResult() {
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(start.getTime()).append(',');
        sb.append(end.getTime()).append(',');
        sb.append(result);
        return sb.toString();
    }
}
