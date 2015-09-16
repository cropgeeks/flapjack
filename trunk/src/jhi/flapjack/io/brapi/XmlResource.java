// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.net.*;
import javax.swing.*;
import javax.xml.bind.annotation.*;

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
		try { image = new ImageIcon(new URL(logo));	}
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