package com.klapeks.notaplugin.checker;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.klapeks.coserver.aConfig;
import com.klapeks.coserver.dCoserver;

public class CheckerMain {
	public static void main(String[] args) {
		Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()){
            try {
            	Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","echo aboba"});
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return;
        }
        Scanner sc = new Scanner(System.in);
        File cfgfile = new File("securityKey.txt");
        System.out.println("Hello, this miniporgramm will help you with CoserverAPI issues.");
        if (!cfgfile.exists()) {
            System.out.println("First copy-paste here a securityKey from config.yml");
            System.out.println("Write  -  to don't useSecurity");
            aConfig.securityKey = sc.nextLine();
            try {
                cfgfile.createNewFile();
                FileWriter fw = new FileWriter(cfgfile);
                fw.write(aConfig.securityKey);
                fw.flush();
                fw.close();
                System.out.println("Config was succesfully created");
            } catch (Throwable t) {
                System.out.println("Unnable to create config file");
            }
        } else {
        	try {
				BufferedReader br = new BufferedReader(new FileReader(cfgfile));
				String line = null;
				aConfig.securityKey = "";
				while ((line=br.readLine())!=null) {
					aConfig.securityKey += line;
				}
				System.out.println("Securiy key: " + aConfig.securityKey);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				sc.close();
				return;
			}
        }

        System.out.println("Now you can write a command");
        System.out.println(" connect <ip:port>");
        System.out.println(" exit");
        System.out.println();
        while (true) {
        	String msg = sc.nextLine();
        	while(msg.contains("  ")) msg = msg.replace("  ", " ");
        	if (msg.startsWith(" ")) msg = msg.replaceFirst(" ", "");
        	String command = msg.split(" ")[0];
        	msg = msg.substring(command.length()+1);
        	switch (command) {
			case "exit": {
				sc.close();
				System.out.println("Thank you and goodbye");
				return;
			}
			case "connect": {
				try {
					String ip = msg.split(":")[0];
					int port = Integer.parseInt(msg.split(":")[1]);
					String response = null;
					if (aConfig.securityKey.equals("-")) {
						response = dCoserver.send(ip, port, "coservertestbtchecker", false);
					}
					else {
						response = dCoserver.securitySend(ip, port, "coservertestbtchecker", false);
					}
					System.out.println("Server answered with response: " + response);
				} catch (Throwable t) {
					t.printStackTrace();
				}
				break;
			}

			default:
				System.out.println("Unknown command");
				break;
			}
        	
        }
	}
	
	static int getPort(Scanner sc) {
        try {
        	int port = sc.nextInt();
        	return port;
        } catch (Throwable t) {
        	t.printStackTrace();
        	System.out.println("Probably you don't write an integer. Try againt");
        	return getPort(sc);
        }
	}
}
