package flapjack.io;

import java.io.*;

import org.exolab.castor.mapping.*;
import org.exolab.castor.xml.*;

import flapjack.data.*;

public class ProjectSerializer
{
	private static Mapping mapping;

	private static void initialize()
		throws Exception
	{
		org.exolab.castor.util.LocalConfiguration.getInstance().getProperties()
			.setProperty("org.exolab.castor.parser", "org.xml.sax.helpers.XMLReaderAdapter");

		org.exolab.castor.util.LocalConfiguration.getInstance().getProperties()
			.setProperty("org.exolab.castor.indent", "true");

		org.exolab.castor.util.LocalConfiguration.getInstance().getProperties()
			.setProperty("org.exolab.castor.xml.serializer.factory", "org.exolab.castor.xml.XercesJDK5XMLSerializerFactory");

		mapping = new Mapping();
		mapping.loadMapping(
			new ProjectSerializer().getClass().getResource("/res/flapjack-castor.xml"));
	}

	public static void save(Project project)
		throws Exception
	{
		initialize();


		Writer writer = new FileWriter("test.xml");

		Marshaller marshaller = new Marshaller(writer);
		marshaller.setMapping(mapping);

		marshaller.marshal(project);

		writer.close();
	}

	public static Project load()
		throws Exception
	{
		initialize();


		Reader reader = new FileReader("test.xml");

		Unmarshaller unmarshaller = new Unmarshaller(mapping);

		Project p = (Project) unmarshaller.unmarshal(reader);
		reader.close();

		System.out.println("LOADED XML");

		return p;
	}
}