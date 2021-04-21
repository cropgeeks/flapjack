package jhi.flapjack.io.brapi;

public class BrapiSessionToken
{
//	private Metadata metadata;

	private String access_token;
	private String userDisplayName;
	private int expires_in = Integer.MAX_VALUE;

	public BrapiSessionToken() {}

	public String getAccess_token()
		{ return access_token; }

	public void setAccess_token(String access_token)
		{ this.access_token = access_token; }

//	public Metadata getMetadata()
//		{ return metadata; }
//
//	public void setMetadata(Metadata metadata)
//		{ this.metadata = metadata; }

	public String getUserDisplayName()
		{ return userDisplayName; }

	public void setUserDisplayName(String userDisplayName)
		{ this.userDisplayName = userDisplayName; }

	public int getExpires_in()
		{ return expires_in; }

	public void setExpires_in(int expires_in)
		{ this.expires_in = expires_in; }
}