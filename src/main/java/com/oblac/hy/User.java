package com.oblac.hy;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class User {

	private static final File usersRoot = new File("users");
	private static AtomicInteger atomicInteger = new AtomicInteger(1);
	private static List<User> allUsers;

	private final String name;
	private final File userFacesFolder;
	private final File userRawsFolder;
	private final int id;

	public User(File userFolder) {
		this.id = atomicInteger.getAndIncrement();
		this.name = userFolder.getName();
		this.userFacesFolder = new File(userFolder, "faces");
		this.userRawsFolder = new File(userFolder, "raw");
	}

	public synchronized static List<User> allUsers() {
		if (allUsers == null) {
			File[] users = usersRoot.listFiles();
			if (users == null) {
				return Collections.emptyList();
			}
			allUsers = Arrays.stream(users).map(User::new).collect(Collectors.toList());
		}
		return allUsers;
	}

	public static void findUserById(int userId, Consumer<User> userConsumer, Runnable userNotFoundBlock) {
		LongAdder usersFounded = new LongAdder();
		allUsers().forEach(user -> {
			if (user.id() == userId) {
				usersFounded.increment();
				userConsumer.accept(user);
			}
		});
		if (usersFounded.intValue() == 0) {
			userNotFoundBlock.run();
		}
	}

	// ---------------------------------------------------------------- user

	public List<File> listRawFiles() {
		File[] raws = userRawsFolder.listFiles();
		if (raws == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(raws);
	}

	public List<File> listFaceFiles() {
		File[] faces = userFacesFolder.listFiles();
		if (faces == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(faces);
	}

	/**
	 * Creates a new face file with given name.
	 */
	public File newFaceFile(String fileName) {
		return new File(userFacesFolder, fileName);
	}

	/**
	 * Returns users name.
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns user id.
	 */
	public int id() {
		return id;
	}

}
