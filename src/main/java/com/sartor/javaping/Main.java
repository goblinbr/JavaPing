/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Rodrigo de Bona Sartor
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.sartor.javaping;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sartor.javaping.db.DbManager;
import com.sartor.javaping.db.dao.HostDao;
import com.sartor.javaping.db.entity.Host;
import com.sartor.javaping.services.PingService;
import com.sartor.javaping.types.EnumCommand;

public class Main {
    
    private static final Logger logger = LogManager.getLogger();

    private String countParam = "";

    private Host[] redundantHosts = new Host[] { new Host(0, "www.google.com.br", 80, EnumCommand.CONNECT), new Host(0, "www.amazon.com.br", 80, EnumCommand.CONNECT) };

    private Main() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (isWindows) {
            this.countParam = "-n";
        } else {
            this.countParam = "-c";
        }
        
        logger.info("Initializing at " + System.getProperty("os.name"));
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        HostDao hostDao = null;
        PingService pingService = null;

        while (true) {
            try {
                if (hostDao == null) {
                    hostDao = new HostDao();
                }
                if (pingService == null) {
                    pingService = new PingService();
                }

                List<Host> hostList = hostDao.findAllPaidGreaterThanToday();
                for (Host host : hostList) {
                    PingReturn pr = pingOrConnect(host, 500);

                    boolean connectionOk = true;
                    if (!pr.isSuccessful()) {
                        for (Host redundantHost : this.redundantHosts) {
                            if (!pingOrConnect(redundantHost, 3000).isSuccessful()) {
                                connectionOk = false;
                            }
                        }
                        if (connectionOk) {
                            pr = pingOrConnect(host, 3000);
                        }
                    }
                    if (connectionOk) {
                        pingService.insertPing(host, pr.isSuccessful(), pr.getMs());
                    }
                }
                Thread.sleep(1000);
            } catch (Exception ex) {
                logger.catching(Level.ERROR, ex);

                hostDao = null;
                pingService = null;

                try {
                    DbManager.getInstance().closeAll();
                    Thread.sleep(10000);
                } catch (Exception exp) {
                    logger.catching(Level.ERROR, exp);
                }
            }
        }
    }

    private PingReturn pingOrConnect(Host host, int timeout) throws Exception {
        long startMs = System.currentTimeMillis();
        int ms = 0;

        boolean ok;
        if (host.getCommand() == EnumCommand.PING) {
            ProcessBuilder processBuilder = new ProcessBuilder("ping", this.countParam, "1", host.getAddress());
            Process proc = processBuilder.start();

            int returnVal = proc.waitFor();
            ms = (int) (System.currentTimeMillis() - startMs);

            ok = returnVal == 0;
        } else {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host.getAddress(), host.getPort()), timeout);

                ms = (int) (System.currentTimeMillis() - startMs);

                socket.close();

                ok = true;
            } catch (Exception e) {
                ok = false;
            }
        }

        return new PingReturn(ok, ms);
    }

    class PingReturn {
        private boolean successful;
        private int ms;

        public PingReturn(boolean successful, int ms) {
            super();
            this.successful = successful;
            this.ms = ms;
        }

        public int getMs() {
            return ms;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }
}
