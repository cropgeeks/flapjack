// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.io.brapi;

import retrofit2.*;
import retrofit2.http.*;

interface TokenService
{
	/**
	 * Queries the BrAPI provider to attempt to retrieve an authentication token
	 * using the provided username and password.
	 *
	 * @param tokenPost An BrapiTokenLoginPost object with appropriate user data
	 * @return			A Retrofit Call object which wraps a BrapiSeesionToken
	 * 					object to authenticate the user with the BrAPI provider
	 * 					going forward
	 */
	@POST("token")
	Call<BrapiSessionToken> getAuthToken(@Body BrapiTokenLoginPost tokenPost);
}