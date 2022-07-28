package br.com.letscode.screens;

public enum ScreensList {
    START, EXIT;

    public ScreenInterface createInstance() {
        ScreenInterface screenInstance;
        switch (this) {
            case START:
                screenInstance = new StartScreen();
                break;
            case EXIT:
                screenInstance = new ExitScreen();
                break;
            default:
                screenInstance = new ExitScreen();
                break;
        }
        return screenInstance;
    }
}
