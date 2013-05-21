// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.awt.image.*;
import java.util.*;

public class Dendrogram extends XMLRoot
{
	private BufferedImage image;

	private ArrayList<LineInfo> order;


	public BufferedImage getImage()
		{ return image; }

	public void setImage(BufferedImage image)
		{ this.image = image; }

	public ArrayList<LineInfo> getOrder()
		{ return order; }

	public void setOrder(ArrayList<LineInfo> order)
		{ this.order = order; }
}