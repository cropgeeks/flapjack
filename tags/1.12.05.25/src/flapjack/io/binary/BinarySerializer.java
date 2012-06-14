// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.io.binary;

import java.io.*;

import flapjack.data.*;
import flapjack.gui.*;
import flapjack.io.*;

// Changes to versions happen in THREE places, marked up with:
///////////////////////////////////////

public class BinarySerializer
{
	///////////////////////////////////////
	static final int VERSION = 4;
	///////////////////////////////////////

	public BinarySerializer()
	{
	}

	public void serialize(Project project)
		throws Exception
	{
		File file = project.fjFile.getFile();
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		///////////////////////////////////////
		FlapjackSerializer serializer = new SerializerV04(null, out);
		///////////////////////////////////////

		serializer.writeHeader();
		serializer.saveProject(project);

		out.close();
	}


	public Project deserialize(FlapjackFile fjFile, boolean fullRead)
		throws Exception
	{
		DataInputStream in = new DataInputStream(new BufferedInputStream(fjFile.getInputStream()));

		// Header information
		byte[] header = new byte[9];
		int headerLength = in.read(header);

		if (headerLength != 9 || !(new String(header).equals("FLAPJACK\032")))
			throw new DataFormatException("File does not contain a Flapjack header");

		// Version information
		int version = in.readInt();
		// Created by
		byte[] creator = new byte[in.readInt()];
		in.read(creator);

		System.out.println("Version " + version + " (" + new String(creator, "UTF8") + ")");

		if (version > VERSION)
			return null;


		// The project itself
		Project project = null;

		if (fullRead)
		{
			FlapjackSerializer serializer = null;

			switch (version)
			{
				///////////////////////////////////////
				case 1: serializer = new SerializerV01(in, null); break;

				case 2: serializer = new SerializerV02(in, null); break;

				case 3: serializer = new SerializerV03(in, null); break;

				case 4: serializer = new SerializerV04(in, null); break;
				///////////////////////////////////////
			}

			project = serializer.loadProject();
		}

		in.close();

		return project;
	}
}

abstract class FlapjackSerializer
{
	protected static boolean DEBUG = false;

	protected DataOutputStream out;
	protected DataInputStream in;


	FlapjackSerializer(DataInputStream in, DataOutputStream out)
	{
		this.in = in;
		this.out = out;
	}

	void writeHeader()
		throws Exception
	{
		// Header information
		out.writeBytes("FLAPJACK\032");

		// Version information
		out.writeInt(BinarySerializer.VERSION);
		// Created by
		writeString("Flapjack " + Install4j.VERSION);
	}

	protected abstract void saveProject(Project project)
		throws Exception;

	protected abstract Project loadProject()
		throws Exception;


	// Reads and returns a string from the bytestream, by expecting to read a
	// single integer defining the string's length; then that number of bytes
	protected String readString()
		throws Exception
	{
		byte[] data = new byte[in.readInt()];

		in.read(data);

		return new String(data, "UTF8");
	}

	protected void writeString(String str)
		throws Exception
	{
		out.writeInt(str.length());
		out.write(str.getBytes("UTF8"));
	}
}