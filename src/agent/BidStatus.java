package agent;

import java.io.Serializable;

/*enum for status of bids*/
public enum BidStatus implements Serializable {
    ACCEPTED(),
    REJECTED(),
    OUTBID(),
    ACTIVE,
    WIN();

    BidStatus() {}

}
