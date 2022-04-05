package pt.unl.fct.di.adc.teste.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.teste.util.AuthToken;
import pt.unl.fct.di.adc.teste.util.LogoutData;


@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {


	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private final Gson g = new Gson();

	@POST
	@Path("/op")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLogout(LogoutData data) {

		LOG.fine("Logout attempt by user: " + data.username);

		Transaction txn = datastore.newTransaction();

		try {

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = datastore.get(userKey);

			if(user == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("User does not exist.").build();
			} else {
				
					user = Entity.newBuilder(user).setNull("user_token").build();

					txn.update(user);
					LOG.info("User " + data.username + " logged out.");
					txn.commit();

					return Response.ok().build();
			}

		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}

	}


}
