package testbed.participants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import testbed.Logger;
import testbed.agent.Agent;
import testbed.messages.CertaintyReplyMsg;
import testbed.messages.CertaintyRequestMsg;
import testbed.messages.OpinionOrderMsg;
import testbed.messages.OpinionReplyMsg;
import testbed.messages.OpinionRequestMsg;
import testbed.messages.ReputationAcceptOrDeclineMsg;
import testbed.messages.ReputationReplyMsg;
import testbed.messages.ReputationRequestMsg;
import testbed.messages.WeightMsg;
import testbed.sim.AppraisalAssignment;
import testbed.sim.Era;
import testbed.sim.Opinion;
import testbed.sim.Weight;

/**
 *
 * Honest agent:
 *    always promises reputations and opinions, and sends the best it can (cg = 10),
 *    all weights = 1.0
 *    never requests reputations because it never uses them
 *    
 * @author K. Fullam, J. Hubner
 *
 */
public class HonestAgent extends Agent {

    private List<ReputationRequestMsg>          reputationRequestsToAccept;
    private List<OpinionRequestMsg>             opinionRequests;

    private Random random = new Random();
    
    public HonestAgent() {        super();
    }

    public HonestAgent(String file) {        super(file);        Logger.writeToLogger("PARAMETERS: " + paramFile);    }    
    public void initializeAgent() {
    }

    // Reputation protocol
    // -----------------------
    

    /** Ask reputation information (gossips) to other agents.
     * 
     *  Incoming Message Type: OpinionReplyMsg
     *  Outgoing Message Type: ReputationRequestMsg
     */
    @Override
    public void prepareReputationRequests() {
        List<OpinionReplyMsg> opinionReplies = getIncomingMessages();
    }

    /** Accept or refuse requests.
     * 
     *  Incoming Message Type: ReputationRequestMsg
     *  Outgoing Message Type: ReputationAcceptOrDeclineMsg
     */
    @Override
    public void prepareReputationAcceptsAndDeclines() {
        // Get reputation request messages from the message inbox
        List<ReputationRequestMsg> reputationRequests = getIncomingMessages();

        // Send an accept message for every reputation request message
        reputationRequestsToAccept = new ArrayList<ReputationRequestMsg>();
        for (ReputationRequestMsg receivedMsg: reputationRequests) {
            reputationRequestsToAccept.add(receivedMsg);
            // Use convenience method for generating the accept message
            ReputationAcceptOrDeclineMsg msg = receivedMsg.reputationAcceptOrDecline(true);
            sendOutgoingMessage(msg);
        }
    }

    /** Reply to confirmed requests.
     * 
     *  Incoming Message Type: ReputationAcceptOrDeclineMsg
     *  Outgoing Message Type: ReputationReplyMsg
     */
    @Override
    public void prepareReputationReplies() {
        // Naively send a reputation of value = 1.0 for every reputation request
        for (ReputationRequestMsg receivedMsg: reputationRequestsToAccept) {
            // Use convenience method for generating a reputation reply
            ReputationReplyMsg msg = receivedMsg.reputationReply(1.0);
            sendOutgoingMessage(msg);
        }
    }

    // Certainty protocol
    // -----------------------
    

    /** Announce its own expertise to a requester.
     * 
     *  Incoming Message Type: OpinionRequestMsg
     *  Outgoing Message Type: OpinionCertaintyMsg
     */
    @Override
    public void prepareCertaintyRequests() {};
    
    @Override
    public void prepareCertaintyReplies() {
        List<CertaintyRequestMsg> certaintyRequest = getIncomingMessages();
        // For each opinion request, send a certainty value
        for (CertaintyRequestMsg receivedMsg: certaintyRequest) {
            double myExpertise = 1 - myExpertiseValues.get( receivedMsg.getEra().getName());
            CertaintyReplyMsg msg = receivedMsg.certaintyReply(myExpertise);
            sendOutgoingMessage(msg);
        }
    }


    // Opinion protocol
    // -----------------------

    /** Ask opinion to other agents.
     * 
     *  Incoming Message Type: ReputationReplyMsg
     *  Outgoing Message Type: OpinionRequestMsg
     */
    @Override
    public void prepareOpinionRequests() {
        // asks max opinions for painting
        for (AppraisalAssignment assignment: assignedPaintings) {
            for (int i=0; i<super.maxNbOpinionRequests; i++) {
                // choose a random agent to ask (risk to ask twice the same agent!)
                String agentToAsk =  agentNames.get(random.nextInt(agentNames.size()-1));
                OpinionRequestMsg msg = new OpinionRequestMsg(agentToAsk, null, assignment);
                sendOutgoingMessage(msg);
            }
        }
    }

    /** Produce evaluations of paintings.
     * 
     *  Incoming Message Type: OpinionRequestConfirmationMsg
     *  Outgoing Message Type: OpinionOrderMsg (to Sim)
     */
    @Override
    public void prepareOpinionCreationOrders() {
        // Get opinion request confirmation messages from the message inbox
        opinionRequests = getIncomingMessages();

        // Order an opinion (from the sim) for each opinion request message received
        for (OpinionRequestMsg receivedMsg: opinionRequests) {
            OpinionOrderMsg msg = receivedMsg.opinionOrder(10);
            sendOutgoingMessage(msg);
        }
    }

    /** Weight the opinion of other agents.
     * 
     *  Outgoing Message Type: WeightMsg (to Sim)
     */
    @Override
    public void prepareOpinionProviderWeights() {
        for (String agentToWeight: agentNames) {
            for (Era thisEra: eras) {
                WeightMsg msg = new WeightMsg(new Weight(getName(), agentToWeight, 1, thisEra.getName()));
                sendOutgoingMessage(msg);
            }
        }   
    }

    /** Reply to confirmed request for opinion.
     * 
     *  Outgoing Message Type: OpinionReplyMsg
     */
    @Override
    public void prepareOpinionReplies() {
        // For each confirmed request, send the opinion this agent ordered (from sim) 
        // to the requesting agent
        for (OpinionRequestMsg receivedMsg: opinionRequests) {
            Opinion op = findOpinion(receivedMsg.getTransactionID());
            // Use convenience method for generating an opinion reply message
            OpinionReplyMsg msg = receivedMsg.opinionReply(op);
            sendOutgoingMessage(msg);
        }
    }
    
    // Use this method to sort through ordered opinions the sim delivers, by transactionID.
    private Opinion findOpinion(String _transactionID) {
        if (createdOpinions != null) {
            for (Opinion op: createdOpinions) {
                if (op.getTransactionID().equals(_transactionID)) {
                    return op;
                }
            }
        }
        return null;
    }
}
