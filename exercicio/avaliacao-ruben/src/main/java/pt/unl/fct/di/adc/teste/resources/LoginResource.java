package pt.unl.fct.di.adc.teste.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.teste.util.AuthToken;
import pt.unl.fct.di.adc.teste.util.LoginData;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private final Gson g = new Gson();
	
	@POST
	@Path("/op")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.username);
		
		if(!data.validLogin()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing username or password.").build();
		}
		
		Transaction txn = datastore.newTransaction();
		
		
		try {
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
	        Entity user = datastore.get(userKey);
			
	        if(user == null) {
	        	txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("User does not exist.").build();
			} else {
				if((DigestUtils.sha512Hex(data.password)).equals(user.getString("user_pwd"))) {
					
					AuthToken at = new AuthToken(data.username);
					
					Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.username);
					Entity token = txn.get(tokenKey);
					
					token = Entity.newBuilder(tokenKey)
							.set("token_ID", at.tokenID)
							.set("token_creationDate", at.creationData)
							.set("token_expirationDate", at.expirationData)
							.build();
					
					txn.put(token);
					LOG.info("User " + data.username + " logged in.");
					txn.commit();
					
					return Response.ok(g.toJson(token.getProperties().toString())).build();
				} else {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("Incorrect password.").build();
				}
			}
			
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
		
	}
	
	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLoginV2(LoginData data, @Context HttpServletRequest req) {
		LOG.fine("Login attempt by user: " + data.username); 
		
		String locHeader = "X-Appengine-CityLatLong";
		String loc = req.getHeader(locHeader);
		
		String ipHeader = "X-Appengine-User-IP";
		String ip = req.getHeader(ipHeader);
		
		String dataHeader = "Date";
		String date = req.getHeader(dataHeader);
		
		if(!data.validLogin()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing username or password.").build();
		}
		
		Entity user = datastore.get(datastore.newKeyFactory().setKind("User").newKey(data.username));
		
		if(user == null) {
			return Response.status(Status.FORBIDDEN).entity("User do not exist.").build();
		} else {
			if((DigestUtils.sha512Hex(data.password)).equals(user.getString("user_pwd"))) {
				AuthToken at = new AuthToken(data.username);
				return Response.ok(g.toJson(at)).build();
			} else {
				return Response.status(Status.FORBIDDEN).entity("Incorrect password.").build();
			}
		}
	}

	/*@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.username);

		if (data.username.equals("pr.perdigao") && data.password.equals("password")) {
			AuthToken at = new AuthToken(data.username);
			return Response.ok(g.toJson(at)).build();
		}

		return Response.status(Status.FORBIDDEN).entity("Incorrect username or password.").build();
	}

	@GET
	@Path("/{username}")
	public Response checkUsernameAvailable(@PathParam("username") String username) {
		if (username.trim().equals("pr.perdigao")) {
			return Response.ok().entity(g.toJson(true)).build();
		} else {
			return Response.ok().entity(g.toJson(false)).build();
		}
	}*/
}
