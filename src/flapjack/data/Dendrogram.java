// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.awt.image.*;
import java.util.*;

public class Dendrogram extends XMLRoot
{
	private BufferedImage image;

	private GTViewSet viewSet;


	public BufferedImage getImage()
		{ return image; }

	public void setImage(BufferedImage image)
		{ this.image = image; }

	public GTViewSet getViewSet()
		{ return viewSet; }

	public void setViewSet(GTViewSet viewSet)
		{ this.viewSet = viewSet; }

	// Other Methods

	public ArrayList<LineInfo> viewLineOrder()
		{ return viewSet.getLines(); }
}