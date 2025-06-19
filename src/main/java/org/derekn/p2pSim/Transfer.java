package org.derekn.p2pSim;

/**
 * Represents a data transfer event between two peer nodes.
 * Encapsulates the sender and receiver involved in the transfer.
 */
public class Transfer {
    private final PeerNode sender;   // The node sending the data chunk
    private final PeerNode receiver; // The node receiving the data chunk

    /**
     * Constructs a new Transfer instance with specified sender and receiver.
     *
     * @param sender   the peer initiating the transfer
     * @param receiver the peer receiving the transfer
     */
    public Transfer(PeerNode sender, PeerNode receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * @return the sender PeerNode involved in this transfer
     */
    public PeerNode getSender() {
        return sender;
    }

    /**
     * @return the receiver PeerNode involved in this transfer
     */
    public PeerNode getReceiver() {
        return receiver;
    }
}
