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

import pt.unl.fct.di.adc.teste.util.AuthToken;
import pt.unl.fct.di.adc.teste.util.ChangePasswordData;
import pt.unl.fct.di.adc.teste.util.ModifyData;


@Path("/modify")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModifiyDataResource {
	
	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	@POST
	@Path("/userdata")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doModify(ModifyData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		Transaction txn = datastore.newTransaction();
		
		try {
		
			//Creates an entity user from the data. The key is the username
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			
			if(user == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User does not exist.").build();
			} else {
				
				String mobilephone = user.getString("user_mobilephone");
				String phone = user.getString("user_phone");
				String address = user.getString("user_address");
				String nif = user.getString("user_nif");
				String privacy = user.getString("user_privacy");
				
				if(!data.mobilephone.equals("")) {mobilephone = data.mobilephone;}
				if(!data.phone.equals("")) {phone = data.phone;}
				if(!data.address.equals("")) {address = data.address;}
				if(!data.nif.equals("")) {nif = data.nif;}
				if(!data.privacy.equals("")) {privacy = data.privacy;}
				
				user = Entity.newBuilder(userKey)
						.set("user_pwd", user.getString("user_pwd"))
						.set("user_name", user.getString("user_name"))
						.set("user_email", user.getString("user_email"))
						.set("user_mobilephone", mobilephone)
						.set("user_phone", phone)
						.set("user_address", address)
						.set("user_nif", nif)
						.set("user_privacy", privacy)
						.build();
				txn.put(user);
				LOG.info("User updated " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}
		
		} finally {
			
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	
	@POST
	@Path("/password")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doModify(ChangePasswordData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		Transaction txn = datastore.newTransaction();
		
		try {
		
			//Creates an entity user from the data. The key is the username
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			
			
			
					if((DigestUtils.sha512Hex(data.oldPassword)).equals(user.getString("user_pwd"))) {
					
					user = Entity.newBuilder(userKey)
							.set("user_pwd", DigestUtils.sha512Hex(data.newPassword))
							.set("user_name", user.getString("user_name"))
							.set("user_email", user.getString("user_email"))
							.set("user_mobilephone", user.getString("user_mobilephone"))
							.set("user_phone", user.getString("user_phone"))
							.set("user_address", user.getString("user_address"))
							.set("user_nif", user.getString("user_nif"))
							.set("user_privacy", user.getString("user_privacy"))
							.build();
					
					txn.put(user);
					LOG.info("User " + data.username + " logged in.");
					txn.commit();
					
					return Response.ok("{}").build();
				} else {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("Incorrect password.").build();
				}
		
		} finally {
			
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	

}
