package net.glaso.ca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for the application home page.
 */
@RestController
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
//	@RequestMapping(value = "/", method = RequestMethod.GET)
//	public ResponseEntity<?> home(Locale locale, Model model) {
//		logger.info("Welcome home! The client locale is {}.", locale);
//		
//		Date date = new Date();
//		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale.US);
//		
//		String formattedDate = dateFormat.format(date);
//		
//		model.addAttribute("serverTime", formattedDate );
//		
//		ResponseEntity<?> entity;
//		Map<String, Object> entities = new HashMap<String, Object>();
//		entities.put( "test", "testValue" );
//		entity = new ResponseEntity<Map<String,Object>>( entities, HttpStatus.OK );
//		
//		return entity;
//	}
//	
//	@RequestMapping( value="/", method=RequestMethod.POST )
//	public ResponseEntity<?> home( HttpServletRequest request, HttpServletResponse response ) throws IOException {
//		logger.info("test");
//		ResponseEntity<?> entity;
//		Map<String, Object> entities = new HashMap<String, Object>();
//		
////		System.out.println( getJSONObject( request ) );
//		
//		entities.put( "test", "testValue" );
//		entity = new ResponseEntity<Map<String,Object>>( entities, HttpStatus.OK );
//		
//		return entity; 
//	}
//	
//	@RequestMapping( value="/test", method=RequestMethod.GET )
//	public void home2( HttpServletRequest request, HttpServletResponse response ) throws IOException {
//		logger.info("test2");
//		ResponseEntity<?> entity;
//		Map<String, Object> entities = new HashMap<String, Object>();
//		
//		PushBuilder pb = request.newPushBuilder();
//		pb.path( "/test1.jpg").push();
//		
//		PrintWriter pw = response.getWriter();
//		response.setCharacterEncoding( "UTF-8" );
//		response.setContentType( "text/html" );
//		
//		pw.println( "<html>");
//		pw.println( "<body>" );
//		pw.println( "<p>testsetwetsetset</p>" );
//		pw.println( "</body>" );
//		pw.println( "</html>" );
//		
//	}
//	
//	public JSONObject getJSONObject( HttpServletRequest request ) throws IOException {
//		StringBuffer jb = new StringBuffer();
//		String line = null;
//		BufferedReader body = null;
//		
//		try {
//			body = request.getReader();
//			
//			while ( ( line = body.readLine()) != null ) {
//				jb.append(line);
//			}
//	
//			logger.trace( "JSONObject : " + jb.toString() );
//			if ( jb.toString().length() == 0 ) {
//				return null;
//			} else {
//				return new JSONObject( jb.toString() );
//			}
//		} finally {
//			body.close();
//		}
//	}
}
