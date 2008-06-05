package flapjack.gui;

import java.net.*;
import java.util.*;

public class FlapjackUtils
{
	// Checks to see if the IP address of the current user is an SCRI one
	static boolean isSCRIUser()
	{
		try
		{
			// Need to check over all network interfaces (LAN/wireless/etc) to
			// try and find a match...
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

			while (e != null & e.hasMoreElements())
			{
				// And each interface can have multiple IPs...
				Enumeration<InetAddress> e2 = e.nextElement().getInetAddresses();
				while (e2.hasMoreElements())
				{
					String addr = e2.nextElement().getHostAddress();

					if (addr.startsWith("143.234.96.")  || addr.startsWith("143.234.97.") ||
						addr.startsWith("143.234.98.")  || addr.startsWith("143.234.99.") ||
						addr.startsWith("143.234.100.") || addr.startsWith("143.234.101."))
						return true;
				}
			}
		}
		catch (Exception e) {}

		return false;
	}
}