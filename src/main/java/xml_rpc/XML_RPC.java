package xml_rpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class XML_RPC {
    public static void main(String[] args) throws MalformedURLException, XmlRpcException {

        // Configurer les parametres de connexion a la base de données
        final String url = "http://localhost:8016",
                db = "odoo16",
                username = "admin",
                password = "admin";

        final XmlRpcClient client = new XmlRpcClient();

        // Log in
        final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
        common_config.setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));

        //System.out.println(client.execute(common_config, "version", emptyList()));

        int uid = (int) client.execute(common_config, "authenticate", asList(db, username, password, emptyMap()));

        if(uid != 0){
            System.out.println("=====> success of authentication for the user " + uid);

            final XmlRpcClient models = new XmlRpcClient() {{
                setConfig(new XmlRpcClientConfigImpl() {{
                    setServerURL(new URL(String.format("%s/xmlrpc/2/object", url)));
                }});
            }};

            // See if we can read res.partner model
            /*System.out.println(models.execute("execute_kw", asList(
                    db, uid, password,
                    "res.partner", "check_access_rights",
                    asList("read"),
                    new HashMap() {{
                        put("raise_exception", false);
                    }}
            )));*/

            XML_RPC xmlRpc = new XML_RPC();

            //List of instances search
            //xmlRpc.displayListIntance(db, uid, password, models);


            //List of instances search and read specific fields
            /*List list_instances_search_read = new ArrayList<>();
            list_instances_search_read = asList((Object[]) models.execute("execute_kw", asList(
                    db, uid, password,
                    "instance.request", "search_read",
                    asList(asList()),
                    new HashMap() {{
                        put("fields", asList("name", "limit_date", "odoo_version_id"));
                    //    put("limit", 5);
                    }}
            )));
            System.out.println(list_instances_search_read);*/

            //Creation of instances
            final Integer id = (Integer) models.execute("execute_kw", asList(
                    db, uid, password,
                    "instance.request", "create",
                    asList(new HashMap() {{
                        put("name", "Serveur FX");
                        put("limit_date", "2023-04-25");
                        put("state", "submitted");
                    }})
            ));
            System.out.println("=====> Instance created " + id);
            xmlRpc.displayListIntance(db, uid, password, models);

            //Update instance
            models.execute("execute_kw", asList(
                    db, uid, password,
                    "instance.request", "write",
                    asList(
                            asList(id),
                            new HashMap() {{
                                put("state", "draft");
                            }}
                    )
            ));
            //get instance name after the update
            List list_instance = asList((Object[])models.execute("execute_kw", asList(
                    db, uid, password,
                    "instance.request", "name_get",
                    asList(asList(id))
            )));

            System.out.println(list_instance);

            //Delete instance
            models.execute("execute_kw", asList(
                    db, uid, password,
                    "instance.request", "unlink",
                    asList(asList(id))
            ));

            //check if the deleted instance is still in the database
            asList((Object[])models.execute("execute_kw", asList(
                    db, uid, password,
                    "instance.request", "search",
                    asList(asList(asList("id", "=", id)))
            )));

        }
    }

    public void displayListIntance(String db, int uid, String password, XmlRpcClient models) throws XmlRpcException {
        List list_instances = new ArrayList<>();
        list_instances = asList((Object[]) models.execute("execute_kw", asList(
                db, uid, password,
                "instance.request", "search",
                asList(asList())
        )));
        System.out.println(list_instances);
    }
}
