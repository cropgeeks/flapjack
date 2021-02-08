// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import java.util.*;
import javax.xml.bind.annotation.*;

@XmlRootElement (name="brapi-providers")
public class XmlBrapiProvider
{
	private ArrayList<XmlCategory> categories;

	public ArrayList<XmlCategory> getCategories() {
		return categories;
	}

	@XmlElement (name="category")
	public void setCategories(ArrayList<XmlCategory> categories) {
		this.categories = categories;
	}
}