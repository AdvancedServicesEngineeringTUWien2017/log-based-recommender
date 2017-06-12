package runservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Event;
import model.ProcessedEvent;

@SpringBootApplication
@Controller
public class Application {
	


	private static List<Event> allEvents;
	Thread simulation;

    @RequestMapping("/")
    public String home(){
        return "index";
    }
    
    private String preprocess(Event e) throws JsonProcessingException{
    	ProcessedEvent pe=new ProcessedEvent();
    	pe.setEntityId(e.getUser_id());
    	pe.setTargetEntityId(e.getDestination_id());
    	pe.setEntityType("user");
    	pe.setTargetEntityType("item");
    	if (e.isBooking()) {
    		pe.setEventType("buy");
    	}
    	else {
    		pe.setEventType("rate");
    	}
    	ObjectMapper mapper = new ObjectMapper();
  	    String props=mapper.writeValueAsString(e);
    	pe.setProperties("{\"properties\":"+props+"}");
    	pe.setMapping("simulations");
    	System.out.println(pe);
  	    return mapper.writeValueAsString(pe);
    }
    
    @RequestMapping("/startsimulation")
    public String startSimulation(Model model) throws ParseException, JsonProcessingException, InterruptedException {
    	simulation=new Thread(new Runnable() {
    	    public void run() {
    	    	for (int i=0;i<allEvents.size();i++) {
    	    		int delay = (new Random().nextInt(1)) * 100;
    	    		try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				try {
    					
						send_event(preprocess(allEvents.get(i)));
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				model.addAttribute("progress", i);
    	    	}
    	    }
    	});
    	simulation.start();
    	
        return "redirect:http://192.168.99.100:8080";
    }
    @RequestMapping("/stopsimulation")
    public String stopSimulation(Model model) throws ParseException, JsonProcessingException, InterruptedException {
    	simulation.stop();
        return "redirect:http://192.168.99.100:8080";
    }
    
    public int simulate(){
		return 0;
    	
    }
  
    public static void send_event(String event) throws JsonProcessingException {
    	final String uri = "http://192.168.99.100:8080/insertEvent";
 	    RestTemplate restTemplate = new RestTemplate();
 	    // Add the Jackson message converter
 	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
 	    // create request body
 	   
 	    
 	    
 	    // set headers
 	    HttpHeaders headers = new HttpHeaders();
 	    headers.setContentType(MediaType.APPLICATION_JSON);
 	    HttpEntity<String> entity = new HttpEntity<String>(event, headers);

 	    // send request and parse result
 	    ResponseEntity<String> response = restTemplate
 	            .exchange(uri, HttpMethod.POST, entity, String.class);

 	    System.out.println(response);
    }
    
    private static void parseCSV() throws ParseException{
    	 String csvFile = "train.csv";
         BufferedReader br = null;
         String line = "";
         allEvents=new ArrayList<Event>();
         try {

             br = new BufferedReader(new FileReader(csvFile));
             int lineNb=0;
             while (((line = br.readLine()) != null) && (lineNb<200000)) {
            	 if (lineNb>0) {
	                 String[] event = line.split(",");
	                 DateFormat eventformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	                 DateFormat inoutformat=new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	                 Date timestamp = eventformat.parse(event[0]);
	                 Date out=new Date();
	                 if (event[11].length()>0) out=inoutformat.parse(event[11]);
	                 Date in=new Date();
	                 if (event[12].length()>0) in=inoutformat.parse(event[12]);
	                 //if distance is not null
	                 double distance = 0;
	                 if (event[6].length()>0) distance=Double.valueOf(event[6]);
	          
	                 allEvents.add(new Event(timestamp,Integer.valueOf(event[3])
	                		 ,Integer.valueOf(event[4]),Integer.valueOf(event[5]),distance,
	                		 String.valueOf(event[7]),Boolean.valueOf(event[8]),Boolean.valueOf(event[9]),
	                		 in,out,String.valueOf(event[16]),
	                		 Integer.valueOf(event[17]),Integer.valueOf(event[18]),
	                		 Integer.valueOf(event[19]),Integer.valueOf(event[20]),
	                		 Boolean.valueOf(event[21]),Integer.valueOf(event[22]),Integer.valueOf(event[23])));
            	 }
            	 lineNb++;
            	 if (lineNb%10000==0) System.out.println(lineNb);
             }
             System.out.println("done");
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             if (br != null) {
                 try {
                     br.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }

     

    }

    public static void main(String[] args) throws ParseException {
        SpringApplication.run(Application.class, args);  
       	parseCSV();
    }

}
