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

import java.util.*;

public class SlaveAgent extends Agent {
	protected void setup() {
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

		ReceiveMessage rm = new ReceiveMessage();
	    addBehaviour(rm);		
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		System.out.println("REMOVING AGEEENT");
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Printout a dismissal message
		System.out.println("Seller-agent "+getAID().getName()+" terminating.");
	}

	//class called from the setuo(). It is making the slave agents able to listen for a REQUEST messaged addressed to them
 public class ReceiveMessage extends CyclicBehaviour {
	        // Variable to Hold the content of the received Message
	        private String Message_Performative;
	        private String Message_Content;
	        private String SenderName;
	        private String MyPlan;
	        
	        public void action() {
	            //Receive a Message
	            ACLMessage msg = receive();
	            if(msg != null) {
	                Message_Performative = msg.getPerformative(msg.getPerformative());
	                Message_Content = msg.getContent();
	                SenderName = msg.getSender().getLocalName();

	                System.out.println("***"+this.getAgent().getLocalName()+" Received a Message***" +"\n"+
	                            "The Sender Name is:"+ SenderName+"\n"+
	                            "The Content of the Message is::> " + Message_Content+"\n"+
	                            "::: And Performative is:: " + Message_Performative);
	                //Reply to the Message
	                if (Message_Performative.equals("REQUEST")) {
	                    ACLMessage out_msg = new ACLMessage(ACLMessage.INFORM);
	                    out_msg.addReceiver(new AID(SenderName, AID.ISLOCALNAME));
	                    out_msg.setLanguage("English");
	                    out_msg.setContent("Autoreply message");
	                    send(out_msg);
	                    System.out.println("****"+this.getAgent().getLocalName()+" Replied to::> " + SenderName+"***");
	                    System.out.println("The Content of My Reply is:" + out_msg.getContent());
	                }
	            }
	        }
	    }
	
}
