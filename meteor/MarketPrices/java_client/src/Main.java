import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import me.kutrumbos.DdpClient;
import me.kutrumbos.enums.DdpMessageField;

public class Main implements Observer {

	public Main() {

		online();
		// demo();
	}
	
	private void online() {
		/*
		 * NOTE. There is a bug, so that if we loose connection with the server, we fail to establish it again and
		 * have to restart.
		 * 
		 * 
		 */
		DdpClient client;
		try {
			client = new DdpClient("localhost", 3000);

			// create DDP client observer
			// Observer obs = new SimpleDdpClientObserver();

			// add observer
			client.addObserver(this);
			client.connect();
			//	client.subscribe("customers", null);
			
			new IB2Meteor(new IB2Meteor.InstrumentEventHandler() {
				@Override
				public void updated(IB2Meteor.Instrument i) {

					try {
						Object[] objArray = new Object[1];
						
						Double bid = i.getBid();
						Double ask = i.getAsk();
						
						JSONObject j = new JSONObject();
						j.put("name"		, i.getSymbol());
						j.put("bidqty"	, String.format("%d",i.getBidsize()) );
						j.put("bid"		, String.format("%1.2f", i.getBid()));
						j.put("ask"		, String.format("%1.2f", i.getAsk()));
						j.put("askqty"	, String.format("%d",i.getAsksize()) );
						j.put("last"		, String.format("%1.2f", i.getLast()));
						j.put("lastqty"	, String.format("%d", i.getLastsize()));
						j.put("volume"	, String.format("%d", i.getVolume()));
						
						String s2 = j.toString();
						System.out.println("Created JSON:" + s2);
						
						objArray[0] = s2;
						if (client.call("updateProduct", objArray) == null) {
							System.out.println("No connection, retrying in 15 secs:" + s2);
							Thread.sleep(15000);
							client.reconnect();
						};
					}
					catch (JSONException ex) {
						System.out.println("JSON EXCEPTION:" + ex.getLocalizedMessage());
					}
					catch (Exception ex) {
						System.out.println("EXCEPTION:" + ex.getLocalizedMessage());
					}
				}
			});
			
				
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception:" + ex.getLocalizedMessage());
		}
	}
	
	
	private void demo() {
		DdpClient client;
		try {
			client = new DdpClient("localhost", 3000);

			// create DDP client observer
			// Observer obs = new SimpleDdpClientObserver();

			// add observer
			client.addObserver(this);
			client.connect();
			//	client.subscribe("customers", null);
			
			
			Random r = new Random(new Date().getTime());
			
			for (int i=0; i<1220; i++) {
				Object[] objArray = new Object[1];
	
				
				Double bid = r.nextDouble() * 100.0;
				Double ask = r.nextDouble() * 100.0;
				
				JSONObject j = new JSONObject();
				j.put("name"		, "ERIC.B");
				j.put("bidqty"	, "1");
				j.put("bid"		, String.format("%1.2f", bid));
				j.put("ask"		, String.format("%1.2f", ask));
				j.put("askqty"	, "4");
				j.put("last"		, "5");
				j.put("lastqty"	, "6");
				
				String s2 = j.toString();
				System.out.println("Created JSON:" + s2);
				
				objArray[0] = s2;
				if (client.call("updateProduct", objArray) == null) {
					System.out.println("No connection, retrying in 15 secs:" + s2);
					Thread.sleep(15000);
					client.reconnect();
				}
				
				Thread.sleep(5000);
			}
			
			// Map<DdpMessageField, Object> x = new HashMap<DdpMessageField, Object>();
			// x.put(DdpMessageField.msg, "insert");
			// client.send(new Map<DdpMessageField, Object>)
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception:" + ex.getLocalizedMessage());
		}

	}

	public void slask(String method, Object[] params){

		Map<DdpMessageField,Object> callMsg = new HashMap<DdpMessageField,Object>();
		callMsg.put(DdpMessageField.msg, "method");
		callMsg.put(DdpMessageField.method, method);
		callMsg.put(DdpMessageField.params, params);
		
		Gson gson = new Gson();
		String json = gson.toJson(callMsg);
		
		System.out.println("JSON:" + json);
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

	public static void main(String[] args) {
		new Main();
	}

}
