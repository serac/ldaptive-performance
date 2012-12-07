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

import java.util.concurrent.ExecutorService;

/**
 * Description of Recorder.
 *
 * @author Middleware Services
 * @version $Revision: $
 */
public class Recorder implements Runnable {
    private final SharedState state;

    private final ExecutorService runner;

    public Recorder(final SharedState state, final ExecutorService runner) {
        this.state = state;
        this.runner = runner;
    }

    public void run() {
        Sample sample;
        while (!this.runner.isShutdown()) {
            try {
                sample = this.state.getResultQueue().take();
            } catch (InterruptedException e) {
                return;
            }
            System.out.println(sample);
        }
    }
}
