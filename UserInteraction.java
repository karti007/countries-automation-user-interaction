import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONArray;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserInteraction {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException { 
    	//Handle SSL Handshake errors
    	SSLContext context = SSLContext.getInstance("TLSv1.2");
    	context.init(null,null,null);
    	SSLContext.setDefault(context); 
    	SSLSocketFactory factory = (SSLSocketFactory)context.getSocketFactory();
    	SSLSocket socket = (SSLSocket)factory.createSocket();
    	String[] protocols = socket.getSupportedProtocols();
    	protocols = socket.getEnabledProtocols();
    	
    	//Initializing variables
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String exitInput = "";
        String countryInput = "";
        boolean firstTime = true;
               
        //User Messages to begin
        System.out.println("Welcome User to Countries-Capital testing.");
        System.out.println("At any point of time if you want to exit from this process, please type 'EXIT' and hit enter.");
		System.out.print("Enter first Country Name: ");
		exitInput = br.readLine();
		
		while (!exitInput.equalsIgnoreCase("EXIT")) 
        { 
			//not a first time logic to ask for next country
            if (firstTime == false) {
        		System.out.print("Enter another Country Name: ");
        		countryInput = br.readLine();
        		exitInput = countryInput;
        	}
        	//firstTime switch flip and populate countryInput from exitInput
        	if (firstTime == true) {
        		firstTime = false;
        		countryInput = exitInput;
        	}
        	//Not Typing Exit will continue the call and try to retrieve capital
        	if (!exitInput.equalsIgnoreCase("EXIT")) {
        		try {
        			//Rest Assured API GET call to read response body
        			RestAssured.baseURI = "https://restcountries.eu";
        			Response response = 
        			given().
        			param("Accept","application/json").
        				when().
        				get("/rest/v2/name/"+countryInput).
        					then().
        					contentType(ContentType.JSON).extract().response();
        		
        			JSONArray JSONResponseBody = new JSONArray(response.body().asString());
        			
        			//loop response body and find the match to country typed by user
        			for (int i=0; i<JSONResponseBody.length(); i++) {
        				if ((JSONResponseBody.getJSONObject(i).getString("name").equalsIgnoreCase(countryInput)) ||
        					(JSONResponseBody.getJSONObject(i).getString("cioc").equalsIgnoreCase(countryInput)) ||
        					(JSONResponseBody.getJSONObject(i).getString("demonym").toLowerCase().contains(countryInput.toLowerCase())) 
        					){
        					String Capital = JSONResponseBody.getJSONObject(i).getString("capital");
        					System.out.println("Capital of " + JSONResponseBody.getJSONObject(i).getString("name") + " is: "+ Capital);
        				}
        			}
        		}
                catch (Exception e) {
                	System.out.println("Not a Valid country. Please check Spellings or Country name and Try again");
                }
        	}
        	System.out.println("----------------------------------------------------------------------------------");
        } 
 		System.out.println("Thanks for using our product!!!");
    }
}