package com.acc.property;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Comparator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class MyNode {
    public JsonNode jsonnode;
    public double propertyprice;

    public MyNode(JsonNode jsonNode, double propertyprice) {
        this.jsonnode = jsonnode;
        this.propertyprice = propertyprice;
    }
    public double getValue() {
        return propertyprice;
    }
    
    @Override
    public String toString() {
        return "MyNode{" +
                "jsonnode='" + jsonnode + '\'' +
                ", propertyprice=" + propertyprice +
                '}';
    }
}

public class SearchProperty {
    public int minPrice,maxPrice,minBed,maxBed,minBath,maxBath;  //  0 means no limited
    public String propertyType,searchWord;
    public int sortType;	//0- no sort, 1-ascending by price, 2-descending by price
    private List<JsonNode> propertyList = new ArrayList<>();
    private List<MyNode> myList = new ArrayList<>();
    
    public SearchProperty() {
    	minPrice=0;			//  0 means no limited
    	maxPrice=0;			//  0 means no limited
    	minBed=0;
    	maxBed=0;
    	minBath=0;
    	maxBath= 0;
    	sortType=0;			//0- no sort, 1-ascending by price, 2-descending by price
    	propertyType="";  	//like house condo apartment etc.
    	searchWord="";
    }
    public SearchProperty(String keyword,String propertype,int minprice,int maxprice,int minbed,int maxbed,int minbath,int maxbath,int sortmode) {
    	minPrice=minprice;
    	maxPrice=maxprice;
    	minBed=minbed;
    	maxBed=maxbed;
    	minBath=minbath;
    	maxBath= maxbath;
    	sortType=sortmode;			
    	propertyType=propertype;  	
    	searchWord=keyword;
    }

    public void loadfromfile(String jsonFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(jsonFilePath));
        Pattern patternNumber = Pattern.compile("\\d+(\\.\\d+)?"); 
        Pattern patternPrice = Pattern.compile("(\\d{1,3}(,\\d{3})*(\\.\\d+)?)");         
        
        propertyList.clear();
        myList.clear();
        
        for (JsonNode listing : root) {
        	if (listing.isEmpty()==false) {  //confirm node is not empty
        		int findmark=1;	
        		double propertyprice=0;
        		
        		//search by property Type
        		if (searchWord.trim().length()>0) {
    	        	String s = listing.get("location").asText();
    	            if (s.toLowerCase().contains(searchWord.toLowerCase())) {
    	            }    
    	            else
    	            	findmark=0;   	            
        		}
        		//search by location key word
        		if ((findmark==1)&&(propertyType.trim().length()>0)) {
    	        	String s = listing.get("propertyType").asText();
    	            if (s.toLowerCase().contains(propertyType.toLowerCase())) {
    	            }    
    	            else
    	            	findmark=0;          			
        		}
        		//search by price key word
	        	String strPrice = listing.get("price").asText();
	            Matcher matcherPrice = patternPrice.matcher(strPrice); 
	            if (matcherPrice.find()) {
	            	propertyprice=Double.parseDouble(matcherPrice.group().replaceAll(",",""));
	            }
        		if ((findmark==1)&&((minPrice>0)||(maxPrice>0))) {
    	            //maxPrice=0 means no upper bound 
        			double value=propertyprice;
        			
        			if (maxPrice==0) {  
        	            if (value<minPrice) 
        	            	findmark=0; 
        			}
        			else {
        	            if ((value<minPrice)||(value>maxPrice))
        	            	findmark=0;       				
        			}       				
        		}
        		//search by bed number
        		if ((findmark==1)&&((minBed>0)||(maxBed>0))) {   
    	        	double value=0;
    	        	String s = listing.get("beds").asText();
    	            Matcher matcher = patternNumber.matcher(s); 
    	            if (matcher.find()) {
    	            	value=Double.parseDouble(matcher.group());
    	            }        			
    	            //maxBed=0 means no upper bound 
        			if (maxBed==0) {  
        	            if (value<minBed) 
        	            	findmark=0; 
        			}
        			else {
        	            if ((value<minBed)||(value>maxBed))
        	            	findmark=0;       				
        			}       			
        		}
        		//search by bath number
        		if ((findmark==1)&&((minBath>0)||(maxBath>0))) {   
    	        	double value=0;
    	        	String s = listing.get("baths").asText();
    	            Matcher matcher = patternNumber.matcher(s); 
    	            if (matcher.find()) {
    	            	value=Double.parseDouble(matcher.group());
    	            }        			
    	            //maxBed=0 means no upper bound 
        			if (maxBath==0) {  
        	            if (value<minBath) 
        	            	findmark=0; 
        			}
        			else {
        	            if ((value<minBath)||(value>maxBath))
        	            	findmark=0;       				
        			}       			
        		}

	            //add the node that matched all conditions
        		if (findmark==1) {
        			propertyList.add(listing);
        			MyNode tempmynode=new MyNode(listing,propertyprice);
        			tempmynode.jsonnode=listing;
        			tempmynode.propertyprice=propertyprice;
        			myList.add(tempmynode);

        		}

        	}
        }
        Comparator<MyNode> mynodeComparator= new Comparator<MyNode>() {
            @Override
            public int compare(MyNode s1, MyNode s2) {
                return Double.compare(s1.getValue(), s2.getValue());
            }
        };
        if (sortType==1)   { 
        	Collections.sort(myList, mynodeComparator);
        	propertyList.clear();
        	for (MyNode temp:myList) 
        		propertyList.add(temp.jsonnode);     		
        }
        else if (sortType==2) {
        	Collections.sort(myList, mynodeComparator);
        	Collections.reverse(myList);
        	propertyList.clear();
        	for (MyNode temp:myList) 
        		propertyList.add(temp.jsonnode);     
        }   	
    }
    
    public int count() {
    	return propertyList.size();
    }
    public List<JsonNode> getresult() {
    	return propertyList;
    }    

    public static void main(String[] args) {
    	SearchProperty search = new SearchProperty("CURRY","Residential",0,0,0,0,0,0,1);
        try {
        	int count=0;
        	search.loadfromfile("output.json");		
        	System.out.println(search.count());
        	for (JsonNode node:search.propertyList) 
        		System.out.println(node);
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
