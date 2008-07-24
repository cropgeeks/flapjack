package flapjack.io;

import java.io.*;
import java.net.*;
import java.util.*;

import scri.commons.file.*;

/**
 * Parses the cgi-bin produced log file to determine who has been using
 * Flapjack and where in the world (by country) they're located.
 */
public class LogParser
{
	private Hashtable<String, User> hashtable = new Hashtable<String, User>();

	public static void main(String[] args)
		throws Exception
	{
		new LogParser();
	}

	LogParser()
		throws Exception
	{
		File logFile = new File("\\\\bioinf\\root\\var\\www\\html\\flapjack\\logs\\flapjack.log");

		BufferedReader in = new BufferedReader(new FileReader(logFile));
		String str = in.readLine();

		while (str != null && str.length() > 0)
		{
			processLine(str);
			str = in.readLine();
		}

		in.close();

		printResults();
	}

	private void processLine(String str)
		throws Exception
	{
		String[] tokens = str.split("\t");

		String ip = tokens[1];
		String id = tokens[2];
		String locale = tokens[4];
		String os = tokens[5];
		// Only parse the username if it's actually there
		String username = (tokens.length == 7) ? tokens[6] : "";

		// Check to see if this user has been included already?
		User user = hashtable.get(id);

		// If not, create an entry for them and add to the hashtable
		if (user == null)
		{
			user = new User(id, locale, os, username, ip);
			hashtable.put(id, user);
		}

		user.lastDate = tokens[0];
		user.runCount++;
	}

	private void printResults()
	{
		Enumeration<String> keys = hashtable.keys();
		Vector<User> users = new Vector<User>(hashtable.size());

		while (keys.hasMoreElements())
			users.add(hashtable.get(keys.nextElement()));

		Collections.sort(users);

		System.out.println("\n" + users.size() + " total users:\n");

		for (User user: users)
		{
			System.out.println(user.id.substring(0, 10)
				+ " " + user.runCount
				+ "\t" + user.os
				+ "\t" + user.countryCode
				+ "\t" + user.username
				+ "\t" + user.lastDate);
		}
	}

	private static class User implements Comparable<User>
	{
		String id;
		String locale;
		String os;
		String username;
		String ip;
		String lastDate;

		String country;
		String countryCode;
		int runCount;

		User(String id, String locale, String os, String username, String ip)
			throws Exception
		{
			this.id = id;
			this.locale = locale;
			this.os = os;
			this.username = username;
			this.ip = ip;

			System.out.print("DB lookup for: " + ip);
			getCountry();
		}

		private void getCountry()
			throws Exception
		{
			String address = "http://bioinf.scri.ac.uk/cgi-bin/geolocate?ip=" + ip;

			HttpURLConnection url =	(HttpURLConnection)
				new URL(address).openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(url.getInputStream()));
			String response = in.readLine();
			in.close();

			country = response.split("\t")[0];
			countryCode = response.split("\t")[1];

			System.out.println("\t" + country);
		}

		public int compareTo(User other)
		{
			if (runCount > other.runCount)
				return -1;
			else if (runCount == other.runCount)
				return 0;
			else return 1;
		}
	}
}

