package itemServer;

/*enum for hardcoded items*/
public enum ItemTypes {
    STORMLIGHT_ARCHIVE("The Stormlight Archive"),
    MISTBORN("Mistborn"),
    ELANTRIS("Elantris"),
    WARBREAKER("Warbreaker"),
    DUNE("Dune"),
    THE_EXPANSE("The Expanse"),
    SONG_OF_ICE_AND_FIRE("A Song of Ice and Fire"),
    WHEEL_OF_TIME("The Wheel of Time"),
    MALAZAN_THE_FALLEN("Malazan the Fallen");

    private final String name;

    ItemTypes(String name) {
        this.name = name;
    }

    public String getItemType() {
        return name;
    }
}
