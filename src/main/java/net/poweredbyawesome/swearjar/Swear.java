package net.poweredbyawesome.swearjar;

public class Swear {

    private SwearJar plugin;
    private String name;
    private String pattern;
    private double price;

    public Swear(SwearJar plugin, String swear, String pattern, double price) {
        this.plugin = plugin;
        this.name = swear;
        this.pattern = pattern;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public String getPattern() {
        return this.pattern;
    }

    public double getPrice() {
        return this.price;
    }
}
