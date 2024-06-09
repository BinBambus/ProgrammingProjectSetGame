package org.example.setgame;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Cards {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "shape")
    private int shape;
    @Column(name = "colour")
    private int colour;
    @Column(name = "filling")
    private int filling;
    @Column(name = "count_")
    private int count;
    @Column(name = "name_")
    private String name = "s";
    public Cards() {}
    public Cards(int shape, int colour, int filling, int count, int id) {
        this.id = id;
        this.shape = shape;
        this.colour = colour;
        this.filling = filling;
        this.count = count;
        name = name + toString(count);
        name = name + toString(filling);
        name = name + toString(colour);
        name = name + toString(shape);
    }

    private String toString(int count) {
        String str = "";
        str = str + count;
        return str;
    }

    public String getName() {
        return name;
    }
    public int getCount() {
        return count;
    }
    public int getFilling() {
        return filling;
    }
    public int getColour() {
        return colour;
    }
    public int getShape() {
        return shape;
    }
}
