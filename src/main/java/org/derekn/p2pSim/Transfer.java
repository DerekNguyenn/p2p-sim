package org.derekn.p2pSim;

public class Transfer {
    private final PeerNode sender;
    private final PeerNode receiver;

    public Transfer(PeerNode sender, PeerNode receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public PeerNode getSender() {
        return sender;
    }

    public PeerNode getReceiver() {
        return receiver;
    }
}

