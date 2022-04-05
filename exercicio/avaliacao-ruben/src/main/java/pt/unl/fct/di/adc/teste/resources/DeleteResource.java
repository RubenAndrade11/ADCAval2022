package pt.unl.fct.di.adc.teste.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.adc.teste.util.UsernameData;

@Path("/delete")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DeleteResource {

    /**
     * A Logger Object
     */
    private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public DeleteResource() {    } // Nothing to be done here (could be omitted)

    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doDelete(UsernameData data) {
        LOG.fine("Attempt to delete user: " + data.username);

        Transaction txn = datastore.newTransaction();
        
        try {
        	Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
	        Entity user = datastore.get(userKey);
	        
	        Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.username);
			Entity token = datastore.get(tokenKey);
	        
	        
        	if(user == null || token == null) {
        		txn.rollback();
                return Response.status(Status.BAD_REQUEST).entity("User does not exist.").build();
        	} else {

            txn.delete(userKey);
            txn.delete(tokenKey);
            LOG.info("User deleted " + data.username);
            txn.commit();

            return Response.ok("{}").build();
        	}
        } finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
     }
}