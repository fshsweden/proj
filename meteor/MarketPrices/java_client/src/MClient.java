import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import me.kutrumbos.DdpClient;

public class MClient implements Observer {
	
	private String host;
	private Integer port;
	private DdpClient client;
	
	public MClient(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	public void connect() {
		
		try {
			client = new DdpClient(host, port);

			client.addObserver(this);
			client.connect();
			//	client.subscribe("customers", null);
			
			System.out.println("CONNECTED!");
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception:" + ex.getLocalizedMessage());
		}
	}
	
	public void updateProduct
	(
		String name,
		Integer bidqty,
		Double bid,
		Double ask,
		Integer askqty,
		Double last,
		Integer lastqty,
		Integer volume
	) 
	{
		JSONObject j = new JSONObject();
		try {
			j.put("name"		, name);
			j.put("bidqty"	, String.format("%d", bidqty));
			j.put("bid"		, String.format("%1.2f", bid));
			j.put("ask"		, String.format("%1.2f", ask));
			j.put("askqty"	, String.format("%d", askqty));
			j.put("last"		, String.format("%1.2f", last));
			j.put("lastqty"	, String.format("%d", lastqty));
			
			call(j);
		}
		catch(JSONException ex) {
			System.err.println("JSON Exception:" + ex.getLocalizedMessage());
		}
	}
	
	
	public void call(JSONObject j) {
		String s2 = j.toString();
		System.out.println("Created JSON:" + s2);
		
		Object[] objArray = new Object[1];
		objArray[0] = s2;
		client.call("updateProduct", objArray);
	}
	
	@Override
	public void update(Observable client, Object msgObj) {

		if (msgObj instanceof String) {
			System.out.println("X Received response: " + msgObj);
			
			String str = (String)msgObj;
			
			try {
				JSONObject nodeRoot  = new JSONObject(str);
				
//				for (String n: JSONObject.getNames(nodeRoot)) {
//					System.out.println(n);
//				}
				
				String msg = nodeRoot.getString("msg");
				String ids;
				
				switch (msg) {
					case "added":
						ids = nodeRoot.getString("id");
						System.out.println(ids + " was added");
					break;
					case "changed":
						ids = nodeRoot.getString("id");
						System.out.println(ids + " was changed");
					break;
					case "deleted":
						ids = nodeRoot.getString("id");
						System.out.println(ids + " was deleted");
					break;
				}
			} catch (JSONException e) {
				
				System.out.println("Exception:" + e.getLocalizedMessage());
			}
		}

	}
	
}
