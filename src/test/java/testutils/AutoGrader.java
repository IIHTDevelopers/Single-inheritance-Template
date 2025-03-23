package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration; // Correct import
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class AutoGrader {

	// Test if the code implements single inheritance correctly
	public boolean testSingleInheritance(String filePath) throws IOException {
		System.out.println("Starting testSingleInheritance with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		boolean hasInheritance = false;

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean speciesPrinted = new AtomicBoolean(false);
		AtomicBoolean speakExecuted = new AtomicBoolean(false);

		// Check for class inheritance (Dog class inherits from Animal)
		System.out.println("------ Inheritance Check ------");
		boolean dogClassFound = false;
		boolean animalClassFound = false;

		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;
				if (classDecl.getNameAsString().equals("Dog")) {
					System.out.println("Class 'Dog' found: " + classDecl.getName());
					dogClassFound = true;
					if (classDecl.getExtendedTypes().size() > 0
							&& classDecl.getExtendedTypes(0).getNameAsString().equals("Animal")) {
						System.out.println("Class 'Dog' correctly inherits from 'Animal'");
						hasInheritance = true;
					}
				} else if (classDecl.getNameAsString().equals("Animal")) {
					System.out.println("Class 'Animal' found: " + classDecl.getName());
					animalClassFound = true;
				}
			}
		}

		if (!dogClassFound || !animalClassFound) {
			System.out.println("Error: One or both classes (Animal/Dog) are missing.");
			return false; // Early exit if classes are missing
		}

		if (!hasInheritance) {
			System.out.println("Class Dog is not inherited from Animal");
			return false; // Early exit if inheritance is missing
		}

		// Check for method overriding (speak method in Dog)
		System.out.println("------ Method Override Check ------");
		boolean methodOverriden = false;

		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("speak") && method.getParentNode().get().toString().contains("Dog")) {
				System.out.println("Method 'speak' is overridden in 'Dog' class.");
				methodOverriden = true;
			}
		}

		if (!methodOverriden) {
			System.out.println("Error: Method 'speak' not overridden in 'Dog' class.");
			return false;
		}

		// Check if species is printed in the main method
		System.out.println("------ Species Printed Check ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				// Check for print statements that print species
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						if (callExpr.getNameAsString().equals("println")
								&& callExpr.getArguments().toString().contains("species")) {
							speciesPrinted.set(true);
							System.out.println("Species printed in main.");
						}
					});
				}
			}
		}

		if (!speciesPrinted.get()) {
			System.out.println("Error: Species is not printed in the main method.");
			return false;
		}

		// Check if the speak method is executed in the main method
		System.out.println("------ speak() Executed Check ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						if (callExpr.getNameAsString().equals("speak")) {
							speakExecuted.set(true);
							System.out.println("speak() method is executed in the main method.");
						}
					});
				}
			}
		}

		if (!speakExecuted.get()) {
			System.out.println("Error: speak() method is not executed in the main method.");
			return false;
		}

		// If inheritance, method overriding, species print, and speak execution are
		// correct
		System.out.println("Test passed: Single inheritance is correctly implemented.");
		return true;
	}
}
