package agent;

import java.util.Scanner;

/*class that acts as main controller of agent client*/
public class AgentClient {

    public static Agent agent = null;

    public AgentClient() {}

    public static void main(String[] args) {
        /*display commands for user*/
        System.out.println("Commands:");
        System.out.println("[n] - Create Agent");
        System.out.println("[s] - Check your current balance");
        System.out.println("[a] - Display Auction Houses");
        System.out.println("[c]  - Connect to Auction House ");
        System.out.println("[i] - Display Auction House Items for Sale");
        System.out.println("[b] - Enter a Bid for an Item");
        System.out.println("[q] - Disconnect Client");

        Scanner scanner = new Scanner(System.in);

        /*handle response*/
        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "n":
                    agent = new Agent();
                    break;
                case "a":
                    if (agent != null) {
                        agent.requestAuctionHouses();
                    } else {
                        System.out.println("You must create an agent before requesting Auction Houses");
                    }
                    break;
                case "i":
                    if (agent != null) {
                        agent.requestItems();
                    } else {
                        System.out.println("You must create an agent before requesting items to bid on");
                    }
                    break;
                case "c":
                    if (agent != null) {
                        while (true) {
                            System.out.println("Enter an Auction name to connect to: ");
                            String ahName = scanner.nextLine();
                            if (agent.connect(ahName)) {
                                break;
                            } else {
                                System.out.println("Incorrect name, try again");
                            }
                        }
                    } else {
                        System.out.println("You must create an agent before connecting to an auction house");
                    }
                    break;
                case "b":
                    if (agent != null) {
                        int item;
                        while (true) {
                            System.out.println("Enter Item Number:");
                            String s = scanner.nextLine();
                            item = Integer.parseInt(s);
                            if (item >= 0 && item <= 7) {
                                break;
                            } else {
                                System.out.println("Item does not exist");
                            }
                        }
                        System.out.println("Enter Bid Price:");
                        String s = scanner.nextLine();
                        int quantity = Integer.parseInt(s);
                        agent.chooseItem(item);
                        agent.bidItem(quantity, item);

                    } else {
                        System.out.println("You must create an agent before placing a bid");
                    }
                    break;
                case "s":
                    if (agent != null) {
                        agent.checkBalance();
                    } else {
                        System.out.println("You must create an agent before checking your balance");
                    }
                    break;
                case "q":
                    if (agent != null) {
                        agent.exit();
                        break;
                    } else {
                        System.out.println("You must create an agent before disconnecting");
                    }
                    break;
                default:
                    System.out.println("Please choose a listed command:");
                    break;
            }
        }
    }
}
