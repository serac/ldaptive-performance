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

import java.util.concurrent.BlockingQueue;

import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * Description of SharedState.
 *
 * @author Middleware Services
 * @version $Revision: $
 */
public class SharedState {
    private BlockingQueue<UsernamePasswordCredentials> workQueue;

    private BlockingQueue<Sample> resultQueue;

    private AuthenticationHandler authenticationHandler;

    public BlockingQueue<UsernamePasswordCredentials> getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(final BlockingQueue<UsernamePasswordCredentials> queue) {
        this.workQueue = queue;
    }

    public BlockingQueue<Sample> getResultQueue() {
        return resultQueue;
    }

    public void setResultQueue(final BlockingQueue<Sample> queue) {
        this.resultQueue = queue;
    }

    public AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(final AuthenticationHandler handler) {
        this.authenticationHandler = handler;
    }

    public boolean hasWorkRemaining() {
        return !(this.workQueue.isEmpty() && this.resultQueue.isEmpty());
    }
}
