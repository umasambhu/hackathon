package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@GetMapping("/convert")
	public void convert()
	{
		/*try {
			String filename ="MT103.txt";
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        System.out.println(data);
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }*/
		String data = "{1:F01BICFOOYYAXXX8683497519}{2:O1031535051028ESPBESMMAXXX54237522470510281535N}{3:{113:ROMF}{108:0510280182794665}{119:STP}}{4:\n"
				+ ":20:0061350113089908\n"
				+ ":13C:/RNCTIME/1534+0000\n"
				+ ":23B:CRED\n"
				+ ":23E:SDVA\n"
				+ ":32A:061028EUR100000,\n"
				+ ":33A:081029EUR120000,\n"
				+ ":33B:EUR100000,\n"
				+ ":50K:/12345678\n"
				+ "AGENTES DE BOLSA FOO AGENCIA\n"
				+ "AV XXXXX 123 BIS 9 PL\n"
				+ "12345 BARCELONA\n"
				+ ":52A:/2337\n"
				+ "FOOAESMMXXX\n"
				+ ":53A:FOOAESMMXXX\n"
				+ ":57A:BICFOOYYXXX\n"
				+ ":59:/ES0123456789012345671234\n"
				+ "FOO AGENTES DE BOLSA ASOC\n"
				+ ":71A:OUR\n"
				+ ":72:/BNF/TRANSF. BCO. FOO\n"
				+ "-}{5:{MAC:88B4F929}{CHK:22EF370A4073}}";
				
		int count = data.length();
		StringBuilder extracted = new StringBuilder();
		int flagAppend = 0;
		for(int i =0;i<count;i++)
		{
			//System.out.print(data.charAt(i));
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
				//System.out.println("new line");
				//System.out.println(extracted.toString());
				mapping(extracted.toString());
				extracted= new StringBuilder();
			}
		}
		//print all MX1 values.
		//PmtId_InstrId,PmtTpInf_LclInstrm_Prtry,IntrBkSttlmAmt,Dbtr_Nm,Dbtr_PstlAdr_AdrLine,DbtrAcct_Id_Othr_Id;
		//String Cdtr_Nm, CdtrAcct_Id_Othr_Id;
		System.out.println("PmtId_InstrId : " + MX1.PmtId_InstrId );
		System.out.println("PmtTpInf_LclInstrm_Prtry : " + MX1.PmtTpInf_LclInstrm_Prtry );
		System.out.println("IntrBkSttlmAmt : " + MX1.IntrBkSttlmAmt );
		System.out.println("Dbtr_Nm : " + MX1.Dbtr_Nm );
		System.out.println("Dbtr_PstlAdr_AdrLine : " + MX1.Dbtr_PstlAdr_AdrLine );
		System.out.println("DbtrAcct_Id_Othr_Id : " + MX1.DbtrAcct_Id_Othr_Id );
		System.out.println("Cdtr_Nm : " + MX1.Cdtr_Nm );
		System.out.println("CdtrAcct_Id_Othr_Id : " + MX1.CdtrAcct_Id_Othr_Id );
		
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
		//System.out.println("ProcessText");
		//System.out.println(secondString);
		String balance = secondString;//secondString.substring(secondString.indexOf(':')+1);
		if(balance.indexOf(':')<0)  return;		
		//System.out.println(newLine);		
		//System.out.println(balance);		
		//String firstString = balance.substring(0,balance.indexOf(':'));
		String KeyString =  getNext(balance);
		//System.out.println(firstString);
		balance = UpdateBalance(balance);
		//System.out.println(balance);
		String value= getNext(balance);
		//if(balance.indexOf(':')>0) 
		// value = balance.substring(0, balance.indexOf(':'));	
		//else
		//	value= balance;
		//System.out.println(value);
		//if(balance.indexOf(':')<0) 
		//	balance="";
		//else
		//	balance = balance.substring(balance.indexOf(':')+1);
		//
		switch(KeyString) {
		case "13C": break;// cant find mapping	
		case "20":  
			MX1.PmtId_InstrId = value; break; // Transaction Reference Number
		case "23B": 
			MX1.PmtTpInf_LclInstrm_Prtry = value; break; //Bank Operation Code
		case "32A": break; //Value Date / Currency / Interbank Settled
		case "33B": 
			MX1.IntrBkSttlmAmt = GetDecimal(value); break; //EUR preceeds need to remove it. Currency / Original Ordered Amount
		case "50A":
		case "50F":
		case "50K": 
		 MX1.Dbtr_Nm = GetSecondLine(value); //50A, F or K Ordering Customer (Payer)
		 MX1.Dbtr_PstlAdr_AdrLine = GetThirdLine(value);  
		 MX1.DbtrAcct_Id_Othr_Id = GetFirstLine(value);   
		 break;
		case "52A":
		case "52D":break; //Ordering Institution (Payer's Bank)
		case "54A": //Sender's Correspondent (Bank)
		case "54B":
		case "54D":break;	
		case "56A": //Intermediary (Bank)
		case "56C":
		case "56D":break;
		case "57A": //Account with Institution (Beneficiary's Bank)
		case "57B":
		case "57C":
		case "57D":break;
		case "59": //Beneficiary
		case "59A": 
		 MX1.Cdtr_Nm = GetSecondLine(value);  
		 MX1.CdtrAcct_Id_Othr_Id = GetFirstLine(value);   
		 break;
		case "70":break; //Remittance Information (Payment Reference)
		case "71A":break; //Details of Charges (BEN / OUR / SHA)
		case "72":break; //Sender to Receiver Information
		case "77B":break; //Regulatory Reporting
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
		
	}
	public void validation()
	{
		//https://www.paiementor.com/swift-mt-message-block-1-basic-header-description/
		
	}
}
