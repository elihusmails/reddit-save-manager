package com.markwebb.reddit.save.manager.ingest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BaseTest {

	protected String readFile( String filename ) throws IOException{
		return new String(Files.readAllBytes(Paths.get(filename)));
	}
	
}
