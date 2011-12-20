package me.matterz.supernaturals.io;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import me.matterz.supernaturals.SupernaturalsPlugin;

public class SNVersionHandler {

	public static SupernaturalsPlugin plugin = SupernaturalsPlugin.instance;
	public static File versionFile = new File(plugin.getDataFolder(), "VERSION");
	public static File versionNewestFile = new File(plugin.getDataFolder(), "VERSIONNEWEST");

	public static boolean fileExists() {
		if(versionFile.exists()) {
			return true;
		} else {
			return false;
		}
	}
	public static void writeVersion() {
		try {
			versionFile.createNewFile();
			BufferedWriter vout = new BufferedWriter(new FileWriter(versionFile));
			vout.write(plugin.getDescription().getVersion());
			vout.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
	}

	public static String readVersion() {
		byte[] buffer = new byte[6];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(versionFile));
			f.read(buffer);
			
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}

		return new String(buffer);
	}

	public static String readNewestVersion() {
		byte[] buffer = new byte[6];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(versionNewestFile));
			f.read(buffer);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}

		return new String(buffer);
	}

	public static void getNewestVersion() throws MalformedURLException, IOException { // Downloads the newest version-check from my dropbox
		versionNewestFile.createNewFile();
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL("http://dl.dropbox.com/u/21118041/NEWESTVERSION").openStream());
			fout = new FileOutputStream(versionNewestFile);

			byte data[] = new byte[6];
			int count;
			while ((count = in.read(data, 0, 6)) != -1) {
				fout.write(data, 0, count);
			}
		}
		finally {
			if (in != null) {
				in.close();
			}
			if (fout != null) {
				fout.flush();
				fout.close();
			}
		}
	}
}
