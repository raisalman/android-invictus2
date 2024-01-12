package net.invictusmanagement.invictuslifestyle.models;

import com.google.gson.annotations.SerializedName;

public class Feedback extends ModelBase {

    public String message;
    public Type type;

    public enum Type {

        @SerializedName("1")
        Bug(1),

        @SerializedName("2")
        Enhancement(2),

        @SerializedName("3")
        General(3);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
