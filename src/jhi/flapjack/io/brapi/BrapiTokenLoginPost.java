package jhi.flapjack.io.brapi;

public class BrapiTokenLoginPost
{
	private String username;
	private String password;
	private String grant_type;
	private String client_id;

	public BrapiTokenLoginPost()
	{
	}

	public BrapiTokenLoginPost(String username, String password, String grant_type, String client_id)
	{
		this.username = username;
		this.password = password;
		this.grant_type = grant_type;
		this.client_id = client_id;
	}

	public String getUsername()
		{ return username; }

	public void setUsername(String username)
		{ this.username = username; }

	public String getPassword()
		{ return password; }

	public void setPassword(String password)
		{ this.password = password; }

	public String getGrant_type()
		{ return grant_type; }

	public void setGrant_type(String grant_type)
		{ this.grant_type = grant_type; }

	public String getClient_id()
		{ return client_id; }

	public void setClient_id(String client_id)
		{ this.client_id = client_id; }
}