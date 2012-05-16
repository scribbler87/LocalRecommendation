package fi.uni.aalto.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fi.uni.aalto.controllers.HTTPHelper;
import android.app.Activity;
import android.os.Bundle;

public class InformationVisualizerActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_visualizer);
		System.err.println("hello");
	//	String s = HTTPHelper.executeGetRequest(null, "http://playground.cs.hut.fi/t-110.5140_2012/visualizer/2hour/average/xml");
	//	System.err.println("inovis: " + s);
		try {
			getPosts();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private void getPosts() throws ParserConfigurationException, SAXException,
	IOException, URISyntaxException {
		//			 create url for getting information about location from the according woeid
		String urlString = "http://playground.cs.hut.fi/t-110.5140_2012/visualizer/2hour/average/xml";


		
		// create a new factory, which defines the API for the DOM-Parser
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Defines API for getting XML-Document
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parse the actual XML-Document
		Document doc = null;
		try{
			doc = builder.parse(urlString);
			doc.getDocumentElement().normalize();
			
			
			NodeList childNodes = doc.getChildNodes();
			if(childNodes.getLength() > 0)
				System.err.println("Amazing");
		}
		catch(FileNotFoundException e){
			System.err.println(e.getStackTrace());
		}

		//doc.
//		posts = new ArrayList<Post>();
		if(doc != null)
		{
			NodeList wall  = doc.getElementsByTagName("post");

			for(int i = 0; i < wall.getLength(); i++)
			{
				Node wallEl = wall.item(i);

				// get information about the location
				Node userData = null;

				// check if we have get an error file
				/*
				 * <socialgags>
				 * 		<!-- Extra things in head block -->
				 * 		<error>
				 * 			The user bla is not in our system
				        		</error>
				        		<!-- End-->
				   </socialgags>
				 */

//				userData = XMLHelper.getUserData(wallEl, "error");
//				if(userData != null)
//				{
//					Toast.makeText(SocialGagActivity.this,"There is no post at the moment. Maybe the server is not available at the moment" +
//							"Please try later again.", Toast.LENGTH_SHORT).show();
//					return;
//				}
//
//
//				userData = XMLHelper.getUserData(wallEl, "title");
//				String title = userData.getTextContent();
//				userData = XMLHelper.getUserData(wallEl, "description");
//				String description = userData.getTextContent();
//				userData = XMLHelper.getUserData(wallEl, "link");
//				String link = userData.getTextContent();
//				userData = XMLHelper.getUserData(wallEl, "date");
//				String date = userData.getTextContent();
//
//				Post post = new Post();
//				post.setDate(date);
//				post.setTitle(title);
//				post.setUrl(link);
//				post.setDescription(description);
//
//				posts.add(post);
			}
		}


	}
}
