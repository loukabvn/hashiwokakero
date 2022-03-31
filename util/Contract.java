package util;

public final class Contract {
	
	// CONSTRUCTEURS
	
    private Contract() {
    	// rien ici
    }
    
    /**
     * Vérifie la condition condition et si elle n'est pas correct, lève une
     * AssertionError avec ou sans message.
     */
    public static void checkCondition(final boolean condition,
    		final String... msg) {
        if (condition) {
            return;
        }
        if (msg.length > 0) {
            throw new AssertionError((Object)msg[0]);
        }
        throw new AssertionError();
    }
}