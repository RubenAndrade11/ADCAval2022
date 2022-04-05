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

import pt.unl.fct.di.adc.teste.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")

public class RegisterResource {
	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RegisterResource() {	} // Nothing to be done here (could be omitted)
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doResgister(RegisterData data) {
	
		LOG.fine("Attempt to register user: " + data.username);
		
		
		//Check input data
		if(!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		
		Transaction txn = datastore.newTransaction();
		
		try {
		
			//Creates an entity user from the data. The key is the username
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			
			if(user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey)
						.set("user_name", data.name)
						.set("user_pwd", DigestUtils.sha512Hex(data.password))
						.set("user_email", data.email)
						.set("user_mobilephone", data.mobilephone)
						.set("user_phone", data.phone)
						.set("user_address", data.address)
						.set("user_nif", data.nif)
						.set("user_privacy", data.privacy)
						.set("user_role", "user")
						.build();
				txn.add(user);
				LOG.info("User registered " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
		
		} finally {
			
			if(txn.isActive()) {
				txn.rollback();
			}
		}

	}
	
	/*@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doResgister2(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		//Check input data
		if(!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
	}*/

}
