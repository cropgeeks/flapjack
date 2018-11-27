// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import java.util.*;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jhi.flapjack.gui.*;

public class AuthManager implements Serializable
{
	private static final String P_KEY = "CKR9NQGEMUEKKE2M";

	private static HashMap<String,Credentials> credentials = new HashMap<>();

	public static Credentials getCredentials(String url)
	{
		readCredentials();

		if (credentials.containsKey(url))
			return credentials.get(url);

		return new Credentials(false, false, "", "");
	}

	public static void setCredentials(String url, boolean useAuthentication, boolean saveCredentials, String username, String password)
	{
		credentials.put(url, new Credentials(useAuthentication, saveCredentials, username, password));
		writeCredentials();
	}

	@SuppressWarnings("unchecked")
	private static void readCredentials()
	{
		try (ObjectInputStream in = new ObjectInputStream(
			new FileInputStream(new File(Flapjack.getPrefsFile().getParentFile(), "flapjack.brapi"))))
		{
			credentials = (HashMap<String,Credentials>) in.readObject();
		}
		catch (Exception e) {}
	}

	private static void writeCredentials()
	{
		try (ObjectOutputStream out = new ObjectOutputStream(
			new FileOutputStream(new File(Flapjack.getPrefsFile().getParentFile(), "flapjack.brapi"))))
		{
			out.writeObject(credentials);
		}
		catch (Exception e)
		{
            e.printStackTrace();
        }
	}

	private static byte[] encrypt(String string)
	{
		try
		{
			Key aesKey = new SecretKeySpec(P_KEY.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");

			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			return cipher.doFinal(string.getBytes());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static String decrypt(byte[] data)
	{
		try
		{
			Key aesKey = new SecretKeySpec(P_KEY.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");

			cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(data));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static class Credentials implements Serializable
	{
		boolean useAuthentication, saveCredentials;
		byte[] username, password;

		Credentials(boolean useAuthentication, boolean saveCredentials, String username, String password)
		{
			this.useAuthentication = useAuthentication;
			this.saveCredentials = saveCredentials;

			if (saveCredentials)
			{
				this.username = encrypt(username);
				this.password = encrypt(password);
			}
		}

		public boolean useAuthentication()
			{ return useAuthentication; }

		public boolean saveCredentials()
			{ return saveCredentials; }

		public String getUsername()
			{ return username != null ? decrypt(username) : ""; }

		public String getPassword()
			{ return password != null ? decrypt(password) : ""; }
	}
}