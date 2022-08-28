package com.example.demo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	String data = new String();
	@GetMapping("/readData")
	public void readFile()
	{	
	try {
		String filename ="input.txt";
	      File myObj = new File(filename);
	      Scanner myReader = new Scanner(myObj);
	      while (myReader.hasNextLine()) {
	       String data1 = myReader.nextLine();
	       if(data.length()<=0) 
	    	   {data=data1;}
	       else     {data = data.concat("\n"+ data1);}	
	       System.out.println(data);
	      }
	     
	      myReader.close();
	    } catch (FileNotFoundException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	}
	@GetMapping("/convert")
	public void convert()
	{
	      System.out.println("reading from file.");
		readFile();
		if(data == null)
		{
		      System.out.println("An error occurred.");
			return;
		}
	      System.out.println("parsing");
		int count = data.length();
		if(count<=0) 
			{
			System.out.println("data empty"); 
			return;
			}
		StringBuilder extracted = new StringBuilder();
		int flagAppend = 0;
		for(int i =0;i<count;i++)
		{
			System.out.print(data.charAt(i));
			char c = data.charAt(i);
			switch(c)
			{
			case '{' : // add to another variable 
				//extracted.append();
				flagAppend++;
				continue;
			case '}' : // process the current value				
				//extracted=null;
				flagAppend--;
				break;
			};
			if (flagAppend>0)
			{
				extracted.append(c);
			}
			if(flagAppend == 0)
			{
				System.out.println("new line");
				System.out.println(extracted.toString());
				mapping(extracted.toString());
				extracted= new StringBuilder();
			}
		}
		//print all MX1 values.
		//PmtId_InstrId,PmtTpInf_LclInstrm_Prtry,IntrBkSttlmAmt,Dbtr_Nm,Dbtr_PstlAdr_AdrLine,DbtrAcct_Id_Othr_Id;
		//String Cdtr_Nm, CdtrAcct_Id_Othr_Id;
	      System.out.println("Write.");
		System.out.println("PmtId_InstrId : " + MX1.PmtId_InstrId );
		System.out.println("PmtTpInf_LclInstrm_Prtry : " + MX1.PmtTpInf_LclInstrm_Prtry );
		System.out.println("IntrBkSttlmAmt : " + MX1.IntrBkSttlmAmt );
		System.out.println("Dbtr_Nm : " + MX1.Dbtr_Nm );
		System.out.println("Dbtr_PstlAdr_AdrLine : " + MX1.Dbtr_PstlAdr_AdrLine );
		System.out.println("DbtrAcct_Id_Othr_Id : " + MX1.DbtrAcct_Id_Othr_Id );
		System.out.println("Cdtr_Nm : " + MX1.Cdtr_Nm );
		System.out.println("CdtrAcct_Id_Othr_Id : " + MX1.CdtrAcct_Id_Othr_Id );
		WriteXML();
	}
	MXformat MX1 = new MXformat();
	MT103Format MT1 = new MT103Format();
	private void mapping(String currentString)
	{
		String firstString = currentString.substring(0,currentString.indexOf(':') );
		String secondString = currentString.substring(currentString.indexOf(':')+1);
		//System.out.println(firstString);
		//System.out.println(secondString);
		switch(firstString) {
		case "1": MT1.basicHeader = secondString; // basic header  // cant find a mapping
			break;
		case "2":  MT1.ApplicationHeader = secondString; // Application header  // cant a find mapping
			break;
		case "3":  MT1.UserHeader = secondString; // User header  // cant find a mapping
			break;
		case "4": // Text block 
			//String newLine = secondString.substring(0,secondString.indexOf(':')); // skip		
			MT1.TextBlock = new MT103TextBlock();
			processTextBlock(secondString.substring(secondString.indexOf(':')+1));
			break;
		case "5":  MT1.trailers = secondString; // Trailer  // cant find a mapping
			break;		
			
		}		
	}
	private String getNext(String currentString)
	{
		if(currentString.indexOf(':')<0)  
			return currentString;
		return currentString.substring(0,currentString.indexOf(':')).trim();
	}
	private String UpdateBalance(String currentString)
	{
		if(currentString.indexOf(':')<0)
			return "";
		return currentString.substring(currentString.indexOf(':')+1);	
	}
	private void processTextBlock(String secondString)
	{		
		String balance = secondString;
		if(balance.indexOf(':')<0)  return;		
		String KeyString =  getNext(balance);	
		balance = UpdateBalance(balance);
		String value= getNext(balance);
	
		switch(KeyString) {		
		case "20":  //16x Mandatory // Transaction Reference Number Sender's Reference
			MX1.PmtId_InstrId = value;
			MT1.TextBlock.t_20 =  value;
			break; 
			
		case "13C": // /8c/4!n1!x4!n // Time Indication
			break;// cant find mapping
			
		case "23B": //4!c Mandatory //Bank Operation Code
			MX1.PmtTpInf_LclInstrm_Prtry = value;
			MT1.TextBlock.t_23B =  value;
			break;
			
		case "23E": //4!c[/30x] //Instruction Code
			break;
		
		case "26T": //3!c //Transaction Type Code
			break;
			
		case "32A": //6!n3!a15d Mandatory  //Value Date/Currency/Interbank Settled Amount
			MT1.TextBlock.t_32A =  value;
			// To Do find out the mapping field. 
			break; 
			
		case "33B": //3!a15d Currency/Instructed Amount
			MX1.IntrBkSttlmAmt = GetDecimal(value); //EUR preceeds need to remove it. Currency / Original Ordered Amount
			break; 
		
		case "36": //12d //Exchange Rate
			break;
			
		case "50A": // A, F, or K Mandatory //Ordering Customer
		case "50F":
		case "50K":
		 MT1.TextBlock.t_50a =  value;
		 MX1.Dbtr_Nm = GetSecondLine(value); //50A, F or K Ordering Customer (Payer)
		 MX1.Dbtr_PstlAdr_AdrLine = GetThirdLine(value);  
		 MX1.DbtrAcct_Id_Othr_Id = GetFirstLine(value);   
		 break;
		
		case "51A": //[/1!a][/34x]4!a2!a2!c[3!c] //Sending Institution
			break; 
		
		case "52A": // A or D //Ordering Institution (Payer's Bank)
		case "52D":
			break; 
		
		case "53A": // A, B, or D //Sender's Correspondent
		case "53B":	
		case "53D":
			break; 
			
		case "54A": //A, B, or D //Receiver's Correspondent
		case "54B":
		case "54D":
			break;
			
		case "56A": //A, C, or D //Intermediary Institution
		case "56C":
		case "56D":
			break;
			
		case "57A": //A, B, C, or D//Account with Institution (Beneficiary's Bank)
		case "57B":
		case "57C":
		case "57D":
			break;
		
		case "59": //No letter option, A, or F Mandatory //Beneficiary Customer
		case "59A":
		case "59F":
		 MT1.TextBlock.t_59a =  value;
		 MX1.Cdtr_Nm = GetSecondLine(value);  
		 MX1.CdtrAcct_Id_Othr_Id = GetFirstLine(value);   
		 break;
		 
		case "70": //4*35x //Remittance Information (Payment Reference)
			break; 
			
		case "71A": //3!a Mandatory //Details of Charges (BEN / OUR / SHA)
			MT1.TextBlock.t_71A =  value;
			// To Do find out the mapping field. 
			break; 
		
		case "71F": //3!a15d //Sender's Charges
			break; 
			
		case "71G": //3!a15d //Receiver's Charges
			break; 
			
		case "72": //6*35x //Sender to Receiver Information
			break; 
			
		case "77B": //3*35x //Regulatory Reporting
			break; 
		};
		
		if(balance.length()>0){			
			processTextBlock(balance);
		}
		return;
	}
	private double GetDecimal(String Value)
	{
		Value = Value.replaceAll("[^\\d]", "");
		return Double.parseDouble(Value);
	}
	private String GetFirstLine(String value)
	{
		if(value.indexOf('\n')<0)  return value;
		return value.substring(0,value.indexOf('\n'));
	}
	private String GetSecondLine(String value)
	{
		if(value.indexOf('\n')>=0)  
		value = value.substring(value.indexOf('\n')+1);
		return GetFirstLine(value);
	}
	private String GetThirdLine(String value)
	{
		if(value.indexOf('\n')>=0)  
		value = value.substring(value.indexOf('\n')+1);
		return GetSecondLine(value);
	}
	private class MXformat
	{
		String PmtId_InstrId,PmtTpInf_LclInstrm_Prtry,Dbtr_Nm,Dbtr_PstlAdr_AdrLine,DbtrAcct_Id_Othr_Id;
		String Cdtr_Nm, CdtrAcct_Id_Othr_Id;
		double IntrBkSttlmAmt;
		
	}
	private class MT103Format
	{
		String basicHeader; // Mandatory
		String ApplicationHeader;
		String UserHeader;
		String trailers;
		MT103TextBlock TextBlock;		
	}
	private class MT103TextBlock
	{
		String t_20; //Mandatory
		String t_23B; //Mandatory
		String t_32A; //Mandatory
		String t_50a; //Mandatory
		String t_59a; //Mandatory
		String t_71A; //Mandatory
	}
	public void validation()
	{
		//https://www.paiementor.com/swift-mt-message-block-1-basic-header-description/
		
	}
	Document doc;
	private Element addChild(Element parent, String ChildName)
	{
		Element Child = doc.createElement(ChildName);
		parent.appendChild(Child);
		return Child;
	}
	private void addChildWithValue(Element parent, String ChildName, String Value)
	{
		Element Child = addChild(parent, ChildName);
		Child.appendChild(doc.createTextNode(Value));
		//return Child;
	}
	@GetMapping("/write")
	public void WriteXML() 
	{
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = (Document) dBuilder.newDocument();
		
		 // root element
        Element rootElement = doc.createElement("Document");
        doc.appendChild(rootElement);
        
        Element firstChild = addChild(rootElement,"FIToFICstmrCdtTrf");
        Element firstGChild = addChild(firstChild,"CdtTrfTxInf");
        
        Element firstGGChild = addChild(firstGChild,"PmtId");
        //Element firstGGGChild = 
        addChildWithValue(firstGGChild,"InstrId",MX1.PmtId_InstrId);
      //  System.out.println("PmtId_InstrId : " + MX1.PmtId_InstrId );
        
        Element SecondGGChild = addChild(firstGChild,"PmtTpInf");
        Element SecondGGGChild = addChild(SecondGGChild,"LclInstrm");
        //Element SecondGGGGChild = 
        addChildWithValue(SecondGGGChild,"Prtry",MX1.PmtTpInf_LclInstrm_Prtry);
        //	System.out.println("PmtTpInf_LclInstrm_Prtry : " + MX1.PmtTpInf_LclInstrm_Prtry );
		
        //Element ThirdGGChild = 
        addChildWithValue(firstGChild,"IntrBkSttlmAmt", String.valueOf((int)MX1.IntrBkSttlmAmt));
        //System.out.println("IntrBkSttlmAmt : " + MX1.IntrBkSttlmAmt );
        
        Element forthGGChild = addChild(firstGChild,"Dbtr");
        //Element forthGGGChild = 
        addChildWithValue(forthGGChild,"Nm",MX1.Dbtr_Nm);
		//System.out.println("Dbtr_Nm : " + MX1.Dbtr_Nm );
        Element forthGGGChild2 = addChild(forthGGChild,"PstlAdr");
        //Element forthGGGGChild2 = 
        addChildWithValue(forthGGGChild2,"AdrLine",MX1.Dbtr_PstlAdr_AdrLine);        
	//	System.out.println("Dbtr_PstlAdr_AdrLine : " + MX1.Dbtr_PstlAdr_AdrLine );
        
        Element fifthGGChild = addChild(firstGChild,"DbtrAcct");
        Element fifthGGGChild = addChild(fifthGGChild,"Id");
        Element fifthGGGGChild = addChild(fifthGGGChild,"Othr");
        //Element fifthGGGGGChild = 
        addChildWithValue(fifthGGGGChild,"Id",MX1.DbtrAcct_Id_Othr_Id);
	//	System.out.println("DbtrAcct_Id_Othr_Id : " + MX1.DbtrAcct_Id_Othr_Id );
        
        Element sixthGGChild = addChild(firstGChild,"Cdtr");
        //Element sixthGGGChild = 
        addChildWithValue(sixthGGChild,"Nm",MX1.Cdtr_Nm);
	//	System.out.println("Cdtr_Nm : " + MX1.Cdtr_Nm );
        
        Element seventhGGChild = addChild(firstGChild,"CdtrAcct");
        Element seventhGGGChild = addChild(seventhGGChild,"Id");
        Element seventhGGGGChild = addChild(seventhGGGChild,"Othr");
        //Element fifthGGGGGChild = 
        addChildWithValue(seventhGGGGChild,"Id",MX1.CdtrAcct_Id_Othr_Id);
        
	//	System.out.println("CdtrAcct_Id_Othr_Id : " + MX1.CdtrAcct_Id_Othr_Id );
        
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("output.xml"));
        transformer.transform(source, result);
        
        // Output to console for testing
        StreamResult consoleResult = new StreamResult(System.out);
        transformer.transform(source, consoleResult);
        
		}catch (Exception e) {
	         e.printStackTrace();
	      }
		
	}	
}
