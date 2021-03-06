/**
 * 
 */
package agent.decision;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import testbed.sim.AppraisalAssignment;
import testbed.sim.Era;
import agent.CCMPAgent;
import agent.decision.DecisionTree;


/**
 * Simple extension of the DecisionTree class
 * 
 * @author Mike Lepard
 */
public class SimpleDT extends DecisionTree {
	
	public SimpleDT(CCMPAgent agent)
	{
		super(agent);
	}	
	
	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#adjustAppraisalValue(java.lang.String, testbed.sim.Era, int)
	 */
	public int adjustAppraisalValue(String toAgent, Era era, int appraisal) {
		// Simple DT doesn't adjust the appraisal value
		return appraisal;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#generateOpinion(java.lang.String, testbed.sim.Era)
	 */
	public boolean generateOpinion(String requestingAgent, Era era) {
		// Simple DT always generates an opinion when asked
		return true;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#getAppraisalCost(java.lang.String, testbed.sim.Era)
	 */
	public double getAppraisalCost(String requestingAgent, Era era)
	{
        // if the requester is trusted we pay 80% for the opinion
        // otherwise we pay 0.01
        if (mReputations.get(requestingAgent) > 0.5)
        	return mAgent.getOpinionCost() * 0.8; // spend 80% of the opinion cost  
        else 
            return 0.01;
    }

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#getCertaintyRequestValue(java.lang.String, testbed.sim.Era)
	 */
	public double getCertaintyRequestValue(String agent, Era era)
	{
		// SimpleDT responds with the agents true expertise value
        String eraName = era.getName();
        double myExpertise = mAgent.getExpertise(eraName);
		return 1-myExpertise;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#getReputationRequestValue(java.lang.String, java.lang.String, testbed.sim.Era)
	 */
	public double getReputationRequestValue(String requestingAgent,
			String aboutAgent, Era era)
	{
		// The simple agent returns are real values
		return mReputations.get(aboutAgent).doubleValue();
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#provideCertaintyRequest(java.lang.String, testbed.sim.Era)
	 */
	public boolean provideCertaintyReply(String agent, Era era)
	{
		//we will always provide our reply that we committed to
		return true;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#provideOpinion(java.lang.String, testbed.sim.Era)
	 */
	public boolean provideOpinion(String requestionAgent, Era era)
	{
		//Simple DT always provides an opinion
		return true;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#provideReputationReply(java.lang.String, java.lang.String, testbed.sim.Era)
	 */
	public boolean provideReputationReply(String requestingAgent,
			String aboutAgent, Era era)
	{
		// The simple agent always provides the reply it committed to
		return true;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#requestAgentCertainty(java.lang.String, testbed.sim.Era)
	 */
	public boolean requestAgentCertainty(String toAgent, Era era)
	{
		//If we haven't sent to many requests, and we don't already know the agents
		//certainty, return true.
		if( mNumCertaintyRequestsLeft > 0 )
		{
		    Map<Era,Double> agCert = mCertainties.get(toAgent); 
		    if (agCert == null || !agCert.containsKey(era))
		    {			
		    	return true;
		    }
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#requestAgentOpinion(java.lang.String, testbed.sim.AppraisalAssignment)
	 */
	public boolean requestAgentOpinion(String toAgent, AppraisalAssignment art)
	{
		if( mNumOpinionRequestsLeft.get(art.getPaintingID()) > 0 )
		{
            if (mReputations.get(toAgent) > 0.5)
            {
                return true;
            }
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#requestAgentReputationUpdate(java.lang.String, java.lang.String, testbed.sim.Era, int)
	 */
	public boolean requestAgentReputationUpdate(String toAgent,
			String aboutAgent, Era era, int currentTimestep)
	{
		// Simple DT never requests an reputation update.
		return true;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#respondToCertaintyRequest(java.lang.String, testbed.sim.Era)
	 */
	public boolean respondToCertaintyRequest(String agent, Era era)
	{
		// We respond to all certainty requests
		return true;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#respondToReputationRequest(java.lang.String, java.lang.String, testbed.sim.Era)
	 */
	public boolean respondToReputationRequest(String requestingAgent,
			String aboutAgent, Era era)
	{
		//The agent always replies to all the requests.
		return true;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#sentCertaintyRequest(java.lang.String, testbed.sim.Era)
	 */
	public void sentCertaintyRequest(String toAgent, Era era)
	{
		mNumCertaintyRequestsLeft--;
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#sentOpinionRequest(java.lang.String, testbed.sim.AppraisalAssignment)
	 */
	public void sentOpinionRequest(String toAgent, AppraisalAssignment art)
	{
		Integer currentLeft = mNumOpinionRequestsLeft.get(art.getPaintingID());
		currentLeft--;		
		mNumOpinionRequestsLeft.put( art.getPaintingID(), currentLeft);
	}

	/* (non-Javadoc)
	 * @see agent.learning.LearningInterface#sentReputationRequest(java.lang.String, java.lang.String)
	 */
	public void sentReputationRequest(String toAgent, String aboutAgent)
	{
		// Simple DT doesn't send reputation requests and doesn't care if we do
	}

	/* (non-Javadoc)
	 * @see agent.decision.DecisionTree#setAgentEraCertainty(java.lang.String, testbed.sim.Era, double)
	 */
	public void setAgentEraCertainty(String agent, Era era, double certainty)
	{
        Map<Era,Double> agCert = mCertainties.get(agent);
        if (agCert == null) {
            agCert = new HashMap<Era,Double>();
            mCertainties.put(agent, agCert);
        }
        agCert.put(era, certainty);
	}

	/* (non-Javadoc)
	 * @see agent.decision.DecisionTree#setAgentPerceivedTrust(java.lang.String, testbed.sim.Era, double)
	 */
	public void setAgentPerceivedTrust(String agent, Era era, double trust)
	{
		// Not used in Simple DecisionTree
	}

	/* (non-Javadoc)
	 * @see agent.decision.DecisionTree#setAgentTrust(java.lang.String, testbed.sim.Era, double)
	 */
	public void setAgentTrust(String agent, Era era, double trust)
	{
		mReputations.put(agent, trust);
	}

	/* (non-Javadoc)
	 * @see agent.decision.DecisionTree#setOurEraCertainty(testbed.sim.Era, double)
	 */
	public void setOurEraCertainty(Era era, double certainty)
	{
		// Not used in SimpleDT, it just explicity asks for the agents expertice 
	}
	
	/* (non-Javadoc)
	 * @see agent.decision.DecisionTree#initTrustNetwork()
	 */
	public void init()
	{
		mReputations = new Hashtable<String,Double>();
		mCertainties = new Hashtable<String,Map<Era,Double>>();
		mNumCertaintyRequestsLeft = 0;
		mNumOpinionRequestsLeft = new Hashtable<String,Integer>();
		
		mNumOpinionRequestsLeft.clear();
	}
	
	/* (non-Javadoc)
	 * @see agent.decision.DecisionTree#frameReset()
	 */
	public void frameReset()
	{
		mNumCertaintyRequestsLeft= mAgent.getMaxCertaintyRequests();;
		mNumOpinionRequestsLeft.clear();
		for( AppraisalAssignment art: mAgent.getAppraisalAssignments())
		{
			mNumOpinionRequestsLeft.put(art.getPaintingID(), mAgent.getMaxOpinionRequests());
		}
	}		

	/* (non-Javadoc)
	 * @see agent.decision.DecisionTree#addAgent()
	 */	
	public void addAgent( String newAgent )
	{
		mReputations.put(newAgent, new Double(1.0));	
	}
	
	/* (non-Javadoc)
	 * @see agent.decision.DecisionTree#removeAgent()
	 */	
	public void removeAgent( String agent )
	{
		mReputations.remove(agent);
	}	
	
	public boolean provideWeight( String aboutAgent, Era era )
	{
        if (mReputations.get(aboutAgent) > 0.8)
        	return true;
        else
        	return false;
	}
	
	public void agentDidNotAcceptReputationRequest( String agent, Era era ) {}
	public void agentDidNotProvideReputation( String agent, Era era ) {}
	public void agentDidNotProvideCertainty( String agent, Era era) {}
	public void agentDidNotProvideOpinion( String agent, Era era) {}
	public void agentDidNotAcceptCertainty( String agent, Era era, double certaintyValue ) {}
	
	public void agentDidAcceptReputationRequest( String agent, Era era ) {}
	public void agentDidProvideReputation( String agent, Era era ) {}
	public void agentDidProvideCertainty( String agent, Era era) {}
	public void agentDidProvideOpinion( String agent, Era era) {}
	public void agentDidAcceptCertainty( String agent, Era era, double certaintyValue ) {}	
}
