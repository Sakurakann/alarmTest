package com.utils.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Shell
{
  private static List<Process> processes = new ArrayList<Process>();

  public static void main(String[] args)
    throws ParserConfigurationException, IOException, SAXException
  {
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      public void run() {
        for (Process process : Shell.processes) {
          process.close();
          process = null;
        }
        Shell.processes.clear();
      }
    }));
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new File(args[0]));
    NodeList processes = doc.getElementsByTagName("process");
    for (int i = 0; i < processes.getLength(); i++) {
      Node node = processes.item(i);
      if ((node instanceof Element)) {
        Element process = (Element)node;
        List<String> command = new ArrayList<String>();
        NodeList children = process.getElementsByTagName("arg");
        for (int j = 0; j < children.getLength(); j++) {
          Node child = children.item(j);
          if ((child instanceof Element)) {
            Element arg = (Element)child;
            command.add(arg.getAttribute("value"));
          }
        }
        int period = Integer.parseInt(process.getAttribute("period").trim());
        Shell.processes.add(new Process(process.getAttribute("name"), period, command));
      }
    }
  }
}