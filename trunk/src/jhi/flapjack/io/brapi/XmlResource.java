// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.io.*;
import javax.swing.*;
import javax.xml.bind.annotation.*;

import jhi.flapjack.gui.*;

public class XmlResource
{
	private String name;
	private String url;
	private String certificate;
	private ImageIcon image;
	private String description;

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public ImageIcon getImage() {
		return image;
	}

	public String getLogo() { return ""; }

	@XmlElement
	public void setLogo(String logo)
	{
		File dir = new File(FlapjackUtils.getCacheDir(), "brapi");

		try { image = new ImageIcon(new File(dir, logo).toString()); }
		catch (Exception e) {}
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	@XmlElement (name="base-url")
	public void setUrl(String url) {
		this.url = url;
	}

	public String getCertificate()
		{ return certificate; }

	@XmlElement
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getDescription() {
		return description;
	}

	@XmlElement
	public void setDescription(String description) {
		this.description = description;
	}
}