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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Description of LoadDriver.
 *
 * @author Middleware Services
 * @version $Revision: $
 */
public class LoadDriver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ApplicationContext context;

    private final ExecutorService workExecutor;

    private final ExecutorService resultExecutor;

    private final SharedState state = new SharedState();

    private File credentialsFile;


    public static void main(final String[] args) {
        if (args.length < 4) {
            System.out.println(
                    "USAGE: LoadDriver sample_count thread_count " +
                    "path/to/credentials.csv path/to/spring-context.xml");
            return;
        }
        final int samples = Integer.parseInt(args[0]);
        final int threads = Integer.parseInt(args[1]);
        final File credentials = new File(args[2]);
        if (!credentials.exists()) {
            throw new IllegalArgumentException(credentials + " does not exist.");
        }
        ApplicationContext context;
        try {
            context = new ClassPathXmlApplicationContext(args[3]);
        } catch (BeanDefinitionStoreException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                // Try treating path as filesystem path
                context = new FileSystemXmlApplicationContext(args[3]);
            } else {
                throw e;
            }
        }
        final LoadDriver driver = new LoadDriver(samples, threads, credentials, context);
        System.err.println("Load test configuration:");
        System.err.println("\tthreads: " + threads);
        System.err.println("\tsamples: " + samples);
        System.err.println("\tcredentials: " + credentials);
        driver.start();
        while (driver.getState().hasWorkRemaining()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        driver.stop();
    }

    public LoadDriver(
            final int sampleCount,
            final int workerCount,
            final File credentialsFile,
            final ApplicationContext context) {

        this.credentialsFile = credentialsFile;
        this.resultExecutor = Executors.newSingleThreadExecutor();
        this.workExecutor = Executors.newFixedThreadPool(workerCount);
        this.context = context;
        final AuthenticationHandler handler = this.context.getBean(AuthenticationHandler.class);
        if (handler == null) {
            throw new IllegalStateException("AuthenticationHandler bean not found.");
        }
        if (!handler.supports(new UsernamePasswordCredentials())) {
            throw new IllegalStateException("AuthenticationHandler bean does not support password authentication");
        }
        this.state.setWorkQueue(new ArrayBlockingQueue<UsernamePasswordCredentials>(sampleCount));
        this.state.setResultQueue(new ArrayBlockingQueue<Sample>(sampleCount));
        this.state.setAuthenticationHandler(handler);
    }

    public void start() {
        this.state.getResultQueue().clear();
        this.state.getWorkQueue().clear();
        populateWorkQueue(this.credentialsFile);
        final int sampleCount = this.state.getWorkQueue().size();
        System.err.println("Starting load test with sample size " + sampleCount);
        System.err.println("Terminate process to quit.");
        this.resultExecutor.execute(new Recorder(this.state, this.resultExecutor));
        for (int i = 0; i < sampleCount; i++) {
            this.workExecutor.execute(new Authenticator(this.state));
        }
    }

    public void stop() {
        this.workExecutor.shutdownNow();
        this.resultExecutor.shutdownNow();
    }

    public SharedState getState() {
        return state;
    }

    private void populateWorkQueue(final File credentials) {
        BufferedReader reader = null;
        String line;
        String[] tokens;
        UsernamePasswordCredentials credential;
        final List<UsernamePasswordCredentials> credentialsList = new ArrayList<UsernamePasswordCredentials>();
        try {
            reader = new BufferedReader(new FileReader(credentials));
            while ((line = reader.readLine()) != null) {
                tokens = line.split(",");
                if (tokens.length == 2) {
                    credential = new UsernamePasswordCredentials();
                    credential.setUsername(tokens[0]);
                    credential.setPassword(tokens[1]);
                    credentialsList.add(credential);
                }
            }
            int i = 0;
            final int size = credentialsList.size();
            while (this.state.getWorkQueue().remainingCapacity() > 0) {
                credential = credentialsList.get(i++ % size);
                this.state.getWorkQueue().put(credential);
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(credentials + " does not exist.");
        } catch (IOException e) {
            throw new IllegalStateException("Error reading credentials from " + credentials, e);
        } catch (InterruptedException e) {
            return;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.debug("Error closing " + credentials);
                }
            }
        }
    }
}
