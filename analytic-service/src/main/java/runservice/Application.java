package runservice;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.EventType;
import model.RecommenderEvent;
import model.RecommenderRequest;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ExecCreation;

@SpringBootApplication
@Controller
public class Application {

	private static int nbOfInsertedEvents=0;
	
    @RequestMapping("/index")
    public String home(Model model) {
    	model.addAttribute("events", nbOfInsertedEvents);   	
        return "index";
    }
    @RequestMapping("/")
    public String home2(Model model) {
    	model.addAttribute("events", nbOfInsertedEvents);
        return "index";
    }
    
   
    /*
     * Insert event to recommender and elastic
     */
    @PostMapping("/insertEvent")
    public ResponseEntity<?> insertEvent(@RequestBody String event) throws JsonParseException, JsonMappingException, IOException, JSONException {
    	ObjectMapper mapper = new ObjectMapper();
    	RecommenderEvent re=mapper.readValue(event,RecommenderEvent.class);
    	sendToRecommender(re,null);
        System.out.println("Inserted: "+event);
        return new ResponseEntity<String>("Inserted: "+event, new HttpHeaders(), HttpStatus.OK);

    }
    
    /*
     * Insert event type for logging only
     */  
    @PostMapping("/inserteventtype")
    public String insertEventType(@ModelAttribute EventType neweventtype) {
    	 // set headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    String authString="elastic:changeme";
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
	    headers.set("Authorization", "Basic " + authStringEnc);
	    RestTemplate restTemplate = new RestTemplate();
	    // Add the Jackson message converter
	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	    
	    
    	//insert into old index
    	if (neweventtype.getNewIndex().length()==0) {
    		final String uri = "http://192.168.99.100:9200/"+neweventtype.getOldIndex()+"/_mapping/"+neweventtype.getMapping();
    	    HttpEntity<String> entity = new HttpEntity<String>(neweventtype.getProperties(), headers);
             
    	    // send request and parse result
    	    ResponseEntity<String> response = restTemplate
    	            .exchange(uri, HttpMethod.PUT, entity, String.class); 
 
    	}
    	else {
    		final String uri = "http://192.168.99.100:9200/"+neweventtype.getNewIndex();
    		String req="{\"mappings\":{\""+neweventtype.getMapping()+"\":"+neweventtype.getProperties()+"}}";
    	    System.out.println(req);
    		HttpEntity<String> entity = new HttpEntity<String>(req, headers);
             
    	    // send request and parse result
    	    ResponseEntity<String> response = restTemplate
    	            .exchange(uri, HttpMethod.PUT, entity, String.class); 
    	
    	}
    	
		return "redirect:index?res=Event+type+added!";

    }
    
    /*
     * Insert new event type for recommender
     */   
    @PostMapping("/insertrecommendereventtype")
    public String insertRecommenderEventType(@ModelAttribute EventType neweventtype) throws JSONException {
    	 // set headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    String authString="elastic:changeme";
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
	    headers.set("Authorization", "Basic " + authStringEnc);
	    RestTemplate restTemplate = new RestTemplate();
	    // Add the Jackson message converter
	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	    
	    //if recommenderEvents index already exists
	    boolean exists=false;
	    List<String> indices=getElasticIndices();
	    for (int x=0;x<indices.size();x++){
	    	System.out.println(indices.get(x));
	    	System.out.println(indices.get(x).equals("recommender_events"));
	    	if (indices.get(x).equals("recommender_events")) exists=true;
	    }
    	//insert into old index
    	if (exists) {
    		final String uri = "http://192.168.99.100:9200/recommender_events/_mapping/"+neweventtype.getMapping();
    	    HttpEntity<String> entity = new HttpEntity<String>(neweventtype.getProperties(), headers);
             
    	    // send request and parse result
    	    ResponseEntity<String> response = restTemplate
    	            .exchange(uri, HttpMethod.PUT, entity, String.class); 
 
    	}
    	else {
    		final String uri = "http://192.168.99.100:9200/recommender_events";
    		String req="{\"mappings\":{\""+neweventtype.getMapping()+"\":"+neweventtype.getProperties()+"}}";
    	    System.out.println(req);
    		HttpEntity<String> entity = new HttpEntity<String>(req, headers);
             
    	    // send request and parse result
    	    ResponseEntity<String> response = restTemplate
    	            .exchange(uri, HttpMethod.PUT, entity, String.class); 
    	
    	}
    	
		return "redirect:index?res=Event+type+for+recommendations+added!";

    }
    
    /*
     * New event type screen
     */
    @RequestMapping("/neweventtype")
    public String newEventType(@ModelAttribute EventType neweventtype,
    		Model model) throws JSONException {
    	List<String> indices=getElasticIndices();
    	model.addAttribute("indices",indices);
    	model.addAttribute("neweventtype",new EventType());
        return "neweventtype";

    }
    
    /*
     * Get recommendations screen
     */
    @RequestMapping("/getrecommendations")
    public String getRecommendationsForm(@ModelAttribute RecommenderRequest req,
    		Model model) {
    	model.addAttribute("req",new RecommenderRequest());
        return "getrecommendations";

    }
    
 
    
   
    /*
     * Get recommendations for user from notification service
     */
    @PostMapping("/getrecommendations")
    public String returnRecommendation(@ModelAttribute RecommenderRequest req,Model model) {
    	final String uri = "http://192.168.99.100:8070/getrecommendation/"+req.getUserId()+"/"+req.getNbOfItems();
 	    RestTemplate restTemplate = new RestTemplate();
 	    // Add the Jackson message converter
 	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
 	    // set headers
 	    HttpHeaders headers = new HttpHeaders();
 	    headers.setContentType(MediaType.APPLICATION_JSON);
 	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);

 	    // send request and parse result
 	    ResponseEntity<String> response = restTemplate
 	            .exchange(uri, HttpMethod.POST, entity, String.class);

 	    System.out.println(response);
 	    model.addAttribute("userId", req.getUserId());
 	    model.addAttribute("recommendations", response.getBody());
        return "recommendations";

    }
    
    /*
     * Insert event to recommender and elastic
     */
    @RequestMapping("/inserttorecommender")
    public String sendToRecommender(@ModelAttribute RecommenderEvent recommenderevent,
    		Model model) throws JsonProcessingException, JSONException {
    	insertToElastic(recommenderevent);
    	final String uri = "http://192.168.99.100:8070/sendtorecommender";
 	    RestTemplate restTemplate = new RestTemplate();
 	    // Add the Jackson message converter
 	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
 	    // create request body
 	    ObjectMapper mapper = new ObjectMapper();
 	    //recommenderevent.set
 	    String input=mapper.writeValueAsString(recommenderevent);
 	    
 	    
 	    // set headers
 	    HttpHeaders headers = new HttpHeaders();
 	    headers.setContentType(MediaType.APPLICATION_JSON);
 	    HttpEntity<String> entity = new HttpEntity<String>(input, headers);

 	    // send request and parse result
 	    ResponseEntity<String> response = restTemplate
 	            .exchange(uri, HttpMethod.POST, entity, String.class);

 	    System.out.println(response);
    	return "redirect:index?res=Successfully+added+to+recommender!";

    }
    
    /*
     * New event screen
     */
    @RequestMapping("/newrecommenderevent")
    public String newRecommenderEvent(@ModelAttribute RecommenderEvent recommenderevent,
    		Model model) throws JSONException {
    	List<String> types=new ArrayList<String>();
    	Map<String,JSONObject> mappings=getElasticMapping("recommender_events");
    	types.add("clicked");
    	types.add("booked");
    	model.addAttribute("types",types);
    	model.addAttribute("mappings",mappings);
    	model.addAttribute("recommenderevent",new RecommenderEvent());
        return "newrecommenderevent";

    }
    
    /*
     * New event type screen
     */
    @RequestMapping("/eventtypes")
    public String newRecommenderEvent(
    		Model model) throws JSONException {
    	List<String> indices=getElasticIndices(); 
    	Map<String,String> res=new HashMap<String,String>();
    	for (int i=0;i<indices.size();i++){
    		final String uri = "http://192.168.99.100:9200/"+indices.get(i)+"/_mapping";
    	    RestTemplate restTemplate = new RestTemplate();
    	    // Add the Jackson message converter
    	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    	  
    	    // set headers
    	    HttpHeaders headers = new HttpHeaders();
    	    headers.setContentType(MediaType.APPLICATION_JSON);
    	    String authString="elastic:changeme";
    		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
    		String authStringEnc = new String(authEncBytes);
    	    headers.set("Authorization", "Basic " + authStringEnc);
    	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);

    	    // send request and parse result
    	    ResponseEntity<String> response = restTemplate
    	            .exchange(uri, HttpMethod.GET, entity, String.class); 
    	    String mappings=response.getBody();
    	    res.put(indices.get(i), mappings);
    	}
    	model.addAttribute("res", res);
    	return "eventtypes";

    }
    
   
  
    /*
     * Returns list of elastic indices
     */
    private static List<String> getElasticIndices() throws JSONException{
     	final String uri = "http://192.168.99.100:9200/_cat/indices?";
	    RestTemplate restTemplate = new RestTemplate();
	    // Add the Jackson message converter
	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	  
	    // set headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    String authString="elastic:changeme";
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
	    headers.set("Authorization", "Basic " + authStringEnc);
	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);

	    // send request and parse result
	    ParameterizedTypeReference<Map<String, String>[]> typeRef = new ParameterizedTypeReference<Map<String, String>[]>() {};
	    ResponseEntity<Map<String,String>[]> response = restTemplate
	            .exchange(uri, HttpMethod.GET, entity, typeRef);
	    Map<String,String>[] responseBody=response.getBody();
        List<String> res=new ArrayList<String>();
        for (Map<String,String> map:responseBody) {
                res.add(map.get("index"));
        }
		return res;
    	
    }
    /*
     * Returns list of indices with their mappings
     */
    private static Map<String,JSONObject> getElasticMapping(String index) throws JSONException{
     	final String uri = "http://192.168.99.100:9200/"+index+"/_mapping";
	    RestTemplate restTemplate = new RestTemplate();
	    // Add the Jackson message converter
	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	  
	    // set headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    String authString="elastic:changeme";
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
	    headers.set("Authorization", "Basic " + authStringEnc);
	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);

	
	    ResponseEntity<String> response = restTemplate
	            .exchange(uri, HttpMethod.GET, entity, String.class);
	    System.out.println(response.getBody());
	   JSONObject responseBody=new JSONObject(response.getBody());
	   System.out.println(responseBody.getJSONObject("recommender_events"));
	   JSONObject map=responseBody.getJSONObject("recommender_events").getJSONObject("mappings");
	   Iterator<?> keys = map.keys();
	   Map<String,JSONObject> res=new HashMap<String,JSONObject>();
	   while( keys.hasNext() ) {
	       String key = (String)keys.next();
	       if ( map.get(key) instanceof JSONObject ) {
               res.put(key,(JSONObject) map.get(key));
	       }
	   }
       
     // System.out.println(responseBody.get("mappings"));
		return res;
    	
    }
    /*
     * Insert event to elastic
     */
    private static void insertToElastic(RecommenderEvent event) throws JSONException{
    	nbOfInsertedEvents++;
    	 final String uri = "http://192.168.99.100:9200/recommender_events/"+event.getMapping();
    	    RestTemplate restTemplate = new RestTemplate();
    	    // Add the Jackson message converter
    	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    	    // create request body
            
    	    // set headers
    	    HttpHeaders headers = new HttpHeaders();
    	    headers.setContentType(MediaType.APPLICATION_JSON);
    	    String authString="elastic:changeme";
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
    	    headers.set("Authorization", "Basic " + authStringEnc);
    	    JSONObject props=new JSONObject();
    	    if (event.getProperties().length()>0) {
    	    	JSONObject outside=new JSONObject(event.getProperties());
        	    props=outside.getJSONObject("properties");
    	    }
    	    if ( event.getEventType().equals("clicked"))
    	    props.put("eventType", event.getEventType());
    	    props.put("entityType", event.getEntityType());
    	    props.put("entityId", event.getEntityId());
    	    props.put("targetEntityType", event.getTargetEntityType());
    	    props.put("targetEntityId", event.getTargetEntityId());
    	    HttpEntity<String> entity = new HttpEntity<String>(props.toString(), headers);

    	    // send request and parse result
    	    ResponseEntity<String> response = restTemplate
    	            .exchange(uri, HttpMethod.POST, entity, String.class);
    	    System.out.println(response);
    
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);    
    }

}
