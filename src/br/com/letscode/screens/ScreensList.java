package br.com.letscode.screens;

public enum ScreensList {
    START, EXIT, MAIN, PRODUCTS, PRODUCT_TYPES, SESSION, CREATE_PRODUCT;

    public ScreenInterface createInstance() {
        ScreenInterface screenInstance;
        switch (this) {
            case START:
                screenInstance = new StartScreen();
                break;
            case EXIT:
                screenInstance = new ExitScreen();
                break;
            case MAIN:
                screenInstance = new MainScreen();
                break;
            case PRODUCTS:
                screenInstance = new ProductsScreen();
                break;
            case PRODUCT_TYPES:
                screenInstance = new ProductTypesScreen();
                break;
            case SESSION:
                screenInstance = new SessionScreen();
                break;
            case CREATE_PRODUCT:
                screenInstance = new CreateProductScreen();
                break;
            default:
                screenInstance = new ExitScreen();
                break;
        }
        return screenInstance;
    }
}
