//package net.glaso.ca;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.PushBuilder;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//public class PushController {
//	
//	@GetMapping(path= "/demoPush")
//	public String demoPush( HttpServletRequest request ) {
//		System.out.println( request );
//		PushBuilder pb = request.newPushBuilder();
//		
//		System.out.println("22");
//		if ( pb != null ) {
//			System.out.println("33");
//			
//			pb.path( "resources/img/test1.jpg" ).push();
//		}
//		System.out.println("44");
//		return "demo";
//	}
//}
