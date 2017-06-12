package runservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;

import model.EventType;
import model.RecommenderEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import io.prediction.EngineClient;
import io.prediction.Event;
import io.prediction.EventClient;

@SpringBootApplication
@Controller
public class Application {

    /*
     * Function inserts new event to recommender
     */
    @PostMapping("/sendtorecommender")
    public ResponseEntity<?> sendToRecommender(@RequestBody String event) throws JSONException, ExecutionException, InterruptedException, IOException {
    	insertToRecommender(event);
        return new ResponseEntity<String>("Inserted: "+event, new HttpHeaders(), HttpStatus.OK);

    }
    
    /*
     * Function returns recommendations for user
     */
    @RequestMapping("/getrecommendation/{userid}/{num}")
    public ResponseEntity<?> getRecommendation(@PathVariable String userid,@PathVariable String num) throws ExecutionException, InterruptedException, IOException{
    	EngineClient engineClient = new EngineClient("http://192.168.99.100:8000");

    	// query
    	JsonObject response = engineClient.sendQuery(ImmutableMap.<String, Object>of(
    	        "user", userid,
    	        "num",  num
    	    ));
        return new ResponseEntity<String>("Recommendations: "+response.toString(), new HttpHeaders(), HttpStatus.OK);

    }
    


    private static void insertToRecommender(String event) throws JSONException, ExecutionException, InterruptedException, IOException{
    	EventClient client = new EventClient("secret-key", "http://192.168.99.100:7070");
    	JSONObject eventObj = new JSONObject(event);
    	String eventType;
    	if (eventObj.getString("eventType").equals("clicked")) {eventType="rate";}
    	else {
    		eventType="buy";
    	}
    	Event rateEvent = new Event()
    		    .event(eventType)
    		    .entityType(eventObj.getString("entityType"))
    		    .entityId(eventObj.getString("entityId"))
    		    .targetEntityType("item")   //for this template only target entity item is allowed
    		    .targetEntityId(eventObj.getString("targetEntityId"));
    	
    	
    	//mapping clicking as "rating" to match with the used template
    	if (eventType.equals("rate")) rateEvent.property("rating",new Float(3));
    	System.out.println("Event added to recommender:" +rateEvent);
    	client.createEvent(rateEvent);
    }
    
    

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);    
    }

}
