# Auction House

-Ian Bradshaw

## How to run program
##-------------------------------
**1. Run bank with java -jar bank.jar**

**2. Run item server with java -jar item.jar**

**3. Run auction house with java -jar auction.jar**

**4. Run agent with java -jar agent.jar**



## Design
##-------------------------------

**The 4 components talk to each other using request classes to pass information through sockets.**

**Account and auction data is stored in an sql db**

## Bugs
##-------------------------------
**The auction house ports are not dynamic and so cannot be spun up in multiples**

**Bids hang after acceptance and are not finalized (item is not deleted and agent is locked from doing any other action 
other than checking balance)**

