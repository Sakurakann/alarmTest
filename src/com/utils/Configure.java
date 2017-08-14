package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configure {
	protected Properties props = new Properties();

	  public Configure(String file) throws Exception {
	    InputStream is = getClass().getResourceAsStream(file);
	    this.props.load(is);
	    is.close();
	  }

	  public Configure(File file) throws Exception {
	    InputStream is = new FileInputStream(file);
	    this.props.load(is);
	    is.close();
	  }

	  public Properties getProperties() {
	    return this.props;
	  }

	  public String getProperty(String key) {
	    if (this.props.getProperty(key) == null) return null;
	    return this.props.getProperty(key);
	  }
}
