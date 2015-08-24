package it.jaschke.alexandria.bus.events;

/**
 * @author Julio Mendoza on 8/24/15.
 */
public class DatabaseChangedEvent {

    private String message;

    public DatabaseChangedEvent(String message) {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
