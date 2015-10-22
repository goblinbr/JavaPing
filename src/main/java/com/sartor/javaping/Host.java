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

public class Host {
  private long id;
  private String address;
  private int port;
  private EnumCommand command;

  public Host() {
  }

  public Host( long id, String address, int port, EnumCommand command ) {
    this.id = id;
    this.address = address;
    this.port = port;
    this.command = command;
  }

  public long getId() {
    return this.id;
  }

  public void setId( long id ) {
    this.id = id;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress( String address ) {
    this.address = address;
  }

  public int getPort() {
    return port;
  }

  public void setPort( int port ) {
    this.port = port;
  }

  public void setCommand( EnumCommand command ) {
    this.command = command;
  }

  public EnumCommand getCommand() {
    return command;
  }
  
  @Override
  public String toString() {
    return address + ":" + port;
  }
}
