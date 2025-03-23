package com.yaksha.assignment;

class Animal {
    String species;

    public Animal() {
        species = "Unknown species"; // Default species
    }

    public void speak() {
        System.out.println("The animal makes a sound.");
    }
}

class Dog extends Animal {

    public Dog() {
        // Explicitly calling the superclass constructor (default constructor of Animal)
        super();
    }

    @Override
    public void speak() {
        System.out.println("The dog barks.");
    }
}

public class SingleInheritanceAssignment {
    public static void main(String[] args) {
        Dog dog = new Dog(); // Creating a Dog object
        System.out.println("Species: " + dog.species); // Should print "Unknown species" as set in Animal's constructor
        dog.speak(); // Should print "The dog barks." because of method overriding
    }
}
