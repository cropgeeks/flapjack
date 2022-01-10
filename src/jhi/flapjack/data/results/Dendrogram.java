// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.data.results;

import java.util.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

public class Dendrogram extends XMLRoot
{
	private String title;

	private PngImage png = new PngImage();
	private String pngDatabaseID = SystemUtils.createGUID(8);

	private PdfDoc pdf = new PdfDoc();
	private String pdfDatabaseID = SystemUtils.createGUID(8);

	private GTViewSet viewSet;

	public Dendrogram()
	{
	}


	// Methods required for XML serialization

	public String getTitle()
		{ return title; }

	public void setTitle(String title)
		{ this.title = title; }

	public GTViewSet getViewSet()
		{ return viewSet; }

	public void setViewSet(GTViewSet viewSet)
		{ this.viewSet = viewSet; }

	public String getPngDatabaseID()
		{ return pngDatabaseID; }

	public void setPngDatabaseID(String pngDatabaseID)
		{ this.pngDatabaseID = pngDatabaseID; }

	public String getPdfDatabaseID()
		{ return pdfDatabaseID; }

	public void setPdfDatabaseID(String pdfDatabaseID)
		{ this.pdfDatabaseID = pdfDatabaseID; }



	// Other Methods

	public PngImage getPng()
		{ return png; }

	public PdfDoc getPdf()
		{ return pdf; }

	public ArrayList<LineInfo> viewLineOrder()
		{ return viewSet.getLines(); }


	// DB Serializable wrappper around the byte array holding the PNG
	public class PngImage implements ISerializableDB
	{
		public byte[] image;

		public Object dbGetObject()
			{ return image;	}

		@SuppressWarnings("unchecked")
		public void dbSetObject(Object obj)
		{
			image = (byte[]) obj;
		}

		public void dbClear()
			{ image = null;	}

		public String dbGetType()
			{ return "Dendrogram.PngImage"; }

		public String getDatabaseID()
			{ return pngDatabaseID; }
	}

	// DB Serializable wrappper around the byte array holding the PDF
	public class PdfDoc implements ISerializableDB
	{
		public byte[] data;

		public Object dbGetObject()
			{ return data;	}

		@SuppressWarnings("unchecked")
		public void dbSetObject(Object obj)
		{
			data = (byte[]) obj;
		}

		public void dbClear()
			{ data = null;	}

		public String dbGetType()
			{ return "Dendrogram.PdfImage"; }

		public String getDatabaseID()
			{ return pdfDatabaseID; }
	}
}