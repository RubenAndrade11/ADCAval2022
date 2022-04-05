package pt.unl.fct.di.adc.teste.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.teste.util.AuthToken;
import pt.unl.fct.di.adc.teste.util.UsernameData;
import pt.unl.fct.di.adc.teste.util.LoginData;

@Path("/list")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")

public class ListResource {

	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private final Gson g = new Gson();
	
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll() {
		//LOG.fine("Listing attempt by user: " + data.username);
		
		/*if(!data.validLogin()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing username or password.").build();
		}*/
		
		List<String> listedUsers = new LinkedList<>();
		Query<Key> query = Query.newKeyQueryBuilder().setKind("User").build();
		
		
		QueryResults<Key> users = datastore.run(query);
		
		while (users.hasNext()) {
			Key user = users.next();
			listedUsers.add(user.getName());
		}
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i<listedUsers.size(); i++) {
			sb.append(listedUsers.get(i) + " ");
		}
		
		String userData = sb.toString();
		
		return Response.ok(g.toJson(userData)).build();
		
	}
	
	@POST
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listToken(UsernameData data) {
		//LOG.fine("Listing attempt by user: " + data.username);
		
		/*if(!data.validLogin()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing username or password.").build();
		}*/
		
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.username);
		Entity token = datastore.get(tokenKey);
		
		
		return Response.ok(g.toJson(token.getProperties().toString())).build();
		
	}

}
