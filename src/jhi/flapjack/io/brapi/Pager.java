// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import uk.ac.hutton.ics.brapi.resource.base.*;

/**
 * Designed for use with the paginated calls defined within the BrAPI
 * specification. Call paginate with a BrAPI {@link Metadata}
 * object as the parameter and check the status of {@link #isPaging()} to
 * determine whether or not to continue paging through the data. This pager will
 * respect the page sizes specified by BrAPI implementations.
 */
public class Pager
{
	private boolean isPaging = true;
	private int pageSize = 1000;
	private int page = 0;
	private int totalPages = 0;
	private String currentPageToken;
	private String nextPageToken;
	private String prevPageToken;

	public Pager()
	{
	}

	/**
	 * Additional constructor which enables specifying the number of elements
	 * the Pager will ask for in its first request to a BrAPI implementation.
	 * For further calls Pager will respect the page sizes being specified by
	 * the BrAPI implementation it is communicating with.
	 *
	 * @param pageSize	The number of elements asked for in the first request
	 */
	public Pager(int pageSize)
	{
		this.pageSize = pageSize;
	}

	/**
	 * Determine if another page of data is available, and the number of
	 * elements of data to request in that page.
	 *
	 * @param metadata	A {@link Metadata} object which will
	 *                  represnet the Metadata block return from a BrAPI
	 *                  implementation
	 */
	public void paginate(Metadata metadata)
	{
		Pagination p = metadata.getPagination();

		if (p.getTotalPages() == 0 || p.getPage() == p.getTotalPages() - 1)
			isPaging = false;

		// Update the pageSize and page variables as we haven't yet reached the
		// end of the data
		else
		{
			pageSize = p.getPageSize();
			page = (p.getPage() + 1);
			totalPages = p.getTotalPages();
		}
	}

	/**
	 * Determine if another page of data is available, and the number of
	 * elements of data to request in that page.
	 *
	 * @param metadata	A {@link Metadata} object which will
	 *                  represnet the Metadata block return from a BrAPI
	 *                  implementation
	 */
	/*public void paginate(TokenMetadata metadata)
	{
		TokenPagination p = metadata.getPagination();

		if (p.getTotalPages() == 0 || p.getCurrentPage() == p.getTotalPages() - 1 || p.getNextPageToken() == null)
			isPaging = false;

			// Update the pageSize and page variables as we haven't yet reached the
			// end of the data
		else
		{
			pageSize = p.getPageSize();
			page = (p.getCurrentPage() + 1);
			totalPages = p.getTotalPages();
			currentPageToken = p.getCurrentPageToken();
			nextPageToken = p.getNextPageToken();
			prevPageToken = p.getPrevPageToken();
		}
	}*/

	public boolean isPaging()
		{ return isPaging; }

	public void setPaging(boolean paging)
		{ isPaging = paging; }

	public int getPageSize()
		{ return pageSize; }

	public void setPageSize(int pageSize)
		{ this.pageSize = pageSize; }

	public int getPage()
		{ return page; }

	public void setPage(int page)
		{ this.page = page; }

	public String getNextPageToken()
		{ return nextPageToken; }

	public int getTotalPages()
		{ return totalPages; }
}