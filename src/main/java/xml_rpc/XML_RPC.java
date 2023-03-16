package xml_rpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class XML_RPC {
    public static void main(String[] args) throws MalformedURLException, XmlRpcException {
        final String url = "http://localhost:8016",
                db = "odoo16",
                username = "admin",
                password = "admin";

        final XmlRpcClient client = new XmlRpcClient();
        final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
        common_config.setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));
        client.execute(common_config, "version", emptyList());

        int uid = (int) client.execute(common_config, "authenticate", asList(db, username, password, emptyMap()));

        if(uid != 0){
            System.out.println("=====> success of authentication for the user " + uid);

            final XmlRpcClient models = new XmlRpcClient() {{
                setConfig(new XmlRpcClientConfigImpl() {{
                    setServerURL(new URL(String.format("%s/xmlrpc/2/object", url)));
                }});
            }};

            // See if we can read res.partner model
            models.execute("execute_kw", asList(
                    db, uid, password,
                    "res.partner", "check_access_rights",
                    asList("read"),
                    new HashMap() {{
                        put("raise_exception", false);
                    }}
            ));
        }
    }
}
