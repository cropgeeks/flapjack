// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.xml.bind.annotation.*;

public class XmlCategory
{
	private String name;
	private String description;
	private ImageIcon image;
	private ArrayList<XmlResource> resources = new ArrayList<>();

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
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
	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<XmlResource> getResources() {
		return resources;
	}

	@XmlElement (name="resource")
	public void setResources(ArrayList<XmlResource> resources) {
		this.resources = resources;
	}
}