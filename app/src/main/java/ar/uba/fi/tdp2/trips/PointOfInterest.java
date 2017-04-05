package ar.uba.fi.tdp2.trips;

public class PointOfInterest {
    public int id;
    public int order;
    public String name;
    public String description;

    public PointOfInterest(int id, int order, String name, String description) {
        this.id             = id;
        this.order          = order;
        this.name           = name;
        this.description    = description;
    }

    @Override
    public String toString() {
        return "PointOfInterest {\n  name: " + name + "\n  description: " + description +
                "\n  order: " + order + "\n}";
    }

    public String getOrder() {
        return String.valueOf(order);
    }
}
