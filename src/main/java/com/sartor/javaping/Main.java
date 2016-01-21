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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import com.sartor.javaping.db.dao.HostDao;
import com.sartor.javaping.db.dao.PingDao;
import com.sartor.javaping.db.entity.Host;
import com.sartor.javaping.db.entity.Ping;
import com.sartor.javaping.services.PingService;
import com.sartor.javaping.types.EnumCommand;

public class Main {

    private String countParam = "";

    // private Host[] redundantHosts = new Host[] { new
    // Host(0,"www.google.com.br", 80, EnumCommand.CONNECT), new
    // Host(0,"www.amazon.com.br", 80, EnumCommand.CONNECT) };
    private Host[] redundantHosts = new Host[] { new Host(0, "www.google.com.br", 80, EnumCommand.CONNECT) };

    private Main() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (isWindows) {
            this.countParam = "-n";
        } else {
            this.countParam = "-c";
        }
    }

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void run() throws SQLException {
        // List<Host> hostList = new ArrayList<Host>();
        // hostList.add( new Host( 2, "www.kugel.com.br", 80,
        // EnumCommand.CONNECT ) );
        // hostList.add( new Host( 2, "www.kugel.com.br", 80, EnumCommand.PING )
        // );
        // hostList.add( new Host( 3, "www.capal.coop.br", 80,
        // EnumCommand.CONNECT ) );
        // hostList.add( new Host( 3, "www.capal.coop.br", 80, EnumCommand.PING
        // ) );
        // hostList.add( new Host( 4, "200.169.77.34", 80, EnumCommand.CONNECT )
        // );
        // hostList.add( new Host( 4, "200.169.77.34", 80, EnumCommand.PING ) );
        // hostList.add( new Host( 5, "201.47.57.180", 80, EnumCommand.CONNECT )
        // );
        // hostList.add( new Host( 5, "201.47.57.180", 80, EnumCommand.PING ) );
        // hostList.add( new Host( 5, "201.47.57.181", 80, EnumCommand.CONNECT )
        // );
        // hostList.add( new Host( 5, "201.47.57.181", 80, EnumCommand.PING ) );

        // hostList.add( new Host( 5, "200.169.77.40", 3389, EnumCommand.CONNECT
        // ) );
        // hostList.add( new Host( 2, "200.169.77.40", 0, EnumCommand.PING ) );

        HostDao hostDao = new HostDao();
        PingService pingService = new PingService();

        while (true) {
            try {
                List<Host> hostList = hostDao.findAll();
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
                ex.printStackTrace();
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
            System.out.println("ping " + host + ": " + ms + " ms");

            ok = returnVal == 0;
        } else {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host.getAddress(), host.getPort()), timeout);

                ms = (int) (System.currentTimeMillis() - startMs);
                System.out.println("connect " + host + ": " + ms + " ms");

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