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

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class Ping {

	private String countParam = "";
	private String[] redundantHosts = new String[] { "www.google.com", "www.amazon.com" };

	private Ping() {
		boolean isWindows = System.getProperty( "os.name" ).toLowerCase().contains( "win" );
		this.countParam = isWindows ? "-n" : "-c";
	}

	public static void main( String[] args ) {
		new Ping().run();
	}

	private void run() {
		List<Host> hostList = new ArrayList<Host>();
		hostList.add( new Host( 2, "www.kugel.com.br" ) );
		hostList.add( new Host( 3, "www.capal.coop.br" ) );
		hostList.add( new Host( 4, "200.169.77.34" ) );
		hostList.add( new Host( 5, "201.47.57.180" ) );
		hostList.add( new Host( 5, "201.47.57.181" ) );

		while ( true ) {
			try {
				for ( Host host : hostList ) {
					boolean pingOk = ping( host.getAddress() );

					if ( !pingOk ) {
						boolean connectionOk = true;
						for ( String redundantHost : this.redundantHosts ) {
							if ( !ping( redundantHost ) ) {
								connectionOk = false;
							}
						}
						if ( connectionOk ) {
							pingOk = ping( host.getAddress() );
						}
					}
					if ( !pingOk ) {
						System.err.println( host.getAddress() + " caiu!" );
					}
				}
				Thread.sleep( 1000 );
			} catch ( Exception ex ) {
				ex.printStackTrace();
			}
		}
	}

	private boolean ping( String host ) throws Exception {
		// long ms = System.currentTimeMillis();

		ProcessBuilder processBuilder = new ProcessBuilder( "ping", this.countParam, "1", host );
		Process proc = processBuilder.start();

		int returnVal = proc.waitFor();
		// ms = System.currentTimeMillis() - ms;
		// System.out.println( "ping " + host + ": " + ms + " ms" );

		return returnVal == 0;
	}
}
