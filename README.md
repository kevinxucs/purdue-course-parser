# Purdue Course Parser

Purdue Course Parser is a simple library which retrieves course information from [myPurdue](https://mypurdue.purdue.edu/) website.

This project is still under development. Features are not complete yet.

## Legal

The source of Purdue Course Parser is released under Apache License 2.0.

## Build

You will need Java (>= 1.6) and maven (>= 3.0.3) to build.

To build, you wound run:

	mvn compile

To package class files into jar:

	mvn package

## Development

### IDE

Recommended IDE is [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/junosr1).

To develop with Eclipse, you will need following plugins installed:

* m2e
* EGit

To install these plugins:
	
	Help --> Eclipse Marketplace --> search "egit" for EGit -->
	search "Maven Integration for Eclipse" for m2e --> click install --> click finish

Clone the repository (use your own forked repository):

	git clone git@github.com:kevinxucs/purdue-course-parser.git

Import project into Eclipse:

	File --> Import --> General --> Existing Projects into Workspace -->
	Select root directory --> Browser --> select the folder you just cloned --> click finish
