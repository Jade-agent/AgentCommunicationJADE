/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/
 

import jade.core.AID;
import jade.core.Agent;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.*;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.Iterator;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.BufferedReader;
import java.util.*;

public class MainAgent extends Agent {
	private AgentController ac = null;
	private AID[] sellerAgents;
	AgentFrame myGui;
	protected void setup() {
		// Create and show the GUI 
		myGui = new AgentFrame(this);
		myGui.setVisible(true);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("book-selling");
		sd.setName("JADE-book-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	    ReceiveMsg();
		System.out.println("Hello World! My name is " + getAID().getLocalName());

	}

	// When the cancel button is clicked 
	protected void takeDown() {
		System.out.println("REMOVING AGEEENT");
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Close the GUI
		myGui.dispose();
		// Printout a dismissal message
		System.out.println("Seller-agent "+getAID().getName()+" terminating.");
	}

	// When submit button is clicked
	public void createAgents(final int agentsNumber, int i) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				int tmp3;
				for (tmp3=+i; tmp3<=agentsNumber+i-1; tmp3++){
				try {
					// create agent t1 on the same container of the creator agent
					jade.wrapper.AgentContainer container = getContainerController(); // get a container controller for creating new agents					tww1++;
					ac = ((ContainerController) container).createNewAgent("R"+tmp3, "SlaveAgent", null);
					ac.start(); //calling the setuo() method from SlaveAgent class
					String tmp2=Integer.toString(tmp3);
					System.out.println("R"+tmp2+" is created");
				} catch (Exception any) {
					any.printStackTrace();
				}
				}
				myGui.numberofagents(tmp3);
				doWait(1000);
				//listing the all the existing agents registered on DF
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("book-selling");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					System.out.println("Found the following agents:");
					sellerAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						sellerAgents[i] = result[i].getName();
						System.out.println(sellerAgents[i].getName());
						if (sellerAgents[i].getName().contains("Bob"))
						{
							//do nothing, just eliminating Bob
						}
						else{
						myGui.addItemDLL(sellerAgents[i].getName()); //ading the slave agents to the ComboBox
						}
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}				
			}			
		} ); 
	}
	
	//when Send button is clicked
	public void SendMessage(String name){
		addBehaviour(new OneShotBehaviour() {
        public void action() {
        	int msgIntType;
        	String msgtype=myGui.getMsgType(); //getting the message type, REQUEST or INFORM
        	if (msgtype.contains("REQUEST")){
        		msgIntType=16;
        	}
        	else {
        		msgIntType=7;
        	}
        	ACLMessage msg = new ACLMessage(msgIntType);
            msg.addReceiver(new AID(name, AID.ISLOCALNAME));
            msg.setLanguage("English");
            String msgToSend=myGui.getText(); //getting the message from the gui the we want to send
            msg.setContent(msgToSend);
            //send msg to change jtextarea1
            send(msg);
            System.out.println("****I Sent Message to::>"+name+"*****"+"\n"+
                                "The Content of My Message is::>"+ msg.getContent());
        }
		} );
    }

	//function called from the setup(). It is making the main agent able to listen for messaged addressed to him
	public void ReceiveMsg(){
		addBehaviour(new CyclicBehaviour() {
			    private String Message_Performative;
		        private String Message_Content;
		        private String SenderName;
		        private String MyPlan;
		        public void action() {
		            ACLMessage msg = receive();
		            if(msg != null) {
		                Message_Performative = msg.getPerformative(msg.getPerformative());
		                Message_Content = msg.getContent();
		                SenderName = msg.getSender().getLocalName();
		                System.out.println(" ****I Received a Message***" +"\n"+
		                        "The Sender Name is::>"+ SenderName+"\n"+
		                        "The Content of the Message is::> " + Message_Content + "\n"+
		                        "::: And Performative is::> " + Message_Performative + "\n");
		                System.out.println("end");
		                doWait(1000);
		                myGui.setText(SenderName+": "+Message_Content);
		            }
		        }
		} );
    }	
}
